package tobias.moreno.fin.scope.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tobias.moreno.fin.scope.services.interfaces.IYahooFinanceService;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class YahooFinanceService implements IYahooFinanceService {
    
    private static final String YAHOO_FINANCE_URL = "https://query1.finance.yahoo.com/v8/finance/chart/";
    
    private final RestTemplate restTemplate;
    
    @Override
    public BigDecimal getCurrentPrice(String symbol) {
        try {
            String url = YAHOO_FINANCE_URL + symbol + "?interval=1d&range=1d";
            log.info("Obteniendo precio de Yahoo Finance para: {}", symbol);
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("chart")) {
                Map<String, Object> chart = (Map<String, Object>) response.get("chart");
                
                if (chart.containsKey("result") && chart.get("result") != null) {
                    Object[] results = (Object[]) chart.get("result");
                    
                    if (results.length > 0) {
                        Map<String, Object> result = (Map<String, Object>) results[0];
                        
                        if (result.containsKey("meta")) {
                            Map<String, Object> meta = (Map<String, Object>) result.get("meta");
                            
                            if (meta.containsKey("regularMarketPrice")) {
                                Object priceObj = meta.get("regularMarketPrice");
                                if (priceObj instanceof Number) {
                                    BigDecimal price = new BigDecimal(priceObj.toString());
                                    log.info("Precio obtenido para {}: {}", symbol, price);
                                    return price;
                                }
                            }
                        }
                    }
                }
            }
            
            log.warn("No se pudo obtener el precio para: {}", symbol);
            return null;
            
        } catch (Exception e) {
            log.error("Error obteniendo precio de Yahoo Finance para {} (intento {}): {}", 
                     symbol, e.getMessage());
            
            return null;
        }
    }
}
