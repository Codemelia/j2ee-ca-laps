package sg.edu.nus.laps.leave;

import org.springframework.stereotype.Service;

import sg.edu.nus.laps.auth.security.AuthUserDetails;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.employee.repository.EmployeeRepository;
import sg.edu.nus.laps.leave.model.LeaveApplication;

@Service
public class LeaveAccessService {

    // private final LeaveApplicationRepository laRepo;
	// private final LeaveTypeRepository ltRepo;
	// private final LeaveRecordRepository lrRepo;
	// private final HolidayRepository holRepo; 
	private final EmployeeRepository empRepo;

	public LeaveAccessService(
			// LeaveApplicationRepository laRepo,
			// LeaveTypeRepository ltRepo,
			// LeaveRecordRepository lrRepo,
			// HolidayRepository holRepo,
			EmployeeRepository empRepo) {
		// this.laRepo = laRepo;
		// this.ltRepo = ltRepo;
		// this.lrRepo = lrRepo;
		// this.holRepo = holRepo;
		this.empRepo = empRepo;
    }
    
    // LEAVE ACCESS METHODS

    public boolean canAccessLeaveDetails(AuthUserDetails user, LeaveApplication leaveApp) {
        Long leaveEmployeeId = leaveApp.getEmployee().getId();
    }

	// Check if user can view leave details page
	public boolean canViewLeave(AuthUserDetails user, LeaveApplication leaveApp) {
		Long leaveEmployeeId = leaveApp.getEmployee().getId();
		Long currViewerId = user.getEmployeeId();
		boolean isSelf = currViewerId != null && currViewerId.equals(leaveEmployeeId);

		// External admin: no access
		if (user.isExternalAdmin()) return false;
		// Internal admin: only self
		if (user.isInternalAdmin() && !isSelf) return false;
		return true;
	}

	// Check if current user is viewing their own leave
	public boolean isSelf(AuthUserDetails user, LeaveApplication leaveApp) {
		Long leaveEmployeeId = leaveApp.getEmployee().getId();
		Long currViewerId = user.getEmployeeId();
		return currViewerId != null && currViewerId.equals(leaveEmployeeId);
	}

	// Get manager name for an employee
	public String getManagerName(Employee employee) {
		Long managerId = employee.getManagerId();
		if (managerId != null) {
			return empRepo.findById(managerId)
				.map(mgr -> mgr.getFirstName() + " " + mgr.getLastName())
				.orElse(null);
		}
		return null;
	}



}
