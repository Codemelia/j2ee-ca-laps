package sg.edu.nus.laps.leave.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.laps.employee.repository.EmployeeRepository;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveStatus;
import sg.edu.nus.laps.leave.repository.HolidayRepository;
import sg.edu.nus.laps.leave.repository.LeaveApplicationRepository;
import sg.edu.nus.laps.leave.repository.LeaveRecordRepository;
import sg.edu.nus.laps.leave.repository.LeaveTypeRepository;

/*
    LeaveService handles all leave CRUD operations (Employee)

                    SERVICE SCOPE
    ------------------------------------------------
    -- READ --
    findAllByEmployeeId(employeeId) - Retrieve list of all leaves by Employee ID
    findByLeaveId(id)               - Retrieve leave application by Leave ID

    -- CREATE / UPDATE --
    save(leaveApplication)          - Create or update a leave application (JPA maps by ID)

    -- CANCEL --
    cancel(id)                      - Cancel a leave application
*/
@Service
public class LeaveService {

	private final LeaveApplicationRepository laRepo;
	private final LeaveTypeRepository ltRepo;
	private final LeaveRecordRepository lrRepo;
	private final HolidayRepository holRepo;
	private final EmployeeRepository empRepo;

	public LeaveService(LeaveApplicationRepository laRepo, LeaveTypeRepository ltRepo, LeaveRecordRepository lrRepo,
			HolidayRepository holRepo, EmployeeRepository empRepo) {
		this.laRepo = laRepo;
		this.ltRepo = ltRepo;
		this.lrRepo = lrRepo;
		this.holRepo = holRepo;
		this.empRepo = empRepo;
	}

	// --- CRUD OPERATIONS ---
	
	/* 
	 * 1. Create Method: saveAsDraft --> Allows User to Soft-Save their Leave Application.
	 * 		Here a Simple Validation will be Done on the fromDate and toDate
	 */
	@Transactional
	public LeaveApplication saveAsDraft(LeaveApplication leave) {
		validateDate(leave.getFromDate(), leave.getToDate());
		leave.setStatus(LeaveStatus.DRAFT);
		return laRepo.save(leave);
	}

	/* 
	 * 2. Update Method: submitLeave --> Allows User to Submit the Leave Application.
	 * 		Here a More Complex Validation is Implemented. In addition to validateDate,
	 * 		the fromDate and toDate will be validate against previously submitted Leave Application
	 * 		(status = 'APPROVED'), regardless of Leave Type. Current/New Leave Application should not 
	 * 		overlap.
	 * 
	 * 		Additionally, if the New Leave Application Start Date is 1-Day after the End-Date of 
	 * 		previously submitted Leave Application (status = "APPROVED"), regardless of Leave Type, the New
	 * 		Leave Application End-Date cannot be more than 14 successive days, inclusive of Weekends and Public Holidays.
	 * 		This is to discourage back-to-back Leave Application.
	 * 
	 * 		If Leave Type is 'Medical', both 'proof' and'reason' ATTR cannot be NULL.
	 */
	@Transactional
	public LeaveApplication submitLeave(LeaveApplication leave) {
		validateDate(leave.getFromDate(), leave.getToDate());
		
		List<LeaveApplication> overlaps = laRepo.findOverlappingApplication(
				leave.getEmployee(), 
				leave.getFromDate(), 
				leave.getToDate());
		if (!overlaps.isEmpty()) {
			throw new RuntimeException("Selected Dates overlap with an Existing Approved Leave Application.");
		}
		
		int dialback = (leave.getFromDate().getDayOfWeek() == DayOfWeek.MONDAY) ? 3 : 1;
		LocalDate dialbackDate = leave.getFromDate().minusDays(dialback);
		List<LeaveApplication> preLeaves = laRepo.findOverlappingApplication(
				leave.getEmployee(), 
				dialbackDate, 
				leave.getFromDate().minusDays(1));
		if (!preLeaves.isEmpty()) {
			LeaveApplication preLeave = preLeaves.get(0);
			long leaveDuration = ChronoUnit.DAYS.between(preLeave.getFromDate(), leave.getToDate()) + 1;
			if (leaveDuration > 14) {
				throw new RuntimeException("Total Back-to-Back Leave Application cannot exceed 14 Successive Calendar Days.");
			}
		}
		
		if ("Medical".equalsIgnoreCase(leave.getLeaveType().getLeaveType())) {
			if ((leave.getReason() == null || leave.getReason().isBlank()) 
					|| (leave.getProof() == null || leave.getProof().isBlank())) {
				throw new RuntimeException("Proof and Reason are mandatory for Medical Leave Application.");
			}
		}
		
		leave.setStatus(LeaveStatus.APPLIED);
		return laRepo.save(leave);
	}
	
	/* 
	 * 3. Update Method: updateLeave --> Allows User to Update Existing Leave Application.
	 * 		This Method is applicable if and only if the Current Leave Status is 'APPLIED' or 'UPDATED'.
	 * 		All ATTRs are Editable except for Leave Type. Here Validation should still be Enforced.
	 */
	@Transactional
	public void updateLeave(LeaveApplication updatedLeave) {
		
		LeaveApplication existingLeave = laRepo.findById(updatedLeave.getId())
				.orElseThrow(() -> new RuntimeException("Leave Application does not Exist."));
		if (existingLeave.getStatus() != LeaveStatus.APPLIED 
				|| existingLeave.getStatus() != LeaveStatus.UPDATED) {
			throw new RuntimeException("Only Leave Application in 'APPLIED' or 'UPDATED' statecan be Edited. "
					+ "Current Status : " + existingLeave.getStatus());
		}
		if (!existingLeave.getLeaveType().getId().equals(updatedLeave.getLeaveType().getId())) {
			throw new RuntimeException("Leave Type cannot be Edited. Please create a New Leave Application instead.");
		}
		
		validateDate(updatedLeave.getFromDate(), updatedLeave.getToDate());
		
		List<LeaveApplication> overlaps = laRepo.findOverlappingApplication(
				updatedLeave.getEmployee(), 
				updatedLeave.getFromDate(), 
				updatedLeave.getToDate());
		boolean trueOverlap = overlaps.stream()
				.anyMatch(l -> !l.getId().equals(updatedLeave.getId()));
		if (trueOverlap) {
			throw new RuntimeException("Updated Date Range overlaps with Existing Approved Leave Application.");
		}
		
		int dialback = (updatedLeave.getFromDate().getDayOfWeek() == DayOfWeek.MONDAY) ? 3 : 1;
		LocalDate dialbackDate = updatedLeave.getFromDate().minusDays(dialback);
		List<LeaveApplication> preLeaves = laRepo.findOverlappingApplication(
				updatedLeave.getEmployee(), 
				dialbackDate, 
				updatedLeave.getFromDate().minusDays(1));
		if (!preLeaves.isEmpty()) {
			LeaveApplication preLeave = preLeaves.get(0);
			long leaveDuration = ChronoUnit.DAYS.between(preLeave.getFromDate(), updatedLeave.getToDate()) + 1;
			if (leaveDuration > 14) {
				throw new RuntimeException("Total Back-to-Back Leave Application cannot exceed 14 Successive Calendar Days.");
			}
		}
		
		if ("Medical".equalsIgnoreCase(updatedLeave.getLeaveType().getLeaveType())) {
			if ((updatedLeave.getReason() == null || updatedLeave.getReason().isBlank()) 
					|| (updatedLeave.getProof() == null || updatedLeave.getProof().isBlank())) {
				throw new RuntimeException("Proof and Reason are mandatory for Medical Leave Application.");
			}
			existingLeave.setProof(updatedLeave.getProof());
			existingLeave.setReason(updatedLeave.getReason());
		}
		
		existingLeave.setFromDate(updatedLeave.getFromDate());
		existingLeave.setToDate(updatedLeave.getToDate());
		existingLeave.setWorkDissemination(updatedLeave.getWorkDissemination());
		existingLeave.setContactDetails(updatedLeave.getContactDetails());
		
		existingLeave.setStatus(LeaveStatus.UPDATED);
		laRepo.save(existingLeave);
	}
	// --- COMPUTATION & LOGIC ---
	/*
	 * a. Helper Method: ValidateDate --> Validate if the fromDate and toDate is in Chronological Order.
	 * 		Validate if the fromDate is later or equals to Today's Date.
	 * 		Validate if the fromDate and toDate is on WEEKDAY
	 */
	private void validateDate(LocalDate fromDate, LocalDate toDate) {
		if (fromDate.isBefore(LocalDate.now())) {
			throw new RuntimeException("Leave Start Date cannot be Earlier than Today's Date.");
		}
		if (fromDate.isAfter(toDate)) {
			throw new RuntimeException("Leave Start Date cannot be Later than Leave End Date.");
		}
		if (isWeekend(fromDate)) {
			throw new RuntimeException("Leave Start Date must be a working day (Monday to Friday).");
		}
		if (isWeekend(toDate)) {
			throw new RuntimeException("Leave End Date must be a working day (Monday to Friday).");
		}
	}
	
	/*
	 * b. Helper Method: isWeekend --> Check if Selected Date falls on SAT or SUN
	 */
	private boolean isWeekend(LocalDate date) {
		DayOfWeek day = date.getDayOfWeek();
		return (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	    
    // Gets recent leave requests
    public List<LeaveApplication> getRecentLeaveApplications(Long employeeId) {
        return laRepo.findTop5ByEmployeeIdOrderByFromDateDesc(employeeId);
    }
	
	// Retrieve Leave App by employee ID
	@Transactional(readOnly = true)
	public List<LeaveApplication> getLeaveApplicationsforEmployee(Long employeeId) {
		return laRepo.findAllByEmployeeId(employeeId);
	}

	@Transactional(readOnly = true)
	public Optional<LeaveApplication> findLeaveById(Long id) {
	// .findById() is built into JpaRepository by default
	// .orElse(null) handles the case where the ID doesn't exist in the DB
		return laRepo.findById(id);
	}

	// // Find Leave App by ID
	// @Transactional(readOnly = true)
	// public Optional<LeaveApplication> findLeaveById(Long id) {
	// 	return laRepo.findById(id);
	// }

	// Check if ID exists
	@Transactional(readOnly = true)
	public boolean existsByLeaveId(Long id) {
		return laRepo.existsById(id);
	}


	// For deleting an 'Applied' leave
	@Transactional
	public void deleteLeave(Long id) {
		LeaveApplication leave = laRepo.findById(id).orElse(null);
		if (leave != null && leave.getStatus() == LeaveStatus.APPLIED) {
			leave.setStatus(LeaveStatus.DELETED);
			laRepo.save(leave);
		}
	}

	// COMPUTATION

	// 1. The Calculation Logic
	public int calculateActualLeaveDays(LocalDate start, LocalDate end, List<LocalDate> holidays) {
		int count = 0;
		LocalDate curr = start;
		while (!curr.isAfter(end)) {
			DayOfWeek day = curr.getDayOfWeek();
			boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
			boolean isHoliday = holidays.contains(curr);
			if (!isWeekend && !isHoliday) {
				count++;
			}
			curr = curr.plusDays(1);
		}
		return count;
	}

	// 2. The Retrieval Logic (Sharing the count into the entity)
	@Transactional(readOnly = true)
	public List<LeaveApplication> getEmployeeLeaveHistory(Long employeeId) {
		List<LeaveApplication> history = laRepo.findByEmployeeIdOrderByFromDateDesc(employeeId);
		List<LocalDate> holidays = holRepo.findAllHolidayDates();

		for (LeaveApplication leave : history) {
			// Calculate the count
			double days = calculateActualLeaveDays(leave.getFromDate(), leave.getToDate(), holidays);

			// "Share" the count into the duration field in the entity
			leave.setDuration(days);
		}
		return history;

	}

	// RULE: Only 'APPROVED' leaves can be 'CANCELLED'
	@Transactional
	public void cancelLeave(Long id) {
		LeaveApplication leaveApplication = laRepo.findById(id).orElse(null);
		if (leaveApplication != null && leaveApplication.getStatus() == LeaveStatus.APPROVED) {
			leaveApplication.setStatus(LeaveStatus.CANCELLED);
			laRepo.save(leaveApplication);
		}
	}
}