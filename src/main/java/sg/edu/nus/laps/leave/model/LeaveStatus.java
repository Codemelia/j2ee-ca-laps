package sg.edu.nus.laps.leave.model;

/* 
 * Life-cycle State of a Leave Application.
 * Stored as a VARCHAR string via @Enumerated(EnumType.STRING).
 */

public enum LeaveStatus {
	/* 
	 * 1. Setting Up the Possible State of a Leave Application
	 * Note: SUBMITTED has been dropped as it is a transient state.
	 * Once a LA is submitted, it will immediately be in the APPLIED review state.
	*/
	DRAFT("Your leave request has been created, but not yet submitted."),
	APPLIED("Your leave request is awaiting review from your reporting manager."),
	UPDATED("Your leave request is awaiting review from your reporting manager."),
	DELETED("Your leave request has been deleted."),
	CANCELLED("Your leave request has been withdrawn."),
	APPROVED("Your leave request has been approved by your reporting manager."),
	REJECTED("Your leave request has been rejected by your reporting manager.");
	
	private final String displayLeaveStatus;
	
	// 2. Setting the Constructors
	private LeaveStatus(String displayLeaveStatus) { this.displayLeaveStatus = displayLeaveStatus; }

	// 3. Setting the Getters, to enable UI display of Leave Status 
	public String getDisplayLeaveStatus() { return displayLeaveStatus; }
}