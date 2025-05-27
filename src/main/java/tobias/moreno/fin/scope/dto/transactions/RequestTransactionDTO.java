package tobias.moreno.fin.scope.dto.transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tobias.moreno.fin.scope.models.AssetType;
import tobias.moreno.fin.scope.models.TransactionType;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestTransactionDTO {
	private String assetName;
	private AssetType assetType;
	private TransactionType txType;
	private BigDecimal quantity;
	private BigDecimal pricePerUnit;

}
