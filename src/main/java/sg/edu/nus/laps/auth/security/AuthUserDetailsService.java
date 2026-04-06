package sg.edu.nus.laps.auth.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/*
    AuthUserDetailsService is used by Spring Security to load user details during authentication.

                    SERVICE SCOPE
    ------------------------------------------------
    loadUserByUsername(username) - Look up user by email (username) from the database.
*/
public class AuthUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadUserByUsername'");
    }

}
