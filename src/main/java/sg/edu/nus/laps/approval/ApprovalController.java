package sg.edu.nus.laps.approval;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.repository.LeaveApplicationRepository;
import sg.edu.nus.laps.security.AuthUserDetails;

@RequestMapping("/manager")
@Controller
public class ApprovalController {

    private final ApprovalService approvalService;
    private final LeaveApplicationRepository leaveRepo; 

    
    public ApprovalController(ApprovalService approvalService, LeaveApplicationRepository leaveRepo) {
        this.approvalService = approvalService;
        this.leaveRepo = leaveRepo; 
    }
        
    

    /**
     * Corresponds to the "Team Leave Applications" HTML page.
     * Displays all pending leave requests for the manager's team.
     */
    @GetMapping("/team-leaves")
    public String viewTeamLeaves(@AuthenticationPrincipal AuthUserDetails user, Model model) {
        // Retrieve the current manager's ID from the Security Context
        List<LeaveApplication> pendingList = approvalService.getPendingRequests(user.getEmployeeId());
        model.addAttribute("leaveList", pendingList);
        return "manager/team-leave-list"; 
    }

    /**
     * Handles the approval request.
     * Triggered by the form action="/manager/approve" in HTML.
     */
    @PostMapping("/approve")
    public String approveLeave(@RequestParam("id") Long id) {
        approvalService.approveRequest(id);
        return "redirect:/manager/team-leaves";
    }

    /**
     * Handles the rejection request.
     * Triggered by the form action="/manager/reject" in HTML.
     */
    @PostMapping("/reject")
    public String rejectLeave(@RequestParam("id") Long id, @RequestParam("comment") String comment) {
        approvalService.rejectRequest(id, comment);
        return "redirect:/manager/team-leaves";
    }

    /**
     * View the complete leave history for a specific subordinate.
     */
    @GetMapping("/subordinate/history/{empId}")
    public String viewSubordinateHistory(@PathVariable Long empId, Model model) {
        model.addAttribute("history", approvalService.getSubordinateHistory(empId));
        return "manager/subordinate-history";
    }
    
    /**
     * View details of a single leave application.
     * Accessible via /manager/leave/{id}.
     */
   @GetMapping("/leave/{id}")
public String viewLeaveDetails(@PathVariable Long id, @AuthenticationPrincipal AuthUserDetails user, Model model) {
    LeaveApplication la = approvalService.findLeaveById(id).orElseThrow();
    
    // Add logic to see who else is on leave
    List<LeaveApplication> conflicts = leaveRepo.findConflictingLeaves(
        user.getEmployeeId(), la.getFromDate(), la.getToDate(), id);
    
    model.addAttribute("leaveApplication", la);
    model.addAttribute("conflicts", conflicts); // Display this in a table on the page
    model.addAttribute("isSelf", false);
    return "manager/leave-detail";  
}
}
