package ru.radzvbov.rpbo.tutoring.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // JWT → без сессий и без httpBasic
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // ОТКРЫТЫЕ ЭНДПОИНТЫ АВТОРИЗАЦИИ
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/refresh"
                        ).permitAll()

                        // ОТКРЫТЫЙ ДОСТУП К ПРОСМОТРУ КУРСОВ/УРОКОВ
                        .requestMatchers(HttpMethod.GET, "/api/courses/**", "/api/lessons/**")
                            .permitAll()

                        // ТОЛЬКО ADMIN
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")

                        // ADMIN или TUTOR
                        .requestMatchers(HttpMethod.POST, "/api/courses/**", "/api/lessons/**")
                            .hasAnyRole("ADMIN", "TUTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**", "/api/lessons/**")
                            .hasAnyRole("ADMIN", "TUTOR")
                        .requestMatchers("/api/payments/**")
                            .hasAnyRole("ADMIN", "TUTOR")

                        // ЛЮБОЙ ЗАЛОГИНЕННЫЙ
                        .requestMatchers("/api/enrollments/**").authenticated()

                        // всё остальное — просто требуем аутентификацию
                        .anyRequest().authenticated()
                )
                // Подключаем JWT-фильтр
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}