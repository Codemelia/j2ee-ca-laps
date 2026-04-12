package sg.edu.nus.laps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/css/**", 
                    "/favicon.ico") // TO BE IMPLEMENTED
                    .permitAll() // Allow static
                .requestMatchers(
                    "/auth/login", 
                    "/auth/employee/login", 
                    "/auth/admin/login",
                    "/contact") // TO BE IMPLEMENTED
                    .permitAll() // Allow public pages
                .anyRequest().authenticated() // All other routes require login
            ).formLogin(form -> form
                .loginPage("/auth/login") // GET /auth/login
                .loginProcessingUrl("/auth/login") // POST /auth/login
                .usernameParameter("email") // Form field
                .passwordParameter("password") // Form field
                .defaultSuccessUrl("/", true) // Redirect to landing page on successful login
                .failureUrl("/auth/login?error") // Direct back to login page with errors on failed login
                .permitAll()
            ).logout(logout -> logout
                .logoutUrl("/auth/logout") // GET /auth/logout
                .logoutSuccessUrl("/auth/login?logout") // Redirect to login page on successful logout
                .invalidateHttpSession(true) // Invalidate session
                .clearAuthentication(true) // Clear auth data
                .deleteCookies("JSESSIONID") // Delete current session cookies
                .permitAll()
            ).sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Only create session on first login
                .sessionFixation(fix -> fix.migrateSession()) // Issue new ID on login
                .invalidSessionUrl("/auth/login?expired") // Redirect here when session times out (as set on app properties)
                .maximumSessions(1) // Allow only 1 active session per user
                .expiredUrl("/auth/login?expired") // Redirect after expiry
            );

        return http.build();
    }

    // Password encoder for Users
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
