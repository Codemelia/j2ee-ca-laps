package sg.edu.nus.laps.common.exception;

import java.io.IOException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    // EXCEPTION HANDLERS
    // CATCH GLOBAL EXCEPTIONS

    // Handle null user exception
    // Redirect to login page with param unauthorised
    @ExceptionHandler(UnauthorisedUserException.class)
    public void handleUnauthorisedUserException(
        UnauthorisedUserException ex,
        HttpServletRequest request,
        HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();

        // SET REDIRECT TO ADMIN / EMPLOYEE LOGIN BASED ON URI BASE
        if (uri.startsWith("/admin")) {
            response.sendRedirect("/auth/admin/login?unauthorised=true");
        } else {
            response.sendRedirect("/auth/employee/login?unauthorised=true");
        }
    }

}
