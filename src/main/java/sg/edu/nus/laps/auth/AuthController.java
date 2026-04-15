package sg.edu.nus.laps.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import sg.edu.nus.laps.auth.model.LoginUserDTO;
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

    // CONTROLLER HANDLES:
    // Validation

    // Employee - GET /auth/employee/login
    @GetMapping("/employee/login")
    public String employeeLogin(
        @RequestParam(value = "email", required = false) String email,
        Model model) {
        model.addAttribute("entryPoint", "employee");
        model.addAttribute("user", new LoginUserDTO(email));
        return "auth/login";
    }

    // Admin - GET /auth/admin/login
    @GetMapping("/admin/login")
    public String adminLogin(
        @RequestParam(value = "email", required = false) String email,
        Model model) {
        model.addAttribute("entryPoint", "admin");
        model.addAttribute("user", new LoginUserDTO(email));
        return "auth/login";
    }

    // Employee - POST /auth/employee/login-validate
    @PostMapping("/employee/login-validate")
    public String processEmployeeLogin(
        @Valid @ModelAttribute(name="user") LoginUserDTO user,
        BindingResult result, Model model,
        HttpServletRequest request) {

        // Validation error = stay on login page
        if (result.hasErrors()) {
            model.addAttribute("entryPoint", "employee");
            return "auth/login";
        }

        return "forward:/auth/employee/login"; // Let Spring Security authenticate

    }

    // Admin - POST /auth/admin/login-validate
    @PostMapping("/admin/login-validate")
    public String processAdminLogin(
        @Valid @ModelAttribute(name="user") LoginUserDTO user,
        BindingResult result, Model model) {

        // Validation error = stay on login page
        if (result.hasErrors()) {
            model.addAttribute("entryPoint", "admin");
            return "auth/login";
        }

        return "forward:/auth/admin/login"; // Let Spring Security authenticate

    }

    // Handle password change request
    @PostMapping("/change-password")
    @ResponseBody
    public ResponseEntity<?> changePassword(
        @RequestBody @Valid PasswordDTO passwordDto,
        BindingResult result,
        @AuthenticationPrincipal AuthUserDetails user) {

        System.out.println("RECEIVED: " + user.toString());
        System.out.println("RECEIVED: " + passwordDto.toString());
        
        // Check if confirm password same as new
        // If no match, add as field error under confirm password
        if (!userSvc.passwordsMatch(passwordDto)) {
            result.rejectValue("confirmPassword", "Passwords do not match");
        }

        // If there are errors, store in HashMap
        // Return ResponseEntity with errors
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        // Else, attempt an update
        userSvc.changePassword(user.getEmail(), passwordDto);
        return ResponseEntity.ok(
            Map.of("message", "Password updated successfully"));
    }

}
