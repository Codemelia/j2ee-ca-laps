package sg.edu.nus.laps.common.exception.auth;

public class UserAlreadyExistsException extends RuntimeException {

    // Default message
    public UserAlreadyExistsException() {
        super("User already exists");
    }

    // Customised message
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    
}
