package tobias.moreno.fin.scope.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tobias.moreno.fin.scope.services.interfaces.IYahooFinanceService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/yahoo-finance")
@RequiredArgsConstructor
@Tag(name = "Yahoo Finance", description = "Endpoints para obtener datos de mercado de Yahoo Finance")
@SecurityRequirement(name = "Bearer Authentication")
public class YahooFinanceController {

    private final IYahooFinanceService yahooFinanceService;

    @Operation(
            summary = "Obtener precio actual de acción/CEDEAR",
            description = "Obtiene el precio actual de una acción o CEDEAR desde Yahoo Finance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Precio obtenido exitosamente"),
                    @ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
                    @ApiResponse(responseCode = "400", description = "Símbolo inválido")
            }
    )
    @GetMapping("/price/{symbol}")
    public ResponseEntity<Map<String, Object>> getCurrentPrice(@PathVariable String symbol) {
        BigDecimal price = yahooFinanceService.getCurrentPrice(symbol);
        
        Map<String, Object> response = Map.of(
            "symbol", symbol,
            "price", price,
            "success", price != null,
            "message", price != null ? "Precio obtenido exitosamente" : "No se pudo obtener el precio"
        );
        
        return ResponseEntity.ok(response);
    }
}
