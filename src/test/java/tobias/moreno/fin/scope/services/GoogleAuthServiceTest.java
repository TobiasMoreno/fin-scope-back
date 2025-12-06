package tobias.moreno.fin.scope.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tobias.moreno.fin.scope.dto.auth.GoogleTokenRequest;
import tobias.moreno.fin.scope.entities.RoleEntity;
import tobias.moreno.fin.scope.entities.UserEntity;
import tobias.moreno.fin.scope.repositories.auth.RoleRepository;
import tobias.moreno.fin.scope.repositories.auth.UserRepository;
import tobias.moreno.fin.scope.security.JwtUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Spy
    @InjectMocks
    private GoogleAuthServiceImpl googleAuthService;

    @Test
    void testLoginOrRegister_NewUser() throws Exception {
        // Given
        GoogleTokenRequest request = new GoogleTokenRequest();
        request.setIdToken("valid-google-id-token");

        RoleEntity defaultRole = new RoleEntity();
        defaultRole.setName("DEFAULT");

        UserEntity newUser = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .picture("https://example.com/picture.jpg")
                .provider("google")
                .roles(List.of(defaultRole))
                .build();

        // Mock Google token verification
        GoogleIdToken mockIdToken = mock(GoogleIdToken.class);
        Payload mockPayload = mock(Payload.class);
        when(mockPayload.getEmail()).thenReturn("test@example.com");
        when(mockPayload.get("name")).thenReturn("Test User");
        when(mockPayload.get("picture")).thenReturn("https://example.com/picture.jpg");
        when(mockIdToken.getPayload()).thenReturn(mockPayload);
        
        doReturn(mockIdToken).when(googleAuthService).verifyGoogleToken(anyString());

        when(roleRepository.findByName("DEFAULT")).thenReturn(Optional.of(defaultRole));
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
    void testLoginOrRegister_ExistingUser() throws Exception {
        // Given
        GoogleTokenRequest request = new GoogleTokenRequest();
        request.setIdToken("valid-google-id-token");

        RoleEntity defaultRole = new RoleEntity();
        defaultRole.setName("DEFAULT");

        UserEntity existingUser = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .picture("https://example.com/picture.jpg")
                .provider("google")
                .roles(List.of(defaultRole))
                .build();

        // Mock Google token verification
        GoogleIdToken mockIdToken = mock(GoogleIdToken.class);
        Payload mockPayload = mock(Payload.class);
        when(mockPayload.getEmail()).thenReturn("test@example.com");
        when(mockPayload.get("name")).thenReturn("Test User");
        when(mockPayload.get("picture")).thenReturn("https://example.com/picture.jpg");
        when(mockIdToken.getPayload()).thenReturn(mockPayload);
        
        doReturn(mockIdToken).when(googleAuthService).verifyGoogleToken(anyString());

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
