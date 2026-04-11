package sg.edu.nus.laps.me;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import sg.edu.nus.laps.auth.security.AuthUserDetails;
import sg.edu.nus.laps.employee.EmployeeService;
import sg.edu.nus.laps.employee.model.Employee;

/*
    MeController handles user/employee-facing pages (Authenticated users only)

                    CONTROLLER SCOPE
    ------------------------------------------------
    CORE:
    GET /me OR / - Dashboard: show recent leave requests and leave balances

    OPTIONAL:
    GET  /me/profile 		- Display employee profile
    POST /me/profile/edit 	- Process employee profile edit request
    GET  /me/notifications 	- Display notifications list
*/
@RequestMapping(path={"/", "/me"})
@Controller
public class MeController {

    // @Autowired
    private final EmployeeService empSvc;
    public MeController(EmployeeService empSvc) {
        this.empSvc = empSvc;
    }
    
    // AuthenticationPrincipal is used to retrieve user auth details
    // From user auth, we manage the session by accessing its details
    @GetMapping
    public String getDashboard(@AuthenticationPrincipal AuthUserDetails user, 
        Model model) {
        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("userEmail", user.getEmail());

        String userRole = user.getRoleName();
        model.addAttribute("userRole", userRole);
        
        // if admin, check whether internal or external
        if (user.getEmployeeId() != null) {
            Employee emp = empSvc
                .findById(user.getEmployeeId())
                .get(); // Employee is not empty
            
            model.addAttribute("employeeFullName", emp.getFirstName() + " " +emp.getLastName());
            model.addAttribute("employeeTeam", emp.getTeamName());
            model.addAttribute("employeeTitle", emp.getJobTitle());
        }

        return "me/dashboard";
    }


}
