package sg.edu.nus.laps.auth.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.laps.auth.user.model.User;
import sg.edu.nus.laps.auth.user.repository.UserRepository;
import sg.edu.nus.laps.common.exception.auth.InvalidUserException;
import sg.edu.nus.laps.common.exception.auth.UserAlreadyExistsException;

/*
    UserService handles all user data operations

                    SERVICE SCOPE
    ------------------------------------------------
    -- READ --
    findByUserEmail(email) - Retrieve user by User Email
    findByUserId(id)       - Retrieve user by User ID

    -- CREATE / UPDATE --
    save(user)             - Create or update user account (JPA maps by ID)

    -- DELETE --
    delete(user)           - Delete user account
*/
@Service
public class UserService {

    // @Autowired
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    public UserService(PasswordEncoder encoder, UserRepository userRepo) {
        this.encoder = encoder;
        this.userRepo = userRepo;
    }

    // Save user to repo
    // If user already exists in DB, throw exception
    // Else, encode user password and save in DB
    @Transactional(propagation = Propagation.REQUIRED)
    public User saveUser(User user) {

        // Null/empty checks
        if (user == null 
            || user.getEmail().isBlank()
            || user.getPasswordHash().isBlank() 
            || user.getRole() == null) {
            throw new InvalidUserException();
        }
            
        // Check if user already exists in DB
        if (userRepo.existsByEmail(user.getEmail()))
            throw new UserAlreadyExistsException();
        
        // Encode password and store in User
        user.setPasswordHash(encoder.encode(user.getPasswordHash()));
        return userRepo.save(user);
    }

    // Update user (user change password)


    // REPLACE WITH SPRING SECURITY
    // Authenticate email and password
    // public boolean authenticate(String email, String password) {
    //     Optional<User> optUser = userRepo.findByEmailAndEnabledTrue(email); // Find active account
    //     if (optUser.isEmpty()) return false; // If Optional<User> is empty, return false
    //     User savedUser = optUser.get(); // Else, get User object
    //     return encoder.matches(password, savedUser.getPasswordHash()); // Check password against saved password (encoded)
    // }

}
