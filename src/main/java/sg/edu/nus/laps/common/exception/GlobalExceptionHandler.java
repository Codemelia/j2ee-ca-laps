package sg.edu.nus.laps.common.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import sg.edu.nus.laps.common.exception.auth.InvalidUserException;
import sg.edu.nus.laps.common.exception.auth.UserAlreadyExistsException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // TO BE IMPLEMENTED
    @ExceptionHandler(InvalidUserException.class) 
    public String InvalidUserException(InvalidUserException ex) {
        return "";
    }

    // TO BE IMPLEMENTED
    @ExceptionHandler(UserAlreadyExistsException.class) 
    public String handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return "";
    }

}
