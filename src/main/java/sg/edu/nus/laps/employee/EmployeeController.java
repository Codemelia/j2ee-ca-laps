package sg.edu.nus.laps.employee;

import java.time.LocalDate;
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
import sg.edu.nus.laps.auth.service.RoleService;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.employee.model.EmployeeRank;
import sg.edu.nus.laps.leave.model.LeaveRecord;
import sg.edu.nus.laps.leave.service.HolidayService;
import sg.edu.nus.laps.leave.service.LeaveRecordService;
import sg.edu.nus.laps.leave.service.LeaveTypeService;
import sg.edu.nus.laps.security.AuthUserDetails;

/*
    EmployeeController handles employee CRUD operations (Admin only)

                    CONTROLLER SCOPE
    ------------------------------------------------
	BASE: /admin/employees
	GET  /admin/employees/new         - Display create employee form
	POST /admin/employees             - Process employee create request
	GET  /admin/employees/{id}/edit   - Display update employee form
	POST /admin/employees/{id}        - Process employee update request
	POST /admin/employees/{id}/delete - Delete employee by ID
*/
@RequestMapping("/admin/employees")
@Controller
public class EmployeeController {
	
	private final EmployeeService eService;
	private final RoleService rService;
	private final HolidayService hService;
	private final LeaveRecordService lrService;
	private final LeaveTypeService ltService;
	// private LocalDate date;
	
	public EmployeeController(EmployeeService eService,
		RoleService rService, HolidayService hService, LeaveRecordService lrService,
		LeaveTypeService ltService) {
		
		super();
		this.eService = eService;
		this.rService = rService;
		this.hService = hService;
		this.lrService = lrService;
		this.ltService = ltService;
	}
	
	// private boolean isLoggedIn(HttpSession session) {
    //     return session.getAttribute("user") != null;
    // }
	
	@GetMapping("/update-holidays")
	public String updateHolidays(Model model, RedirectAttributes redirectAttrs) {
		
		try { 
			hService.fetchAndSyncHolidays();
			redirectAttrs.addFlashAttribute("successMsg", 
				"Public Holidays have been updated.");
			return "redirect:/admin/employees";
		} catch (Exception ex) { // Catches SQL + Custom exceptions
			redirectAttrs.addFlashAttribute("globalError", 
				"Public Holidays cannot be updated.");
			return "redirect:/admin/employees";
		}
	}
	
	// Original - Deleted 
	// @GetMapping public String showEmployees(@AuthenticationPrincipal AuthUserDetails user,
    //Model model, RedirectAttributes redirectAttrs, @PageableDefault(size = 10) Pageable pageable) { 
	
	// UPDATED
	@GetMapping
	public String showEmployees(@AuthenticationPrincipal AuthUserDetails user, Model model) {
	
		Integer empCount = eService.countEmployeesByRoleIdEmployee();
		model.addAttribute("empCount", empCount);
		Integer mgrCount = eService.countEmployeesByRoleIdManager();
		model.addAttribute("mgrCount", mgrCount);
		Integer adminCount = eService.countEmployeesByRoleIdAdmin();
		model.addAttribute("adminCount", adminCount);

		// UPDATED 
	    List<Employee> allEmployees = eService.findAll();
	    model.addAttribute("allEmployees", allEmployees);
	    //End of update

		// Retrieve user email from session
		// String userEmail = (String) session.getAttribute("userEmail");
		String userEmail = user.getEmail();
		Optional<Employee> loggedInUser = eService.findByEmail(userEmail);
		
		// Base first name: "Admin" - accommodate outsourced admins
		String userFirstName = "admin";
		if (loggedInUser.isPresent()) {
			userFirstName = loggedInUser.get().getFirstName();
		}
		
		model.addAttribute("userFirstName", userFirstName);
		
		return "employee/employee-mgmt";
	}
	
	@GetMapping("/create")
	public String showCreateEmployeeForm(Model model) {

		// New employee for binding, set role name empty
		Employee employee = new Employee();
		employee.setRoleName("");
		model.addAttribute(employee);

		// Add roleList and rankList to model
		model.addAttribute("roleList", rService.findAllRoles());
		model.addAttribute("rankList", EmployeeRank.values());
		
		return "employee/employee-form";
	}
	
	@PostMapping("/create")
	public String createEmployee(@Valid @ModelAttribute (name="employee") Employee employee, 
		BindingResult bindingResult, RedirectAttributes redirectAttrs, Model model) {
		if(employee.getRank().equals(EmployeeRank.PROFESSIONAL)) {
			if(employee.getAnnualLeave() < 18 || employee.getAnnualLeave() > 21) {
				bindingResult.rejectValue("annualLeave", "error.leave", "For Professionals, Annual Leave must be between 18 and 21.");
			}
		}
		
		if(employee.getRank().equals(EmployeeRank.NON_EXECUTIVE)) {
			if(employee.getAnnualLeave() < 14 || employee.getAnnualLeave() > 17) {
				bindingResult.rejectValue("annualLeave", "error.leave", "For Non-Executives, Annual Leave must be between 14 and 17.");
			}
		}
			
		
		if (bindingResult.hasErrors()) {
			// Add roleList and rankList to model
			model.addAttribute("roleList", rService.findAllRoles());
			model.addAttribute("rankList", EmployeeRank.values());
			return "employee/employee-form";
		}
        
		try { 
						
			// find employee's AL leave record
			// set entitled days in employee's AL leave record based on created employee's annualLeave input
			eService.saveNewEmployee(employee);
			redirectAttrs.addFlashAttribute("successMsg", 
				"Employee has been created.");
			
			return "redirect:/admin/employees";
		} catch (Exception ex) { // Catches SQL + Custom exceptions

			model.addAttribute("globalError", 
				"Save failed: " + ex.getMessage());
			model.addAttribute("roleList", rService.findAllRoles());
			// Add enum values for rank
			model.addAttribute("rankList", EmployeeRank.values());
			model.addAttribute("employee", employee);
			return "employee/employee-form";
		}
		
	}
	@GetMapping("/update/{id}")
	public String showUpdateEmployeeForm(@PathVariable Long id, 
			Model model, RedirectAttributes redirectAttrs) {

		// Add roleList and rankList to model
		// Add to model
		model.addAttribute("roleList", rService.findAllRoles());
		model.addAttribute("rankList", EmployeeRank.values());
		
		Optional<Employee> empOpt = eService.findById(id);
		if (empOpt.isPresent()) {
			Employee empToUpdate = empOpt.get();
			
			// Find employee's AL leave record
			// Set employee's annualLeave field to value of entitled days from employee's AL leave record
			// So that the annualLeave input field in html will be populated with number from leave record
			Long annualLeaveTypeId = ltService.findByLeaveType("Annual").get().getId();
			Integer currentYear = LocalDate.now().getYear();
			
			Optional<LeaveRecord> empAnnualRecord = lrService.findByEmployeeIdAndLeaveTypeIdAndCalendarYear(id, annualLeaveTypeId, currentYear);
			if(empAnnualRecord.isPresent()) {
				empToUpdate.setAnnualLeave(empAnnualRecord.get().getEntitledDays());
			} 

			model.addAttribute("employee", empToUpdate);
		} else {
			redirectAttrs.addFlashAttribute("globalError", 
				"Employee not found. Create New Employee first.");
			return "redirect:/admin/employees/create";
		}
		return "employee/employee-form";
	}
	
	@PostMapping("/update/{id}")
	public String updateEmployeeDetails(@PathVariable Long id, 
		@Valid @ModelAttribute (name="employee") Employee employee, 
		BindingResult bindingResult, RedirectAttributes redirectAttrs, Model model) {
		
		if(employee.getRank().equals(EmployeeRank.PROFESSIONAL)) {
			if(employee.getAnnualLeave() < 18 || employee.getAnnualLeave() > 21) {
				bindingResult.rejectValue("annualLeave", "error.leave", 
					"For Professionals, Annual Leave must be between 18 and 21.");
			}
		}
		
		if(employee.getRank().equals(EmployeeRank.NON_EXECUTIVE)) {
			if(employee.getAnnualLeave() < 14 || employee.getAnnualLeave() > 17) {
				bindingResult.rejectValue("annualLeave", "error.leave", 
					"For Non-Executives, Annual Leave must be between 14 and 17.");
			}
		}
		
		if (bindingResult.hasErrors()) {	
			// Add roleList and rankList to model
			model.addAttribute("roleList", rService.findAllRoles());
			model.addAttribute("rankList", EmployeeRank.values());
			return "employee/employee-form";
		}
	
		// ID passed in as hidden form field
		// But if null, set via Path Variable
		if (employee.getId() == null) { employee.setId(id); }

		try {
			// Update employee details
			eService.updateEmployee(employee);
			redirectAttrs.addFlashAttribute("successMsg", 
				"Employee #" + id + " has been updated.");

			return "redirect:/admin/employees";
		} catch (Exception ex) { // Catches SQL + Custom exceptions
			redirectAttrs.addFlashAttribute("globalError", 
				"Update failed: " + ex.getMessage());
			return "redirect:/admin/employees";
		}
	}
	
	@PostMapping("/delete/{id}")
	public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttrs) {
		Optional<Employee> empOpt = eService.findById(id);
		
		if (empOpt.isPresent()) {
			try {
				eService.delete(empOpt.get());
				redirectAttrs.addFlashAttribute("successMsg", 
					"Employee #" + id + " has been deleted.");
			} catch (Exception ex) { // Catches SQL + Custom exceptions
				redirectAttrs.addFlashAttribute("globalError", 
					"Delete failed: " + ex.getMessage());
			}
		} else {
			redirectAttrs.addFlashAttribute("globalError", 
				"Employee does not exist.");
		}
		return "redirect:/admin/employees";
	}
	

}
