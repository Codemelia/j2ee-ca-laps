package sg.edu.nus.laps.common.exception;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import sg.edu.nus.laps.auth.security.AuthUserDetails;
import sg.edu.nus.laps.common.exception.auth.UnauthenticatedUserException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // MODEL ATTRIBUTES
    // RUN LOGIC BEFORE CONTROLLER METHODS

    // Run NULL User check before every Controller method
    // Redirect to login if user is not authenticated
    @ModelAttribute
    public void checkUserAuthentication(@AuthenticationPrincipal AuthUserDetails user,
        HttpServletRequest request) {
        String uri = request.getRequestURI();
        String path = request.getContextPath();

        // All non-authenticated paths
        boolean isPublicPath =
            uri.equals(path + "/auth/login") ||
            uri.equals(path + "/auth/employee/login") ||
            uri.equals(path + "/auth/admin/login") ||
            uri.startsWith(path + "/css/") ||
            uri.startsWith(path + "/js/") ||
            uri.startsWith(path + "/images/") ||
            uri.equals(path + "/favicon.ico") ||
            uri.startsWith(path + "/error");
        
        // Only do null checks for authenticated paths
        if (!isPublicPath && user == null) {
            throw new UnauthenticatedUserException();
        }
    }

    // EXCEPTION HANDLERS
    // CATCH GLOBAL EXCEPTIONS

    // Handle null user exception
    // Redirect to login page with param unauthorised
    @ExceptionHandler(UnauthenticatedUserException.class)
    public String handleUnauthenticatedUserException() {
        return "redirect:/auth/login?unauthorised";
    }

    // TO BE IMPLEMENTED
    // @ExceptionHandler(InvalidUserException.class) 
    // public String handleInvalidUserException(InvalidUserException ex) {
    //     return "";
    // }

    // @ExceptionHandler(UserAlreadyExistsException.class) 
    // public String handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
    //     return "";
    // }

}
