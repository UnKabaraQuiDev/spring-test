package lu.kbra.springtest.config;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lu.kbra.springtest.db.table.UserPermissionTable;
import lu.kbra.springtest.db.table.UserTable;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Value("${server.servlet.session.cookie.name}")
	private String sessionCookieName;

	@Value("${server.servlet.remember-me.cookie.name}")
	private String rememberMeCookieName;

	@Value("${app.remember-me.key}")
	private String rememberMeKey;

	@Value("${app.remember-me.ttl}")
	private Duration rememberMeTtl;

	@Bean
	SecurityFilterChain securityFilterChain(
			final HttpSecurity http,
			final RememberMeServices rememberMeServices,
			@Qualifier("appCorsConfigurationSource") final CorsConfigurationSource corsConfigurationSource,
			final Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer)
			throws Exception {

		return http
				.authorizeHttpRequests(auth -> auth.requestMatchers("/", "/index.html")
						.permitAll()
						.requestMatchers("/f/a/**", "/api/private/**")
						.authenticated()
						.requestMatchers("/f/**", "/api/public/**", "/actuator/**")
						.permitAll()
						.anyRequest()
						.permitAll())

				.formLogin(form -> form.loginPage("/f/user/login")
						.loginProcessingUrl("/api/public/user/login")
						.defaultSuccessUrl("/f/", true)
						.permitAll())

				.httpBasic(HttpBasicConfigurer::disable)

				.exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
						new AntPathRequestMatcher("/api/**")))

				.sessionManagement(
						session -> session.sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::migrateSession))

				.logout(logout -> logout.logoutUrl("/api/public/user/logout")
						.deleteCookies(this.sessionCookieName, this.rememberMeCookieName)
						.invalidateHttpSession(true)
						.clearAuthentication(true))

				.rememberMe(remember -> remember.rememberMeServices(rememberMeServices))

				.csrf(csrfCustomizer)
				.cors(cors -> cors.configurationSource(corsConfigurationSource))

				.build();
	}

	@Bean
	@Profile("debug")
	Customizer<CsrfConfigurer<HttpSecurity>> debugCsrfCustomizer() {
		return CsrfConfigurer::disable;
	}

	@Bean
	@Profile("!debug")
	Customizer<CsrfConfigurer<HttpSecurity>> prodCsrfCustomizer() {
		return csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
	}

	@Bean("appCorsConfigurationSource")
	@Profile("debug")
	CorsConfigurationSource debugCorsConfigurationSource() {
		final CorsConfiguration config = new CorsConfiguration();

		config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173", "http://localhost:8080"));

		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return source;
	}

	@Bean("appCorsConfigurationSource")
	@Profile("!debug")
	CorsConfigurationSource prodCorsConfigurationSource(@Value("${app.domains}") final String[] domains) {
		final CorsConfiguration config = new CorsConfiguration();

		config.setAllowedOrigins(Arrays.asList(domains));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN"));
		config.setAllowCredentials(true);

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return source;
	}

	@Bean
	RememberMeServices
			rememberMeServices(final UserDetailsService userDetailsService, final PersistentTokenRepository persistentTokenRepository) {

		final PersistentTokenBasedRememberMeServices services = new PersistentTokenBasedRememberMeServices(this.rememberMeKey,
				userDetailsService,
				persistentTokenRepository);

		services.setCookieName(this.rememberMeCookieName);
		services.setParameter("rememberMe");
		services.setTokenValiditySeconds((int) this.rememberMeTtl.toSeconds());
		services.setAlwaysRemember(false);

		return services;
	}

	@Bean
	UserDetailsService userDetailsService(final UserTable userTable, final UserPermissionTable userPermissionTable) {

		return username -> userTable.byName(username)
				.map(user -> User.withUsername(user.getName())
						.password(user.getPass())
						.authorities(userPermissionTable.byUser(user.getId())
								.stream()
								.map(val -> val.getPermission().name())
								.toArray(String[]::new))
						.build())
				.orElseThrow(() -> new UsernameNotFoundException(username));
	}

	@Bean
	AuthenticationManager authenticationManager(final AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}