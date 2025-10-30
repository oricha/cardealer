package com.cardealer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // TEMPORARY: Disable security for development - permit all requests
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable())
            // Configure remember-me even in development mode
            .rememberMe(remember -> remember
                .key("uniqueAndSecretCarDealerKey2024")
                .tokenValiditySeconds(86400) // 24 hours
                .rememberMeParameter("remember-me")
            )
            // Configure session management
            .sessionManagement(session -> session
                .sessionFixation().migrateSession()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );
        
        return http.build();
        
        /* ORIGINAL SECURITY CONFIG - TO BE RE-ENABLED LATER
        http
            .authorizeHttpRequests(auth -> auth
                // Public routes
                .requestMatchers(
                    "/",
                    "/cars/**",
                    "/dealers/**",
                    "/login",
                    "/register",
                    "/about",
                    "/contact",
                    "/static/**",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/fonts/**",
                    "/uploads/**"
                ).permitAll()
                // Vendor-only routes
                .requestMatchers("/dashboard/**").hasRole("VENDEDOR")
                // Admin routes (for future use)
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // All other routes require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("uniqueAndSecretCarDealerKey2024")
                .tokenValiditySeconds(86400) // 24 hours
                .rememberMeParameter("remember-me")
            )
            .sessionManagement(session -> session
                .sessionFixation().migrateSession()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );
        
        return http.build();
        */
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}