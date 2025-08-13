package tobias.moreno.fin.scope;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tobias.moreno.fin.scope.entities.PermissionEntity;
import tobias.moreno.fin.scope.entities.RoleEntity;
import tobias.moreno.fin.scope.entities.UserEntity;
import tobias.moreno.fin.scope.repositories.auth.PermissionRepository;
import tobias.moreno.fin.scope.repositories.auth.RoleRepository;
import tobias.moreno.fin.scope.repositories.auth.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DatabaseSetupTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    void testDatabaseSetup() {
        // Test that users were created
        List<UserEntity> users = userRepository.findAll();
        assertFalse(users.isEmpty(), "Users should be created from data.sql");
        
        // Test that roles were created
        List<RoleEntity> roles = roleRepository.findAll();
        assertFalse(roles.isEmpty(), "Roles should be created from data.sql");
        
        // Test that permissions were created
        List<PermissionEntity> permissions = permissionRepository.findAll();
        assertFalse(permissions.isEmpty(), "Permissions should be created from data.sql");
        
        // Test specific data
        assertTrue(users.stream().anyMatch(user -> "admin@example.com".equals(user.getEmail())));
        assertTrue(users.stream().anyMatch(user -> "user@example.com".equals(user.getEmail())));
        assertTrue(roles.stream().anyMatch(role -> "ADMIN".equals(role.getName())));
        assertTrue(roles.stream().anyMatch(role -> "USER".equals(role.getName())));
        assertTrue(permissions.stream().anyMatch(permission -> "READ_PRIVILEGE".equals(permission.getDescription())));
    }
}
