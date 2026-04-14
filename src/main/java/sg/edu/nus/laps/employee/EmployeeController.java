 package sg.edu.nus.laps.employee;

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
import sg.edu.nus.laps.auth.security.AuthUserDetails;
import sg.edu.nus.laps.auth.user.model.Role;
import sg.edu.nus.laps.auth.user.service.RoleService;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.employee.model.EmployeeRank;

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
	
	public EmployeeController(EmployeeService eService,
		RoleService rService) {
		super();
		this.eService = eService;
		this.rService = rService;
	}
	
	// private boolean isLoggedIn(HttpSession session) {
    //     return session.getAttribute("user") != null;
    // }
	
	@GetMapping
	public String showEmployees(@AuthenticationPrincipal AuthUserDetails user,
		Model model, RedirectAttributes redirectAttrs) {
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
		
		List<Employee> allEmployees = eService.findAll();
		model.addAttribute("allEmployees", allEmployees);

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
		
		return "employee/create-employee-form";
	}
	
	@PostMapping("/create")
	public String createEmployee(@Valid @ModelAttribute Employee employee, 
		BindingResult bindingResult, RedirectAttributes redirectAttrs) {
		if (bindingResult.hasErrors()) {
			return "employee/create-employee-form";
		}
        
		try { 
			eService.saveNewEmployee(employee);
			redirectAttrs.addFlashAttribute("success", "Employee has been created.");
			return "redirect:/admin/employees";
		} catch (Exception ex) { // Catches SQL + Custom exceptions
			bindingResult.reject("error", "Save failed: " + ex.getMessage());
			return "employee/create-employee-form";
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
		
		return "employee/update-employee-form";
	}
	
	@PostMapping("/update/{id}")
	public String updateEmployeeDetails(@PathVariable Long id, 
		@Valid @ModelAttribute Employee employee, 
		BindingResult bindingResult, RedirectAttributes redirectAttrs) {
		if (bindingResult.hasErrors()) {
			return "employee/update-employee-form";
		}

		employee.setId(id);

		try {
			eService.updateEmployee(employee);
			redirectAttrs.addFlashAttribute("success", "Employee #" + id + " has been updated.");
			return "redirect:/admin/employees";
		} catch (Exception ex) { // Catches SQL + Custom exceptions
			bindingResult.reject("error", "Update failed: " + ex.getMessage());
			return "employee/create-employee-form";
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
