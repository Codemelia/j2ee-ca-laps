package sg.edu.nus.laps.auth.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.laps.auth.exception.InvalidPasswordException;
import sg.edu.nus.laps.auth.exception.InvalidUserException;
import sg.edu.nus.laps.auth.model.PasswordDTO;
import sg.edu.nus.laps.auth.model.User;
import sg.edu.nus.laps.auth.repository.UserRepository;

/**
 * UserService provides methods:
 * 1. Saving users with encoded passwords
 * 2. Changing user passwords
 * 3. Validating password inputs
 */
@Service
public class UserService {

    private final PasswordEncoder encoder;
    private final UserRepository userRepo;
    public UserService(PasswordEncoder encoder, UserRepository userRepo) {
        this.encoder = encoder;
        this.userRepo = userRepo;
    }

    // Save user to repo - unimplemented, but assuming there is a super admin role
    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
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
        isolation = Isolation.READ_COMMITTED)
    public User changePassword(String email, PasswordDTO passwordDTO) {

        // If user has changed password 

        // Retrieve user by email
        Optional<User> optUser = userRepo.findByEmailAndEnabledTrue(email);

        // If user does not exist or is inactive, throw exception
        if (optUser.isEmpty()) { 
            throw new InvalidUserException("User does not have a valid account");
        }

        User user = optUser.get();
        
        // Old and new raw passwords
        String oldRawPassword = passwordDTO.getOldRawPassword();
        String newRawPassword = passwordDTO.getNewRawPassword();        

        // Use encoder to match old password to hashed password
        if (!encoder.matches(oldRawPassword, user.getPasswordHash())) {
            throw new InvalidPasswordException("Current password is invalid");
        }

        // If match, encode new password, set it to User, and merge
        String newPasswordHash = encoder.encode(newRawPassword);
        user.setPasswordHash(newPasswordHash);
        return userRepo.save(user);
    }

    // Check if new password matches confirm password
    public boolean newPasswordsMatch(PasswordDTO passwordDTO) {
        return passwordDTO.getNewRawPassword()
            .equals(passwordDTO.getConfirmPassword());
    }

    // Check if old password matches DB
    public boolean currentPasswordValid(String email, PasswordDTO passwordDTO) {
        Optional<User> optUser = userRepo.findByEmailAndEnabledTrue(email);
        if (optUser.isPresent()) {
            return encoder.matches(passwordDTO.getOldRawPassword(), 
                optUser.get().getPasswordHash());
        }
        return false;
    }

}
