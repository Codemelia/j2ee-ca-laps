package sg.edu.nus.laps.approval;

import java.util.List;
import java.util.Optional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import sg.edu.nus.laps.employee.EmployeeService;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveStatus;
import sg.edu.nus.laps.leave.service.LeaveService;
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

    private final LeaveService lService;
    private final ApprovalService aService;
    private final EmployeeService eService;
    public ApprovalController(LeaveService lService, 
        ApprovalService aService, EmployeeService eService) {
        this.lService = lService;
        this.aService = aService;
        this.eService = eService;
    }

    /**
     * Displays all pending leave applications for the manager's team.
     * Pending means status is APPLIED or UPDATED.
     * 
     * Request: GET /manager/team-leaves
     * Template: approval/team-leave-list.html
     * 
     * @param user the authenticated manager
     * @param model the model to pass data to template
     * @return the team leave list view
     */
    @GetMapping("/team-leaves")
    public String viewTeamLeaves(@AuthenticationPrincipal AuthUserDetails user, 
        Model model) {
        // Retrieve pending requests for this manager's team
        List<LeaveApplication> pendingList = aService.getPendingRequests(user.getEmployeeId());
        model.addAttribute("leaveList", pendingList);
        
        return "approval/team-leave-list";
    }

    //     /**
    //  * Displays the complete leave history for a specific subordinate.
    //  * Shows all leaves for the current calendar year, including approved and rejected ones.
    //  * Verifies that the requesting manager actually manages the specified employee.
    //  * 
    //  * Request: GET /manager/subordinate/history/{empId}
    //  * Template: leave/leave-list.html
    //  * 
    //  * @param empId the subordinate's employee ID
    //  * @param user the authenticated manager
    //  * @param model the model to pass data to template
    //  * @return the subordinate history view, or redirect if unauthorized
    //  */
    // @GetMapping("/subordinate/history/{empId}")
    // public String viewSubordinateHistory(@PathVariable Long empId, 
    //     @AuthenticationPrincipal AuthUserDetails user, Model model) {
        
    //     // Verify the manager actually manages this employee
    //     Optional<Employee> subordinate = eService.findById(empId);
    //     if (subordinate.isEmpty() || !subordinate.get().getManagerId().equals(user.getEmployeeId())) {
    //         return "redirect:/manager/team-leaves?error=Unauthorized";
    //     }
        
    //     // Retrieve complete leave history for the subordinate
    //     model.addAttribute("leaveList", aService.getSubordinateHistory(empId));
    //     model.addAttribute("empId", empId);
        
    //     return "leave/leave-list";
    // }

        /**
     * Displays detailed information about a single leave application.
     * Also shows conflicting/overlapping leave requests from other team members
     * to help the manager make an informed decision.
     * Verifies that the leave application belongs to an employee managed by this manager.
     * 
     * Request: GET /manager/leave/{id}
     * Template: leave/leave-details.html
     * 
     * @param id the leave application ID to view
     * @param user the authenticated manager
     * @param model the model to pass data to template
     * @return the leave detail view, or redirect if unauthorized
     */
    @GetMapping("/leaves/{id}")
    public String viewLeaveDetails(@PathVariable Long id, 
        @AuthenticationPrincipal AuthUserDetails user, 
        Model model) {

        // Get leave application
        LeaveApplication la = lService.findLeaveById(id)
            .orElseThrow(() -> new RuntimeException("Leave application not found"));

        // Verify the manager owns this leave application (employee is in their team)
        if (!la.getEmployee().getManagerId().equals(user.getEmployeeId())) {
            return "redirect:/manager/team-leaves?error=unauthorized";
        }

        // Get conflicting leave applications from team members during the same period
        List<LeaveApplication> conflicts = aService.getConflictingLeaves(
            user.getEmployeeId(), 
            la.getFromDate(), 
            la.getToDate(), 
            id);

        // Pass data to template
        model.addAttribute("leaveApplication", la);
        model.addAttribute("conflicts", conflicts);
        model.addAttribute("isSelf", false);
        
        return "leave/leave-details";
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
            lService.processApproveOrRejectLeave(id, LeaveStatus.APPROVED, null);
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
    public String rejectLeave(@RequestParam("id") Long id, 
        @RequestParam("comment") String comment) {
        try {
            lService.processApproveOrRejectLeave(id, LeaveStatus.REJECTED, comment);
        } catch (RuntimeException e) {
            return "redirect:/manager/team-leaves?error=" + e.getMessage();
        }
        return "redirect:/manager/team-leaves";
    }

}