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
public class TotalVariationDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal daily;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal total;
}
