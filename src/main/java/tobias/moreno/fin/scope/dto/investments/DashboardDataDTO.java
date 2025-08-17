package tobias.moreno.fin.scope.dto.investments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardDataDTO {
    private List<DashboardSectionDTO> sections;
    
    private TotalVariationDTO totalVariation;
    private String currency;
}
