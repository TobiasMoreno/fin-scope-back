package tobias.moreno.fin.scope.controllers;

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
public class BinanceController {

	private final BinanceService binanceService;

	@GetMapping("/get-symbol")
	public ResponseEntity<String> getSymbol(@RequestParam String symbol) {
		return ResponseEntity.ok(binanceService.getSymbol(symbol));
	}

	@GetMapping("/tickers")
	public ResponseEntity<List<ResponseTickerDto>> getTickers() {
		return ResponseEntity.ok(binanceService.getTickers());
	}

	@GetMapping("/search-tickers")
	public ResponseEntity<List<ResponseTickerDto>> getTickers(@RequestParam String query) {
		return ResponseEntity.ok(binanceService.searchTickers(query));
	}

	@GetMapping("/crypto-chart")
	public ResponseEntity<List<ResponseKlineDataDto>> getCryptoChart(@RequestParam String symbol,
																	 @RequestParam String interval,
																	 @RequestParam String limit) {
		return ResponseEntity.ok(binanceService.getKlineData(symbol, interval, limit));
	}
}
