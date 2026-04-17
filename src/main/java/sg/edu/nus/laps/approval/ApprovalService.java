package sg.edu.nus.laps.approval;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveStatus;
import sg.edu.nus.laps.leave.repository.HolidayRepository;
import sg.edu.nus.laps.leave.repository.LeaveApplicationRepository;

/**
 *  ApprovalService provides methods for 
 *      1. Managing leave approval processes
 *      2. Including retrieving pending requests
 *      3. Subordinate leave history
 *      4. Conflicting leave applications for manager's team
 */
@Service
public class ApprovalService {
	private final HolidayRepository holRepo;
    private final LeaveApplicationRepository laRepo;
    
    public ApprovalService(HolidayRepository holRepo, LeaveApplicationRepository laRepo) {
        this.laRepo = laRepo;
        this.holRepo = holRepo;
    }

    // Manager Approval functions

    /**
     * Retrieves all pending leave applications for a manager's subordinates.
     * Status: APPLIED or UPDATED
     * 
     * @param managerId the manager's employee ID
     * @return list of pending leave applications
     */
    
    //b. Helper Method: isWeekend --> Check if Selected Date falls on SAT or SUN
    private boolean isWeekend(LocalDate date) {
		DayOfWeek day = date.getDayOfWeek();
		return (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
	}
   
    //Helper Method: calcLeaveDeductibles --> If the Total Leave Duration is less than 14 days, weekends and PH should be excluded
	
	private double calcLeaveDeductibles(LocalDate fromDate, LocalDate toDate) {
		List<LocalDate> holidays = holRepo.findAllHolidayDates();
		double dayCounter = 0;
		
		for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
			if (!isWeekend(date) && !holidays.contains(date)) {
				dayCounter ++;
			}
		}
		return dayCounter;
	}
    public List<LeaveApplication> getPendingRequests(Long managerId) {
		List<LeaveApplication> pendingApplications = laRepo.findByEmployeeManagerIdAndStatusIn(
			managerId, List.of(LeaveStatus.APPLIED, LeaveStatus.UPDATED));
		for (LeaveApplication leave : pendingApplications) {
			// Calculate the count
			double days = calcLeaveDeductibles(leave.getFromDate(), leave.getToDate());

			// "Share" the count into the duration field in the entity
			leave.setDuration(days);
		}
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
    	
    	List<LeaveApplication> pendingApplications = laRepo.findByEmployeeIdOrderByFromDateDesc(employeeId);
    	for (LeaveApplication leave : pendingApplications) {
			// Calculate the count
			double days = calcLeaveDeductibles(leave.getFromDate(), leave.getToDate());

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
    public List<LeaveApplication> getConflictingLeaves(
        Long managerId, 
		java.time.LocalDate fromDate, 
		java.time.LocalDate toDate, 
		Long excludeId) {
        return laRepo.findConflictingLeaves(managerId, fromDate, toDate, excludeId);
    }

}
