package sg.edu.nus.laps.approval;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sg.edu.nus.laps.claim.OvertimeClaim;
import sg.edu.nus.laps.claim.OvertimeClaimService;
import sg.edu.nus.laps.claim.OvertimeClaimStatus;
import sg.edu.nus.laps.employee.EmployeeService;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveStatus;
import sg.edu.nus.laps.leave.service.LeaveService;
import sg.edu.nus.laps.security.principal.AuthUserDetails;

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
    private final OvertimeClaimService otService;
    public ApprovalController(LeaveService lService, 
        ApprovalService aService, EmployeeService eService,
        OvertimeClaimService otService) {
        this.lService = lService;
        this.aService = aService;
        this.eService = eService;
        this.otService = otService;
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
    public String viewTeamLeaves(
        @AuthenticationPrincipal AuthUserDetails user,
        @RequestParam(name = "view", defaultValue = "pending") String view,
        Model model) {

        boolean isPending = !"processed".equalsIgnoreCase(view);
        List<LeaveApplication> leaveList;

        if (isPending) {
            leaveList = aService.getTeamLeaveRequests(
                user.getEmployeeId(), LeaveStatus.APPLIED, LeaveStatus.UPDATED);
        } else {
            leaveList = aService
                .getTeamLeaveRequests(user.getEmployeeId(), LeaveStatus.APPROVED, LeaveStatus.REJECTED);
        }

        model.addAttribute("leaveList", leaveList);
        model.addAttribute("isPending", isPending);
        model.addAttribute("currentView", isPending ? "pending" : "processed");
        return "approval/team-leave-list";
    }

        /**
     * Displays the complete leave history for a specific subordinate.
     * Shows all leaves for the current calendar year, including approved and rejected ones.
     * Verifies that the requesting manager actually manages the specified employee.
     * 
     * Request: GET /manager/subordinate/history/{empId}
     * Template: leave/leave-list.html
     * 
     * @param empId the subordinate's employee ID
     * @param user the authenticated manager
     * @param model the model to pass data to template
     * @return the subordinate history view, or redirect if unauthorized
     */
    @GetMapping("/team-leaves/{empId}")
    public String viewSubordinateHistory(@PathVariable Long empId, 
        @AuthenticationPrincipal AuthUserDetails user, Model model,
        RedirectAttributes redirAttr) {
        
        // Verify the manager actually manages this employee
        Optional<Employee> optSubordinate = eService.findById(empId);
        if (optSubordinate.isEmpty() || !optSubordinate.get().getManagerId().equals(user.getEmployeeId())) {
            redirAttr.addFlashAttribute("globalError", 
                "You may only view your team members' leave applications");
            return "redirect:/manager/team-leaves";
        }

        // Retrieve subordinate
        Employee subordinate = optSubordinate.get();
        
        // Retrieve complete leave history for the subordinate
        model.addAttribute("subordinateFullName", 
            subordinate.getFirstName() + " " + subordinate.getLastName());
        model.addAttribute("leaveList", aService.getSubordinateHistory(empId));
        model.addAttribute("isSelf", false);
        return "leave/leave-list";
    }

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
    @GetMapping("/team-leaves/details/{leaveId}")
    public String viewLeaveDetails(@PathVariable Long leaveId, 
        @AuthenticationPrincipal AuthUserDetails user, 
        Model model, RedirectAttributes redirAttr) {

        // Get leave application
        Optional<LeaveApplication> optLa = lService.findLeaveById(leaveId);
        if (optLa.isEmpty()) {
            model.addAttribute("leaveApp", null); // Null handled in thymeleaf
            return "leave/leave-details";
        }

        LeaveApplication la = optLa.get();

        // Verify manager can only view leave applications from their own team
        if (!la.getEmployee().getManagerId().equals(user.getEmployeeId())) {
            redirAttr.addFlashAttribute("globalError",
                "You may only view your team members' leave applications");
            return "redirect:/manager/team-leaves";
        }

        // Get conflicting leave applications from team members during the same period
        List<LeaveApplication> conflicts = aService.getConflictingLeaves(
            user.getEmployeeId(), 
            la.getFromDate(), 
            la.getToDate(), 
            la.getId());

        Long leaveEmpId = la.getEmployee().getId();
        String managerName = eService.getManagerName(leaveEmpId);

        model.addAttribute("managerName", managerName);
        model.addAttribute("leaveApp", la);
        model.addAttribute("conflicts", conflicts);
        model.addAttribute("isSelf", false);
        
        return "leave/leave-details";
    }

    /**
     * Handles the approval of a leave application.
     * Updates leave status to APPROVED and deducts from employee's leave balance.
     * 
     * Request: POST /manager/team-leaves/approve
     * Parameters: id (leave application ID)
     * 
     * @param id the leave application ID to approve
     * @return redirect to team-leaves view with success/error message
     */
    @PostMapping("/team-leaves/approve")
    public String approveLeave(@RequestParam("id") Long id,
        RedirectAttributes redirAttr) {
        try {
            lService.processApproveOrRejectLeave(id, LeaveStatus.APPROVED, null);
            redirAttr.addFlashAttribute("successMsg", 
                String.format("Leave Application #%d approved successfully", id));
        } catch (Exception e) {
            redirAttr.addFlashAttribute("globalError", 
                "Error: " + e.getMessage());
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
    @PostMapping("/team-leaves/reject")
    public String rejectLeave(@RequestParam("id") Long id, 
        @RequestParam("comment") String comment,
        RedirectAttributes redirAttr) {
        try {
            lService.processApproveOrRejectLeave(id, LeaveStatus.REJECTED, comment);
            redirAttr.addFlashAttribute("successMsg", 
                String.format("Leave Application #%d rejected successfully", id));
        } catch (Exception e) {
            redirAttr.addFlashAttribute("globalError", 
                "Error: " + e.getMessage());
        }
        return "redirect:/manager/team-leaves";
    }

    // REST API to export CSV for team leaves
    @ResponseBody
    @PostMapping("/team-leaves/export-csv")
    public ResponseEntity<?> exportLeavesCSV(
        @AuthenticationPrincipal AuthUserDetails user,
        @RequestBody List<Long> leaveAppIdList) {
        if (leaveAppIdList == null || leaveAppIdList.isEmpty()) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Report generation failed on null or empty request"));
        }

        // Attempt to export and return file
        // By manager ID and leave app IDs
        try {
            byte[] csv = aService.processLeavesExportRequest(user.getEmployeeId(), leaveAppIdList);
            String fileName = user.getEmployeeId() + "-leaves-report-" + LocalDate.now() + ".csv";

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                // Content disposition renders CSV file as download
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(csv);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Report generation failed on unexpected error");
        }
    }

    // Overtime Compensation view
    // Same toggle as team leaves view
    @GetMapping("/team-claims")
    public String viewTeamOvertimeClaims(
        @AuthenticationPrincipal AuthUserDetails user,
        @RequestParam(name = "view", defaultValue = "pending") String view,
        Model model) {

        boolean isPending = !"processed".equalsIgnoreCase(view);
        List<OvertimeClaim> teamClaims;

        if (isPending) {
            teamClaims = aService
                .getTeamOvertimeClaims(user.getEmployeeId(), OvertimeClaimStatus.APPLIED);
        } else {
            teamClaims = aService
                .getTeamOvertimeClaims(user.getEmployeeId(), 
                    OvertimeClaimStatus.APPROVED, OvertimeClaimStatus.REJECTED);
        }
        
        model.addAttribute("teamClaims", teamClaims);
        model.addAttribute("isPending", isPending);
        model.addAttribute("currentView", isPending ? "pending" : "processed");

        return "approval/team-claim-list";
    }

    // Approve Compensation Claim
    // Same process as team leave approve
    @PostMapping("/team-claims/approve")
    public String approveTeamOvertimeClaim(
        @AuthenticationPrincipal AuthUserDetails user,
        @RequestParam(name = "id") Long claimId,
        Model model, RedirectAttributes redirAttr) {

        try {
            otService.processApproveOrRejectClaim(claimId, user.getEmployeeId(), OvertimeClaimStatus.APPROVED);
            redirAttr.addFlashAttribute("successMsg", 
                String.format("Compensation Claim #%d approved successfully", claimId));
        } catch (Exception e) {
            redirAttr.addFlashAttribute("globalError", 
                "Error: " + e.getMessage());
        }

        return "redirect:/manager/team-claims";

    }

    // Reject Compensation Claim
    // Same process as team leave reject
    @PostMapping("/team-claims/reject")
    public String rejectTeamOvertimeClaim(
        @AuthenticationPrincipal AuthUserDetails user,
        @RequestParam(name = "id") Long claimId,
        Model model, RedirectAttributes redirAttr) {

        try {
            otService.processApproveOrRejectClaim(claimId, user.getEmployeeId(), OvertimeClaimStatus.REJECTED);
            redirAttr.addFlashAttribute("successMsg", 
                String.format("Compensation Claim #%d rejected successfully", claimId));
        } catch (Exception e) {
            redirAttr.addFlashAttribute("globalError", 
                "Error: " + e.getMessage());
        }

        return "redirect:/manager/team-claims";

    }

    // REST API to export CSV for claims
    @ResponseBody
    @PostMapping("/team-claims/export-csv")
    public ResponseEntity<?> exportClaimsCSV(
        @AuthenticationPrincipal AuthUserDetails user,
        @RequestBody List<Long> claimIdList) {
        if (claimIdList == null || claimIdList.isEmpty()) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Report generation failed on null or empty request"));
        }

        // Attempt to export and return file
        // By manager ID and claim IDs
        try {
            byte[] csv = aService.processClaimsExportRequest(user.getEmployeeId(), claimIdList);
            String fileName = user.getEmployeeId() + "-claims-report-" + LocalDate.now() + ".csv";

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                // Content disposition renders CSV file as download
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(csv);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Report generation failed on unexpected error");
        }
    }

}