package com.banking.app.config;

import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
        .cors(cors -> cors.configurationSource(request -> {
            var corsConfig = new org.springframework.web.cors.CorsConfiguration();
            

            corsConfig.setAllowedOriginPatterns(List.of("*"));

            corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            corsConfig.setAllowedHeaders(List.of("*"));
            corsConfig.setAllowCredentials(true);
            return corsConfig;
        }))
            .csrf(csrf -> csrf.disable()) // disable CSRF for testing
            .sessionManagement(session -> session
                    .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
                )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll() // allow auth APIs
                .requestMatchers("/api/accounts/me/**").authenticated()

                // 👇 optional (admin)
                .requestMatchers("/api/accounts/**").authenticated()
                .anyRequest().authenticated() // secure others
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    private final JwtFilter jwtFilter;
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }
}
