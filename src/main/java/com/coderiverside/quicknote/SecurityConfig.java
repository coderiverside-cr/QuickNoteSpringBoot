package com.coderiverside.quicknote;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // @Bean
    // SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     return http.build();
    // }

    // @Bean
    // SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    //     http.authorizeHttpRequests(
    //             request -> request
    //                     .requestMatchers("/notes/**").authenticated()) // Secure the /notes endpoint
    //             .httpBasic(Customizer.withDefaults())                
    //             .csrf(csrf -> csrf.disable() // Disable CSRF for simplicity in this example
    //             ); // Use basic authentication for simplicity

    //     return http.build();
    // }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
                request -> request
                        .requestMatchers("/notes/**")
                        .hasRole("NOTE-OWNER")) // enable ROLE-BASED access control                        
                .httpBasic(Customizer.withDefaults())                
                .csrf(csrf -> csrf.disable() // Disable CSRF for simplicity in this example
                ); // Use basic authentication for simplicity

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder users = User.builder();
        UserDetails user = users
                .username("sophia")
                .password(passwordEncoder.encode("Zaqwsx"))
                .roles("NOTE-OWNER")
                .build();
        UserDetails noOwnerNotes = users
                .username("no-notes")
                .password(passwordEncoder.encode("Zaq1"))
                .roles("NO-OWNER-NOTES")
                .build();
        return new InMemoryUserDetailsManager(user, noOwnerNotes);
    }
}