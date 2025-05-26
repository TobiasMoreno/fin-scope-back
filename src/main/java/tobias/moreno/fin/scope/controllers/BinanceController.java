package tobias.moreno.fin.scope.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tobias.moreno.fin.scope.dto.ResponseKlineDataDto;
import tobias.moreno.fin.scope.dto.ResponseTickerDto;
import tobias.moreno.fin.scope.services.BinanceService;

import java.util.List;

@RestController
public class BinanceController {

	@Autowired
	private BinanceService binanceService;

	@GetMapping("/api/getSymbol")
	public ResponseEntity<String> getSymbol(@RequestParam String symbol) {
		return ResponseEntity.ok(binanceService.getSymbol(symbol));
	}

	@GetMapping("/api/tickers")
	public ResponseEntity<List<ResponseTickerDto>> getTickers() {
		return ResponseEntity.ok(binanceService.getTickers());
	}

	@GetMapping("/api/search-tickers")
	public ResponseEntity<List<ResponseTickerDto>> getTickers(@RequestParam String query) {
		return ResponseEntity.ok(binanceService.searchTickers(query));
	}

	@GetMapping("/api/crypto-chart")
	public ResponseEntity<List<ResponseKlineDataDto>> getCryptoChart(@RequestParam String symbol,
																	 @RequestParam String interval,
																	 @RequestParam String limit) {
		return ResponseEntity.ok(binanceService.getKlineData(symbol, interval, limit));
	}
}
