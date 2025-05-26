package tobias.moreno.fin.scope.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "alerts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, name = "price_threshold")
	private BigDecimal priceThreshold;

	private String symbol;

	private boolean active;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity userEntity;
}
