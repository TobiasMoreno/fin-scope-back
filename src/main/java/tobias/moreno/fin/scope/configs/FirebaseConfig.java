package tobias.moreno.fin.scope.configs;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class FirebaseConfig {

	@Bean
	public GoogleCredentials firebaseCredentials() throws IOException {
		ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");
		return GoogleCredentials.fromStream(resource.getInputStream());
	}

	@Bean
	public FirebaseApp initializeFirebase(GoogleCredentials credentials) {
		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(credentials)
				.build();
		return FirebaseApp.initializeApp(options);
	}
}
