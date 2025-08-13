package tobias.moreno.fin.scope.services.interfaces;

import tobias.moreno.fin.scope.dto.auth.GoogleLoginRequestDTO;
import tobias.moreno.fin.scope.dto.auth.LoginResponseDTO;
import tobias.moreno.fin.scope.entities.UserEntity;

public interface UserService {
  
    LoginResponseDTO googleLogin(GoogleLoginRequestDTO accessToken);
  
    UserEntity findById(Long id);
  
    UserEntity getUserFromContext();
  
}
