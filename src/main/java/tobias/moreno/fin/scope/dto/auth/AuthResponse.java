package tobias.moreno.fin.scope.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthResponse {
    @Schema(description = "JWT token for the application")
    private String token;
    
    @Schema(description = "User's name")
    private String nombre;
    
    @Schema(description = "User's email")
    private String email;
    
    @Schema(description = "User's profile picture URL")
    private String foto;
}
