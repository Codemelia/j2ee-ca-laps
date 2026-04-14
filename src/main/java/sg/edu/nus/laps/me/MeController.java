package sg.edu.nus.laps.me;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import sg.edu.nus.laps.employee.EmployeeService;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.leave.service.LeaveRecordService;
import sg.edu.nus.laps.leave.service.LeaveService;
import sg.edu.nus.laps.security.AuthUserDetails;

/*
    MeController handles user/employee-facing pages (Authenticated users only)

                    CONTROLLER SCOPE
    ------------------------------------------------
    DASHBOARD:
    GET / OR /me
        - Show dashboard summary and user context

    USER-FACING PAGES:
    GET  /me/profile
        - Display employee profile
    POST /me/profile
        - Process employee profile edit request
    GET  /me/notifications
        - Display notifications list
*/
@RequestMapping(path={"/", "/me"})
@Controller
public class MeController {

    // @Autowired
    private final EmployeeService empSvc;
    private final LeaveService leaveSvc;
    private final LeaveRecordService lrSvc;
    public MeController(
        EmployeeService empSvc, 
        LeaveService leaveSvc, 
        LeaveRecordService lrSvc) {
        this.empSvc = empSvc;
        this.leaveSvc = leaveSvc;
        this.lrSvc = lrSvc;
    }
    
    // AuthenticationPrincipal is used to retrieve user auth details
    // From user auth, we manage the session by accessing its details
    @GetMapping
    public String getDashboard(@AuthenticationPrincipal AuthUserDetails user, 
        Model model) {

        model.addAttribute("userEmail", user.getEmail());

        String userRole = user.getRoleName();
        model.addAttribute("userRole", userRole);

        // Get Employee ID
        Long empId = user.getEmployeeId();
        
        // Only add model attributes for internal employees
        if (empId != null) {
            Employee emp = empSvc
                .findById(user.getEmployeeId())
                .get(); // Employee is not empty
            
            // Bind employee first name
            model.addAttribute("employeeFirstName", emp.getFirstName());

            // Bind leave balances + recentApplications
            model.addAttribute("leaveRecords", lrSvc.getLeaveRecords(empId));
            model.addAttribute("recentApplications", leaveSvc.getRecentLeaveApplications(empId));
        }

        return "me/dashboard";
    }


}
