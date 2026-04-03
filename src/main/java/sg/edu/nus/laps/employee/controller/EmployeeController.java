package sg.edu.nus.laps.employee.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.employee.service.EmployeeService;

@RequestMapping("/employees")
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
	

	@GetMapping("/")
	public String displayDashboard(Model model, HttpSession session, RedirectAttributes redirectAttrs) {
		if (!isLoggedIn(session)) {
            redirectAttrs.addFlashAttribute("errorMessage",
                    "Please log in to view employees.");
            return "redirect:/login";
        }
		
		List<Employee> allEmployees = eService.findAll();
		model.addAttribute("employees", allEmployees);
		
		Optional<Employee> loggedInUser = eService.findByEmail(session.user.email);
		
		String userFirstName = "";
		if (loggedInUser.isPresent()) {
			userFirstName = loggedInUser.get().getFirstName();
		}
		
		if (loggedInUser.isEmpty()) {
			userFirstName = "Admin";
		}
		
		model.addAttribute("userFirstName", userFirstName);
		
		return "dashboard";
	}
	
	@GetMapping("/create")
	public String showCreateEmployeeForm(HttpSession session, RedirectAttributes redirectAttrs) {
		if (!isLoggedIn(session)) {
            redirectAttrs.addFlashAttribute("errorMessage",
                    "Please log in to view Create New Employee form.");
            return "redirect:/login";
        }
		return "form";
	}
	
//	@PostMapping("/create")
//	public String createEmployee(@Valid @ModelAttribute Employee employee, BindingResult bindingResult, RedirectAttributes redirectAttrs) {
//		if (bindingResult.hasErrors()) {
//			return "form";
//		}
//		
//		eService.save(employee);
//		
//		return "redirect:/employees";
//		
//	}
}
