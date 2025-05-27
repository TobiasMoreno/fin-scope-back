package tobias.moreno.fin.scope.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tobias.moreno.fin.scope.configs.LocalizedMessageService;
import tobias.moreno.fin.scope.entities.RoleEntity;
import tobias.moreno.fin.scope.repositories.auth.RoleRepository;
import tobias.moreno.fin.scope.services.interfaces.RoleService;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final LocalizedMessageService localizedMessageService;

    public RoleEntity findByName(String roleName){
        return roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException(this.localizedMessageService.getMessage("role.not_found")));
    }
}
