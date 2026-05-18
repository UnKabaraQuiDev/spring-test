package lu.kbra.springtest.config;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

@Component
public class RedisPersistentTokenRepository implements PersistentTokenRepository {

	private static final String SERIES_PREFIX = "auth:remember-me:series:";
	private static final String USER_PREFIX = "auth:remember-me:user:";

	private final RedisTemplate<String, Object> redisTemplate;
	private final long ttlSeconds;

	public RedisPersistentTokenRepository(
			final RedisTemplate<String, Object> redisTemplate,
			@Value("${app.remember-me.ttl}") final Duration ttl) {
		this.redisTemplate = redisTemplate;
		this.ttlSeconds = ttl.toSeconds();
	}

	@Override
	public void createNewToken(final PersistentRememberMeToken token) {
		this.saveToken(token);
	}

	@Override
	public void updateToken(final String series, final String tokenValue, final Date lastUsed) {
		final PersistentRememberMeToken existingToken = this.getTokenForSeries(series);

		if (existingToken == null) {
			return;
		}

		this.saveToken(new PersistentRememberMeToken(existingToken.getUsername(), series, tokenValue, lastUsed));
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(final String seriesId) {
		final Object value = this.redisTemplate.opsForValue().get(seriesKey(seriesId));

		if (!(value instanceof Map<?, ?> map)) {
			return null;
		}

		final Object username = map.get("username");
		final Object series = map.get("series");
		final Object tokenValue = map.get("tokenValue");
		final Object date = map.get("date");

		if (!(username instanceof String) || !(series instanceof String) || !(tokenValue instanceof String) || !(date instanceof Date)) {
			return null;
		}

		return new PersistentRememberMeToken((String) username, (String) series, (String) tokenValue, (Date) date);
	}

	@Override
	public void removeUserTokens(final String username) {
		final String userKey = userKey(username);
		final Set<Object> seriesIds = this.redisTemplate.opsForSet().members(userKey);

		if (seriesIds != null) {
			for (final Object seriesId : seriesIds) {
				this.redisTemplate.delete(seriesKey(String.valueOf(seriesId)));
			}
		}

		this.redisTemplate.delete(userKey);
	}

	private void saveToken(final PersistentRememberMeToken token) {
		final String seriesKey = seriesKey(token.getSeries());
		final String userKey = userKey(token.getUsername());

		this.redisTemplate.opsForValue()
				.set(seriesKey,
						Map.of("username",
								token.getUsername(),
								"series",
								token.getSeries(),
								"tokenValue",
								token.getTokenValue(),
								"date",
								token.getDate()),
						this.ttlSeconds,
						TimeUnit.SECONDS);

		this.redisTemplate.opsForSet().add(userKey, token.getSeries());
		this.redisTemplate.expire(userKey, this.ttlSeconds, TimeUnit.SECONDS);
	}

	private static String seriesKey(final String series) {
		return SERIES_PREFIX + series;
	}

	private static String userKey(final String username) {
		return USER_PREFIX + username;
	}

}
