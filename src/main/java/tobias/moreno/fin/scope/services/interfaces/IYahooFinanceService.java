package tobias.moreno.fin.scope.services.interfaces;

import java.math.BigDecimal;

public interface IYahooFinanceService {
    /**
     * Obtiene el precio actual de un símbolo desde Yahoo Finance
     * @param symbol El símbolo de la acción/CEDEAR (ej: YPF, AAPL)
     * @return El precio actual o null si no se puede obtener
     */
    BigDecimal getCurrentPrice(String symbol);
}
