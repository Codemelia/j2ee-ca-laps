package sg.edu.nus.laps.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/*
    AuthController handles all user auth operations

                    CONTROLLER SCOPE
    ------------------------------------------------
    GET /auth/login           - Fallback route, redirects to employee login
    GET /auth/employee/login  - Employee login entry point
    GET /auth/admin/login     - Admin login entry point
    POST /auth/login          - Spring Security login processing endpoint
    POST /auth/logout         - Spring Security logout endpoint
*/
@RequestMapping("/auth")
@Controller
public class AuthController {

    // @Autowired
    // private UserService userSvc;

    // USING SPRING SECURITY
    // Fallback - GET /auth/login
    @GetMapping("/login")
    public String getLogin() {
        return "redirect:/auth/employee/login"; // Default employee login
    }

    // Employee - GET /auth/employee/login
    @GetMapping("/employee/login")
    public String employeeLogin(Model model) {
        model.addAttribute("entryPoint", "employee");
        return "auth/login";
    }

    // Admin - GET /auth/admin/login
    @GetMapping("/admin/login")
    public String adminLogin(Model model) {
        model.addAttribute("entryPoint", "admin");
        return "auth/login";
    }

    // REPLACE WITH SPRING SECURITY
    // // POST /auth/login
    // @PostMapping("/login")
    // public String postLogin(@Valid @ModelAttribute _LoginRequest loginUser, 
    //     Model model, HttpSession session, BindingResult bindRes) {

    //     // If session contains user, redirect to index page
    //     if (session.getAttribute("userEmail") != null)
    //         return "redirect/";

    //     // If there are validation errors, return to login page with errors
    //     if (bindRes.hasErrors()) return "login";

    //     // Authenticate user email and password
    //     // boolean auth = authSvc.authenticate(loginUser.getEmail(), loginUser.getPassword());
        
    //     // // If invalid credentials, return error message to login page
    //     // if (!auth) {
    //     //     model.addAttribute("errorMsg", "Your login credentials were invalid. Please try again.");
    //     //     model.addAttribute("email", loginUser.getEmail()); // Re-populate form without password for security
    //     //     return "login";
    //     // } 
        
    //     // // If valid credentials, save user email to session (no password for security)
    //     // // Redirect to index page
    //     // else {
    //     //     session.setAttribute("userEmail", loginUser.getEmail());
    //     //     return "redirect:/";
    //     // }

    //     return "";
    // }

    // // POST /auth/logout
    // @PostMapping("/logout")
    // public void postLogout(HttpSession session) {
    //     session.invalidate(); // Invalidate session
    // }

}
