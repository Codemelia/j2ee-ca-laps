package sg.edu.nus.laps.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import sg.edu.nus.laps.security.session.LapsExpiredSessionStrategy;
import sg.edu.nus.laps.security.session.LapsInvalidSessionStrategy;

/**
 * SecurityConfig configures security filter chains for 
 * admin, employee/manager, and common pages 
 * with role-based authentication and login/logout flows.
 */
@Configuration // Declare to generate bean definitions at runtime
@EnableWebSecurity // Enable Spring Security configuration
public class SecurityConfig {

    // Password encoder for Users
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    // SET BYPASS ON PUBLIC PAGES
    @Bean
    public WebSecurityCustomizer publicCustomiser() {
        return web -> web.ignoring()
            .requestMatchers(
                "/css/**", 
                "/js/**", 
                "/images/**");
    }

    // Auth manager (inject validation provider)
    @Bean
    public AuthenticationManager authManager(
        HttpSecurity http,
        ValidAuthProvider provider) throws Exception {
        return http
            .getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(provider)
            .build();
    }

	// INTERNAL / EXTERNAL ADMIN AUTH LOGIN LOGOUT
    @Bean // Indicate bean to be managed by Spring
    @Order(1) // Define priority higher
    SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http

            // REQUEST AUTHORISATION
            .securityMatcher("/admin/**", "/auth/admin/**") // Apply filter chain to admin-specific URLs
            .authorizeHttpRequests(auth -> auth // Authentication
                .requestMatchers("/auth/admin/**").permitAll() // Allow public access to login page
                .anyRequest().hasRole("ADMIN") // For all other /admin/... endpoints, authenticate via User Role
            )

            // FORM LOGIN FLOW
            .formLogin(form -> form
                .loginPage("/auth/admin/login") // GET /auth/admin/login
                .loginProcessingUrl("/auth/admin/login") // POST /auth/admin/login
                .usernameParameter("email") // Change default username > email field
                .passwordParameter("password") // Default password field
                .successHandler(adminSuccessHandler()) // Role-based redirect / successful login redirect
                .failureHandler((request, response, exception) -> {
                    String email = request.getParameter("email");
                    response.sendRedirect("/auth/admin/login?error=true&email=" + email);
                }) // If login fails, go to login page with error and form params
                .permitAll()
            )

            // FORM LOGOUT FLOW
            .logout(logout -> logout
                .logoutUrl("/auth/admin/logout") // POST /auth/admin/logout
                .logoutSuccessUrl("/auth/admin/login?logout=true") // On successful logout, go to login page with logout param
                .invalidateHttpSession(true) // Invalidate session
                .clearAuthentication(true) // Clear auth data
                .deleteCookies("JSESSIONID") // Delete current session cookies
                .permitAll()
            );

        return http.build();
    }

    // EMPLOYEE / MANAGER AUTH LOGIN LOGOUT
    @Bean // Indicate bean to be managed by Spring
    @Order(2) // Define priority med
    SecurityFilterChain employeeManagerFilterChain(HttpSecurity http) throws Exception {
        http
            
            // REQUEST AUTHORISATION
            .securityMatcher("/manager/**", "/auth/employee/**") // Apply filter chain to manager-specific URLs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/employee/**").permitAll() // Allow public access to login page
                .anyRequest().hasRole("MANAGER") // For all other /manager/... endpoints, authenticate via User Role
            )

            // FORM LOGIN FLOW
            .formLogin(form -> form
                .loginPage("/auth/employee/login") // GET /auth/employee/login
                .loginProcessingUrl("/auth/employee/login") // POST /auth/employee/login
                .usernameParameter("email") // Change default username > email field
                .passwordParameter("password") // Default password field
                .successHandler(employeeSuccessHandler()) // Role-based redirect / successful login redirect
                .failureHandler((request, response, exception) -> {
                    String email = request.getParameter("email");
                    response.sendRedirect("/auth/employee/login?error=true&email=" + email);
                }) // If login fails, go to login page with error and form params
                .permitAll()
            )

            // FORM LOGOUT FLOW
            .logout(logout -> logout
                .logoutUrl("/auth/employee/logout") // POST /auth/admin/logout
                .logoutSuccessUrl("/auth/employee/login?logout=true") // On successful logout, go to login page with logout param
                .invalidateHttpSession(true) // Invalidate session
                .clearAuthentication(true) // Clear auth data
                .deleteCookies("JSESSIONID") // Delete current session cookies
                .permitAll()
            );

        return http.build();
    }

    // COMMON PAGES AUTH LOGIN LOGOUT
    @Bean // Indicate bean to be managed by Spring
    @Order(3) // Define priority low
    SecurityFilterChain commonFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/", "/me/**", "/leaves/**", "/claims/**", "/auth/change-password")
            // REQUEST AUTHORISATION
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/me/**", "/leaves/**") // Common dashboard for EMPLOYEE / MANAGER / INTERNAL ADMIN
                    .hasAnyAuthority("ROLE_EMPLOYEE", "ROLE_MANAGER", "AUTH_INTERNAL_ADMIN")
                .requestMatchers("/auth/change-password") // Allow all roles to change password
                    .hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                .anyRequest().authenticated() // All other pages will be authenticated
            )

            // FORM LOGIN FLOW
            .formLogin(form -> form
                .loginPage("/auth/employee/login") // GET /auth/employee/login - Processing handled by employeeFilterChain
                .permitAll() 
            )

            // SESSION MANAGEMENT
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Only create session on first login
                .sessionFixation(fix -> fix.migrateSession()) // Issue new ID on login
                .invalidSessionStrategy(new LapsInvalidSessionStrategy()) // Redirect here when session times out (as set on app properties)
                .maximumSessions(1) // Allow only 1 active session per user
                .expiredSessionStrategy(new LapsExpiredSessionStrategy()) // Redirect after expiry
            );
        return http.build();
    }

    // FALLBACK FOR OTHER ROUTES
    @Bean
    @Order(4)
    SecurityFilterChain fallbackFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error", "/error/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/employee/login")
                .permitAll()
            );

        return http.build();
    }

    // Define role-based redirect on admin
    @Bean
    public AuthenticationSuccessHandler adminSuccessHandler() {
        return (request, response, auth) -> {
            // If user is not admin and trying to login on admin login page
            // Redirect to admin login page with role-invalid param
            if (!SecurityUtil.isAdmin(auth)) {
                new SecurityContextLogoutHandler()
                    .logout(request, response, auth); // Logout invalid user
                response
                    .sendRedirect("/auth/admin/login?admin=false");
                return;
            }

            // Else, check whether admin internal or external
            if (SecurityUtil.isInternalAdmin(auth)) { response.sendRedirect("/"); } // Default landing for internal admin > dashboard
            else { response.sendRedirect("/admin/employees"); } // Default landing for external admin > employees page
        };
    }

    // Define role-based redirect for employee
    @Bean
    public AuthenticationSuccessHandler employeeSuccessHandler() {
        return (request, response, auth) -> {
            // If user is admin and trying to login on employee login page
            // Redirect to employee login page with role-invalid param
            if (SecurityUtil.isAdmin(auth)) {
                new SecurityContextLogoutHandler()
                    .logout(request, response, auth); // Logout invalid user
                response
                    .sendRedirect("/auth/employee/login?employee=false");
                return;
            }

            // Else successful login > dashboard
            response.sendRedirect("/");
        };
    }

}
