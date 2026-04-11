package sg.edu.nus.laps.common.exception.auth;

public class InvalidUserException extends RuntimeException {
    // Default message
    public InvalidUserException() { super("Invalid user data"); }

    // Customised message
    public InvalidUserException(String message) { super(message); }
}
