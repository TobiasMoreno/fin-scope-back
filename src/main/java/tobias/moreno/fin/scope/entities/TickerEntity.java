package tobias.moreno.fin.scope.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tickers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TickerEntity extends BaseEntity {

	private String symbol;

	private String name;

}
