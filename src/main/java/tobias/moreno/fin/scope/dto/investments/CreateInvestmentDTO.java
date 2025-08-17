package tobias.moreno.fin.scope.dto.investments;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import tobias.moreno.fin.scope.models.InvestmentType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateInvestmentDTO {
    
    @NotBlank(message = "El símbolo es obligatorio")
    private String symbol;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal quantity;
    
    @NotNull(message = "El precio actual es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal lastPrice;
    
    // Opcional - se generará automáticamente si no se proporciona
    private String name;

    @NotNull(message = "El tipo de inversión es obligatorio")
    private InvestmentType type;
}
