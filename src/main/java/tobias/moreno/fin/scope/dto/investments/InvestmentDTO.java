package tobias.moreno.fin.scope.dto.investments;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tobias.moreno.fin.scope.models.InvestmentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvestmentDTO {
    private Long id;
    private Long userId;
    private String symbol;
    private String name;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal quantity;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal lastPrice;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal dailyVariationPercent;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal dailyVariationAmount;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal costPerShare;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal gainLossPercent;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal gainLossAmount;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal total;
    
    private InvestmentType type;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Argentina/Buenos_Aires")
    private LocalDateTime createdAt;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Argentina/Buenos_Aires")
    private LocalDateTime updatedAt;
}
