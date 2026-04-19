package sg.edu.nus.laps.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.validation.Validator;
import sg.edu.nus.laps.auth.model.LoginRequestDTO;

@Component
public class ValidAuthProvider implements AuthenticationProvider {

    private final Validator validator;
    private final UserDetailsService userDetailsSvc;
    private final PasswordEncoder encoder;
    
    public ValidAuthProvider(
        Validator validator,
        UserDetailsService userDetailsSvc,
        PasswordEncoder encoder) {
        this.validator = validator;
        this.userDetailsSvc = userDetailsSvc;
        this.encoder = encoder;
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        
        // get creds
        String email = auth.getName();
        String password = (auth.getCredentials() != null)
            ? auth.getCredentials().toString()
            : "";

        // Validate
        LoginRequestDTO login = new LoginRequestDTO(email, password);
        var violations = validator.validate(login);
        if (!violations.isEmpty()) { 
            throw new BadCredentialsException("Validation failed"); 
        }

        // Usual authentication
        UserDetails user = userDetailsSvc.loadUserByUsername(email);
        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid user credentials");
        }

        return new UsernamePasswordAuthenticationToken(
            user, null, user.getAuthorities());

    }

    @Override
    public boolean supports(Class<?> auth) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(auth);
    } 

}