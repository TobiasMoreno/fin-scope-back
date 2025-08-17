package tobias.moreno.fin.scope.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tobias.moreno.fin.scope.dto.auth.GoogleTokenRequest;
import tobias.moreno.fin.scope.entities.UserEntity;
import tobias.moreno.fin.scope.repositories.auth.UserRepository;
import tobias.moreno.fin.scope.security.JwtUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private GoogleAuthServiceImpl googleAuthService;

    @Test
    void testLoginOrRegister_NewUser() {
        // Given
        GoogleTokenRequest request = new GoogleTokenRequest();
        request.setIdToken("valid-google-id-token");

        UserEntity newUser = UserEntity.builder()
                .email("test@example.com")
                .name("Test User")
                .picture("https://example.com/picture.jpg")
                .provider("google")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(newUser);
        when(jwtUtil.generateToken("test@example.com", "Test User", "https://example.com/picture.jpg", "DEFAULT"))
                .thenReturn("jwt-token");

        // When
        var response = googleAuthService.loginOrRegister(request);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("https://example.com/picture.jpg", response.getPicture());
    }

    @Test
    void testLoginOrRegister_ExistingUser() {
        // Given
        GoogleTokenRequest request = new GoogleTokenRequest();
        request.setIdToken("valid-google-id-token");

        UserEntity existingUser = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .picture("https://example.com/picture.jpg")
                .provider("google")
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(existingUser);
        when(jwtUtil.generateToken("test@example.com", "Test User", "https://example.com/picture.jpg", "DEFAULT"))
                .thenReturn("jwt-token");

        // When
        var response = googleAuthService.loginOrRegister(request);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("https://example.com/picture.jpg", response.getPicture());
    }
}
