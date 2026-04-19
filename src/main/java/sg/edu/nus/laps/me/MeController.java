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
import sg.edu.nus.laps.security.principal.AuthUserDetails;

/*
    MeController handles internal employee dashboard
    Top 5 leave applications ordered by updated date ASC
*/
@RequestMapping(path={"/", "/me"})
@Controller
public class MeController {

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
    
    @GetMapping
    public String getDashboard(@AuthenticationPrincipal AuthUserDetails user, 
        Model model) {
        // Only add model attributes for internal employees
        Long empId = user.getEmployeeId();
        if (empId != null) {
            Employee emp = empSvc
                .findById(empId)
                .get(); // Employee is not empty
            
            // Bind employee first name
            model.addAttribute("employeeFirstName", emp.getFirstName());
            model.addAttribute("leaveRecords", lrSvc.getLeaveRecords(empId));
            model.addAttribute("recentApplications", leaveSvc.getRecentLeaveApplications(empId));
        }

        return "me/dashboard";
    }

}
