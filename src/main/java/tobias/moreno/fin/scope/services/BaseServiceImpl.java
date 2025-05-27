package tobias.moreno.fin.scope.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import tobias.moreno.fin.scope.services.interfaces.BaseService;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class BaseServiceImpl<Req, Res, T, ID extends Serializable>
		implements BaseService<Req, Res, ID> {

	protected final JpaRepository<T, ID> repository;
	protected final ModelMapper modelMapper;
	private final Class<T> entityClass;
	private final Class<Res> responseClass;

	@Override
	public Res save(Req request) {
		T entity = modelMapper.map(request, entityClass);
		T saved = repository.save(entity);
		return modelMapper.map(saved, responseClass);
	}

	@Override
	public Optional<Res> findById(ID id) {
		return repository.findById(id)
				.map(e -> modelMapper.map(e, responseClass));
	}

	@Override
	public List<Res> findAll() {
		return repository.findAll().stream()
				.map(e -> modelMapper.map(e, responseClass))
				.collect(Collectors.toList());
	}

	@Override
	public Res update(ID id, Req request) {
		T existing = repository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Entity with id " + id + " not found"));

		modelMapper.map(request, existing);
		try {
			Field idField = null;
			Class<?> current = entityClass;
			while (current != null) {
				try {
					idField = current.getDeclaredField("id");
					idField.setAccessible(true);
					break;
				} catch (NoSuchFieldException e) {
					current = current.getSuperclass();
				}
			}

			if (idField == null) {
				throw new RuntimeException("Field 'id' not found in class hierarchy");
			}

			idField.set(existing, id);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException("Could not set id on entity for update", ex);
		}
		T updated = repository.save(existing);
		return modelMapper.map(updated, responseClass);
	}

	@Override
	public void deleteById(ID id) {
		repository.deleteById(id);
	}
}