package sg.edu.nus.laps.employee.model;

// To temporarily store employee, email, password
// Admin/System may need the value
public record NewEmployeeRecord(
    Employee employee,
    String email,
    String password
) {}