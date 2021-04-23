package com.codetalk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange()
                .pathMatchers("/static/js/**")
                .permitAll()
                .pathMatchers("/admin/**")
                .hasRole("ADMIN")
                .pathMatchers("/**").permitAll()
                .and().httpBasic();
        http.csrf().disable();
        return http.build();
    }
}

