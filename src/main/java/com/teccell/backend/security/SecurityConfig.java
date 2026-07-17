package com.teccell.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(customUserDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Permitir comunicación con frontend
                .cors(cors -> {})

                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth

                        // Login público
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login"
                        ).permitAll()


                        // Públicos
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/api/publico/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()


                        // ADMIN
                        .requestMatchers("/api/usuarios/**")
                        .hasRole("ADMIN")


                        .requestMatchers("/api/dashboard/admin")
                        .hasRole("ADMIN")


                        .requestMatchers("/api/dashboard/carga-tecnicos")
                        .hasRole("ADMIN")


                        // ADMIN + TECNICO
                        .requestMatchers("/api/dashboard/tecnico/**")
                        .hasAnyRole("ADMIN", "TECNICO")


                        .requestMatchers("/api/dashboard/proximas-entregas")
                        .hasAnyRole("ADMIN", "TECNICO")


                        .requestMatchers("/api/dashboard/vencidas")
                        .hasAnyRole("ADMIN", "TECNICO")


                        .requestMatchers("/api/ordenes/vencidas")
                        .hasAnyRole("ADMIN", "TECNICO")


                        .requestMatchers("/api/ordenes/proximas-entregas")
                        .hasAnyRole("ADMIN", "TECNICO")


                        .requestMatchers("/api/equipos/*/reincidencia")
                        .hasAnyRole("ADMIN", "TECNICO")


                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )


                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);


        return http.build();
    }



    /**
     * Configuración CORS para permitir frontend HTML/JS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {


        CorsConfiguration configuration = new CorsConfiguration();


        configuration.setAllowedOrigins(List.of(
                "http://127.0.0.1:5500",
                "http://localhost:5500"
        ));


        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));


        configuration.setAllowedHeaders(List.of("*"));


        configuration.setAllowCredentials(true);



        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }
}