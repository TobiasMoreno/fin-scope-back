package tobias.moreno.fin.scope.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import tobias.moreno.fin.scope.security.firebase.FirebaseAuthenticationProvider;

@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig {
    private final UserDetailsService userDetailsService;

    @Bean
    FirebaseAuthenticationProvider firebaseAuthenticationProvider(){
        return new FirebaseAuthenticationProvider(userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return new ProviderManager(firebaseAuthenticationProvider());
    }
}
