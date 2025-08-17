package tobias.moreno.fin.scope.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tobias.moreno.fin.scope.entities.InvestmentEntity;
import tobias.moreno.fin.scope.models.InvestmentType;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Repository
public interface InvestmentRepository extends JpaRepository<InvestmentEntity, Long> {

    List<InvestmentEntity> findByUserId(Long userId);

    Page<InvestmentEntity> findByUserId(Long userId, Pageable pageable);

    List<InvestmentEntity> findByUserIdAndType(Long userId, InvestmentType type);

    Page<InvestmentEntity> findByUserIdAndType(Long userId, InvestmentType type, Pageable pageable);

    @Query("SELECT i FROM InvestmentEntity i WHERE i.userId = :userId AND (LOWER(i.symbol) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(i.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<InvestmentEntity> findByUserIdAndSymbolOrNameContainingIgnoreCase(
            @Param("userId") Long userId, 
            @Param("search") String search, 
            Pageable pageable
    );

    Optional<InvestmentEntity> findByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

    /**
     * Busca una inversión existente por usuario y símbolo
     */
    Optional<InvestmentEntity> findByUserIdAndSymbol(Long userId, String symbol);

    @Query("SELECT SUM(i.total) FROM InvestmentEntity i WHERE i.userId = :userId")
    Optional<BigDecimal> getTotalInvestmentValueByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(i.total) FROM InvestmentEntity i WHERE i.userId = :userId AND i.type = :type")
    Optional<BigDecimal> getTotalInvestmentValueByUserIdAndType(
            @Param("userId") Long userId, 
            @Param("type") InvestmentType type
    );

    @Query("SELECT SUM(i.gainLossAmount) FROM InvestmentEntity i WHERE i.userId = :userId")
    Optional<BigDecimal> getTotalGainLossByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(i.dailyVariationAmount) FROM InvestmentEntity i WHERE i.userId = :userId")
    Optional<BigDecimal> getTotalDailyVariationByUserId(@Param("userId") Long userId);

    // Nuevos métodos para el enfoque híbrido
    
    /**
     * Obtiene el historial completo de compras por símbolo
     */
    @Query("SELECT i FROM InvestmentEntity i WHERE i.userId = :userId AND i.symbol = :symbol ORDER BY i.createdAt DESC")
    List<InvestmentEntity> findHistoryByUserIdAndSymbol(@Param("userId") Long userId, @Param("symbol") String symbol);

    /**
     * Obtiene datos consolidados por símbolo para el dashboard
     */
    @Query("SELECT " +
           "i.symbol as symbol, " +
           "i.name as name, " +
           "i.type as type, " +
           "SUM(i.quantity) as totalQuantity, " +
           "AVG(i.lastPrice) as averageLastPrice, " +
           "SUM(i.quantity * i.costPerShare) / SUM(i.quantity) as averageCostPerShare, " +
           "SUM(i.total) as totalValue, " +
           "SUM(i.gainLossAmount) as totalGainLoss, " +
           "SUM(i.dailyVariationAmount) as totalDailyVariation " +
           "FROM InvestmentEntity i " +
           "WHERE i.userId = :userId " +
           "GROUP BY i.symbol, i.name, i.type " +
           "ORDER BY i.symbol")
    List<Object[]> getConsolidatedInvestmentsByUserId(@Param("userId") Long userId);

    /**
     * Obtiene datos consolidados por tipo para el dashboard
     */
    @Query("SELECT " +
           "i.type as type, " +
           "SUM(i.total) as totalValue, " +
           "SUM(i.gainLossAmount) as totalGainLoss, " +
           "SUM(i.dailyVariationAmount) as totalDailyVariation " +
           "FROM InvestmentEntity i " +
           "WHERE i.userId = :userId " +
           "GROUP BY i.type " +
           "ORDER BY i.type")
    List<Object[]> getConsolidatedInvestmentsByType(@Param("userId") Long userId);

    /**
     * Obtiene símbolos únicos del usuario
     */
    @Query("SELECT DISTINCT i.symbol FROM InvestmentEntity i WHERE i.userId = :userId ORDER BY i.symbol")
    List<String> findDistinctSymbolsByUserId(@Param("userId") Long userId);
}
