package tobias.moreno.fin.scope.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tobias.moreno.fin.scope.configs.LocalizedMessageService;
import tobias.moreno.fin.scope.dto.auth.CustomUserDetails;
import tobias.moreno.fin.scope.entities.UserEntity;
import tobias.moreno.fin.scope.repositories.auth.UserRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final LocalizedMessageService localizedMessageService;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(this.localizedMessageService.getMessage("user.not_found")));
        return new CustomUserDetails(user);
    }
}
