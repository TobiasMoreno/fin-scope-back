package tobias.moreno.fin.scope.services.interfaces;


import tobias.moreno.fin.scope.dto.ResponseKlineDataDto;
import tobias.moreno.fin.scope.dto.ResponseTickerDto;

import java.util.List;

public interface IBinanceService {
	String getSymbol(String symbol);

	List<ResponseTickerDto> getTickers();

	List<ResponseTickerDto> searchTickers(String query);
	List<ResponseKlineDataDto> getKlineData(String symbol, String interval, String limit);
}
