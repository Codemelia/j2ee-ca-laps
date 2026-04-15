 package sg.edu.nus.laps.employee;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import sg.edu.nus.laps.auth.model.Role;
import sg.edu.nus.laps.auth.service.RoleService;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.employee.model.EmployeeRank;
import sg.edu.nus.laps.leave.dto.NewAlEntitlement;
import sg.edu.nus.laps.leave.model.LeaveRecord;
import sg.edu.nus.laps.leave.model.LeaveType;
import sg.edu.nus.laps.leave.service.HolidayService;
import sg.edu.nus.laps.leave.service.LeaveRecordService;
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
	private LocalDate date;
	
	public EmployeeController(EmployeeService eService,
		RoleService rService, HolidayService hService, LeaveRecordService lrService) {
		super();
		this.eService = eService;
		this.rService = rService;
		this.hService = hService;
		this.lrService = lrService;
	}
	
	// private boolean isLoggedIn(HttpSession session) {
    //     return session.getAttribute("user") != null;
    // }
	
	@GetMapping("/updateHolidays")
	public String updateHolidays(Model model, RedirectAttributes redirectAttrs) {
		
		try { 
			hService.fetchAndSyncHolidays();
			redirectAttrs.addFlashAttribute("success", "Public Holidays have been updated.");
			return "redirect:/admin/employees";
		} catch (Exception ex) { // Catches SQL + Custom exceptions
			redirectAttrs.addFlashAttribute("failure", "Public Holidays cannot be updated.");
			return "redirect:/admin/employees";
		}
	}
	
	@GetMapping("/update-entitlement")
	public String showAlEntitlementForm(Model model, RedirectAttributes redirectAttrs) {
		
//		NewAlEntitlement newAlEntitlement = new NewAlEntitlement();
//		newAlEntitlement.getNewNonExecAnnual().setCalendarYear(LocalDate.now().getYear());
//		newAlEntitlement.getNewNonExecAnnual().setLeaveType()
		LeaveRecord newAlEntitlement = new LeaveRecord();
		Long leaveTypeId = 1L;
		
		// Get Non-Executive AL entitlement for current Year, default is 14.0 days
		// Get List<Employee> by EmployeeRank, get EmployeeId of first employee in list
		// Get AL LeaveRecord of employee, get entitled days
		List<Employee> nonExecEmployees = eService.findByRank(EmployeeRank.NON_EXECUTIVE);
		Long firstNonExecId = nonExecEmployees.get(0).getId();
		
		Optional<LeaveRecord> nonExecAlRecord = lrService.findByEmployeeIdAndLeaveTypeIdAndCalendarYear(firstNonExecId, leaveTypeId, date.now().getYear());
		
		if(nonExecAlRecord.isPresent()) {
			Double currentNonExecAl = nonExecAlRecord.get().getEntitledDays();
			model.addAttribute("currentNonExecAnnual", currentNonExecAl);
		} else if(nonExecAlRecord.isEmpty()) {
			model.addAttribute("currentNonExecAnnual", 14.0);
		}
		
		// Get Professional AL entitlement for current Year, default is 18.0 days
		List<Employee> proEmployees = eService.findByRank(EmployeeRank.PROFESSIONAL);
		Long firstProId = proEmployees.get(0).getId();
		
		Optional<LeaveRecord> proAlRecord = lrService.findByEmployeeIdAndLeaveTypeIdAndCalendarYear(firstProId, leaveTypeId, date.now().getYear());
		
		if(proAlRecord.isPresent()) {
			Double currentProAl = proAlRecord.get().getEntitledDays();
			model.addAttribute("currentProAnnual", currentProAl);
		} else if(proAlRecord.isEmpty()) {
			model.addAttribute("currentProAnnual", 18.0);
		}
		
		return "/al-entitlement-form";
	}
	
	@PostMapping("/update-entitlement")
	public String updateAnnualLeaveEntitlement(@Valid @ModelAttribute LeaveRecord newAlEntitlement, 
			BindingResult bindingResult, RedirectAttributes redirectAttrs) {
		if (bindingResult.hasErrors()) {
			return "/al-entitlement-form";
		}
		
		Long leaveTypeId = 1L;
		List<Employee> nonExecEmployees = eService.findByRank(EmployeeRank.NON_EXECUTIVE);
		
//		for( Employee e : nonExecEmployees) {
//			Long employeeId = e.getId();
//			Optional<LeaveRecord> lrService.findByEmployeeIdAndLeaveTypeIdAndCalendarYear(employeeId, leaveTypeId, newAlEntitlement.)
//		}
		
		// find list of nonExecEmployees
		// for each employee, get employeeId
		// find leaveRecords by employeeId and leaveTypeId
		// assign newEntitlement to entitledDays field
		// BUT we have 2 variants of newEntitlement values: Non-Executive & Professional
		
		return "";
	}
	
	@GetMapping
	public String showEmployees(@AuthenticationPrincipal AuthUserDetails user,
		Model model, RedirectAttributes redirectAttrs, @PageableDefault(size = 10) Pageable pageable) {
		// if (!isLoggedIn(session)) {
        //     redirectAttrs.addFlashAttribute("errorMessage",
        //             "Please log in to view employees.");
        //     return "redirect:/login";
        // }
		
		Integer empCount = eService.countEmployeesByRoleIdEmployee();
		model.addAttribute("empCount", empCount);
		
		Integer mgrCount = eService.countEmployeesByRoleIdManager();
		model.addAttribute("mgrCount", mgrCount);
		
		Integer adminCount = eService.countEmployeesByRoleIdAdmin();
		model.addAttribute("adminCount", adminCount);
		
//		List<Employee> allEmployees = eService.findAll();
//		model.addAttribute("allEmployees", allEmployees);
		
		Page<Employee> page = eService.findAll(pageable);
        model.addAttribute("allEmployees", page.getContent());
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("page", page);

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
		// if (!isLoggedIn(session)) {
        //     redirectAttrs.addFlashAttribute("errorMessage",
        //             "Please log in to view Create New Employee form.");
        //     return "redirect:/auth/admin/login";
        // }

		// New employee for binding, set role name empty
		Employee employee = new Employee();
		employee.setRoleName("");

		// Pull list of roles
		List<Role> roleList = rService.findAllRoles();

		// Add to model
		model.addAttribute("roleList", roleList);
		model.addAttribute("employee", employee); // Use the same instance for binding
		// Add enum values for rank
		model.addAttribute("rankList", EmployeeRank.values());
		
		return "employee/employee-form";
	}
	
	@PostMapping("/create")
	public String createEmployee(@Valid @ModelAttribute Employee employee, 
		BindingResult bindingResult, RedirectAttributes redirectAttrs) {
		if (bindingResult.hasErrors()) {
			return "employee/employee-form";
		}
        
		try { 
			eService.saveNewEmployee(employee);
			redirectAttrs.addFlashAttribute("success", "Employee has been created.");
			return "redirect:/admin/employees";
		} catch (Exception ex) { // Catches SQL + Custom exceptions
			bindingResult.reject("error", "Save failed: " + ex.getMessage());
			return "employee/employee-form";
		}
		
	}
	@GetMapping("/update/{id}")
	public String showUpdateEmployeeForm(@PathVariable Long id, 
			Model model, RedirectAttributes redirectAttrs) {
		// if (!isLoggedIn(session)) {
        //     redirectAttrs.addFlashAttribute("errorMessage",
        //             "Please log in to update employee details.");
        //     return "redirect:/auth/admin/login";
        // }
		
		model.addAttribute("rankList", EmployeeRank.values());
		
		Optional<Employee> empToUpdate = eService.findById(id);
		if (empToUpdate.isPresent()) {
			model.addAttribute("employee", empToUpdate.get());
		} 

		// Not needed - can set if/else on Thymeleaf
		// else {
		// 	// redirectAttrs.addFlashAttribute("errorMessage", 
		// 	// 		"Employee does not exist.");
		// 	// return "redirect:/admin/employees";
		// }
		
		return "employee/employee-form";
	}
	
	@PostMapping("/update/{id}")
	public String updateEmployeeDetails(@PathVariable Long id, 
		@Valid @ModelAttribute Employee employee, 
		BindingResult bindingResult, RedirectAttributes redirectAttrs) {
		if (bindingResult.hasErrors()) {
			return "employee/employee-form";
		}

		// ID passed in as hidden form field
		// But if null, set via Path Variable
		if (employee.getId() == null) { employee.setId(id); }

		try {
			eService.updateEmployee(employee);
			redirectAttrs.addFlashAttribute("success", "Employee #" + id + " has been updated.");
			return "redirect:/admin/employees";
		} catch (Exception ex) { // Catches SQL + Custom exceptions
			bindingResult.reject("error", "Update failed: " + ex.getMessage());
			return "employee/employee-form";
		}
	}
	
	@PostMapping("/delete/{id}")
	public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttrs) {
		Optional<Employee> empOpt = eService.findById(id);
		
		if (empOpt.isPresent()) {
			try {
				eService.delete(empOpt.get());
				redirectAttrs.addFlashAttribute("success", "Employee #" + id + " has been deleted.");
			} catch (Exception ex) { // Catches SQL + Custom exceptions
				redirectAttrs.addFlashAttribute("failure", "Delete failed: " + ex.getMessage());
			}
		} else {
			redirectAttrs.addFlashAttribute("failure", "Employee does not exist.");
		}
		return "redirect:/admin/employees";
	}

}
