package sg.edu.nus.laps.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    // @Autowired
    // private UserService userSvc;

    // // USING SPRING SECURITY
    // // Fallback - GET /auth/login
    // @GetMapping("/login")
    // public String getLogin(
    //     @RequestParam(required = false) String unauth,
    //     @RequestParam(required = false) String error,
    //     @RequestParam(required = false) String logout,
    //     @RequestParam(required = false) String expired,
    //     HttpServletRequest request) {

    //     // Build base redirect string - default to employee login for employee/manager
    //     StringBuilder redirect = new StringBuilder("redirect:/auth/employee/login");

    //     // Retrieve referer from request
    //     String ref = request.getHeader("Referer");
        
    //     // If user was trying to access admin pages, redirect to admin instead
    //     if (ref != null && ref.contains("/admin")) {
    //         redirect = new StringBuilder("redirect:/auth/admin/login");
    //     }

    //     // set to hold param
    //     Set<String> params = new HashSet<>();

    //     // Add params based on param value
    //     if (unauth != null) { params.add("unauthorised"); }
    //     if (error != null) { params.add("error"); }
    //     if (logout != null) { params.add("logout"); }
    //     if (expired != null) { params.add("expired"); }

    //     // Build and return final redir string
    //     if (!params.isEmpty()) { redirect.append("?").append(String.join("&", params)); }
    //     return redirect.toString();
    // }

    // ASSIGNMENT: 2 ENTRY POINTS
    // Param-handling is on Thymeleaf through SpringSecurity6 dependency

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

}
