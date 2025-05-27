package tobias.moreno.fin.scope.services;

import com.google.firebase.auth.FirebaseAuthException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tobias.moreno.fin.scope.configs.LocalizedMessageService;
import tobias.moreno.fin.scope.configs.mapper.ModelMapperUtils;
import tobias.moreno.fin.scope.dto.auth.CustomUserDetails;
import tobias.moreno.fin.scope.dto.auth.EmailChangePasswordRequestDTO;
import tobias.moreno.fin.scope.dto.auth.EmailLoginRequestDTO;
import tobias.moreno.fin.scope.dto.auth.GoogleLoginRequestDTO;
import tobias.moreno.fin.scope.dto.auth.LoginResponseDTO;
import tobias.moreno.fin.scope.dto.auth.UserLoginResponseDTO;
import tobias.moreno.fin.scope.entities.RoleEntity;
import tobias.moreno.fin.scope.entities.UserEntity;
import tobias.moreno.fin.scope.exceptions.UnauthorizedActionException;
import tobias.moreno.fin.scope.repositories.auth.UserRepository;
import tobias.moreno.fin.scope.security.firebase.FirebaseAuthService;
import tobias.moreno.fin.scope.security.firebase.FirebaseAuthenticationToken;
import tobias.moreno.fin.scope.security.jwt.Jwt;
import tobias.moreno.fin.scope.security.jwt.JwtService;
import tobias.moreno.fin.scope.services.interfaces.RoleService;
import tobias.moreno.fin.scope.services.interfaces.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private static final String API_KEY = "AIzaSyDCNiCZ5oOFW6akmLvX4RdWcdwv5wQLWHQ";
	private static final String FIREBASE_AUTH_URL_WITH_PASSWORD = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;
	private static final String FIREBASE_AUTH_URL_WITH_PROVIDER = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=" + API_KEY;
	private final UserRepository userRepository;
	private final FirebaseAuthService firebaseAuthService;
	private final AuthenticationManager authenticationManager;
	private final RoleService roleService;
	private final JwtService jwtService;
	private final ModelMapperUtils modelMapperUtils;
	private final PasswordEncoder passwordEncoder;
	private final LocalizedMessageService localizedMessageService;


	@Override
	public void emailRegister(EmailLoginRequestDTO newUser) throws FirebaseAuthException {
		String email = newUser.getEmail();
		String password = newUser.getPassword();
		String uid = firebaseAuthService.registerUser(email, password);

		if (userRepository.findByEmail(email).isEmpty()) addUserToDb(email, newUser.getRole(), password);
	}

	@Override
	public LoginResponseDTO emailLogin(EmailLoginRequestDTO loginRequestDTO) throws FirebaseAuthException {
		String email = loginRequestDTO.getEmail();
		String password = loginRequestDTO.getPassword();

		Map<String, Object> request = new HashMap<>();
		request.put("email", email);
		request.put("password", password);
		request.put("returnSecureToken", true);

		Map<String, Object> response;
		try {
			response = callFirebaseAuth(FIREBASE_AUTH_URL_WITH_PASSWORD, request);
		} catch (RestClientException e) {
			throw new AuthenticationServiceException(localizedMessageService.getMessage("firebase.auth.error", e.getMessage()), e);
		}

		if (response == null || !response.containsKey("idToken")) {
			throw new AuthenticationServiceException(localizedMessageService.getMessage("firebase.invalid_id_token"));
		}

		String idToken = (String) response.get("idToken");


		firebaseAuthService.verifyToken(idToken);

		if (userRepository.findByEmail(email).isEmpty()) addUserToDb(email, loginRequestDTO.getRole(), password);

		Authentication auth = authenticationManager.authenticate(
				new FirebaseAuthenticationToken(idToken)
		);

		CustomUserDetails userAuthenticated = (CustomUserDetails) auth.getPrincipal();

		Jwt jwt = new Jwt(jwtService.generateToken(userAuthenticated));

		UserLoginResponseDTO userResponse = modelMapperUtils.map(userAuthenticated.getUserEntity(), UserLoginResponseDTO.class);

		return new LoginResponseDTO(userResponse, jwt);
	}

	@Override
	public LoginResponseDTO googleLogin(GoogleLoginRequestDTO googleRequest) throws FirebaseAuthException {
		String accessToken = googleRequest.getAccessToken();

		Map<String, Object> request = new HashMap<>();
		request.put("postBody", "access_token=" + accessToken + "&providerId=google.com");
		request.put("requestUri", "http://localhost");
		request.put("returnIdpCredential", true);
		request.put("returnSecureToken", true);

		Map<String, Object> response;
		try {
			response = callFirebaseAuth(FIREBASE_AUTH_URL_WITH_PROVIDER, request);
		} catch (RestClientException e) {
			throw new AuthenticationServiceException("Auth with firebase error: " + e.getMessage(), e);
		}

		if (response == null || !response.containsKey("idToken")) {
			throw new AuthenticationServiceException(localizedMessageService.getMessage("firebase.invalid_id_token"));
		}

		String idToken = (String) response.get("idToken");

		firebaseAuthService.verifyToken(idToken);

		String email = (String) response.get("email");

		if (response.containsKey("isNewUser") && userRepository.findByEmail(email).isEmpty())
			addUserToDb(email, googleRequest.getRole(), null);

		if (!response.containsKey("isNewUser") && userRepository.findByEmail(email).isEmpty())
			addUserToDb(email, googleRequest.getRole(), null);

		Authentication auth = authenticationManager.authenticate(
				new FirebaseAuthenticationToken(idToken)
		);

		CustomUserDetails userAuthenticated = (CustomUserDetails) auth.getPrincipal();

		Jwt jwt = new Jwt(jwtService.generateToken(userAuthenticated));

		UserLoginResponseDTO userResponse = modelMapperUtils.map(userAuthenticated.getUserEntity(), UserLoginResponseDTO.class);

		return new LoginResponseDTO(userResponse, jwt);
	}

	@Override
	public void changePassword(EmailChangePasswordRequestDTO changePasswordRequestDTO) throws FirebaseAuthException {
		String email = changePasswordRequestDTO.getEmail();
		UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
		String newPassword = changePasswordRequestDTO.getNewPassword();

		Map<String, Object> request = new HashMap<>();
		request.put("email", email);
		request.put("password", changePasswordRequestDTO.getOldPassword());

		try {
			callFirebaseAuth(FIREBASE_AUTH_URL_WITH_PASSWORD, request);
		} catch (RestClientException e) {
			throw new AuthenticationServiceException("Auth with firebase error: " + e.getMessage(), e);
		}

		if (!changePasswordRequestDTO.getConfirmPassword().equals(newPassword)) {
			throw new RuntimeException(localizedMessageService.getMessage("user.passwords_not_match"));
		}

		firebaseAuthService.changeUserPassword(email, newPassword);
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	@Override
	@Transactional
	public boolean disableUser(Long id) {
		UserEntity userContext = getUserFromContext();
		if (userContext.getId().equals(id)) {

			UserEntity user = this.findById(id);

			if (user.getId().equals(id)) {
				if (user.isEnabled()) {
					user.setEnabled(false);
					userRepository.save(user);
					return true;
				}
				return true;
			}
		}

		throw new UnauthorizedActionException(localizedMessageService.getMessage("user.without_permissions"));
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

		if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
			throw new AuthenticationServiceException(localizedMessageService.getMessage("user.invalid_authentication"));
		}

		return ((CustomUserDetails) authentication.getPrincipal()).getUserEntity();

	}

	private void addUserToDb(String email, String roleName, String password) {
		UserEntity newUser = new UserEntity();
		newUser.setEmail(email);
		newUser.setEnabled(true);
		newUser.setIsAccountNotLocked(true);
		newUser.setIsAccountNotExpired(true);
		newUser.setIsAccountNotLocked(true);
		if (password != null) newUser.setPassword(passwordEncoder.encode(password));

		List<RoleEntity> roles = new ArrayList<>();

		if (roleName != null) roles.add(roleService.findByName(roleName.toUpperCase()));

		newUser.setRoles(roles);

		userRepository.save(newUser);

	}

	protected Map<String, Object> callFirebaseAuth(String url, Map<String, Object> request) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.postForObject(url, request, Map.class);
	}
}
