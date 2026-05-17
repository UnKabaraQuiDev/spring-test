package lu.kbra.springtest.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBConfig {

	@Bean
	@ConditionalOnMissingBean
	ApplicationConversionService conversionService() {
		return new ApplicationConversionService();
	}

}
