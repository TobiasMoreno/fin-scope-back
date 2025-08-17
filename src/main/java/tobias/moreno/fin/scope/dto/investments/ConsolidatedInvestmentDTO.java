package tobias.moreno.fin.scope.dto.investments;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tobias.moreno.fin.scope.models.InvestmentType;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsolidatedInvestmentDTO {
    
    private String symbol;
    private String name;
    private InvestmentType type;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal totalQuantity;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal averageLastPrice;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal averageCostPerShare;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal totalValue;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal totalGainLoss;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal totalDailyVariation;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal gainLossPercent;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal dailyVariationPercent;
}
