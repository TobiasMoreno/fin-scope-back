package tobias.moreno.fin.scope.services.interfaces;

import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Service
public interface BaseService<Req,Res, ID extends Serializable> {

	Res save(Req request);

	Optional<Res> findById(ID id);

	List<Res> findAll();

	Res update(ID id, Req request);

	void deleteById(ID id);
}
