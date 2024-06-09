package com.java.projetocesar;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public InMemoryUserDetailsService inMemoryUserDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsService(passwordEncoder);
    }
}