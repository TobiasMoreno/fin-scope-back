package tobias.moreno.fin.scope.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tobias.moreno.fin.scope.models.AssetType;
import tobias.moreno.fin.scope.models.TransactionType;

import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEntity extends BaseEntity {

	@Column(nullable = false, name = "asset_name")
	private String assetName;

	@Column(name = "asset_type", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private AssetType assetType;

	@Column(name = "tx_type", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private TransactionType txType;

	@Column(nullable = false, precision = 20, scale = 8)
	private BigDecimal quantity;

	@Column(name = "price_per_unit", nullable = false, precision = 18, scale = 8)
	private BigDecimal pricePerUnit;
}