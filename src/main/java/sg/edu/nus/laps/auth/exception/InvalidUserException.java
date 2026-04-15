package sg.edu.nus.laps.auth.exception;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException() { super("Invalid user data"); } // Default
    public InvalidUserException(String message) { super(message); } // Custom
}
