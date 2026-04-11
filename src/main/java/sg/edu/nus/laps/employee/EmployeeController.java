package sg.edu.nus.laps.employee;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.edu.nus.laps.employee.model.Employee;

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
	
	public EmployeeController(EmployeeService eService) {
		super();
		this.eService = eService;
	}
	
	private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }
	
	// Not part of Employee CRUD
	// @GetMapping("/")
	// public String showDashboard(Model model, HttpSession session, RedirectAttributes redirectAttrs) {
	// 	if (!isLoggedIn(session)) {
    //         redirectAttrs.addFlashAttribute("errorMessage",
    //                 "Please log in to view employees.");
    //         return "redirect:/login";
    //     }
		
	// 	List<Employee> allEmployees = eService.findAll();
	// 	model.addAttribute("allEmployees", allEmployees);

	// 	// Retrieve user email from session attribute
	// 	String userEmail = (String) session.getAttribute("userEmail");
	// 	Optional<Employee> loggedInUser = eService.findByEmail(userEmail);
		
	// 	// Base first name: "Admin" - accommodate outsourced admins
	// 	String userFirstName = "admin";
	// 	if (loggedInUser.isPresent()) {
	// 		userFirstName = loggedInUser.get().getFirstName();
	// 	}
		
	// 	model.addAttribute("userFirstName", userFirstName);
		
	// 	return "dashboard";
	// }
	
	@GetMapping("/create")
	public String showCreateEmployeeForm(HttpSession session, RedirectAttributes redirectAttrs) {
		if (!isLoggedIn(session)) {
            redirectAttrs.addFlashAttribute("errorMessage",
                    "Please log in to view Create New Employee form.");
            return "redirect:/auth/admin/login";
        }
		return "employee/create-employee-form";
	}
	
	@PostMapping("/create")
	public String createEmployee(@Valid @ModelAttribute Employee employee, 
			BindingResult bindingResult, RedirectAttributes redirectAttrs) {
		if (bindingResult.hasErrors()) {
			return "employee/create-employee-form";
		}
		
		eService.save(employee);
		redirectAttrs.addFlashAttribute("success", "Employee has been created.");
		
		return "redirect:/";
		
	}
	@GetMapping("/update/{id}")
	public String showUpdateEmployeeForm(@PathVariable Long id, Model model, 
			HttpSession session, RedirectAttributes redirectAttrs) {
		if (!isLoggedIn(session)) {
            redirectAttrs.addFlashAttribute("errorMessage",
                    "Please log in to update employee details.");
            return "redirect:/auth/admin/login";
        }
		
		Optional<Employee> empToUpdate = eService.findById(id);
		if (empToUpdate.isPresent()) {
			model.addAttribute("employee", empToUpdate.get());
		}
		
		if (empToUpdate.isEmpty()) {
			redirectAttrs.addFlashAttribute("errorMessage", 
					"Employee does not exist.");
			return "redirect:/";
		}
		
		return "employee/update-employee-form";
	}
	
	@PostMapping("/update/{id}")
	public String updateEmployeeDetails(@PathVariable Long id, @Valid @ModelAttribute Employee employee, 
			BindingResult bindingResult, RedirectAttributes redirectAttrs) {
		if (bindingResult.hasErrors()) {
			return "employee/update-employee-form";
		}
		employee.setId(id);
		eService.save(employee);
		redirectAttrs.addFlashAttribute("success", "Employee #" + id + " has been updated.");
		return "redirect:/";
	}
	
	@DeleteMapping("/delete/{id}")
	public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttrs) {
		Optional<Employee> empOpt = eService.findById(id);
		
		if (empOpt.isPresent()) {
			redirectAttrs.addFlashAttribute("success", "Employee #" + id + " has been deleted.");
			eService.delete(empOpt.get());
		}
		
		if (empOpt.isEmpty()) {
			redirectAttrs.addFlashAttribute("failure", "Employee does not exist.");
		}
		return "redirect:/";
	}
}
