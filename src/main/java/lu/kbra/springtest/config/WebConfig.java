package lu.kbra.springtest.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import jakarta.annotation.PostConstruct;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);

	@Value("${frontend.dir}")
	private String frontendDir;

	@PostConstruct
	public void init() throws IOException {
		Files.createDirectories(Paths.get(this.frontendDir));
		WebConfig.LOGGER.info("Serving files from: " + this.frontendDir);
	}

	@Override
	public void addViewControllers(final ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "/f/");
		registry.addRedirectViewController("/index.html", "/f/");

		registry.addRedirectViewController("/{path:^(?!api$|f$).*$}", "/f/");
		registry.addRedirectViewController("/{path:^(?!api$|f$).*$}/**", "/f/");
	}

	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/f/**")
				.addResourceLocations("file:" + new File(this.frontendDir).getAbsolutePath() + "/")
				.setCachePeriod(60)
				.resourceChain(true)
				.addResolver(new PathResourceResolver() {

					@Override
					protected Resource getResource(final String resourcePath, final Resource location) throws IOException {
						final Resource requested = location.createRelative(resourcePath);

						if (requested.exists() && requested.isReadable() && !requested.getFile().isDirectory()) {
							return requested;
						}

						final String indexPath = resourcePath == null || resourcePath.isBlank() ? "index.html"
								: resourcePath.endsWith("/") ? resourcePath + "index.html"
								: resourcePath + "/index.html";

						final Resource index = location.createRelative(indexPath);

						if (index.exists() && index.isReadable()) {
							return index;
						}

						return null;
					}
				});
	}

}
