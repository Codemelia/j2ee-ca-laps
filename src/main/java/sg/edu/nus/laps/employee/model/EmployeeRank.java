package sg.edu.nus.laps.employee.model;

public enum EmployeeRank {
	NON_EXECUTIVE("Non-Executive"),
	PROFESSIONAL("Professional");
	
	private final String displayName;

	EmployeeRank(String displayName) { this.displayName = displayName; }
	public String getDisplayName() { return displayName; }
}
