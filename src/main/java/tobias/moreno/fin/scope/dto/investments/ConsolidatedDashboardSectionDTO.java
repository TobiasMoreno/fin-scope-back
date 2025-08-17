package tobias.moreno.fin.scope.dto.investments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tobias.moreno.fin.scope.models.InvestmentType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsolidatedDashboardSectionDTO {
    
    private InvestmentType type;
    private List<ConsolidatedInvestmentDTO> investments;
}
