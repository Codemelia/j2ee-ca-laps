package sg.edu.nus.laps.auth.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.laps.auth.user.model.User;
import sg.edu.nus.laps.auth.user.repository.UserRepository;

/*
    AuthUserDetailsService is used by Spring Security to load user details during authentication
*/
@Service
public class AuthUserDetailsService implements UserDetailsService {

    // @Autowired
    private final UserRepository userRepo;
    public AuthUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // SpringSecurity calls this during login
    // User has PK email, not username
    // Spring Security matches BCrypt to raw password via auto-configured DaoAuthenticationProvider
    @Override
    @Transactional(readOnly = true) // Allows lazy fields to initialise
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmailAndEnabledTrue(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found or disabled: " + email));
        return new AuthUserDetails(user);
    }

}
