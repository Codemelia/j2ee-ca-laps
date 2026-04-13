package sg.edu.nus.laps.leave;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import sg.edu.nus.laps.auth.security.AuthUserDetails;
import sg.edu.nus.laps.employee.EmployeeService;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.service.LeaveService;

/*
    LeaveController handles employee's OWN leaves
    
                    CONTROLLER SCOPE
    ------------------------------------------------
    GET /leaves
        - Display list of leaves for current employee
    GET /leaves/new
        - Display apply-for-leave form
    POST /leaves
        - Process leave application request
    GET /leaves/{id}
        - Display specific leave information
    GET /leaves/{id}/edit
        - Display leave info and populate leave edit form
    POST /leaves/{id}
        - Process leave update request
    POST /leaves/{id}/cancel
        - Process leave cancel request
*/
@RequestMapping("/leaves")
@Controller
public class LeaveController {
	@Autowired
    private LeaveService leaveService;
	// View personal leave history

    private final EmployeeService empService;
    private final LeaveService lService;

    public LeaveController(LeaveService lService,
        EmployeeService empService) {
        this.lService = lService;
        this.empService = empService;
    }

    // TEST leave-details.html - DELETE when updated
    @GetMapping("/{id}")
    public String showLeaves(@AuthenticationPrincipal AuthUserDetails user,
        @PathVariable Long id, Model model) {

        // Get curr leave app and viewer id
        Optional<LeaveApplication> leaveAppOpt = lService.findLeaveById(id);

        // Handle null leave app
        if (leaveAppOpt.isEmpty()) {
            model.addAttribute("errorMessage", "No such leave application exists");
            return "leave/leave-details";
        }
            
        LeaveApplication leaveApp = leaveAppOpt.get();
        Long leaveEmpId = leaveApp.getEmployee().getId();
        Long currViewerId = user.getEmployeeId();

        // External admins cannot access leave details
        // Internal admins cannot access others' leave details
        if (user.isExternalAdmin()
            || (user.isInternalAdmin() 
            && (currViewerId == null || !currViewerId.equals(leaveEmpId)))) {
            return "error/forbidden";
        }

        // If current session user = id, employee is viewing own page
        boolean isSelf = currViewerId != null && currViewerId.equals(leaveEmpId);
    
        // Else, manager is viewing employee's page
        String managerName = isSelf ? null : empService.getManagerName(leaveEmpId);
        model.addAttribute("managerName", managerName);

        model.addAttribute("isSelf", isSelf);
        model.addAttribute("leaveApplication", leaveApp);

        return "leave/leave-details";
    }

    @GetMapping("/history") 
    public String viewHistory(@AuthenticationPrincipal AuthUserDetails user, Model model) {
        // Employee currentEmployee = (Employee) session.getAttribute("userSession");
        
        // if (currentEmployee == null) {
        //     return "redirect:/login";
        // }
        List<LeaveApplication> leaveList = leaveService
            .getLeaveApplicationsforEmployee(user.getEmployeeId());
        model.addAttribute("leaveList", leaveList);
        
        return "leave/leave-list"; // The Thymeleaf template
    }

}
