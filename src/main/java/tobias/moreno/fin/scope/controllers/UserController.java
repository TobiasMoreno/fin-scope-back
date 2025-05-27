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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tobias.moreno.fin.scope.dto.auth.EmailChangePasswordRequestDTO;
import tobias.moreno.fin.scope.dto.auth.EmailLoginRequestDTO;
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
            summary = "Registro de usuario por email",
            description = "Registra un usuario con email y contraseña mediante Firebase Authentication.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    @PostMapping("/email-register")
    public ResponseEntity<?> emailRegister(@RequestBody @Valid EmailLoginRequestDTO userRequest) throws FirebaseAuthException {
        userService.emailRegister(userRequest);
        return ResponseEntity.status(201).build();
    }

    @Operation(
            summary = "Inicio de sesión con email",
            description = "Inicia sesión con email y contraseña y devuelve un token JWT.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
                            content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Credenciales incorrectas", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    @PostMapping("/email-login")
    public ResponseEntity<LoginResponseDTO> emailLogin(@RequestBody @Valid EmailLoginRequestDTO loginRequest) throws FirebaseAuthException {
        LoginResponseDTO response = userService.emailLogin(loginRequest);
        return ResponseEntity.status(200).body(response);
    }

    @Operation(
            summary = "Inicio de sesión con Google",
            description = "Autentica a un usuario con Google OAuth y devuelve un token JWT.",
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

    @Operation(
            summary = "Cambio de contraseña",
            description = "Permite a un usuario cambiar su contraseña mediante su correo electrónico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    @PostMapping("/change-password")
    public ResponseEntity<?> emailChangePassword (@RequestBody @Valid EmailChangePasswordRequestDTO emailChangePasswordRequestDTO) throws FirebaseAuthException {
        userService.changePassword(emailChangePasswordRequestDTO);
        return ResponseEntity.status(200).build();
    }
    @Operation(
            summary = "Deshabilitar usuario",
            description = "Solo El usuario propietario puede desabilitar su cuenta, debe estar logueado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario deshabilitado exitosamente"),
                    @ApiResponse(responseCode = "403", description = "El usuario logueado es distinto al que esta intentando deshabilitar", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
            }
    )
    @PatchMapping("/user-disable/{id}")
    public ResponseEntity<?> disableUser(@PathVariable Long id){
        return ResponseEntity.status(200).body(userService.disableUser(id));
    }
}
