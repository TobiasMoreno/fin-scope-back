package tobias.moreno.fin.scope.dto.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tobias.moreno.fin.scope.entities.UserEntity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {

    private final UserEntity user;

    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRoles() == null) {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
        
        // Agregar roles
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));
            
            // Agregar permisos del rol
            if (role.getPermissions() != null) {
                role.getPermissions().forEach(permission -> 
                    authorities.add(new SimpleGrantedAuthority(permission.getDescription()))
                );
            }
        });
        
        return authorities;
    }

    	@Override
	public String getPassword() {
		// Since we're using OAuth, we don't have passwords
		return null;
	}

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
