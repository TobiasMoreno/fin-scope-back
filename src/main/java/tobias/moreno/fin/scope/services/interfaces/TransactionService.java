package tobias.moreno.fin.scope.services.interfaces;

import org.springframework.stereotype.Service;
import tobias.moreno.fin.scope.dto.transactions.PortfolioCompositionDTO;
import tobias.moreno.fin.scope.dto.transactions.RequestTransactionDTO;
import tobias.moreno.fin.scope.dto.transactions.ResponseTransactionDTO;
import tobias.moreno.fin.scope.models.AssetType;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface TransactionService extends BaseService<RequestTransactionDTO, ResponseTransactionDTO, Long> {

	List<ResponseTransactionDTO> findAllByAssetType(AssetType assetType);
	BigDecimal calculateCurrentBalance();
	PortfolioCompositionDTO getPortfolioComposition();
}
