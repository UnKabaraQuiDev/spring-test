package lu.kbra.springtest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;

@Component
public class StripeConfig {

	@Value("${stripe.api.key}")
	private String apiKey;

	@PostConstruct
	public void init() {
		Stripe.apiKey = apiKey;
	}

}