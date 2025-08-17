package tobias.moreno.fin.scope.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tobias.moreno.fin.scope.dto.auth.CustomUserDetails;
import tobias.moreno.fin.scope.dto.investments.*;
import tobias.moreno.fin.scope.models.InvestmentType;
import tobias.moreno.fin.scope.services.interfaces.InvestmentService;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/investments")
@RestController
@RequiredArgsConstructor
@Tag(name = "Inversiones", description = "Endpoints para gestión de inversiones de usuarios")
@SecurityRequirement(name = "Bearer Authentication")
public class InvestmentController {

    private final InvestmentService investmentService;

    @Operation(
            summary = "Obtener datos del dashboard",
            description = "Obtiene todos los datos del dashboard para el usuario autenticado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Datos del dashboard obtenidos exitosamente",
                            content = @Content(schema = @Schema(implementation = DashboardDataDTO.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('READ_BASIC')")
    public ResponseEntity<DashboardDataDTO> getDashboard(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        DashboardDataDTO dashboardData = investmentService.getDashboardData(userId);
        return ResponseEntity.ok(dashboardData);
    }

    @Operation(
            summary = "Obtener dashboard con precios en tiempo real",
            description = "Obtiene todos los datos del dashboard con precios actualizados desde Binance en tiempo real",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Datos del dashboard obtenidos exitosamente",
                            content = @Content(schema = @Schema(implementation = DashboardDataDTO.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @GetMapping("/dashboard/realtime")
    @PreAuthorize("hasAuthority('READ_BASIC')")
    public ResponseEntity<DashboardDataDTO> getDashboardWithRealTimePrices(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        DashboardDataDTO dashboardData = investmentService.getDashboardData(userId);
        return ResponseEntity.ok(dashboardData);
    }

    @Operation(
            summary = "Obtener todas las inversiones",
            description = "Obtiene todas las inversiones del usuario autenticado con paginación y filtros",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de inversiones obtenida exitosamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('READ_BASIC')")
    public ResponseEntity<PaginatedResponseDTO<InvestmentDTO>> getAll(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) InvestmentType type,
            @RequestParam(required = false) String search
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(direction, sort));
        
        Page<InvestmentDTO> investments;
        
        if (search != null && !search.trim().isEmpty()) {
            investments = investmentService.searchByUserIdAndSymbolOrName(userId, search.trim(), pageable);
        } else if (type != null) {
            investments = investmentService.findByUserIdAndType(userId, type, pageable);
        } else {
            investments = investmentService.findAllByUserId(userId, pageable);
        }
        
        PaginatedResponseDTO<InvestmentDTO> response = PaginatedResponseDTO.<InvestmentDTO>builder()
                .content(investments.getContent())
                .page(investments.getNumber())
                .size(investments.getSize())
                .totalElements(investments.getTotalElements())
                .totalPages(investments.getTotalPages())
                .hasNext(investments.hasNext())
                .hasPrevious(investments.hasPrevious())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obtener inversión por ID",
            description = "Obtiene una inversión específica por su ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inversión encontrada",
                            content = @Content(schema = @Schema(implementation = InvestmentDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Inversión no encontrada"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_BASIC')")
    public ResponseEntity<InvestmentDTO> getById(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        InvestmentDTO investment = investmentService.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Inversión no encontrada con ID: " + id));
        return ResponseEntity.ok(investment);
    }

    @Operation(
            summary = "Crear nueva inversión",
            description = "Crea una nueva inversión para el usuario autenticado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inversión creada exitosamente",
                            content = @Content(schema = @Schema(implementation = InvestmentDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de inversión inválidos"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_BASIC')")
    public ResponseEntity<InvestmentDTO> create(
            @Valid @RequestBody CreateInvestmentDTO createDto,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        InvestmentDTO created = investmentService.create(createDto, userId);
        return ResponseEntity.ok(created);
    }

    @Operation(
            summary = "Actualizar inversión",
            description = "Actualiza una inversión existente por su ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inversión actualizada exitosamente",
                            content = @Content(schema = @Schema(implementation = InvestmentDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Inversión no encontrada"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_BASIC')")
    public ResponseEntity<InvestmentDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInvestmentDTO updateDto,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        InvestmentDTO updated = investmentService.update(id, updateDto, userId);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Eliminar inversión",
            description = "Elimina una inversión por su ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inversión eliminada exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Inversión no encontrada"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_BASIC')")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        investmentService.deleteById(id, userId);
        
        return ResponseEntity.ok(Map.of("success", true, "message", "Inversión eliminada exitosamente"));
    }

    @Operation(
            summary = "Obtener resumen de inversiones",
            description = "Obtiene un resumen de métricas de todas las inversiones del usuario",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resumen obtenido exitosamente",
                            content = @Content(schema = @Schema(implementation = InvestmentSummaryDTO.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('READ_BASIC')")
    public ResponseEntity<InvestmentSummaryDTO> getSummary(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        InvestmentSummaryDTO summary = investmentService.getSummary(userId);
        return ResponseEntity.ok(summary);
    }
    
    @Operation(
            summary = "Obtener resumen de inversiones con precios en tiempo real",
            description = "Obtiene un resumen de métricas con precios actualizados desde Binance en tiempo real",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resumen obtenido exitosamente",
                            content = @Content(schema = @Schema(implementation = InvestmentSummaryDTO.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @GetMapping("/summary/realtime")
    @PreAuthorize("hasAuthority('READ_BASIC')")
    public ResponseEntity<InvestmentSummaryDTO> getSummaryWithRealTimePrices(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        InvestmentSummaryDTO summary = investmentService.getSummary(userId);
        return ResponseEntity.ok(summary);
    }

    @Operation(
            summary = "Obtener inversiones por tipo",
            description = "Obtiene todas las inversiones de un tipo específico",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de inversiones obtenida exitosamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAuthority('READ_BASIC')")
    public ResponseEntity<List<InvestmentDTO>> getByType(
            @PathVariable InvestmentType type,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<InvestmentDTO> investments = investmentService.findByUserIdAndType(userId, type);
        return ResponseEntity.ok(investments);
    }

    @Operation(
            summary = "Recalcular valores de inversión",
            description = "Recalcula automáticamente todos los valores de una inversión específica",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Valores recalculados exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Inversión no encontrada"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @PostMapping("/{id}/recalculate")
    @PreAuthorize("hasAuthority('WRITE_BASIC')")
    public ResponseEntity<Map<String, Object>> recalculateInvestment(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        investmentService.recalculateInvestmentValues(id);
        
        return ResponseEntity.ok(Map.of("success", true, "message", "Valores de inversión recalculados exitosamente"));
    }

    @Operation(
            summary = "Recalcular valores de todas las inversiones",
            description = "Recalcula automáticamente los valores de todas las inversiones del usuario",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Valores recalculados exitosamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @PostMapping("/recalculate-all")
    @PreAuthorize("hasAuthority('WRITE_BASIC')")
    public ResponseEntity<Map<String, String>> recalculateAllInvestmentValues(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        investmentService.recalculateAllInvestmentValues(userId);
        return ResponseEntity.ok(Map.of("message", "Valores recalculados exitosamente"));
    }

    // Nuevos endpoints para el enfoque híbrido

    @Operation(
            summary = "Obtener dashboard consolidado",
            description = "Obtiene el dashboard con inversiones consolidadas por símbolo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dashboard consolidado obtenido exitosamente",
                            content = @Content(schema = @Schema(implementation = ConsolidatedDashboardDataDTO.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @GetMapping("/dashboard/consolidated")
    @PreAuthorize("hasAuthority('READ_BASIC')")
    public ResponseEntity<ConsolidatedDashboardDataDTO> getConsolidatedDashboard(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        ConsolidatedDashboardDataDTO dashboard = investmentService.getConsolidatedDashboardData(userId);
        return ResponseEntity.ok(dashboard);
    }

    @Operation(
            summary = "Obtener inversiones consolidadas",
            description = "Obtiene todas las inversiones consolidadas por símbolo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inversiones consolidadas obtenidas exitosamente",
                            content = @Content(schema = @Schema(implementation = ConsolidatedInvestmentDTO.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @GetMapping("/consolidated")
    @PreAuthorize("hasAuthority('READ_BASIC')")
    public ResponseEntity<List<ConsolidatedInvestmentDTO>> getConsolidatedInvestments(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<ConsolidatedInvestmentDTO> investments = investmentService.getConsolidatedInvestments(userId);
        return ResponseEntity.ok(investments);
    }

    @Operation(
            summary = "Obtener historial de inversiones por símbolo",
            description = "Obtiene el historial completo de compras para un símbolo específico",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente",
                            content = @Content(schema = @Schema(implementation = InvestmentDTO.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @GetMapping("/history/{symbol}")
    @PreAuthorize("hasAuthority('READ_BASIC')")
    public ResponseEntity<List<InvestmentDTO>> getInvestmentHistory(
            @PathVariable String symbol,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<InvestmentDTO> history = investmentService.getInvestmentHistory(userId, symbol);
        return ResponseEntity.ok(history);
    }

    @Operation(
            summary = "Obtener símbolos únicos",
            description = "Obtiene la lista de símbolos únicos del usuario",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Símbolos obtenidos exitosamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @GetMapping("/symbols")
    @PreAuthorize("hasAuthority('READ_BASIC')")
    public ResponseEntity<List<String>> getDistinctSymbols(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<String> symbols = investmentService.getDistinctSymbols(userId);
        return ResponseEntity.ok(symbols);
    }

    @Operation(
            summary = "Actualizar precios desde Binance",
            description = "Actualiza los precios actuales de todas las inversiones usando datos de Binance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Precios actualizados exitosamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes"),
                    @ApiResponse(responseCode = "500", description = "Error al obtener datos de Binance")
            }
    )
    @PutMapping("/update-prices")
    @PreAuthorize("hasAuthority('READ_BASIC')")
    public ResponseEntity<Map<String, Object>> updatePricesFromBinance(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Map<String, Object> result = investmentService.updatePricesFromBinance(userId);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Obtener inversiones con precios en tiempo real",
            description = "Obtiene las inversiones con precios actualizados desde Binance en tiempo real",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inversiones obtenidas exitosamente",
                            content = @Content(schema = @Schema(implementation = InvestmentDTO.class))),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "403", description = "Prohibido - Sin permisos suficientes")
            }
    )
    @GetMapping("/realtime")
    @PreAuthorize("hasAuthority('READ_BASIC')")
    public ResponseEntity<List<InvestmentDTO>> getInvestmentsWithRealTimePrices(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<InvestmentDTO> investments = investmentService.getInvestmentsWithRealTimePrices(userId);
        return ResponseEntity.ok(investments);
    }

    // Método de ayuda para obtener el ID del usuario desde la autenticación
    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Usuario no autenticado");
        }
        
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUser().getId();
        }
        
        throw new RuntimeException("No se pudo obtener el ID del usuario desde la autenticación");
    }
}
