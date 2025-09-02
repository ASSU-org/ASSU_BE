package com.assu.server;

import com.google.firebase.messaging.FirebaseMessaging;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
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
	}


	@Test
	void contextLoads() {
	}

}
