package tobias.moreno.fin.scope.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tobias.moreno.fin.scope.dto.ResponseKlineDataDto;
import tobias.moreno.fin.scope.dto.ResponseTickerDto;
import tobias.moreno.fin.scope.services.BinanceService;

import java.util.List;

@RestController
@RequestMapping("values")
@RequiredArgsConstructor
@Tag(name = "Binance", description = "Endpoints para obtener datos de mercado de Binance")
@SecurityRequirement(name = "Bearer Authentication")
public class BinanceController {

	private final BinanceService binanceService;

	@Operation(
			summary = "Obtener información de símbolo",
			description = "Retorna información detallada de un símbolo específico de Binance",
			responses = {
					@ApiResponse(responseCode = "200", description = "Información del símbolo obtenida exitosamente"),
					@ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
					@ApiResponse(responseCode = "400", description = "Símbolo inválido")
			}
	)
	@GetMapping("/get-symbol")
	public ResponseEntity<String> getSymbol(@RequestParam String symbol) {
		return ResponseEntity.ok(binanceService.getSymbol(symbol));
	}

	@Operation(
			summary = "Obtener todos los tickers",
			description = "Retorna la lista completa de tickers disponibles en Binance",
			responses = {
					@ApiResponse(responseCode = "200", description = "Lista de tickers obtenida exitosamente",
							content = @Content(schema = @Schema(implementation = ResponseTickerDto.class))),
					@ApiResponse(responseCode = "401", description = "No autorizado - Token inválido")
			}
	)
	@GetMapping("/tickers")
	public ResponseEntity<List<ResponseTickerDto>> getTickers() {
		return ResponseEntity.ok(binanceService.getTickers());
	}

	@Operation(
			summary = "Buscar tickers",
			description = "Busca tickers que coincidan con el query proporcionado",
			responses = {
					@ApiResponse(responseCode = "200", description = "Tickers encontrados exitosamente",
							content = @Content(schema = @Schema(implementation = ResponseTickerDto.class))),
					@ApiResponse(responseCode = "401", description = "No autorizado - Token inválido")
			}
	)
	@GetMapping("/search-tickers")
	public ResponseEntity<List<ResponseTickerDto>> getTickers(@RequestParam String query) {
		return ResponseEntity.ok(binanceService.searchTickers(query));
	}

	@Operation(
			summary = "Obtener datos de gráfico de criptomoneda",
			description = "Retorna datos de velas (kline) para un símbolo específico con intervalo y límite dados",
			responses = {
					@ApiResponse(responseCode = "200", description = "Datos del gráfico obtenidos exitosamente",
							content = @Content(schema = @Schema(implementation = ResponseKlineDataDto.class))),
					@ApiResponse(responseCode = "401", description = "No autorizado - Token inválido"),
					@ApiResponse(responseCode = "400", description = "Parámetros inválidos")
			}
	)
	@GetMapping("/crypto-chart")
	public ResponseEntity<List<ResponseKlineDataDto>> getCryptoChart(@RequestParam String symbol,
																	 @RequestParam String interval,
																	 @RequestParam String limit) {
		return ResponseEntity.ok(binanceService.getKlineData(symbol, interval, limit));
	}
}
