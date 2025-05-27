package tobias.moreno.fin.scope.dto.auth;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tobias.moreno.fin.scope.entities.UserEntity;

import java.util.Collection;
import java.util.stream.Collectors;

@Builder
@RequiredArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {

    private final UserEntity userEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role.getName().toUpperCase()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return userEntity.getIsAccountNotExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return userEntity.getIsAccountNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userEntity.getIsCredentialNotExpired();
    }

    @Override
    public boolean isEnabled() {
        return userEntity.isEnabled();
    }
}

