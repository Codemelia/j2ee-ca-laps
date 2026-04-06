package sg.edu.nus.laps.auth.user;

import org.springframework.stereotype.Service;

/*
    UserService handles all user data operations

                    SERVICE SCOPE
    ------------------------------------------------
    -- READ --
    findByUserEmail(email)          - Find user by email
    findByUserId(id)                - Find user by ID

    -- CREATE / UPDATE --
    save(user)                      - Create or update user account (JPA maps by ID)

    -- DELETE --
    delete(user)                    - Delete user account
*/
@Service
public class UserService {

    // @Autowired
    // private UserRepository userRepo;

    // @Autowired
    // private PasswordEncoder encoder;

    // Not needed since implementing Spring Security
    // Authenticate email and password
    // public boolean authenticate(String email, String password) {
    //     Optional<User> optUser = userRepo.findByEmailAndEnabledTrue(email); // Find active account
    //     if (optUser.isEmpty()) return false; // If Optional<User> is empty, return false
    //     User savedUser = optUser.get(); // Else, get User object
    //     return encoder.matches(password, savedUser.getPasswordHash()); // Check password against saved password (encoded)
    // }
	
}
