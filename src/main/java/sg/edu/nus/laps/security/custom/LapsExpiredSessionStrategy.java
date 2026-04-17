package sg.edu.nus.laps.security.custom;

import java.io.IOException;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import jakarta.servlet.http.HttpServletResponse;

// SET REDIRECT TO ADMIN / EMPLOYEE LOGIN BASED ON URI BASE
public class LapsExpiredSessionStrategy implements SessionInformationExpiredStrategy {
    @Override
    public void onExpiredSessionDetected(
        SessionInformationExpiredEvent event) throws IOException {
        // Get URI and response
        String uri = event.getRequest().getRequestURI();
        HttpServletResponse response = event.getResponse();

        if (uri.startsWith("/admin")) { response
            .sendRedirect("/auth/admin/login?expired=true"); } 
        else { response
            .sendRedirect("/auth/employee/login?expired=true"); }
    }
}
