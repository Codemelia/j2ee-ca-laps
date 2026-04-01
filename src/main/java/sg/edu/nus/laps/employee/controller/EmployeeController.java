package sg.edu.nus.laps.employee.controller;

import org.springframework.stereotype.Controller;
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
	
	private Long requireLogin(HttpSession session, RedirectAttributes redirectAttr) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			redirectAttr.addFlashAttribute("error", "Please log in first!");
		}
		
		return userId;
	}

	@GetMapping("/employees")
	public String displayEmployeesList() {
		return "details";
	}
	
	@GetMapping("/employees/create")
	public String showCreateEmployeeForm() {
		return "form";
	}
	
//	@PostMapping("/employees/create")
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
