package sg.edu.nus.laps.approval;

import java.util.List;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveStatus;
import sg.edu.nus.laps.leave.repository.LeaveApplicationRepository;

public class ApprovalService {

    private final LeaveApplicationRepository laRepo;
    public ApprovalService(LeaveApplicationRepository laRepo) {
        this.laRepo = laRepo;
    }

    // Manager Approval functions

    /**
     * Retrieves all pending leave applications for a manager's subordinates.
     * Status: APPLIED or UPDATED
     * 
     * @param managerId the manager's employee ID
     * @return list of pending leave applications
     */
    public List<LeaveApplication> getPendingRequests(Long managerId) {
		List<LeaveApplication> pendingApplications = laRepo.findByEmployeeManagerIdAndStatusIn(
			managerId, List.of(LeaveStatus.APPLIED, LeaveStatus.UPDATED));
        return pendingApplications;
    }

    /**
     * Retrieves the complete leave history for a specific subordinate.
     * All records are sorted by from date in descending order.
     * 
     * @param employeeId the subordinate's employee ID
     * @return list of all leave applications for the employee
     */
    public List<LeaveApplication> getSubordinateHistory(Long employeeId) {
        return laRepo.findByEmployeeIdOrderByFromDateDesc(employeeId);
    }

    /**
     * Retrieves conflicting/overlapping leave applications for the manager's team.
     * This helps the manager see how many team members are on leave during a specific period.
     * 
     * @param managerId the manager's ID
     * @param fromDate leave start date
     * @param toDate leave end date
     * @param excludeId leave application ID to exclude from results
     * @return list of approved leave applications that overlap the specified date range
     */
    public List<LeaveApplication> getConflictingLeaves(Long managerId, 
		java.time.LocalDate fromDate, 
		java.time.LocalDate toDate, 
		Long excludeId) {
        return laRepo.findConflictingLeaves(managerId, fromDate, toDate, excludeId);
    }

}
