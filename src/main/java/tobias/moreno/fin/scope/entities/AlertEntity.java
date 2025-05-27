package tobias.moreno.fin.scope.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "alerts")
@Getter
@Setter
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
