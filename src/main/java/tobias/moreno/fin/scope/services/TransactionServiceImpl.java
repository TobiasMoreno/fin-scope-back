package tobias.moreno.fin.scope.services;

import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import tobias.moreno.fin.scope.dto.transactions.PortfolioCompositionDTO;
import tobias.moreno.fin.scope.dto.transactions.RequestTransactionDTO;
import tobias.moreno.fin.scope.dto.transactions.ResponseTransactionDTO;
import tobias.moreno.fin.scope.entities.TransactionEntity;
import tobias.moreno.fin.scope.models.AssetType;
import tobias.moreno.fin.scope.models.TransactionType;
import tobias.moreno.fin.scope.repositories.TransactionRepository;
import tobias.moreno.fin.scope.services.interfaces.TransactionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

	@Override
	public List<PortfolioCompositionDTO> getPortfolioComposition() {
		List<TransactionEntity> allTransactions = repository.findAll();
		List<PortfolioCompositionDTO> portfolioCompositionDTOs = new ArrayList<>();

		Map<String, BigDecimal> values = new HashMap<>();

		for (TransactionEntity transaction : allTransactions) {
			BigDecimal totalTxValue = transaction.getQuantity().multiply(transaction.getPricePerUnit()).setScale(4, RoundingMode.HALF_UP);
			String assetName = transaction.getAssetName();

			if (transaction.getTxType() == TransactionType.BUY) {
				values.merge(assetName, totalTxValue, BigDecimal::add);
			} else if (transaction.getTxType() == TransactionType.SELL) {
				values.merge(assetName, totalTxValue.negate(), BigDecimal::add);
			}
		}

		BigDecimal totalPortfolioValue = calculateCurrentBalance();

		for (Map.Entry<String, BigDecimal> entry : values.entrySet()) {
			String assetName = entry.getKey();
			BigDecimal totalValue = entry.getValue();
			BigDecimal percentage = BigDecimal.ZERO;

			if (totalPortfolioValue.compareTo(BigDecimal.ZERO) > 0) {
				percentage = totalValue.divide(totalPortfolioValue, 8, BigDecimal.ROUND_HALF_UP);
			}

			portfolioCompositionDTOs.add(
					PortfolioCompositionDTO.builder().assetName(assetName).percentage(percentage).totalValue(totalValue).build()
			);
		}
		return portfolioCompositionDTOs;

	}
}