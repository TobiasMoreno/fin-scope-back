package tobias.moreno.fin.scope.dto.investments;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tobias.moreno.fin.scope.models.InvestmentType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateInvestmentDTO {
    
    private String symbol;
    private String name;
    
    @Positive(message = "La cantidad debe ser mayor a 0")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal quantity;
    
    @DecimalMin(value = "0.01", message = "El último precio debe ser mayor a 0")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal lastPrice;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal dailyVariationPercent;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal dailyVariationAmount;
    
    @DecimalMin(value = "0.01", message = "El precio de compra debe ser mayor a 0")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal costPerShare;
    
    private InvestmentType type;
}
