package com.example.crudthymeilif.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public
                .requestMatchers("/login", "/register", "/auth/**", "/css/**", "/js/**", "/uploads/**", "/h2-console/**").permitAll()
                // Admin-only pages
                .requestMatchers(HttpMethod.GET,  "/competiciones/nuevo").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,  "/competiciones/*/editar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/competiciones", "/competiciones/*/editar", "/competiciones/*/eliminar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,  "/usuaris/editar/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/usuaris/editar/*").authenticated()
                .requestMatchers("/usuaris/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,  "/resultats/nou", "/resultats/*/editar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/resultats", "/resultats/*/editar", "/resultats/*/eliminar").hasRole("ADMIN")
                // Everything else requires login
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/competiciones", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
