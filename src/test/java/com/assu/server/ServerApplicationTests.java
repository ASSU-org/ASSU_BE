package com.assu.server;

import com.assu.server.domain.auth.security.jwt.JwtUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ServerApplicationTests {

	@Mock
	private FirebaseMessaging firebaseMessaging;

	@TestConfiguration
	static class MockConfig {
		@Bean
		FirebaseMessaging firebaseMessaging() {
			return Mockito.mock(FirebaseMessaging.class);
		}

		@Bean
		RedisConnectionFactory redisConnectionFactory() {
			return Mockito.mock(RedisConnectionFactory.class);
		}

		@Bean
        @SuppressWarnings("unchecked")
		RedisTemplate<String, Object> redisTemplate() {
			return Mockito.mock(RedisTemplate.class);
		}

		@Bean
		StringRedisTemplate stringRedisTemplate() {
			return Mockito.mock(StringRedisTemplate.class);
		}

		@Bean
		JwtUtil jwtUtil() {
			return Mockito.mock(JwtUtil.class);
		}
	}

	@Test
	void contextLoads() {
	}

}
