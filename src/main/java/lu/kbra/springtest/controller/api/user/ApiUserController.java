package lu.kbra.springtest.controller.api.user;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lu.kbra.springtest.db.data.UserData;
import lu.kbra.springtest.db.table.UserTable;

@RestController
@RequestMapping("/api/user")
public class ApiUserController {

	public record RegisterRequest(@NotBlank String username, @NotBlank @Email String email, @NotBlank String password) {

	}

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserTable userTable;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private RememberMeServices rememberMeServices;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Value("${spring.session.timeout}")
	private Duration sessionTtl;

	@PostMapping("/register")
	public ResponseEntity<?> register(
			@Valid @ModelAttribute final RegisterRequest request,
			final HttpServletRequest httpRequest,
			final HttpServletResponse httpResponse) {
		if (userTable.byName(request.username()).isPresent() || userTable.byEmail(request.email()).isPresent()) {
			return ResponseEntity.badRequest().build();
		}

		final UserData user = new UserData(request.username(), request.email(), this.passwordEncoder.encode(request.password()));

		final UserData newUser = this.userTable.insertAndReload(user);

		final Authentication auth = this.authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

		SecurityContextHolder.getContext().setAuthentication(auth);

		final HttpSession session = httpRequest.getSession(true);
		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

		this.redisTemplate.opsForValue()
				.set("auth:session:" + session.getId(), Map.of("username", auth.getName()), this.sessionTtl.toSeconds(), TimeUnit.SECONDS);

		this.rememberMeServices.loginSuccess(new HttpServletRequestWrapper(httpRequest) {
			@Override
			public String getParameter(final String name) {
				if ("rememberMe".equals(name)) {
					return "true";
				}

				return super.getParameter(name);
			}
		}, httpResponse, auth);

		return ResponseEntity.status(HttpStatus.CREATED).location(URI.create("/")).build();
	}

}
