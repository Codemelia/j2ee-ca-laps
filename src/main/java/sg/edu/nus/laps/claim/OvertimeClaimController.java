package sg.edu.nus.laps.claim;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        return "claim/claim-list";
    }

    // get claim form
    @GetMapping("/claims/submit")
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



}
