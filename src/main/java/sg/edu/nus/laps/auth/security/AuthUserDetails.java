package sg.edu.nus.laps.auth.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import sg.edu.nus.laps.auth.user.model.Role;
import sg.edu.nus.laps.auth.user.model.User;

public class AuthUserDetails implements UserDetails {

    private final String email;
    private final String passwordHash;
    private final Role role;
    private final Long employeeId; // Nullable
    private final boolean enabled;
    
    public AuthUserDetails(User user) {
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.role = user.getRole();
        this.employeeId = user.getEmployee() != null
            ? user.getEmployee().getId()
            : null; // Allow nullable
        this.enabled = user.isEnabled();
    }

    // Get authorities (role) granted to user
    // Return singleton list bc Spring expects a collection
    // But 1 user can only have 1 role
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null || role.getName() == null)
            throw new IllegalStateException("User does not have a valid role: " + email);
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()) // Ensure uppercase
        );
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

}
