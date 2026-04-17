package sg.edu.nus.laps.employee.exception;

public class InvalidEmployeeException extends RuntimeException {
    public InvalidEmployeeException() { super("Invalid employee profile"); } // Default
    public InvalidEmployeeException(String message) { super(message); } // Custom
}
