package tobias.moreno.fin.scope.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResponseKlineDataDto {

	private String openTime;
	private BigDecimal open;
	private BigDecimal high;
	private BigDecimal low;
	private BigDecimal close;
	private BigDecimal volume;
	private String closeTime;

}
