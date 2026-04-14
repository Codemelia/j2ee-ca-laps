package sg.edu.nus.laps.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import sg.edu.nus.laps.security.strategy.LapsExpiredSessionStrategy;
import sg.edu.nus.laps.security.strategy.LapsInvalidSessionStrategy;

@Configuration // Declare to generate bean definitions at runtime
@EnableWebSecurity // Enable Spring Security configuration
public class SecurityConfig {

    // SET BYPASS ON PUBLIC PAGES
    @Bean
    public WebSecurityCustomizer publicCustomiser() {
        return web -> web.ignoring()
            .requestMatchers(
                "/css/**", 
                "/js/**", 
                "/images/**", 
                "/facicon.ico",
                "/contact");
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
                .successHandler((request, response, auth) -> { // HttpServletRequest, HttpServletResponse, Authentication
                    
                    // If user is not admin and trying to login on admin login page
                    // Redirect to admin login page with role-invalid param
                    if (!SecurityUtil.isAdmin(auth)) {
                        new SecurityContextLogoutHandler()
                            .logout(request, response, auth); // Logout invalid user
                        response.sendRedirect("/auth/admin/login?adminInvalid");
                        return;
                    }

                    // Else, check whether admin internal or external
                    if (SecurityUtil.isInternalAdmin(auth)) { response.sendRedirect("/"); } // Default landing for internal admin > dashboard
                    else { response.sendRedirect("/admin/employees"); } // Default landing for external admin > employees page
                })
                .failureUrl("/auth/admin/login?error") // If login fails, go to login page with error param
                .permitAll()
            )

            // FORM LOGOUT FLOW
            .logout(logout -> logout
                .logoutUrl("/auth/admin/logout") // POST /auth/admin/logout
                .logoutSuccessUrl("/auth/admin/login?logout") // On successful logout, go to login page with logout param
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
                .successHandler((request, response, auth) -> {

                    // If user is admin and trying to login on employee login page
                    // Redirect to employee login page with role-invalid param
                    if (SecurityUtil.isAdmin(auth)) {
                        new SecurityContextLogoutHandler()
                            .logout(request, response, auth); // Logout invalid user
                        response.sendRedirect("/auth/employee/login?employeeInvalid");
                        return;
                    }

                    // Else successful login > dashboard
                    response.sendRedirect("/");

                }) // Go to index (dashboard) after logged in
                .failureUrl("/auth/employee/login?error") // If login fails, go to login page with error param
                .permitAll()
            )

            // FORM LOGOUT FLOW
            .logout(logout -> logout
                .logoutUrl("/auth/employee/logout") // POST /auth/admin/logout
                .logoutSuccessUrl("/auth/employee/login?logout") // On successful logout, go to login page with logout param
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
            // REQUEST AUTHORISATION
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/me/**", "/leaves/**") // Common dashboard for EMPLOYEE / MANAGER / INTERNAL ADMIN
                    .hasAnyAuthority("ROLE_EMPLOYEE", "ROLE_MANAGER", "AUTH_INTERNAL_ADMIN")
                .requestMatchers("/auth/change-password") // Allow all roles to change password
                    .hasAnyAuthority("ROLE_EMPLOYEE", "ROLE_MANAGER", "ROLE_ADMIN")
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


    /*
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
    */

    // Password encoder for Users
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
