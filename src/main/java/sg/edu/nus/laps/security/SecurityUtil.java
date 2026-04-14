package sg.edu.nus.laps.security;

import java.util.List;

import org.springframework.security.core.Authentication;

public class SecurityUtil {

    // Check if user has specified authority
    public static boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(authority));
    }

    // Check if user has any of specified authorities
    public static boolean hasAnyAuthority(Authentication authentication, List<String> authorities) {
        return authentication.getAuthorities().stream()
            .anyMatch(a -> authorities.contains(a.getAuthority()));
    }

    // Check if user is admin
    public static boolean isAdmin(Authentication auth) {
        return hasAuthority(auth, "ROLE_ADMIN");
    }

    // Check if user is internal admin
    public static boolean isInternalAdmin(Authentication auth) {
        return hasAuthority(auth, "AUTH_INTERNAL_ADMIN");
    }

    // Check if user should have access to employee-facing pages
    public static boolean isInternalEmployee(Authentication auth) {
        return hasAnyAuthority(auth, 
            List.of(
                "ROLE_EMPLOYEE", 
                "ROLE_MANAGER", 
                "AUTH_INTERNAL_ADMIN"
            )
        );
    }

}
