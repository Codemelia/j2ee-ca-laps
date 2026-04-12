package sg.edu.nus.laps.employee;

public class EmployeeUtil {

    protected static boolean roleIsValid(String roleName) {
		return (roleName != null && !roleName.isBlank());
	}
    
}
