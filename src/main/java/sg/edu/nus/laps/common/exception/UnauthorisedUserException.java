package sg.edu.nus.laps.common.exception;

public class UnauthorisedUserException extends RuntimeException {
    // Default message
    public UnauthorisedUserException() { super("User is not authorised"); }

    // Customised message
    public UnauthorisedUserException(String message) { super(message); }
}