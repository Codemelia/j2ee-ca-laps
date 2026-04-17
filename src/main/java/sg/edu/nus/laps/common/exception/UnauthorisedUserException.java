package sg.edu.nus.laps.common.exception;

public class UnauthorisedUserException extends RuntimeException {
    public UnauthorisedUserException() { super("User is not authorised"); } // Default
    public UnauthorisedUserException(String message) { super(message); } // Custom
}