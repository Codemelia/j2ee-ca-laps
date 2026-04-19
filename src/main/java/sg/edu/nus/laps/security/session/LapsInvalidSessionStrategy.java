package sg.edu.nus.laps.security.session;

import java.io.IOException;

import org.springframework.security.web.session.InvalidSessionStrategy;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// SET REDIRECT TO ADMIN / EMPLOYEE LOGIN BASED ON URI BASE
public class LapsInvalidSessionStrategy implements InvalidSessionStrategy {
    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, 
        HttpServletResponse response) throws IOException, ServletException {
        String uri = request.getRequestURI();

        if (uri.startsWith("/admin")) { 
            response
                .sendRedirect("/auth/admin/login?invalid=true"); 
        } 
        else { 
            response
                .sendRedirect("/auth/employee/login?invalid=true"); 
        }
    }
}
