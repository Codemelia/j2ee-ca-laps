package sg.edu.nus.laps.security.custom;

import java.io.IOException;

import org.springframework.security.web.session.InvalidSessionStrategy;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LapsInvalidSessionStrategy implements InvalidSessionStrategy {

    // SET CUSTOM INVALID SESSION URLS
    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, 
        HttpServletResponse response) throws IOException, ServletException {
        
        String uri = request.getRequestURI();

        // SET REDIRECT TO ADMIN / EMPLOYEE LOGIN BASED ON URI BASE
        if (uri.startsWith("/admin")) {
            response.sendRedirect("/auth/admin/login?invalid");
        } else {
            response.sendRedirect("/auth/employee/login?invalid");
        }

    }
    
}
