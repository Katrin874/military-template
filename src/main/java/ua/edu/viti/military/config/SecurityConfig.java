package ua.edu.viti.military.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.edu.viti.military.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Вимикаємо CSRF (бо ми використовуємо REST API і токени)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Налаштування доступу
                .authorizeHttpRequests(auth -> auth
                        // Публічні ендпоінти (Вхід + Swagger)
                        .requestMatchers(
                                "/api/auth/**",          // Вхід
                                "/v3/api-docs/**",       // Swagger API Docs
                                "/swagger-ui/**",        // Swagger UI
                                "/swagger-ui.html"
                        ).permitAll()

                        // Всі інші запити вимагають аутентифікації
                        .anyRequest().authenticated()
                )

                // 3. Stateless сесія (ми не зберігаємо сесію на сервері)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Підключаємо наш провайдер (з BCrypt і UserDetailsService)
                .authenticationProvider(authenticationProvider)

                // 5. Додаємо наш фільтр перед стандартним
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}