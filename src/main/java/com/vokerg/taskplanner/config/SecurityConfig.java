package com.vokerg.taskplanner.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize

                // Public operational endpoint
                .requestMatchers("/actuator/health").permitAll()
                
                // Swagger/OpenAPI documentation
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                
                // Public read endpoints
                .requestMatchers(HttpMethod.GET, "/api/projects").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/projects/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/projects/*/tasks").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tasks/*").permitAll()

                // Admin-only project management
                .requestMatchers(HttpMethod.POST, "/api/projects").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/projects/*").hasRole("ADMIN")

                // Authenticated write endpoints
                .requestMatchers(HttpMethod.POST, "/api/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()

                // Anything else requires login
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .build();
    }

    @Bean
    public UserDetailsService userDetailsService(
        @Value("${app.security.user.name}") String userName,
        @Value("${app.security.user.password}") String userPassword,
        @Value("${app.security.admin.name}") String adminName,
        @Value("${app.security.admin.password}") String adminPassword,
        PasswordEncoder passwordEncoder
    ) {
        return new InMemoryUserDetailsManager(
            User.withUsername(userName)
                .password(passwordEncoder.encode(userPassword))
                .roles("USER")
                .build(),
            User.withUsername(adminName)
                .password(passwordEncoder.encode(adminPassword))
                .roles("USER", "ADMIN")
                .build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
