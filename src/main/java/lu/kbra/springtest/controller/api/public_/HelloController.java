package lu.kbra.springtest.controller.api.public_;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@Value("${app.message}")
	private String message;

	@GetMapping("/")
	public String hello(Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			return message + ", " + authentication.getName();
		}

		return message;
	}

}