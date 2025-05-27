package tobias.moreno.fin.scope.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historical_prices")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoricalPriceEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String symbol;
	private BigDecimal price;
	private LocalDateTime timestamp;
}
