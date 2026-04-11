package sg.edu.nus.laps.common.exception.auth;

public class UnauthenticatedUserException extends RuntimeException {
    // Default message
    public UnauthenticatedUserException() { super("User is not authenticated"); }

    // Customised message
    public UnauthenticatedUserException(String message) { super(message); }
}