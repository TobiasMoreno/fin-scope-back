package tobias.moreno.fin.scope.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tobias.moreno.fin.scope.dto.ResponseKlineDataDto;
import tobias.moreno.fin.scope.dto.ResponseTickerDto;
import tobias.moreno.fin.scope.entities.UserEntity;
import tobias.moreno.fin.scope.services.interfaces.IBinanceService;
import tobias.moreno.fin.scope.services.interfaces.UserService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BinanceService implements IBinanceService {
	private static final String BINANCE_API_URL = "https://api.binance.com/api/v3";

	private final RestTemplate restTemplate;

	public String getSymbol(String symbol) {
		return restTemplate.getForObject(BINANCE_API_URL + "/ticker/price?symbol=" + symbol.toUpperCase(), String.class);
	}

	public List<ResponseTickerDto> getTickers() {
		ResponseTickerDto[] tickerDtos = restTemplate.getForObject(BINANCE_API_URL + "/ticker/price", ResponseTickerDto[].class);
		if (tickerDtos != null) {
			return List.of(tickerDtos);
		}
		return Collections.emptyList();

	}

	public List<ResponseTickerDto> searchTickers(String query) {
		List<ResponseTickerDto> tickers = getTickers();

		if (tickers.isEmpty()) {
			return Collections.emptyList();
		}
		return tickers.stream()
				.filter(ticker -> ticker.getSymbol().toUpperCase().contains(query.toUpperCase()))
				.collect(Collectors.toList());
	}

	@Override
	public List<ResponseKlineDataDto> getKlineData(String symbol, String interval, String limit) {
		String url = BINANCE_API_URL + "/klines?symbol=" + symbol + "&interval=" + interval + "&limit=" + limit;

		List<List<Object>> rawKlines = restTemplate.getForObject(url, List.class);
		List<ResponseKlineDataDto> klines = new ArrayList<>();
		if (rawKlines != null && !rawKlines.isEmpty()) {

			for (List<Object> rawKline : rawKlines) {
				ResponseKlineDataDto klineData = new ResponseKlineDataDto();

				klineData.setOpenTime(rawKline.get(0).toString());
				klineData.setOpen(new BigDecimal(rawKline.get(1).toString()));
				klineData.setHigh(new BigDecimal(rawKline.get(2).toString()));
				klineData.setLow(new BigDecimal(rawKline.get(3).toString()));
				klineData.setClose(new BigDecimal(rawKline.get(4).toString()));
				klineData.setVolume(new BigDecimal(rawKline.get(5).toString()));
				klineData.setCloseTime(rawKline.get(6).toString());

				klines.add(klineData);
			}
		}
		return klines;
	}
}
