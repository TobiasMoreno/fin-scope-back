package tobias.moreno.fin.scope.controllers;

import jakarta.persistence.EntityNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tobias.moreno.fin.scope.dto.transactions.PortfolioCompositionDTO;
import tobias.moreno.fin.scope.dto.transactions.RequestTransactionDTO;
import tobias.moreno.fin.scope.dto.transactions.ResponseTransactionDTO;
import tobias.moreno.fin.scope.models.AssetType;
import tobias.moreno.fin.scope.services.interfaces.TransactionService;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("transactions")
@RestController
@RequiredArgsConstructor
@Tag(name = "Transacciones", description = "Endpoints para gestión de transacciones financieras")
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {

	private final TransactionService transactionService;

	@Operation(
			summary = "Obtener todas las transacciones",
			description = "Retorna todas las transacciones del usuario autenticado",
			responses = {
					@ApiResponse(responseCode = "200", description = "Lista de transacciones obtenida exitosamente"),
					@ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
					@ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
			}
	)
	@GetMapping()
	@PreAuthorize("hasAuthority('READ_BASIC')")
	public ResponseEntity<List<ResponseTransactionDTO>> getAll() {
		List<ResponseTransactionDTO> txs = transactionService.findAll();
		return ResponseEntity.ok(txs);
	}

	@Operation(
			summary = "Obtener transacción por ID",
			description = "Retorna una transacción específica por su ID",
			responses = {
					@ApiResponse(responseCode = "200", description = "Transacción encontrada"),
					@ApiResponse(responseCode = "404", description = "Transacción no encontrada"),
					@ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
					@ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
			}
	)
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('READ_BASIC')")
	public ResponseEntity<ResponseTransactionDTO> getById(@PathVariable Long id) {
		ResponseTransactionDTO dto = transactionService.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Transaction with ID " + id + " not found"));
		return ResponseEntity.ok(dto);
	}

	@Operation(
			summary = "Obtener balance actual",
			description = "Calcula y retorna el balance actual del usuario",
			responses = {
					@ApiResponse(responseCode = "200", description = "Balance calculado exitosamente"),
					@ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
					@ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
			}
	)
	@GetMapping("current-balance")
	@PreAuthorize("hasAuthority('READ_BASIC')")
	public ResponseEntity<BigDecimal> getCurrentBalance() {
		return ResponseEntity.ok(transactionService.calculateCurrentBalance());
	}

	@Operation(
			summary = "Crear nueva transacción",
			description = "Crea una nueva transacción financiera",
			responses = {
					@ApiResponse(responseCode = "200", description = "Transacción creada exitosamente",
							content = @Content(schema = @Schema(implementation = ResponseTransactionDTO.class))),
					@ApiResponse(responseCode = "400", description = "Datos de transacción inválidos"),
					@ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
					@ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
			}
	)
	@PostMapping
	@PreAuthorize("hasAuthority('WRITE_BASIC')")
	public ResponseEntity<ResponseTransactionDTO> create(@RequestBody RequestTransactionDTO tx) {
		return ResponseEntity.ok(transactionService.save(tx));
	}

	@Operation(
			summary = "Actualizar transacción",
			description = "Actualiza una transacción existente por su ID",
			responses = {
					@ApiResponse(responseCode = "200", description = "Transacción actualizada exitosamente"),
					@ApiResponse(responseCode = "404", description = "Transacción no encontrada"),
					@ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
					@ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
			}
	)
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('WRITE_BASIC')")
	public ResponseEntity<ResponseTransactionDTO> update(
			@PathVariable Long id,
			@RequestBody RequestTransactionDTO tx
	) {
		ResponseTransactionDTO updated = transactionService.update(id, tx);
		return ResponseEntity.ok(updated);
	}

	@Operation(
			summary = "Eliminar transacción",
			description = "Elimina una transacción por su ID",
			responses = {
					@ApiResponse(responseCode = "204", description = "Transacción eliminada exitosamente"),
					@ApiResponse(responseCode = "404", description = "Transacción no encontrada"),
					@ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
					@ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
			}
	)
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('DELETE_BASIC')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		transactionService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(
			summary = "Filtrar transacciones por tipo de activo",
			description = "Retorna transacciones filtradas por tipo de activo específico",
			responses = {
					@ApiResponse(responseCode = "200", description = "Transacciones filtradas obtenidas exitosamente"),
					@ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
					@ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
			}
	)
	@GetMapping("asset-type")
	@PreAuthorize("hasAuthority('READ_BASIC')")
	public ResponseEntity<List<ResponseTransactionDTO>> findAllByAssetType(@RequestParam AssetType assetType) {
		List<ResponseTransactionDTO> txs = transactionService.findAllByAssetType(assetType);
		return ResponseEntity.ok(txs);
	}

	@Operation(
			summary = "Obtener composición del portafolio",
			description = "Retorna la composición detallada del portafolio de inversiones",
			responses = {
					@ApiResponse(responseCode = "200", description = "Composición del portafolio obtenida exitosamente"),
					@ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
					@ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
			}
	)
	@GetMapping("portfolio-composition")
	@PreAuthorize("hasAuthority('READ_ADVANCED')")
	public ResponseEntity<List<PortfolioCompositionDTO>> getPortfolioComposition() {
		return ResponseEntity.ok(transactionService.getPortfolioComposition());
	}

}
