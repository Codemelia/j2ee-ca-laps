package sg.edu.nus.laps.security.custom;

import java.io.IOException;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LapsExpiredSessionStrategy implements SessionInformationExpiredStrategy {

    // SET CUSTOM INVALID SESSION URLS
    @Override
    public void onExpiredSessionDetected(
        SessionInformationExpiredEvent event) throws IOException {
        
        // Get request and response from event
        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = event.getResponse();

        String uri = request.getRequestURI();

        // SET REDIRECT TO ADMIN / EMPLOYEE LOGIN BASED ON URI BASE
        if (uri.startsWith("/admin")) {
            response.sendRedirect("/auth/admin/login?expired");
        } else {
            response.sendRedirect("/auth/employee/login?expired");
        }

    }
    
}
