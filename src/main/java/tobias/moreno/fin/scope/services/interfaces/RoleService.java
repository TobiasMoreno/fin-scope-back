package tobias.moreno.fin.scope.services.interfaces;

import tobias.moreno.fin.scope.entities.RoleEntity;
import tobias.moreno.fin.scope.entities.UserEntity;
import tobias.moreno.fin.scope.models.UserRole;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    
    List<RoleEntity> getAllRoles();
    
    Optional<RoleEntity> getRoleByName(String name);
    
    RoleEntity getDefaultRole();
    
    RoleEntity getPremiumRole();
    
    RoleEntity getAdminRole();
    
    boolean upgradeUserToPremium(Long userId);
    
    boolean downgradeUserToDefault(Long userId);
    
    boolean hasRole(UserEntity user, UserRole role);
    
    boolean hasAnyRole(UserEntity user, List<UserRole> roles);
    
    boolean hasPermission(UserEntity user, String permission);
}
