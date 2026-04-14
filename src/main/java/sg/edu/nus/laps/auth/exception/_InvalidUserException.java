package sg.edu.nus.laps.auth.exception;

public class _InvalidUserException extends RuntimeException {
    // Default message
    public _InvalidUserException() { super("Invalid user data"); }

    // Customised message
    public _InvalidUserException(String message) { super(message); }
}
