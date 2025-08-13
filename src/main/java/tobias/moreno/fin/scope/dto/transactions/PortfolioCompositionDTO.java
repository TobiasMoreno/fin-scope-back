package tobias.moreno.fin.scope.dto.transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PortfolioCompositionDTO {
	private String assetName; //Todo sacar el assetName q corresponde a la key del map
	private BigDecimal totalValue;
	private BigDecimal percentage;
}
