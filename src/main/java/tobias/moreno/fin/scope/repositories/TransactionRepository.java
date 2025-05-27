package tobias.moreno.fin.scope.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tobias.moreno.fin.scope.entities.TransactionEntity;
import tobias.moreno.fin.scope.models.AssetType;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
	List<TransactionEntity> findAllByAssetType(AssetType assetType);
}
