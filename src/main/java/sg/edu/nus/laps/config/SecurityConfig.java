package sg.edu.nus.laps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Declare to generate bean definitions at runtime
@EnableWebSecurity // Enable Spring Security configuration
public class SecurityConfig {

	// Enable all HTTP requests for development
    @Bean // Indicate bean to be managed by Spring
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> 
            	auth.anyRequest().permitAll()) // Permit all HTTP requests
            .csrf(csrf -> csrf.disable()); // Disable CSRF protection against state modification

        return http.build();
    }

    // Password encoder for Login Request
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
