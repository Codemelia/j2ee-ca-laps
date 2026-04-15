package sg.edu.nus.laps.leave.dto;

import sg.edu.nus.laps.leave.model.LeaveRecord;

public class NewAlEntitlement {
	private LeaveRecord newNonExecAnnual;
	private LeaveRecord newProAnnual;
	
	public NewAlEntitlement() {
		this.newNonExecAnnual = new LeaveRecord();
		this.newProAnnual = new LeaveRecord();
	}

	public LeaveRecord getNewNonExecAnnual() {
		return newNonExecAnnual;
	}

	public void setNewNonExecAnnual(LeaveRecord newNonExecAnnual) {
		this.newNonExecAnnual = newNonExecAnnual;
	}

	public LeaveRecord getNewProAnnual() {
		return newProAnnual;
	}

	public void setNewProAnnual(LeaveRecord newProAnnual) {
		this.newProAnnual = newProAnnual;
	}
	
	
}
