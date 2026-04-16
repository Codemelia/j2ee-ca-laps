package sg.edu.nus.laps.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import sg.edu.nus.laps.auth.model.User;

public class AuthUserDetails implements UserDetails {

    private final String email;
    private final String passwordHash;
    private final String roleName;
    private final Long employeeId; // Nullable
    private final boolean enabled;
    
    public AuthUserDetails(User user) {
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.roleName = user.getRole() != null 
            ? user.getRole().getName() 
            : "UNKNOWN";
        this.employeeId = user.getEmployee() != null
            ? user.getEmployee().getId()
            : null; // Allow nullable
        this.enabled = user.isEnabled();
    }

    // Get authorities (role) granted to user
    // Return collection which SpringSecurity will save as Authorities
    // Main auth: User Role and whether Admin is internal employee or outsourced
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roleName == null) {
            throw new IllegalStateException("User does not have a valid role: " + email);
        }

        List<GrantedAuthority> auths = new ArrayList<>();
        auths.add(new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase())); // Add role as authority

        if ("ADMIN".equalsIgnoreCase(roleName)) {
            if (employeeId == null) {
                auths.add(
                    new SimpleGrantedAuthority("AUTH_EXTERNAL_ADMIN")); // External admin
            } else {
                auths.add(
                    new SimpleGrantedAuthority("AUTH_INTERNAL_ADMIN")); // Internal admin
            }
        }

        return auths;
    }

    // For password matching
    @Override
    public String getPassword() { return passwordHash; }

    @Override
    public String getUsername() { return email; }
    
    // Spring Security throws DisabledException and rejects login
    @Override
    public boolean isEnabled() { return enabled; }

    public Long getEmployeeId() { return employeeId; }
    public String getEmail() { return email; }
    public String getRoleName() { return roleName; }
    public boolean getEnabled() { return this.enabled; }

    // Check internal vs external admin
    public boolean isInternalAdmin() {
        return getAuthorities().stream()
            .anyMatch(auth -> auth
                .getAuthority().equals("AUTH_INTERNAL_ADMIN"));
    }

    public boolean isExternalAdmin() {
        return getAuthorities().stream()
            .anyMatch(auth -> auth
                .getAuthority().equals("AUTH_EXTERNAL_ADMIN"));
    }

    // TO STRING

    @Override
    public String toString() {
        return "{" +
            " email='" + getEmail() + "'" +
            ", passwordHash='" + getPassword() + "'" +
            ", roleName='" + getRoleName() + "'" +
            ", employeeId='" + getEmployeeId() + "'" +
            ", enabled='" + isEnabled() + "'" +
            "}";
    }

}
