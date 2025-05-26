package tobias.moreno.fin.scope.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Alert Controller", description = "Controlador para gestionar alertas financieras")
public class AlertController {

	@GetMapping("/api/alerts")
	@Operation(summary = "Listar todas las alertas", description = "Devuelve una lista de todas las alertas configuradas")
	public String getAlerts() {
		return "Lista de alertas";
	}
}
