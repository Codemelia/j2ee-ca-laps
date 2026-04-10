package sg.edu.nus.laps.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/*
    AuthController handles all user auth operations

                    CONTROLLER SCOPE
    ------------------------------------------------
    GET /auth/login  - Display login form
    POST /auth/login - Process login request
    GET /auth/logout - Logout and invalidate session
*/
@RequestMapping("/auth")
@Controller
public class AuthController {

    // @Autowired
    // private UserService userSvc;

    // USING SPRING SECURITY
    // GET /auth/login
    @GetMapping("/login")
    public String getLogin() {
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
