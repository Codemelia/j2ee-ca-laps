package sg.edu.nus.laps.claim.model;

public enum OvertimeClaimStatus {

    /* 
	 * 1. Setting Up the Possible State of an Overtime Claim
	 * Once a Claim is submitted, it will immediately be in the APPLIED review state.
	*/
	APPLIED("Your compensation claim is awaiting review from your reporting manager."),
	APPROVED("Your compensation claim has been approved by your reporting manager."),
	REJECTED("Your compensation claim has been rejected by your reporting manager.");
	
	private final String displayOvertimeClaimStatus;
	
	// 2. Setting the Constructors
	private OvertimeClaimStatus(String displayLeaveStatus) { this.displayOvertimeClaimStatus = displayLeaveStatus; }

	// 3. Setting the Getters, to enable UI display of Overtime Claim Status 
	public String getDisplayOvertimeClaimStatus() { return displayOvertimeClaimStatus; }

}
