package sg.edu.nus.laps.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import sg.edu.nus.laps.auth.model.PasswordDTO;
import sg.edu.nus.laps.auth.service.UserService;
import sg.edu.nus.laps.security.principal.AuthUserDetails;

/**
 * AuthController handles authentication-related requests:
 * 1. Employee and admin login
 * 2. Password change functionality
 * 
 * Logout, authentication, authorisation, session management handled by Spring Security
 */
@RequestMapping("/auth")
@Controller
public class AuthController {

    private final UserService userSvc;
    public AuthController(UserService userSvc) { this.userSvc = userSvc; }

    // Employee Login - GET /auth/employee/login
    @GetMapping("/employee/login")
    public String employeeLogin(
        @RequestParam(value = "email", required = false) String email,
        Model model) {
        model.addAttribute("entryPoint", "employee");
        return "auth/login";
    }

    // Admin Login - GET /auth/admin/login
    @GetMapping("/admin/login")
    public String adminLogin(
        @RequestParam(value = "email", required = false) String email,
        Model model) {
        model.addAttribute("entryPoint", "admin");
        return "auth/login";
    }

    // Password Change - PUT /auth/change-password
    @PutMapping("/change-password")
    @ResponseBody
    public ResponseEntity<?> changePassword(
        @RequestBody @Valid PasswordDTO passwordDto,
        BindingResult result,
        @AuthenticationPrincipal AuthUserDetails user) {

        // 1. Check if old password matches DB
        if (!userSvc.currentPasswordValid(user.getEmail(), passwordDto)) {
            return ResponseEntity.badRequest().body(
                Map.of("errors", Map.of("oldRawPassword", "Incorrect password"))
            );
        }

        // 2. Validation errors; mapped via static/change-password.js
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        // 3. Check if confirm password same as new
        if (!userSvc.newPasswordsMatch(passwordDto)) {
            return ResponseEntity.badRequest().body(
                Map.of("errors", Map.of("confirmPassword", "Passwords do not match"))
            );
        }

        // If all OK, attempt an update
        userSvc.changePassword(user.getEmail(), passwordDto);
        return ResponseEntity.ok(
            Map.of("message", "Password updated successfully"));
    }

}
