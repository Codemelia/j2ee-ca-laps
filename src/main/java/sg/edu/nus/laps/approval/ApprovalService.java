package sg.edu.nus.laps.approval;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.laps.claim.OvertimeClaim;
import sg.edu.nus.laps.claim.OvertimeClaimService;
import sg.edu.nus.laps.claim.OvertimeClaimStatus;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveRecord;
import sg.edu.nus.laps.leave.model.LeaveStatus;
import sg.edu.nus.laps.leave.repository.LeaveApplicationRepository;
import sg.edu.nus.laps.leave.service.LeaveService;

/**
 *  ApprovalService provides methods for 
*      1. Managing leave approval processes
*      2. Including retrieving pending requests
*      3. Subordinate leave history
*      4. Conflicting leave applications for manager's team
 */
@Service
public class ApprovalService {
    private final LeaveApplicationRepository laRepo;
    private final LeaveService lService; // unidirectional
    private final OvertimeClaimService otService;

    private static final DateTimeFormatter DATE_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd"); // For start end date
    private static final DateTimeFormatter DATETIME_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // For createdAt
    
    public ApprovalService(
        LeaveApplicationRepository laRepo,
        LeaveService lService,
        OvertimeClaimService otService) {
        this.laRepo = laRepo;
        this.lService = lService;
        this.otService = otService;
    }

    // Manager Approval functions

	/**
	 * Retrieves all pending leave applications for a manager's subordinates.
	 * Status: APPLIED or UPDATED Status: APPROVED or REJECTED
	 * 
	 * @param managerId the manager's employee ID
	 * @return list of pending leave applications
	 */
	@Transactional(readOnly = true)
	public List<LeaveApplication> getTeamLeaveRequests(Long managerId, LeaveStatus... statuses) {
		if (statuses == null || statuses.length == 0) {
			return List.of();
		}

		List<LeaveApplication> leaveApps = laRepo.findByEmployeeManagerIdAndStatusIn(managerId,
				Arrays.asList(statuses));
		for (LeaveApplication leave : leaveApps) {
			String typeName = leave.getLeaveType().getLeaveType();
			double days;

			// 3. Route to the correct calculation logic
			if ("Compensation".equalsIgnoreCase(typeName)) {
				// Use the AM/PM aware logic
				days = lService.calcCompDeductibles(leave);
			} else {
				// Use the standard working-day logic for Annual/Medical
				// This still uses the 'old' logic of counting full days
				days = lService.calcLeaveDeductibles(leave.getFromDate(), leave.getToDate());
			}

			// 4. "Share" the count into the duration field in the entity
			leave.setDuration(days);
		}
		return leaveApps;
	}

    /**
     * Retrieves the complete leave history for a specific subordinate.
     * All records are sorted by from date in descending order.
     * 
     * @param employeeId the subordinate's employee ID
     * @return list of all leave applications for the employee
     */
    @Transactional(readOnly = true)
    public List<LeaveApplication> getSubordinateHistory(Long employeeId) {
    	List<LeaveApplication> pendingApplications = laRepo.findByEmployeeIdOrderByFromDateDesc(employeeId);
    	for (LeaveApplication leave : pendingApplications) {
			// Calculate the count
			double days = lService.calcLeaveDeductibles(leave.getFromDate(), leave.getToDate());

			// "Share" the count into the duration field in the entity
			leave.setDuration(days);}
			return pendingApplications;
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
    @Transactional(readOnly = true)
    public List<LeaveApplication> getConflictingLeaves(
        Long managerId, 
		LocalDate fromDate, 
		LocalDate toDate, 
		Long excludeId) {
        return laRepo.findConflictingLeaves(managerId, fromDate, toDate, excludeId);
    }

    /*
    // Retrieve overtime compensation claims for manager's team
    @Transactional(readOnly = true)
    public List<LeaveRecord> getTeamOvertimeClaims(Long managerId, OvertimeClaimStatus... statuses) {
        if (statuses == null || statuses.length == 0) {
            return List.of();
        }

		List<OvertimeClaim> teamClaims = otService.retrieveTeamClaims(managerId, Arrays.asList(statuses));
		for (OvertimeClaim claim : teamClaims) {
			// Calculate the count
			double days = lService.calcCompDeductibles();
		}
        return leaveApps;
    }
     */

    // Process report export in CSV format
    @Transactional(readOnly = true)
    public byte[] processExportRequest(Long managerId, List<Long> leaveAppIdList) {

        // Retrieve leave apps by Leave ID and Manager ID
        List<LeaveApplication> leaveApps = laRepo.findByLeaveAppIdInAndManagerId(leaveAppIdList, managerId);
        if (leaveApps == null || leaveApps.isEmpty()) {
            throw new IllegalArgumentException("Report generation failed on null or empty leave list");
        }

        // Build CSV file
        StringBuilder csvSB = new StringBuilder();
        csvSB.append("Leave ID,Employee ID,Employee Name,Leave Type,Start Date,End Date,Duration in Days,Leave Status,Applied On,Manager Comment")
            .append(System.lineSeparator()); // Separate row

        // For each leave app in list, retrieve rows and append to csv
        for (LeaveApplication leaveApp : leaveApps) {
            double daysDuration = lService.calcLeaveDeductibles(leaveApp.getFromDate(), leaveApp.getToDate());
            appendCsvRow(csvSB, leaveApp, daysDuration);
        }

        return csvSB.toString().getBytes(StandardCharsets.UTF_8);
    }

    // Escape quotes for CSV generation
    // Wraps value in double quotes
    private String csvEscape(String value) {
        if (value == null) { return "\"\""; } // Empty
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    // append csv columns for each row
    private void appendCsvRow(StringBuilder csvSB, LeaveApplication leaveApp, double daysDuration) {
        csvSB.append(leaveApp.getId()).append(',')
            .append(leaveApp.getEmployee().getId()).append(',')
            .append(csvEscape(leaveApp.getEmployee().getFirstName() + " " + leaveApp.getEmployee().getLastName())).append(',')
            .append(csvEscape(leaveApp.getLeaveType() != null ? leaveApp.getLeaveType().getLeaveType() : "")).append(',')
            .append(csvEscape(leaveApp.getFromDate() != null ? leaveApp.getFromDate().format(DATE_FORMAT) : "")).append(',')
            .append(csvEscape(leaveApp.getToDate() != null ? leaveApp.getToDate().format(DATE_FORMAT) : "")).append(',')
            .append(daysDuration).append(',')
            .append(csvEscape(leaveApp.getStatus() != null ? leaveApp.getStatus().name() : "")).append(',')
            .append(csvEscape(leaveApp.getCreatedAt() != null ? leaveApp.getCreatedAt().format(DATETIME_FORMAT) : "")).append(',')
            .append(csvEscape(leaveApp.getManagerComment()))
            .append(System.lineSeparator());
    }

}
