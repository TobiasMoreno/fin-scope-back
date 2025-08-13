package tobias.moreno.fin.scope.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		Server devServer = new Server();
		devServer.setUrl("http://localhost:8080");
		devServer.setDescription("Server URL in Development environment");

		Contact contact = new Contact();
		contact.setEmail("tobias.moreno@example.com");
		contact.setName("Tobias Moreno");

		Info info = new Info()
				.title("Financial Management API")
				.version("1.0")
				.contact(contact)
				.description("API para gestionar alertas financieras en tiempo real");

		return new OpenAPI()
				.info(info)
				.servers(List.of(devServer));
	}

}
