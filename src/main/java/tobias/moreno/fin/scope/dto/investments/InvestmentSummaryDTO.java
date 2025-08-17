package tobias.moreno.fin.scope.dto.investments;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvestmentSummaryDTO {
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal totalInvested;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal totalCurrent;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal totalGain;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal gainPercentage;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal dailyVariation;
    
    private String currency;
}
