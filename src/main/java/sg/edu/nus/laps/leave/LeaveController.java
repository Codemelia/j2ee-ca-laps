package sg.edu.nus.laps.leave;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;

import sg.edu.nus.laps.employee.EmployeeService;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.service.LeaveService;
import sg.edu.nus.laps.security.AuthUserDetails;

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
    @GetMapping("/details/{id}")
    public String showLeaveDetails(@AuthenticationPrincipal AuthUserDetails user,
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
    
    @GetMapping
    public String viewLeaveHistory(@AuthenticationPrincipal AuthUserDetails user, @PageableDefault(size = 5) Pageable pageable, 
    	    Model model) {

        Page<LeaveApplication> page = leaveService.getEmployeeLeaveHistory(user.getEmployeeId(), pageable);
        model.addAttribute("leaveList", page.getContent());
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("page", page);
        return "leave/leave-list"; // The Thymeleaf template
    }

    @PostMapping("/cancel/{id}")
    public String cancelLeave(@AuthenticationPrincipal AuthUserDetails user, @PathVariable Long id, RedirectAttributes ra) {
    	LeaveApplication leaveCancel = lService.findLeaveById(id).orElse(null);
    	if (leaveCancel == null) {
            ra.addFlashAttribute("error", "No such leave application exists");
            return "redirect:/leaves";
    	 }
    	Long leaveEmpId = leaveCancel.getEmployee().getId();
        Long currViewerId = user.getEmployeeId();
     // Security Check
        boolean isSelf = currViewerId != null && currViewerId.equals(leaveEmpId);

     // Logic: Block if not self 
        if (!isSelf) {
            return "error/forbidden";
        }
    	try {
            leaveService.cancelLeave(id);
            ra.addFlashAttribute("message", "Leave application cancelled.");
        } catch (RuntimeException e) {
            //  catches "started leave" or "Not Approved" exceptions
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/leaves"; // Redirect back to the list
    }
    
    @PostMapping("/delete/{id}")
    public String deleteLeave(@AuthenticationPrincipal AuthUserDetails user, @PathVariable Long id,RedirectAttributes ra) {
    	LeaveApplication leaveDel = lService.findLeaveById(id).orElse(null);
    	if (leaveDel == null) {
            ra.addFlashAttribute("error", "No such leave application exists");
            return "redirect:/leaves";
        }
    	Long leaveEmpId = leaveDel.getEmployee().getId();
        Long currViewerId = user.getEmployeeId();
     // Security Check
        boolean isSelf = currViewerId != null && currViewerId.equals(leaveEmpId);

     // Logic: Block if not self 
        if (!isSelf) {
            return "error/forbidden";
        } 

     // 3. Perform delete action  
        try {
            lService.deleteLeave(id); 
            ra.addFlashAttribute("message", "Leave application deleted.");
        } catch (RuntimeException e) {
            // catches (APPLIED/UPDATED only)
            ra.addFlashAttribute("error", e.getMessage());
        }
        // 4. Redirect to the list
        return "redirect:/leaves"; 
    }
       
  
}
