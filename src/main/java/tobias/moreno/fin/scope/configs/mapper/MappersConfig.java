package tobias.moreno.fin.scope.configs.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tobias.moreno.fin.scope.entities.InvestmentEntity;
import tobias.moreno.fin.scope.dto.investments.InvestmentDTO;

@Configuration
public class MappersConfig {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration()
				.setAmbiguityIgnored(true)
				.setSkipNullEnabled(true);
		
		// Configurar mapeo específico para InvestmentEntity -> InvestmentDTO
		mapper.createTypeMap(InvestmentEntity.class, InvestmentDTO.class)
				.addMappings(mapping -> {
					mapping.map(InvestmentEntity::getUserId, InvestmentDTO::setUserId);
				});
		
		return mapper;
	}

	@Bean("mergerMapper")
	public ModelMapper mergerMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration()
				.setPropertyCondition(Conditions.isNotNull());
		return mapper;
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
	}
}
