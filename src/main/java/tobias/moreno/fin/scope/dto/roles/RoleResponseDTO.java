package tobias.moreno.fin.scope.dto.roles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tobias.moreno.fin.scope.entities.RoleEntity;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDTO {
    private Long id;
    private String name;
    private List<String> permissions;

    public static RoleResponseDTO fromEntity(RoleEntity role) {
        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setPermissions(role.getPermissions().stream()
                .map(permission -> permission.getDescription())
                .collect(Collectors.toList()));
        return dto;
    }
}
