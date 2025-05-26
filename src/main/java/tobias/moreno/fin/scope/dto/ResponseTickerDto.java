package tobias.moreno.fin.scope.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResponseTickerDto {
	private String symbol;
	private BigDecimal price;
}
