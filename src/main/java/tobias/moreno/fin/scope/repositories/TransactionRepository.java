package tobias.moreno.fin.scope.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tobias.moreno.fin.scope.entities.TransactionEntity;
import tobias.moreno.fin.scope.models.AssetType;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
	List<TransactionEntity> findAllByAssetType(AssetType assetType);

	@Query("""
    SELECT SUM(
        CASE
            WHEN t.txType = "BUY" THEN t.quantity * t.pricePerUnit
            WHEN t.txType = "SELL" THEN -t.quantity * t.pricePerUnit
            ELSE 0
        END
    )
    FROM TransactionEntity t
""")
	BigDecimal calculateCurrentBalance();
}
