package com.assu.server.global.config;

import com.assu.server.domain.auth.security.provider.RoutingAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthProviderConfig {

    private final RoutingAuthenticationProvider routingAuthenticationProvider;

    @Bean
    public AuthenticationManager authenticationManager(
    ) {
        return new org.springframework.security.authentication.ProviderManager(
                List.of(routingAuthenticationProvider)
        );
    }
}
