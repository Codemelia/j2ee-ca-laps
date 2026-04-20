package sg.edu.nus.laps.leave;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import sg.edu.nus.laps.employee.EmployeeService;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveType;
import sg.edu.nus.laps.leave.service.LeaveService;
import sg.edu.nus.laps.leave.service.LeaveTypeService;
import sg.edu.nus.laps.security.principal.AuthUserDetails;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * LeaveController handles various operations related to leave applications
 * Viewing details, submitting, updating, deleting, and canceling leave applications.
 */
@RequestMapping("/leaves")
@Controller
public class LeaveController {

	private final LeaveService lService;
    private final EmployeeService empService;
    private final LeaveTypeService ltService;
    public LeaveController(
        LeaveService lService,
        EmployeeService empService,
        LeaveTypeService ltService) {
        this.lService = lService;
        this.empService = empService;
        this.ltService = ltService;
    }

    // Leave Details View
    @GetMapping("/details/{id}")
    public String showLeaveDetails(@AuthenticationPrincipal AuthUserDetails user,
        @PathVariable Long id, Model model) {

        // Get curr leave app and viewer id
        Optional<LeaveApplication> leaveAppOpt = lService.findLeaveById(id);

        // Handle null leave app
        if (leaveAppOpt.isEmpty()) {
            model.addAttribute("globalError", "No such leave application exists");
            return "leave/leave-details";
        }
            
        LeaveApplication leaveApp = leaveAppOpt.get();
        Long leaveEmpId = leaveApp.getEmployee().getId();
        String managerName = empService.getManagerName(leaveEmpId);

        model.addAttribute("managerName", managerName);
        model.addAttribute("isSelf", true);
        model.addAttribute("leaveApp", leaveApp);

        return "leave/leave-details";
    }
    
    // View personal leave history
    @GetMapping
    public String viewLeaveHistory(@AuthenticationPrincipal AuthUserDetails user,Model model) {
        List <LeaveApplication> leaveList  = lService.getEmployeeLeaveHistory(user.getEmployeeId());
        model.addAttribute("leaveList", leaveList);
        model.addAttribute("isSelf", true);
        return "leave/leave-list";
    }

    // View new Leave Application form
    @GetMapping("/apply")
    public String showApplyForm(Model model) {
        List<LeaveType> leaveTypes = ltService.findAllLeaveTypes();
        model.addAttribute("leaveTypes", leaveTypes);
        model.addAttribute("leaveApp", new LeaveApplication());
        return "leave/leave-form";
    }

    // View existing Leave Application form
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model,
        RedirectAttributes redirAttr) {
        Optional<LeaveApplication> optLeaveApp = lService.findLeaveById(id);
        if (optLeaveApp.isEmpty()) {
            model.addAttribute("globalError", "No such application exists. Please fill a new application.");
            return "redirect:/leaves/apply"; // redirect to new form endpoint with no population
        }

        List<LeaveType> leaveTypes = ltService.findAllLeaveTypes();
        LeaveApplication leaveApp = optLeaveApp.get();
        
        // Set leave type from existing leave application
        if (leaveApp.getLeaveType() != null) {
            Long leaveTypeId = leaveApp.getLeaveType().getId();
            leaveApp.setLeaveTypeId(leaveTypeId);
        }

        System.out.println("LeaveApp: " + leaveApp);

        model.addAttribute("leaveTypes", leaveTypes);
        model.addAttribute("leaveApp", leaveApp);
        return "leave/leave-form";
    }
    
    @Autowired
    private LeaveService leaveService;

    // Movement Register: displays leave records by month/year with pagination
    @GetMapping("/movement-register")
    public String movementRegister(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        // Default current month/year
        LocalDate now = LocalDate.now();
        int m = (month != null) ? month : now.getMonthValue();
        int y = (year != null) ? year : now.getYear();

        // Fetch paginated data
        Page<LeaveApplication> records =
            leaveService.getMovementRegister(m, y, PageRequest.of(page, 10));

        model.addAttribute("records", records);
        model.addAttribute("month", m);
        model.addAttribute("year", y);

        return "movement-register";
    }
    
    // Processing save draft
    // DRAFT -> DRAFT
    @PostMapping("/save")
    public String saveLeaveApplication(@AuthenticationPrincipal AuthUserDetails user, 
        @Valid @ModelAttribute("leaveApp") LeaveApplication leaveApp, 
        BindingResult result, Model model,  
        RedirectAttributes redirAttr) {
        List<LeaveType> leaveTypes = ltService.findAllLeaveTypes();

        // If error, go back to form
        // result maps LeaveApplication
    	if (result.hasErrors()) { 
            model.addAttribute("leaveTypes", leaveTypes);
            return "leave/leave-form"; 
        }

        try {
            lService.saveAsDraft(user.getEmployeeId(), leaveApp);
            redirAttr.addFlashAttribute("successMsg", 
                String.format("Leave Application #%d was saved successfully", leaveApp.getId()));
            return "redirect:/leaves";
        } catch (Exception ex) {
            model.addAttribute("globalError", 
                "Error: " + ex.getMessage() + ", Status: " + leaveApp.getStatus()
                .getDisplayLeaveStatus());
            model.addAttribute("leaveApp", leaveApp);
            model.addAttribute("leaveTypes", leaveTypes);
            return "leave/leave-form";
        }
    }

    // Processing submit new/draft leave app
    // DRAFT / - -> APPLIED
    @PostMapping("/submit")
    public String submitLeaveApplication(@AuthenticationPrincipal AuthUserDetails user, 
        @Valid @ModelAttribute("leaveApp") LeaveApplication leaveApp, 
        BindingResult result, Model model,
        @RequestParam(name = "action", required = false) String action, // Determines whether it is a save/submit
        RedirectAttributes redirAttr) {
        List<LeaveType> leaveTypes = ltService.findAllLeaveTypes();

        if (result.hasErrors()) { 
            model.addAttribute("leaveTypes", leaveTypes);
            return "leave/leave-form"; 
        }

        try {
            lService.submitLeave(user.getEmployeeId(), leaveApp);
            redirAttr.addFlashAttribute("successMsg", 
                String.format("Leave Application #%d was submitted successfully", leaveApp.getId()));
            return "redirect:/leaves";
        } catch (Exception ex) {
            model.addAttribute("globalError", 
                "Error: " + ex.getMessage() + ", Status: " + leaveApp.getStatus()
                .getDisplayLeaveStatus());
            model.addAttribute("leaveApp", leaveApp);
            model.addAttribute("leaveTypes", leaveTypes);
            return "leave/leave-form";
        }
    }

    // Processing update existing leave app
    // APPLIED / UPDATED -> UPDATED
    @PostMapping("/update")
    public String updateLeaveApplication(@AuthenticationPrincipal AuthUserDetails user, 
        @Valid @ModelAttribute("leaveApp") LeaveApplication leaveApp, 
        BindingResult result, Model model,
        RedirectAttributes redirAttr) {
        List<LeaveType> leaveTypes = ltService.findAllLeaveTypes();

        if (result.hasErrors()) { 
            model.addAttribute("leaveTypes", leaveTypes);
            return "leave/leave-form"; 
        }

        try {
            lService.updateLeave(user.getEmployeeId(), leaveApp);
            redirAttr.addFlashAttribute("successMsg", 
                String.format("Leave Application #%d was updated successfully", leaveApp.getId()));
            return "redirect:/leaves";
        } catch (Exception ex) {
            model.addAttribute("globalError", 
                "Error: " + ex.getMessage() + ", Status: " + leaveApp.getStatus()
                .getDisplayLeaveStatus());
            model.addAttribute("leaveApp", leaveApp);
            model.addAttribute("leaveTypes", leaveTypes);
            return "leave/leave-form";
        }
    }

    // Delete leave application
    // APPLIED / UPDATED -> DELETED
    @PostMapping("/delete/{id}")
    public String deleteLeave(@AuthenticationPrincipal AuthUserDetails user, 
        @PathVariable Long id, RedirectAttributes redirAttr) {
        try {
            lService.deleteLeave(id, user.getEmployeeId());
            redirAttr.addFlashAttribute("successMsg", 
                String.format("Leave Application #%d was deleted successfully", id));
        } catch (Exception e) {
            redirAttr.addFlashAttribute("globalError",
                "Delete failed: " + e.getMessage());
        }
        return "redirect:/leaves";
    }

    // Cancel leave application
    // APPROVED -> CANCELLED
    @PostMapping("/cancel/{id}")
    public String cancelLeave(@AuthenticationPrincipal AuthUserDetails user, 
        @PathVariable Long id, RedirectAttributes redirAttr) {
    	try {
            lService.cancelLeave(id, user.getEmployeeId());
            redirAttr.addFlashAttribute("successMsg", 
                String.format("Leave Application #%d was cancelled successfully", id));
        } catch (Exception e) {
            redirAttr.addFlashAttribute("globalError",
                "Cancel failed: " + e.getMessage());
        }
        return "redirect:/leaves";
    }
    
    @GetMapping("/team-movement")
    public String viewTeamMovement(
    @AuthenticationPrincipal AuthUserDetails user, 
    @RequestParam(required = false) Integer month,
    Model model, RedirectAttributes redirect) {
        // 1. Get the current date if parameters are missing
        LocalDate now = LocalDate.now();
        int activeMonth = (month == null) ? now.getMonthValue() : month;
        int currentYear = LocalDate.now().getYear();

        // 2. Extract the team name from the authenticated user
        // (Assuming your AuthUserDetails has a method to get the Employee object or teamName)
        Long empId = user.getEmployeeId();
        Employee employee = empService.findById(empId).orElse(null);
        if(employee == null) {
            redirect.addFlashAttribute("globalError", "employee not found.");
            return "redirect:/leaves"; 
        }
        String teamName = employee.getTeamName();

        // 3. Call the service (using the variables we just created)
        List<LeaveApplication> teamLeaveList = lService.getTeamMovement(teamName, activeMonth, currentYear);

        // 4. Add data to the model for the UI
        model.addAttribute("leaveList", teamLeaveList);
        model.addAttribute("currentMonth", activeMonth);
        model.addAttribute("displayYear", currentYear);
        return "leave/team-movement";
    }


}
