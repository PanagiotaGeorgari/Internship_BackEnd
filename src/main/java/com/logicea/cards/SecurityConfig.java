package com.logicea.cards;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Base64;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity



public class SecurityConfig {
    /*@Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetailsOne = User.withUsername("User1")
                .password(passwordEncoder().encode("pass1"))
                .roles("MEMBER").build();
        UserDetails userDetailsTwo = User.withUsername("User2")
                .password(passwordEncoder().encode("pass2"))
                .roles("MEMBER").build();
        UserDetails admin = User.withUsername("Admin")
                .password(passwordEncoder().encode("Admin1"))
                .roles("ADMIN").build();
        return new InMemoryUserDetailsManager(userDetailsOne, userDetailsTwo, admin);

    }*/
    @Autowired
    private UserDetailsService userDetailsService;
   @Bean
   public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}


    @Bean
    @Order(0) // This filter chain has higher priority
    public SecurityFilterChain publicFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrfCustomizer -> csrfCustomizer.disable())
                .securityMatchers(matchers -> matchers
                        .requestMatchers("/api/cardswelcome", "/user-info/register")
                )
                .authorizeHttpRequests(request ->
                        request.anyRequest().permitAll() // Permit all requests that match the matchers above
                );
        return httpSecurity.build();
    }
    // Filter chain for all other authenticated endpoints
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrfCustomizer -> csrfCustomizer.disable())
                .authorizeHttpRequests(request ->
                        request.anyRequest().authenticated() // All other requests must be authenticated
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return httpSecurity.build();
    }

}
