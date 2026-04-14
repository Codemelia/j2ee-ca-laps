package sg.edu.nus.laps.auth;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException() { super("Invalid user data"); } // Default
    public InvalidUserException(String message) { super(message); } // Custom
}
