package sg.edu.nus.laps.auth.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.laps.auth.InvalidUserException;
import sg.edu.nus.laps.auth.model.User;
import sg.edu.nus.laps.auth.repository.UserRepository;

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

    private final PasswordEncoder encoder;
    private final UserRepository userRepo;
    public UserService(PasswordEncoder encoder, UserRepository userRepo) {
        this.encoder = encoder;
        this.userRepo = userRepo;
    }

    // Save user to repo
    // If user already exists in DB, throw exception
    // Else, encode user password and save in DB
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED
    )
    public User saveUser(User user) {
            
        // Check if user already exists in DB
        if (userRepo.existsByEmailAndEnabledTrue(user.getEmail())) {
            throw new InvalidUserException("User already exists");
        }
        
        // Encode password and store in User
        user.setPasswordHash(encoder.encode(user.getPasswordHash()));
        return userRepo.save(user);
    }

    // Update user (user change password)
    // Takes in old email from UserDetails
    // New + old password from User input
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED
    )
    public void changePassword(String email, String passwordNewRaw, String passwordOldRaw) {

        // Retrieve user by email
        Optional<User> optUser = userRepo.findByEmailAndEnabledTrue(email);

        // If user does not exist or is inactive, throw exception
        if (optUser.isEmpty()) { 
            throw new InvalidUserException("User does not have a valid account");
        }

        // Retrieve old hashed password from DB
        User user = optUser.get();
        String passwordOldHash = user.getPasswordHash();

        // use encoder to match old passwords
        boolean match = encoder.matches(passwordOldRaw, passwordOldHash);

        // If no match, throw exception
        if (!match) {
            throw new InvalidUserException("Old password is not a match");
        }

        // If match, encode new password, set it to User, and merge
        String passwordNewHash = encoder.encode(passwordNewRaw);
        user.setPasswordHash(passwordNewHash);
        userRepo.save(user);

    }

    // REPLACE WITH SPRING SECURITY
    // Authenticate email and password
    // public boolean authenticate(String email, String password) {
    //     Optional<User> optUser = userRepo.findByEmailAndEnabledTrue(email); // Find active account
    //     if (optUser.isEmpty()) return false; // If Optional<User> is empty, return false
    //     User savedUser = optUser.get(); // Else, get User object
    //     return encoder.matches(password, savedUser.getPasswordHash()); // Check password against saved password (encoded)
    // }
        
}
