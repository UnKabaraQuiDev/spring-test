package lu.kbra.springtest.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${frontend.dir}")
	private String frontendDir;

	@PostConstruct
	public void init() throws IOException {
		Files.createDirectories(Paths.get(frontendDir));
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "/f/");
		registry.addRedirectViewController("/index.html", "/f/");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/f/**")
				.addResourceLocations("file:" + new File(frontendDir).getAbsolutePath() + "/").setCachePeriod(60);
	}

}
