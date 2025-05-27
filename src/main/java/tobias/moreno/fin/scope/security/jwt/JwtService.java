package tobias.moreno.fin.scope.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(UserDetails user);
    Boolean validateToken(String token, UserDetails userDetails);
    String extractUsername(String token);
}
