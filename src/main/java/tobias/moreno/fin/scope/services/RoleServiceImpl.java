package tobias.moreno.fin.scope.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tobias.moreno.fin.scope.entities.RoleEntity;
import tobias.moreno.fin.scope.entities.UserEntity;
import tobias.moreno.fin.scope.models.UserRole;
import tobias.moreno.fin.scope.repositories.auth.RoleRepository;
import tobias.moreno.fin.scope.repositories.auth.UserRepository;
import tobias.moreno.fin.scope.services.interfaces.RoleService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public List<RoleEntity> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<RoleEntity> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public RoleEntity getDefaultRole() {
        return roleRepository.findByName("DEFAULT")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
    }

    @Override
    public RoleEntity getPremiumRole() {
        return roleRepository.findByName("PREMIUM")
                .orElseThrow(() -> new RuntimeException("Premium role not found"));
    }

    @Override
    public RoleEntity getAdminRole() {
        return roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
    }

    @Override
    public boolean upgradeUserToPremium(Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            RoleEntity premiumRole = getPremiumRole();
            
            // Verificar si el usuario ya tiene rol PREMIUM
            boolean hasPremiumRole = user.getRoles().stream()
                    .anyMatch(role -> "PREMIUM".equals(role.getName()));
            
            if (!hasPremiumRole) {
                user.getRoles().add(premiumRole);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean downgradeUserToDefault(Long userId) {
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            RoleEntity defaultRole = getDefaultRole();
            
            // Remover rol PREMIUM si existe
            user.getRoles().removeIf(role -> "PREMIUM".equals(role.getName()));
            
            // Asegurar que tenga rol DEFAULT
            boolean hasDefaultRole = user.getRoles().stream()
                    .anyMatch(role -> "DEFAULT".equals(role.getName()));
            
            if (!hasDefaultRole) {
                user.getRoles().add(defaultRole);
            }
            
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasRole(UserEntity user, UserRole role) {
        return user.getRoles().stream()
                .anyMatch(userRole -> userRole.getName().equals(role.name()));
    }

    @Override
    public boolean hasAnyRole(UserEntity user, List<UserRole> roles) {
        return user.getRoles().stream()
                .anyMatch(userRole -> roles.stream()
                        .anyMatch(role -> role.name().equals(userRole.getName())));
    }

    @Override
    public boolean hasPermission(UserEntity user, String permission) {
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(perm -> perm.getDescription().equals(permission));
    }
}
