package com.github.spring_security_form_auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {


    // Configuration for Default Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .formLogin(Customizer.withDefaults());

        return http.build();
    }


/*
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user3 =
                User.withUsername("user_3")
                        .password("password_3")
                        .roles("ADMIN")
                        .build();

        return new InMemoryUserDetailsManager(user3);
    }
*/

/*
    // In memory user creation
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user1 =
                User.withUsername("user_1")
                        .password("{noop}password_1")
                        .roles("ADMIN")
                        .build();

        UserDetails user2 =
                User.withUsername("user_2")
                        .password("{noop}password_2")
                        .roles("ADMIN")
                        .build();

        return new InMemoryUserDetailsManager(user1, user2);
    }

*/
}
