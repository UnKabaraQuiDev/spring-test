package lu.kbra.springtest.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lu.kbra.springtest.comp.perm.AnyPermission;
import lu.kbra.springtest.db.UserPermission;

@RestController
public class HelloController {

	@Value("${app.message}")
	private String message;

	@AnyPermission(UserPermission.ITEM_CREATE)
	@GetMapping("/hello")
	public String hello(Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			return message + ", " + authentication.getName();
		}

		return message;
	}

}