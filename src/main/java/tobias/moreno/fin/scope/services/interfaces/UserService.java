package tobias.moreno.fin.scope.services.interfaces;

import com.google.firebase.auth.FirebaseAuthException;
import tobias.moreno.fin.scope.dto.auth.EmailChangePasswordRequestDTO;
import tobias.moreno.fin.scope.dto.auth.EmailLoginRequestDTO;
import tobias.moreno.fin.scope.dto.auth.GoogleLoginRequestDTO;
import tobias.moreno.fin.scope.dto.auth.LoginResponseDTO;
import tobias.moreno.fin.scope.entities.UserEntity;

public interface UserService {
    void emailRegister (EmailLoginRequestDTO newUser) throws FirebaseAuthException;
  
    LoginResponseDTO emailLogin(EmailLoginRequestDTO user) throws FirebaseAuthException;
  
    LoginResponseDTO googleLogin(GoogleLoginRequestDTO accessToken) throws FirebaseAuthException;
  
    void changePassword (EmailChangePasswordRequestDTO changePasswordRequestDTO) throws FirebaseAuthException;
  
    boolean disableUser(Long id);
  
    UserEntity findById(Long id);
  
    UserEntity getUserFromContext();
  
}
