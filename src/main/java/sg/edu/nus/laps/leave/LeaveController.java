package sg.edu.nus.laps.leave;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
<<<<<<< Updated upstream
import org.springframework.web.bind.annotation.PathVariable;
=======
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
>>>>>>> Stashed changes
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import sg.edu.nus.laps.employee.EmployeeService;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.leave.model.LeaveApplication;

import jakarta.servlet.http.HttpSession;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.leave.model.LeaveApplication;

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
	
	@GetMapping("/leave/history") 
	public String viewHistory(HttpSession session, Model model) {
Employee currentEmployee = (Employee) session.getAttribute("userSession");
        
        if (currentEmployee == null) {
            return "redirect:/login";
        }

<<<<<<< Updated upstream
    private final LeaveService lService;
    private final EmployeeService eService;
    
    public LeaveController(LeaveService lService, EmployeeService eService) {
        this.lService = lService;
        this.eService = eService;
    }

    // TEST leave-details.html - DELETE when updated
    @GetMapping("/{id}")
    public String showLeaves(@PathVariable Long id, Model model) {

        // Retrieve leave app info
        if (id != null && lService.existsByLeaveId(id)) {
            LeaveApplication leaveApp = lService.findLeaveById(id).get();

            // On employee, find Manager ID
            Employee employee = leaveApp.getEmployee();
            Long managerId = employee.getManagerId();

            // Retrieve manager name by manager ID
            if (managerId != null) {
                Employee manager = eService.findById(managerId).get();
                String managerName = manager.getFirstName() + " " + manager.getLastName();
                model.addAttribute("managerName", managerName);
            }

            model.addAttribute("leaveApplication", leaveApp);
        }        

        return "leave/leave-details.html";
    }

}
=======
        List<LeaveApplication> leaveList = leaveService.getLeaveRecordsforEmployee(currentEmployee.getId());
        model.addAttribute("leaveList", leaveList);
        
        return "employee-leave-list"; // The Thymeleaf template
    }
	}
>>>>>>> Stashed changes
