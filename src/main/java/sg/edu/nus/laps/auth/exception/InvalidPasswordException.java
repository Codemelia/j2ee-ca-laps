package sg.edu.nus.laps.auth.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() { super("Password is invalid"); } // Default
    public InvalidPasswordException(String message) { super(message); } // Custom
}
