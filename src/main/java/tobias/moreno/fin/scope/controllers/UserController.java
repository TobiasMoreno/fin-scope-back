package tobias.moreno.fin.scope.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tobias.moreno.fin.scope.dto.auth.GoogleLoginRequestDTO;
import tobias.moreno.fin.scope.dto.auth.LoginResponseDTO;
import tobias.moreno.fin.scope.services.interfaces.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Autenticacion", description = "Endpoints para autenticación y registro de usuarios")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Inicio de sesión con Google",
            description = "Autentica a un usuario con Google OAuth y devuelve un token JWT. Este endpoint no requiere autenticación previa.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
                            content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Token inválido o credenciales incorrectas", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    @PostMapping("/google-login")
    public ResponseEntity<LoginResponseDTO> googleLogin(@RequestBody @Valid GoogleLoginRequestDTO googleToken) throws FirebaseAuthException {
        LoginResponseDTO response = userService.googleLogin(googleToken);
        return ResponseEntity.status(200).body(response);
    }
   
}
