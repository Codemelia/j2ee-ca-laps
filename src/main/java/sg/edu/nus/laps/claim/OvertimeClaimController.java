package sg.edu.nus.laps.claim;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import sg.edu.nus.laps.claim.model.OvertimeClaim;
import sg.edu.nus.laps.employee.EmployeeService;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.security.principal.AuthUserDetails;

@RequestMapping("/claims")
@Controller
public class OvertimeClaimController {

    private final OvertimeClaimService otService;
    private final EmployeeService eService;
    public OvertimeClaimController(
        OvertimeClaimService otService,
        EmployeeService eService) {
        this.otService = otService;
        this.eService = eService;
    }

    // Retrieve personal claim history
    @GetMapping
    public String viewClaimHistory(
        @AuthenticationPrincipal AuthUserDetails user,
        Model model) {

        List<OvertimeClaim> teamClaims  = otService.getClaimHistory(user.getEmployeeId());
        model.addAttribute("teamClaims", teamClaims);
        model.addAttribute("isSelf", true);
        if (!model.containsAttribute("claim")) {
            model.addAttribute("claim", new OvertimeClaim());
        }
        return "claim/claim-list";
    }

    // get claim form
    @GetMapping("/submit")
    public String viewClaimForm(
        @AuthenticationPrincipal AuthUserDetails user,
        Model model, RedirectAttributes redirAttr) {
        OvertimeClaim claim = new OvertimeClaim();

        // Retrieve employee to bind ID to form
        Optional<Employee> emp = eService.findById(user.getEmployeeId());

        if (emp.isEmpty()) {
            redirAttr.addFlashAttribute("globalError", "Employee not found");
            return "redirect:/claims";
        }

        claim.setEmployee(emp.get()); // Must have employee to submit claim
        model.addAttribute("claim", claim);
        return"claim/claim-form";
    }

    // process claim submission
    @PostMapping("/submit")
    public String postClaimForm(
        @AuthenticationPrincipal AuthUserDetails user,
        @Valid @ModelAttribute("claim") OvertimeClaim claim,
        BindingResult result, Model model, 
        RedirectAttributes redirAttr) {
        
        // validation error
        if (result.hasErrors()) {
            model.addAttribute("teamClaims", otService.getClaimHistory(user.getEmployeeId()));
            model.addAttribute("isSelf", true);
            model.addAttribute("showClaimFormModal", true);
            model.addAttribute("claim", claim);
            return "claim/claim-list";
        }

        try {
            otService.submitClaim(user.getEmployeeId(), claim);
            redirAttr.addFlashAttribute("successMsg", 
                String.format("Claim #%d was submitted successfully", claim.getId()));
            return "redirect:/claims";
        } catch (Exception ex) {
            model.addAttribute("globalError", 
                "Error: " + ex.getMessage() + ", Status: " + claim.getStatus()
                .getDisplayOvertimeClaimStatus());
            model.addAttribute("teamClaims", otService.getClaimHistory(user.getEmployeeId()));
            model.addAttribute("isSelf", true);
            model.addAttribute("showClaimFormModal", true);
            model.addAttribute("claim", claim);
            return "claim/claim-list";
        }

    }

    // Soft delete claims
    @PostMapping("/delete/{id}")
    public String deleteClaim(
        @AuthenticationPrincipal AuthUserDetails user,
        @PathVariable Long id,
        Model model, RedirectAttributes redirAttr) {

        try {
            otService.deleteClaim(user.getEmployeeId(), id);
            redirAttr.addFlashAttribute("successMsg", 
                String.format("Claim #%d was deleted successfully", id));
            return "redirect:/claims";
        } catch (Exception ex) {
            redirAttr.addFlashAttribute("globalError",
                "Delete failed: " + ex.getMessage());
            return "redirect:/claims";
        }

    }

}
