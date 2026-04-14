package sg.edu.nus.laps.common.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // EXCEPTION HANDLERS
    // CATCH GLOBAL EXCEPTIONS

    // Handle null user exception
    // Redirect to login page with param unauthorised
    @ExceptionHandler(UnauthorisedUserException.class)
    public String handleUnauthorisedUserException() {
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
