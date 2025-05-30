package tobias.moreno.fin.scope.services;

import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tobias.moreno.fin.scope.dto.transactions.RequestTransactionDTO;
import tobias.moreno.fin.scope.dto.transactions.ResponseTransactionDTO;
import tobias.moreno.fin.scope.entities.TransactionEntity;
import tobias.moreno.fin.scope.models.AssetType;
import tobias.moreno.fin.scope.repositories.TransactionRepository;
import tobias.moreno.fin.scope.services.interfaces.TransactionService;

import java.math.BigDecimal;
import java.util.List;


@Service
public class TransactionServiceImpl extends BaseServiceImpl<
		RequestTransactionDTO,
		ResponseTransactionDTO,
		TransactionEntity,
		Long
		> implements TransactionService {

	@Getter
	private final TransactionRepository repository;

	private final ModelMapper modelMapper;

	public TransactionServiceImpl(
			TransactionRepository repository,
			ModelMapper modelMapper
	) {
		super(repository,
				modelMapper,
				TransactionEntity.class,
				ResponseTransactionDTO.class
		);
		this.repository = repository;
		this.modelMapper = modelMapper;
	}

	@Override
	public List<ResponseTransactionDTO> findAllByAssetType(AssetType assetType) {
		return repository.findAllByAssetType(AssetType.valueOf(assetType.toString().toUpperCase()))
				.stream()
				.map(entity -> modelMapper.map(entity, ResponseTransactionDTO.class))
				.toList();
	}

	@Override
	public BigDecimal calculateCurrentBalance() {
		return repository.calculateCurrentBalance() != null
				? repository.calculateCurrentBalance()
				: BigDecimal.ZERO;
	}
}