package sg.edu.nus.laps.common;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import sg.edu.nus.laps.common.exception.UnauthorisedUserException;
import sg.edu.nus.laps.employee.EmployeeService;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.security.principal.AuthUserDetails;

@ControllerAdvice
public class GlobalModelAttributes {

    // For global use of app name
    @Value("${spring.application.name}")
    private String appName;

    @ModelAttribute("appName")
    public String appName() { return appName; }

    private final EmployeeService eSvc;
    public GlobalModelAttributes(EmployeeService eSvc) { this.eSvc = eSvc; }

    // Run NULL User check before every Controller method - safety net
    @ModelAttribute
    public void checkUserAuthentication(@AuthenticationPrincipal AuthUserDetails user,
        HttpServletRequest request) {
        String uri = request.getRequestURI();
        String path = request.getContextPath();

        // All non-authenticated paths
        boolean isPublicPath =
            uri.startsWith(path + "/auth/employee/login") ||
            uri.startsWith(path + "/auth/admin/login") ||
            uri.startsWith(path + "/css/") ||
            uri.startsWith(path + "/js/") ||
            uri.startsWith(path + "/images/") ||
            uri.equals(path + "/favicon.ico") ||
            uri.startsWith(path + "/error");
        
        // Only do null checks for authenticated paths
        if (!isPublicPath && user == null) {
            throw new UnauthorisedUserException();
        }
    }

    // Retrieve display employee details for all pages
    @ModelAttribute
    public void getEmployeeDetails(
        @AuthenticationPrincipal AuthUserDetails user, Model model) {
        if (user != null && user.getEmployeeId() != null) {
            // Retrieve employee from user
            Long empId = user.getEmployeeId();
            Optional<Employee> optEmp = eSvc.findById(empId);

            // If employee present, add details to model
            if (optEmp.isPresent()) {
                Employee emp = optEmp.get();
                model.addAttribute("employeeFullName", 
                    emp.getFirstName() + " " + emp.getLastName());
                model.addAttribute("employeeTeam", emp.getTeamName());
                model.addAttribute("employeeTitle", emp.getJobTitle());
            }
        }
    }

}
