package tobias.moreno.fin.scope.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tobias.moreno.fin.scope.dto.auth.AuthResponse;
import tobias.moreno.fin.scope.dto.auth.GoogleTokenRequest;
import tobias.moreno.fin.scope.services.GoogleAuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Google Authentication", description = "Endpoints for Google OAuth 2.0 authentication")
@CrossOrigin(origins = "*")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    @PostMapping("/google")
    @Operation(summary = "Authenticate with Google", description = "Authenticate user using Google ID token")
    public ResponseEntity<AuthResponse> authenticateWithGoogle(@Valid @RequestBody GoogleTokenRequest request) {
        AuthResponse response = googleAuthService.loginOrRegister(request);
        return ResponseEntity.ok(response);
    }
}
