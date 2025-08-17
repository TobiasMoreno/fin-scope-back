package tobias.moreno.fin.scope.dto.investments;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tobias.moreno.fin.scope.models.InvestmentType;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardSectionDTO {
    private InvestmentType type;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal total;
    
    private List<InvestmentDTO> investments;
}
