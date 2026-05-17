package lu.kbra.springtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lu.kbra.springtest.db.table.UserTable;

@RestController
public class HelloController {

	@Value("${app.message}")
	private String message;
	
	@Autowired
	private UserTable userTable;

	@GetMapping("/")
	public String hello() {
		userTable.byName("user0");
		return message;
	}

}