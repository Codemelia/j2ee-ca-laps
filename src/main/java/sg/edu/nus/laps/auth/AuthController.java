package sg.edu.nus.laps.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import sg.edu.nus.laps.auth.model.LoginUserDTO;

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

}
