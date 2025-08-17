package tobias.moreno.fin.scope.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tobias.moreno.fin.scope.dto.investments.*;
import tobias.moreno.fin.scope.models.InvestmentType;

import java.util.List;
import java.util.Optional;

public interface InvestmentService {
    
    // CRUD básico
    List<InvestmentDTO> findAllByUserId(Long userId);
    
    Page<InvestmentDTO> findAllByUserId(Long userId, Pageable pageable);
    
    Optional<InvestmentDTO> findByIdAndUserId(Long id, Long userId);
    
    InvestmentDTO create(CreateInvestmentDTO createDto, Long userId);
    
    InvestmentDTO update(Long id, UpdateInvestmentDTO updateDto, Long userId);
    
    void deleteById(Long id, Long userId);
    
    // Filtros y búsqueda
    List<InvestmentDTO> findByUserIdAndType(Long userId, InvestmentType type);
    
    Page<InvestmentDTO> findByUserIdAndType(Long userId, InvestmentType type, Pageable pageable);
    
    Page<InvestmentDTO> searchByUserIdAndSymbolOrName(Long userId, String search, Pageable pageable);
    
    // Dashboard y métricas
    DashboardDataDTO getDashboardData(Long userId);
    
    InvestmentSummaryDTO getSummary(Long userId);
    
    // Cálculos automáticos
    void recalculateInvestmentValues(Long investmentId);
    
    void recalculateAllInvestmentValues(Long userId);
    
    /**
     * Obtiene el dashboard consolidado por símbolo
     */
    ConsolidatedDashboardDataDTO getConsolidatedDashboardData(Long userId);
    
    /**
     * Obtiene inversiones consolidadas por símbolo
     */
    List<ConsolidatedInvestmentDTO> getConsolidatedInvestments(Long userId);
    
    /**
     * Obtiene el historial de compras por símbolo
     */
    List<InvestmentDTO> getInvestmentHistory(Long userId, String symbol);
    
    /**
     * Obtiene símbolos únicos del usuario
     */
    List<String> getDistinctSymbols(Long userId);
    
    /**
     * Actualiza precios desde Binance
     */
    java.util.Map<String, Object> updatePricesFromBinance(Long userId);
    
    /**
     * Obtiene inversiones con precios en tiempo real desde Binance
     */
    java.util.List<InvestmentDTO> getInvestmentsWithRealTimePrices(Long userId);
}
