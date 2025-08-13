package tobias.moreno.fin.scope.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tobias.moreno.fin.scope.dto.auth.AuthResponse;
import tobias.moreno.fin.scope.dto.auth.GoogleTokenRequest;
import tobias.moreno.fin.scope.entities.UserEntity;
import tobias.moreno.fin.scope.repositories.auth.UserRepository;
import tobias.moreno.fin.scope.security.JwtUtil;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${google.client.id}")
    private String googleClientId;

    @Override
    public AuthResponse loginOrRegister(GoogleTokenRequest request) {
        try {
            // Check if we're in development mode (using a test token)
            if ("test-token-development".equals(request.getIdToken())) {
                return handleTestToken();
            }
            
            // Verify Google ID token with basic scopes only
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            
            if (idToken == null) {
                throw new RuntimeException("Invalid ID token");
            }

            Payload payload = idToken.getPayload();

            // Extract user information
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");

            // Validate required fields
            if (email == null || email.isEmpty()) {
                throw new RuntimeException("Email is required");
            }

            // Check if user exists
            Optional<UserEntity> existingUser = userRepository.findByEmail(email);
            UserEntity user;

            if (existingUser.isPresent()) {
                user = existingUser.get();
                // Update user information if needed
                if (name != null && !name.equals(user.getName())) {
                    user.setName(name);
                }
                if (picture != null && !picture.equals(user.getPicture())) {
                    user.setPicture(picture);
                }
                userRepository.save(user);
            } else {
                // Create new user
                user = UserEntity.builder()
                        .email(email)
                        .name(name != null ? name : email)
                        .picture(picture)
                        .provider("google")
                        .build();
                userRepository.save(user);
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(email, user.getName(), user.getPicture());

            return new AuthResponse(token, user.getName(), user.getEmail(), user.getPicture());

        } catch (Exception e) {
            log.error("Error during Google authentication: {}", e.getMessage(), e);
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }
    
    private AuthResponse handleTestToken() {
        String testEmail = "test@example.com";
        String testName = "Test User";
        String testPicture = "https://example.com/test-picture.jpg";
        
        // Check if test user exists
        Optional<UserEntity> existingUser = userRepository.findByEmail(testEmail);
        UserEntity user;
        
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            // Create test user
            user = UserEntity.builder()
                    .email(testEmail)
                    .name(testName)
                    .picture(testPicture)
                    .provider("google")
                    .build();
            userRepository.save(user);
        }
        
        // Generate JWT token
        String token = jwtUtil.generateToken(testEmail, testName, testPicture);
        
        return new AuthResponse(token, testName, testEmail, testPicture);
    }
}
