package lu.kbra.springtest.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!noCache")
@EnableCaching
public class CacheConfig {
}