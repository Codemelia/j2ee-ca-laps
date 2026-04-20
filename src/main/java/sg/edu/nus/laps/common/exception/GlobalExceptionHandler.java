package sg.edu.nus.laps.common.exception;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sg.edu.nus.laps.auth.exception.InvalidPasswordException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = Logger
        .getLogger(GlobalExceptionHandler.class.getName());

    // EXCEPTION HANDLERS
    
    // Handle null user exception
    // Redirect to login page with param unauthorised
    @ExceptionHandler(UnauthorisedUserException.class)
    public void handleUnauthorisedUserException(
        UnauthorisedUserException ex,
        HttpServletRequest request,
        HttpServletResponse response) throws IOException {
        logger.warning("User Unauthorised: " + ex.getMessage());

        String uri = request.getRequestURI();

        // SET REDIRECT TO ADMIN / EMPLOYEE LOGIN BASED ON URI BASE
        if (uri.startsWith("/admin")) {
            response.sendRedirect("/auth/admin/login?unauthorised=true");
        } else {
            response.sendRedirect("/auth/employee/login?unauthorised=true");
        }
    }

    // USER AUTH

    // Handle invalid password errors (REST)
    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseBody
    public ResponseEntity<Map<String, ?>> handleInvalidPasswordException(InvalidPasswordException ex) {
        logger.warning("Invalid Password: " + ex.getMessage());

        return ResponseEntity.badRequest().body(
            Map.of("message", "Password change request was invalid"));
    }


}
