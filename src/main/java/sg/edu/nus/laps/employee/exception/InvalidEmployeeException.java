package sg.edu.nus.laps.employee.exception;

public class InvalidEmployeeException extends RuntimeException {
    // Default message
    public InvalidEmployeeException() { super("Invalid employee profile"); }

    // Customised message
    public InvalidEmployeeException(String message) { super(message); }
}
