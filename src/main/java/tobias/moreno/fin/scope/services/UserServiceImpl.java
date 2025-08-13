package tobias.moreno.fin.scope.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tobias.moreno.fin.scope.configs.LocalizedMessageService;
import tobias.moreno.fin.scope.dto.auth.GoogleLoginRequestDTO;
import tobias.moreno.fin.scope.dto.auth.LoginResponseDTO;
import tobias.moreno.fin.scope.entities.UserEntity;
import tobias.moreno.fin.scope.repositories.auth.UserRepository;
import tobias.moreno.fin.scope.services.interfaces.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final LocalizedMessageService localizedMessageService;

	@Override
	public LoginResponseDTO googleLogin(GoogleLoginRequestDTO googleRequest) {
		return null;
	}

	@Override
	public UserEntity findById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage("user.not_found")));
	}

	public UserEntity getUserFromContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AuthenticationServiceException(localizedMessageService.getMessage("user.not_authenticated"));
		}

		if (!(authentication.getPrincipal() instanceof UserEntity)) {
			throw new AuthenticationServiceException(localizedMessageService.getMessage("user.invalid_authentication"));
		}

		return ((UserEntity) authentication.getPrincipal());

	}

}
