package sg.edu.nus.laps.approval;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.security.AuthUserDetails;

/**
 * ApprovalController handles manager's leave approval workflow.
 * 
 * URL Mapping: /manager
 * 
 * Responsibilities:
 * - Display team leave applications
 * - Approve/Reject leave requests
 * - Display leave details with team conflict information
 * - View subordinate leave history
 */
@RequestMapping("/manager")
@Controller
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    /**
     * Displays all pending leave applications for the manager's team.
     * Pending means status is APPLIED or UPDATED.
     * 
     * Request: GET /manager/team-leaves
     * Template: manager/team-leave-list.html
     * 
     * @param user the authenticated manager
     * @param model the model to pass data to template
     * @return the team leave list view
     */
    @GetMapping("/team-leaves")
    public String viewTeamLeaves(@AuthenticationPrincipal AuthUserDetails user, Model model) {
        // Security check
        if (user == null) {
            return "redirect:/auth/login";
        }

        // Retrieve pending requests for this manager's team
        List<LeaveApplication> pendingList = approvalService.getPendingRequests(user.getEmployeeId());
        model.addAttribute("leaveList", pendingList);
        
        return "manager/team-leave-list";
    }

    /**
     * Handles the approval of a leave application.
     * Updates leave status to APPROVED and deducts from employee's leave balance.
     * 
     * Request: POST /manager/approve
     * Parameters: id (leave application ID)
     * 
     * @param id the leave application ID to approve
     * @return redirect to team-leaves view with success/error message
     */
    @PostMapping("/approve")
    public String approveLeave(@RequestParam("id") Long id) {
        try {
            approvalService.approveRequest(id);
        } catch (RuntimeException e) {
            return "redirect:/manager/team-leaves?error=" + e.getMessage();
        }
        return "redirect:/manager/team-leaves";
    }

    /**
     * Handles the rejection of a leave application.
     * Updates leave status to REJECTED and stores manager's comment.
     * Manager comment is mandatory and visible to the employee.
     * 
     * Request: POST /manager/reject
     * Parameters: id (leave application ID), comment (rejection reason)
     * 
     * @param id the leave application ID to reject
     * @param comment the mandatory reason for rejection
     * @return redirect to team-leaves view with success/error message
     */
    @PostMapping("/reject")
    public String rejectLeave(@RequestParam("id") Long id, @RequestParam("comment") String comment) {
        try {
            approvalService.rejectRequest(id, comment);
        } catch (RuntimeException e) {
            return "redirect:/manager/team-leaves?error=" + e.getMessage();
        }
        return "redirect:/manager/team-leaves";
    }

    /**
     * Displays the complete leave history for a specific subordinate.
     * Shows all leaves for the current calendar year, including approved and rejected ones.
     * 
     * Request: GET /manager/subordinate/history/{empId}
     * Template: manager/subordinate-history.html
     * 
     * @param empId the subordinate's employee ID
     * @param model the model to pass data to template
     * @return the subordinate history view
     */
    @GetMapping("/subordinate/history/{empId}")
    public String viewSubordinateHistory(@PathVariable Long empId, Model model) {
        // Retrieve complete leave history for the subordinate
        model.addAttribute("history", approvalService.getSubordinateHistory(empId));
        model.addAttribute("empId", empId);
        
        return "manager/subordinate-history";
    }

    /**
     * Displays detailed information about a single leave application.
     * Also shows conflicting/overlapping leave requests from other team members
     * to help the manager make an informed decision.
     * 
     * Request: GET /manager/leave/{id}
     * Template: manager/leave-detail.html
     * 
     * @param id the leave application ID to view
     * @param user the authenticated manager
     * @param model the model to pass data to template
     * @return the leave detail view
     */
    @GetMapping("/leave/{id}")
    public String viewLeaveDetails(@PathVariable Long id, 
                                   @AuthenticationPrincipal AuthUserDetails user, 
                                   Model model) {
        // Security check
        if (user == null) {
            return "redirect:/auth/login";
        }

        // Get the leave application
        LeaveApplication la = approvalService.findLeaveById(id)
            .orElseThrow(() -> new RuntimeException("Leave application not found"));

        // Get conflicting leave applications from team members during the same period
        List<LeaveApplication> conflicts = approvalService.getConflictingLeaves(
            user.getEmployeeId(), 
            la.getFromDate(), 
            la.getToDate(), 
            id);

        // Pass data to template
        model.addAttribute("leaveApplication", la);
        model.addAttribute("conflicts", conflicts);
        model.addAttribute("isSelf", false);
        
        return "manager/leave-detail";
    }
}