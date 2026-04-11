package sg.edu.nus.laps.common.exception.employee;

public class InvalidEmployeeException extends RuntimeException {
    // Default message
    public InvalidEmployeeException() { super("Invalid employee profile"); }

    // Customised message
    public InvalidEmployeeException(String message) { super(message); }
}
