package sg.edu.nus.laps.leave;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import sg.edu.nus.laps.employee.EmployeeService;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.repository.LeaveTypeRepository;
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
	/* 20260416 by ymw
	 * @Autowired
    private LeaveService leaveService;*/
	private final LeaveService leaveService;
	// View personal leave history

    private final EmployeeService empService;
    private final LeaveService c;
    private final LeaveTypeRepository ltRepo;

    /* by ymw
     * public LeaveController(LeaveService lService,
        EmployeeService empService,
        LeaveTypeRepository ltRepo) {
        this.lService = lService;
        this.empService = empService;
		this.ltRepo = ltRepo;
    } */
    public LeaveController(LeaveService leaveService,
            EmployeeService empService,
            LeaveTypeRepository ltRepo, LeaveService c) {
            this.leaveService = leaveService;
			this.empService = empService;
			this.c = c;
			this.ltRepo = ltRepo;
    }

    // TEST leave-details.html - DELETE when updated
    @GetMapping("/details/{id}")
    public String showLeaveDetails(@AuthenticationPrincipal AuthUserDetails user,
        @PathVariable Long id, Model model) {

        // Get curr leave app and viewer id
        Optional<LeaveApplication> leaveAppOpt = leaveService.findLeaveById(id);

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

    
    
    @GetMapping ("")
    public String viewLeaveHistory(@AuthenticationPrincipal AuthUserDetails user, @PageableDefault(size = 5) Pageable pageable, 
    	    Model model) {
     
        
        // if (currentEmployee == null) {
        //     return "redirect:/login";
        // }
        Page<LeaveApplication> page = leaveService.getEmployeeLeaveHistory(user.getEmployeeId(), pageable);
        model.addAttribute("leaveList", page.getContent());
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("page", page);
        return "leave/leave-list"; // The Thymeleaf template
    }
 /* by ymw
  *   @GetMapping("/apply")
    public String showApplyForm(Model model) {
        
        model.addAttribute("leaveApplication", new LeaveApplication()); 
        model.addAttribute("leaveTypes", ltRepo.findAll());
        return "leave/leave-form"; 
    } */
    @GetMapping("/apply")
    public String showApplyForm(Model model) {

        model.addAttribute("leaveApplication", new LeaveApplication());

        var leaveTypes = ltRepo.findAll();

        System.out.println("leaveTypes = " + leaveTypes); // 👈 加这个

        model.addAttribute("leaveTypes", leaveTypes);

        return "leave/leave-form";
    }

    @PostMapping("/apply")
    public String saveLeaveApplication(@Valid @ModelAttribute LeaveApplication leaveApp, BindingResult result, Model model) {
    	if (result.hasErrors()) {
    		model.addAttribute("leaveTypes", ltRepo.findAll());
    		return "leave/leave-form";
    		}
    	leaveService.submitLeave(leaveApp); 
        
        return "redirect:/leaves";

}
    }
