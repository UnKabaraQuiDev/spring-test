package lu.kbra.springtest.config;

import java.time.Duration;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import lu.kbra.springtest.db.table.UserTable;

@Configuration
@EnableWebSecurity
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
	SecurityFilterChain securityFilterChain(final HttpSecurity http, final RememberMeServices rememberMeServices) throws Exception {
		return http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/user/login", "/user/register", "/api/user/login", "/api/user/register", "/api/public/**", "/actuator/**")
				.permitAll()
				.anyRequest()
				.authenticated())

				.csrf(CsrfConfigurer::disable)

				.formLogin(form -> form.loginPage("/user/login")
						.loginProcessingUrl("/api/user/login")
						.defaultSuccessUrl("/", true)
						.permitAll())

				.httpBasic(HttpBasicConfigurer::disable)

				.sessionManagement(
						session -> session.sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::migrateSession))

				.logout(logout -> logout.logoutUrl("/api/user/logout")
						.deleteCookies(sessionCookieName, rememberMeCookieName)
						.invalidateHttpSession(true)
						.clearAuthentication(true))

				.rememberMe(remember -> remember.rememberMeServices(rememberMeServices))

				.build();
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
	UserDetailsService userDetailsService(final UserTable userTable) {
		return username -> userTable.byName(username)
				.map(user -> User.withUsername(user.getName()).password(user.getPass()).authorities(Collections.emptyList()).build())
				.orElseThrow(() -> new UsernameNotFoundException(username));
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
