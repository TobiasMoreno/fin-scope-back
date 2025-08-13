package tobias.moreno.fin.scope;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testSecurityBeansAreCreated() {
        // Test that AuthenticationManager bean exists
        assertNotNull(applicationContext.getBean(AuthenticationManager.class));
        
        // Test that UserDetailsService bean exists
        assertNotNull(applicationContext.getBean(UserDetailsService.class));
        
        // Test that JwtUtil bean exists
        assertNotNull(applicationContext.getBean("jwtUtil"));
        
        // Test that JwtAuthenticationFilter bean exists
        assertNotNull(applicationContext.getBean("jwtAuthenticationFilter"));
    }
}
