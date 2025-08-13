package tobias.moreno.fin.scope.services;

import tobias.moreno.fin.scope.dto.auth.AuthResponse;
import tobias.moreno.fin.scope.dto.auth.GoogleTokenRequest;

public interface GoogleAuthService {
    AuthResponse loginOrRegister(GoogleTokenRequest request);
}
