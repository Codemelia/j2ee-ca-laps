package sg.edu.nus.laps.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sg.edu.nus.laps.auth.model.User;
import sg.edu.nus.laps.auth.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    // Authenticate email and password
    public boolean authenticate(String email, String password) {
        Optional<User> optUser = userRepo.findByEmailAndEnabledTrue(email); // Find active account
        if (optUser.isEmpty()) return false; // If Optional<User> is empty, return false
        User savedUser = optUser.get(); // Else, get User object
        return encoder.matches(password, savedUser.getPasswordHash()); // Check password against saved password (encoded)
    }
	
}
