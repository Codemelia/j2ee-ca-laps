package sg.edu.nus.laps.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import sg.edu.nus.laps.auth.model.PasswordDTO;
import sg.edu.nus.laps.auth.service.UserService;
import sg.edu.nus.laps.security.AuthUserDetails;

/*
    AuthController handles all user auth operations

                    CONTROLLER SCOPE
    ------------------------------------------------
    GET /auth/employee/login  - Employee login entry point
    GET /auth/admin/login     - Admin login entry point
*/
@RequestMapping("/auth")
@Controller
public class AuthController {

    private final UserService userSvc;
    public AuthController(UserService userSvc) {
        this.userSvc = userSvc;
    }

    // SPRING SECURITY HANDLES:
    // Login/logout, authentication, authorisation, session management

    // Employee - GET /auth/employee/login
    @GetMapping("/employee/login")
    public String employeeLogin(
        @RequestParam(value = "email", required = false) String email,
        Model model) {
        model.addAttribute("entryPoint", "employee");
        // model.addAttribute("user", new LoginUserDTO(email));
        return "auth/login";
    }

    // Admin - GET /auth/admin/login
    @GetMapping("/admin/login")
    public String adminLogin(
        @RequestParam(value = "email", required = false) String email,
        Model model) {
        model.addAttribute("entryPoint", "admin");
        // model.addAttribute("user", new LoginUserDTO(email));
        return "auth/login";
    }

    // Handle password change request
    @PostMapping("/change-password")
    @ResponseBody
    public ResponseEntity<?> changePassword(
        @RequestBody @Valid PasswordDTO passwordDto,
        BindingResult result,
        @AuthenticationPrincipal AuthUserDetails user) {

        // Validation order
        // - Verify old password
        // - Jakarta Validation
        // - Confirm/new password matching

        // Check if old password matches DB
        if (!userSvc.currentPasswordValid(user.getEmail(), passwordDto)) {
            return ResponseEntity.badRequest().body(
                Map.of("errors", Map.of("oldRawPassword", "Incorrect password"))
            );
        }

        // Validation errors
        // Handled on JS - change-password.js
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        // Check if confirm password same as new
        // If no match, add as field error under confirm password
        if (!userSvc.newPasswordsMatch(passwordDto)) {
            return ResponseEntity.badRequest().body(
                Map.of("errors", Map.of("confirmPassword", "Passwords do not match"))
            );
        }

        // Else, attempt an update
        userSvc.changePassword(user.getEmail(), passwordDto);
        return ResponseEntity.ok(
            Map.of("message", "Password updated successfully"));
    }

    // Getting errors using this method - let Spring Security handle full login flow
    // // Employee - POST /auth/employee/login-validate
    // @PostMapping("/employee/login-validate")
    // public String processEmployeeLogin(
    //     @Valid @ModelAttribute(name="user") LoginUserDTO user,
    //     BindingResult result, Model model,
    //     HttpServletRequest request) {

    //     // Validation error = stay on login page
    //     if (result.hasErrors()) {
    //         model.addAttribute("entryPoint", "employee");
    //         return "auth/login";
    //     }

    //     return "forward:/auth/employee/login"; // Let Spring Security authenticate

    // }

    // // Admin - POST /auth/admin/login-validate
    // @PostMapping("/admin/login-validate")
    // public String processAdminLogin(
    //     @Valid @ModelAttribute(name="user") LoginUserDTO user,
    //     BindingResult result, Model model) {

    //     // Validation error = stay on login page
    //     if (result.hasErrors()) {
    //         model.addAttribute("entryPoint", "admin");
    //         return "auth/login";
    //     }

    //     return "forward:/auth/admin/login"; // Let Spring Security authenticate

    // }

}
