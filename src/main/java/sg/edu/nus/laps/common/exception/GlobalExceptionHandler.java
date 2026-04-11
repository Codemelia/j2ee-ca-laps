package sg.edu.nus.laps.common.exception;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import sg.edu.nus.laps.auth.security.AuthUserDetails;
import sg.edu.nus.laps.common.exception.auth.InvalidUserException;
import sg.edu.nus.laps.common.exception.auth.UnauthenticatedUserException;
import sg.edu.nus.laps.common.exception.auth.UserAlreadyExistsException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // MODEL ATTRIBUTES
    // RUN LOGIC BEFORE CONTROLLER METHODS

    // Run NULL User check before every Controller method
    // Redirect to login if user is not authenticated
    @ModelAttribute
    public void checkUserAuthentication(@AuthenticationPrincipal AuthUserDetails user,
        HttpServletRequest request) {
        boolean isPublicPath = request.getRequestURI()
            .equals(request.getContextPath() + "/auth/login"); // Only login is public
        if (!isPublicPath && user == null) { // Only do null checks for authenticated paths
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
