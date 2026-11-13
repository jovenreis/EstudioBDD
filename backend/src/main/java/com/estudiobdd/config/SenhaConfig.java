package com.estudiobdd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SenhaConfig {

    // BCrypt gera um hash diferente a cada vez (usa "sal") e é lento de propósito,
    // o que dificulta descobrir a senha a partir do hash
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
