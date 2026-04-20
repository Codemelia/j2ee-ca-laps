package sg.edu.nus.laps.leave.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.employee.model.EmployeeRank;
import sg.edu.nus.laps.employee.repository.EmployeeRepository;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveRecord;
import sg.edu.nus.laps.leave.model.LeaveStatus;
import sg.edu.nus.laps.leave.model.LeaveType;
import sg.edu.nus.laps.leave.repository.HolidayRepository;
import sg.edu.nus.laps.leave.repository.LeaveApplicationRepository;
import sg.edu.nus.laps.leave.repository.LeaveRecordRepository;
import sg.edu.nus.laps.leave.repository.LeaveTypeRepository;

/*
    LeaveService handles all leave CRUD operations
	as well as complex computation methods that are
	shared between features involving leaves
*/
@Service
public class LeaveService {

	private final LeaveApplicationRepository laRepo;
	private final LeaveTypeRepository ltRepo;
	private final LeaveRecordRepository lrRepo;
	private final HolidayRepository holRepo;
	private final EmployeeRepository empRepo;

	public LeaveService(LeaveApplicationRepository laRepo, 
		LeaveRecordRepository lrRepo, HolidayRepository holRepo,
		LeaveTypeRepository ltRepo, EmployeeRepository empRepo) {
		this.laRepo = laRepo;
		this.lrRepo = lrRepo;
		this.holRepo = holRepo;
		this.ltRepo = ltRepo;
		this.empRepo = empRepo;
	}

	// --- CRUD OPERATIONS ---
	
	// /* 
	//  * 1. Create Method: saveAsDraft --> Allows User to Soft-Save their Leave Application.
	//  * 		Here slight validation will be done to ensure data integrity
	//  */
	@Transactional
	public LeaveApplication saveAsDraft(Long empId, LeaveApplication leave) {
		Employee employee = validateAndRetrieveEmployee(empId);
		LeaveType leaveType = validateAndRetrieveLeaveType(leave.getLeaveTypeId());
		leave.setEmployee(employee);
        leave.setLeaveType(leaveType);
		validateDate(leave);
		leave.setStatus(LeaveStatus.DRAFT);
		return laRepo.save(leave);
	}

	/* 
	 * 2. Update Method: submitLeave --> Allows User to Submit the Leave Application.
	 * 		Here a More Complex Validation is Implemented. In addition to validateDate, if the 
	 * 		Current Leave Type is'ANNUAL', the fromDate and toDate will be validate against 
	 * 		previously submitted Leave Application (leaveType = 'ANNUAL' AND status = 'APPROVED'). 
	 * 		Current/New Leave Application should not overlap.
	 * 
	 * 		If Leave Type is 'Medical', both 'proof' and'reason' ATTR cannot be NULL.
	 */
	@Transactional
	public LeaveApplication submitLeave(Long empId, LeaveApplication leave) {
		Employee employee = validateAndRetrieveEmployee(empId);
		LeaveType leaveType = validateAndRetrieveLeaveType(leave.getLeaveTypeId());
		leave.setEmployee(employee);
		leave.setLeaveType(leaveType);
		validateDate(leave);
		validateMedicalLeave(leave);
		
		List<LeaveApplication> overlaps = laRepo.findOverlappingApplication(
				List.of(LeaveStatus.DRAFT, 
					LeaveStatus.REJECTED, 
					LeaveStatus.CANCELLED, 
					LeaveStatus.DELETED),
				leave.getEmployee(), 
				leave.getFromDate(), 
				leave.getToDate());
		if (!overlaps.isEmpty()) {
			throw new RuntimeException("Selected Dates overlap with an Existing Approved Leave Application.");
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
	public void updateLeave(Long empId, LeaveApplication updatedLeave) {
		if (empId == null) { throw new RuntimeException("Invalid Employee Profile"); }
		
		LeaveApplication existingLeave = laRepo.findById(updatedLeave.getId())
				.orElseThrow(() -> new RuntimeException("Leave Application does not Exist."));
		if (existingLeave.getStatus() != LeaveStatus.APPLIED 
			&& existingLeave.getStatus() != LeaveStatus.UPDATED) {
			throw new RuntimeException("Only Leave Application in 'APPLIED' or 'UPDATED' state can be Edited. "
				+ "Current Status : " + existingLeave.getStatus());
		}
		if (updatedLeave.getLeaveTypeId() != null
				&& !existingLeave.getLeaveType().getId().equals(updatedLeave.getLeaveTypeId())) {
			throw new RuntimeException("Leave Type cannot be Edited. Please create a New Leave Application instead.");
		}

		verifySelfIdentity(empId, existingLeave);
		updatedLeave.setEmployee(existingLeave.getEmployee());
		updatedLeave.setLeaveType(existingLeave.getLeaveType());
		validateDate(updatedLeave);
		validateMedicalLeave(updatedLeave);
		
		List<LeaveApplication> overlaps = laRepo.findOverlappingApplication(
				List.of(LeaveStatus.REJECTED, LeaveStatus.CANCELLED, LeaveStatus.DELETED),
				updatedLeave.getEmployee(), 
				updatedLeave.getFromDate(), 
				updatedLeave.getToDate());
		boolean trueOverlap = overlaps.stream()
				.anyMatch(l -> !l.getId().equals(updatedLeave.getId()));
		if (trueOverlap) {
			throw new RuntimeException("Updated Date Range overlaps with Existing Approved Leave Application.");
		}
		
		existingLeave.setFromDate(updatedLeave.getFromDate());
		existingLeave.setToDate(updatedLeave.getToDate());
		existingLeave.setProof(updatedLeave.getProof());
		existingLeave.setReason(updatedLeave.getReason());
		existingLeave.setWorkDissemination(updatedLeave.getWorkDissemination());
		existingLeave.setContactDetails(updatedLeave.getContactDetails());
		
		existingLeave.setStatus(LeaveStatus.UPDATED);
		laRepo.save(existingLeave);
	}
	
	/* 
	 * 4. Delete Method: deleteLeave --> Allows User to Soft-Delete Existing Leave Application.
	 * 		This Method is applicable if and only if the Current Leave Status is 'APPLIED' or 'UPDATED'.
	 * 		Once deleted, the record remains in the DB but is locked from further edits or approval.
	 */
	@Transactional
	public void deleteLeave(Long leaveId, Long empId) {

		LeaveApplication leave = laRepo.findById(leaveId)
			.orElseThrow(() -> new RuntimeException("Leave Application does not Exist."));
		
		verifySelfIdentity(empId, leave);

		if (leave.getStatus() != LeaveStatus.APPLIED 
			&& leave.getStatus() != LeaveStatus.UPDATED
			&& leave.getStatus() != LeaveStatus.DRAFT) {
			throw new RuntimeException("Only Leave Application in 'DRAFT', 'APPLIED' or'UPDATED' state can be DELETED. "
				+ "Current Status : " + leave.getStatus());
		}
		
		leave.setStatus(LeaveStatus.DELETED);
		laRepo.save(leave);
	}

	/*
	 * 5. Update Method: processApproveOrRejectLeave --> Allows Manager to Approve or Reject Existing Leave Application.
	 * 		This Method is applicable if and only if the Current Leave Status is 'APPLIED' or 'UPDATED'.
	 * 		Once 'APPROVED', the 14-Day Successive Rules Applies, where if the leave period span over 14 calendar days,
	 * 		then, both weekends and public holidays are included, otherwise they are excluded in the Effective Leave Duration
	 * 		period. Additional Validation includes
	 * 		Year Crossover Leave Application.
	 */
	@Transactional
	public void processApproveOrRejectLeave(Long id, LeaveStatus decision, String managerComment) {
		LeaveApplication leave = laRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Leave Application does not Exist."));
		
		if (leave.getStatus() != LeaveStatus.APPLIED && leave.getStatus() != LeaveStatus.UPDATED) {
			throw new RuntimeException("Leave Application has been Processed. Current Status: " + leave.getStatus());
		}
		
		if (decision == LeaveStatus.REJECTED) {
			if (managerComment == null || managerComment.isBlank()) {
				throw new RuntimeException("Manager Comment is Mandatory for Rejection of Leave Application.");
				}
			leave.setStatus(LeaveStatus.REJECTED);
		} else if (decision == LeaveStatus.APPROVED) {
			runApproveLeaveBizRules(leave);
			leave.setStatus(LeaveStatus.APPROVED);			
		}
		
		leave.setManagerComment(managerComment);
		laRepo.save(leave);
	}

	/*
	 * 6. Update Method: cancelLeave --> Allows User to Cancel an already APPROVED Leave Application.
	 * 		Cancellation is only permitted if the start date hasn't passed.
	 */
	@Transactional
	public void cancelLeave(Long leaveId, Long empId) {
		LeaveApplication leave = laRepo.findById(leaveId)
			.orElseThrow(() -> new RuntimeException("Leave Application does not Exist."));

		verifySelfIdentity(empId, leave);

		if (leave.getStatus() != LeaveStatus.APPROVED) {
			throw new RuntimeException("Only APPROVED applications can be cancelled.");
		}

		if (LocalDate.now().isAfter(leave.getFromDate())) {
			throw new RuntimeException("Cannot cancel leave that has already started.");
		}

		double amountToRestore = calculateEffectiveDurationForRestore(leave);

		reverseDeduction(leave, amountToRestore);

		leave.setStatus(LeaveStatus.CANCELLED);
		laRepo.save(leave);
	}

	// Verification + Validation helpers

	// Helper method: Verify employee is performing delete/cancel/update for themselves
	private void verifySelfIdentity(Long empId, LeaveApplication leave) {
		// Check if employee id matches records
		if (!empId.equals(leave.getEmployee().getId())) {
			throw new RuntimeException("Leave Application can only be deleted by requester");
		}
	}

	// Helper method: validateAndRetrieveEmployee --> Null / Exists Employee check
	private Employee validateAndRetrieveEmployee(Long empId) {
		if (empId == null) { throw new RuntimeException("Invalid Employee Profile"); }
		return empRepo.findById(empId)
			.orElseThrow(() -> new RuntimeException("Employee Profile does not exist"));
	}

	// Helper method: ValidateLeaveType --> Null / Exists LeaveType check
	private LeaveType validateAndRetrieveLeaveType (Long leaveTypeId) {
		if (leaveTypeId == null) { throw new RuntimeException("Invalid Leave Type"); }
		return ltRepo.findById(leaveTypeId)
			.orElseThrow(() -> new RuntimeException("Leave Type does not exist"));
	}

	// Helper method: ValidateMedicalLeave --> Validate fields required for Medical Leave
	private void validateMedicalLeave(LeaveApplication leave) {
		if ("Medical".equalsIgnoreCase(leave.getLeaveType().getLeaveType())) {
			if ((leave.getReason() == null || leave.getReason().isBlank()) 
					|| (leave.getProof() == null || leave.getProof().isBlank())) {
				throw new RuntimeException("Proof and Reason are mandatory for Medical Leave Application.");
			}
		}
	}

	/*
	 * a. Helper Method: ValidateDate --> Validate if the fromDate and toDate is in Chronological Order.
	 * 		Validate if the fromDate is later or equals to Today's Date.
	 * 		Validate if the fromDate and toDate is on WEEKDAY
	 */
	private void validateDate(LeaveApplication leave) {
		LocalDate fromDate = leave.getFromDate();
		LocalDate toDate = leave.getToDate();
		String typeName = leave.getLeaveType().getLeaveType();
		
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
		if (("Annual".equalsIgnoreCase(typeName) || "Medical".equalsIgnoreCase(typeName)) 
				&& leave.isHalfDay()) {
			throw new RuntimeException("Half-Day are only Permitted for Compensation Leave.");
		}
	}

	// Computation + Logic helpers
	
	/*
	 * b. Helper Method: isWeekend --> Check if Selected Date falls on SAT or SUN
	 */
	private boolean isWeekend(LocalDate date) {
		DayOfWeek day = date.getDayOfWeek();
		return (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
	}
	
	/*
	 * c. Helper Method: runApproveLeaveBizRules --> Validate for Back-to-Back Leave Applications.
	 * 		Determine the Effective Leave Application Duration.
	 * 		Verify for Crossover Year Leave Application. 
	 */
	private void runApproveLeaveBizRules(LeaveApplication leave) {
		String leaveType = leave.getLeaveType().getLeaveType();
		long currentLeaveDuration = ChronoUnit.DAYS.between(leave.getFromDate(), leave.getToDate()) + 1;
		double effectiveLeaveDuration = 0;
		
		// 1. Computing Effective Leave Duration
		if ("Medical".equalsIgnoreCase(leaveType)) {
			effectiveLeaveDuration = (double) currentLeaveDuration;
		} else if ("Annual".equalsIgnoreCase(leaveType)) {
			
			long totalLeaveChainSpan = getCombinedLeaveChainSpan(leave, currentLeaveDuration);
			
			if (currentLeaveDuration > 14) {
				effectiveLeaveDuration = (double) currentLeaveDuration;
			} else if (totalLeaveChainSpan > 14) {
				effectiveLeaveDuration = (double) totalLeaveChainSpan - getAlreadyDeductionInChain(leave);
			} else {
				effectiveLeaveDuration = calcLeaveDeductibles(leave.getFromDate(), leave.getToDate());
			}
		} else if ("Compensation".equalsIgnoreCase(leaveType)) {
				effectiveLeaveDuration = leave.isHalfDay() ? 0.5 : calcLeaveDeductibles(leave.getFromDate(), leave.getToDate());
		}
		
		applyDeduction(leave, effectiveLeaveDuration);
	}

	/*
	 * d. Helper Method: getCombinedLeaveChainSpan --> Find the Total Calendar Days for Chained Leave
	 */
	private long getCombinedLeaveChainSpan(LeaveApplication leave, Long currentLeaveDuration) {
		// --> Check if there is an existing leave application just before the new leave application starts
		int dialback = (leave.getFromDate().getDayOfWeek() == DayOfWeek.MONDAY) ? 3 : 1;
		LocalDate backBridge = leave.getFromDate().minusDays(dialback);
		List<LeaveApplication> preLeaves = laRepo.findOverlappingApplication(
				List.of(LeaveStatus.REJECTED, LeaveStatus.CANCELLED, LeaveStatus.DELETED), leave.getEmployee(),
				backBridge, leave.getFromDate().minusDays(1));

		// --> Check if there is an existing leave application just after the new leave application ends
		int dialForward = (leave.getToDate().getDayOfWeek() == DayOfWeek.FRIDAY) ? 3 : 1;
		LocalDate forwardBridge = leave.getToDate().plusDays(dialForward);
		List<LeaveApplication> postLeaves = laRepo.findOverlappingApplication(
				List.of(LeaveStatus.REJECTED, LeaveStatus.CANCELLED, LeaveStatus.DELETED), leave.getEmployee(),
				leave.getToDate().plusDays(1), forwardBridge);

		long totalLeaveDuration = currentLeaveDuration;
		
		// --> Validate if the 'ANNUAL' leaves are chained
		boolean hasPreAnnual = !preLeaves.isEmpty()
				&& "Annual".equalsIgnoreCase(preLeaves.get(0).getLeaveType().getLeaveType());
		boolean hasPostAnnual = !postLeaves.isEmpty()
				&& "Annual".equalsIgnoreCase(postLeaves.get(0).getLeaveType().getLeaveType());

		if (hasPreAnnual) {
			return ChronoUnit.DAYS.between(preLeaves.get(0).getFromDate(), leave.getToDate()) + 1;
		} else if (hasPostAnnual) {
			return ChronoUnit.DAYS.between(leave.getFromDate(), postLeaves.get(0).getToDate()) + 1;
		}
		return totalLeaveDuration;
	}

	/*
	 * e. Helper Method: getAlreadyDeductionInChain --> Identifies the deduction already taken by the "Other" half of a chain
	 */
	private double getAlreadyDeductionInChain(LeaveApplication leave) {
		// --> Check if there is an existing leave application just before the new leave application starts
		int dialback = (leave.getFromDate().getDayOfWeek() == DayOfWeek.MONDAY) ? 3 : 1;
		LocalDate backBridge = leave.getFromDate().minusDays(dialback);
		List<LeaveApplication> preLeaves = laRepo.findOverlappingApplication(
				List.of(LeaveStatus.REJECTED, LeaveStatus.CANCELLED, LeaveStatus.DELETED), leave.getEmployee(),
				backBridge, leave.getFromDate().minusDays(1));

		// --> Check if there is an existing leave application just after the new leave application ends
		int dialForward = (leave.getToDate().getDayOfWeek() == DayOfWeek.FRIDAY) ? 3 : 1;
		LocalDate forwardBridge = leave.getToDate().plusDays(dialForward);
		List<LeaveApplication> postLeaves = laRepo.findOverlappingApplication(
				List.of(LeaveStatus.REJECTED, LeaveStatus.CANCELLED, LeaveStatus.DELETED), leave.getEmployee(),
				leave.getToDate().plusDays(1), forwardBridge);

		// --> Validate if the 'ANNUAL' leaves are chained
		boolean hasPreAnnual = !preLeaves.isEmpty()
				&& "Annual".equalsIgnoreCase(preLeaves.get(0).getLeaveType().getLeaveType());
		boolean hasPostAnnual = !postLeaves.isEmpty()
				&& "Annual".equalsIgnoreCase(postLeaves.get(0).getLeaveType().getLeaveType());

		if (hasPreAnnual) {
			return calcLeaveDeductibles(preLeaves.get(0).getFromDate(), preLeaves.get(0).getToDate());
		} else if (hasPostAnnual) {
			return calcLeaveDeductibles(postLeaves.get(0).getFromDate(), postLeaves.get(0).getToDate());
		}
		return 0;
	}
	/*
	 * e. Helper Method: calcLeaveDeductibles --> If the Total Leave Duration is less than 14 days, weekends and PH should be excluded
	 */
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

	/*
	 * f. Helper Method: applyDeduction --> Translate the Calculated Leave Deductible into No. of Consumed Days
	 */
	private void applyDeduction(LeaveApplication leave, double effectiveLeaveDuration) {
		int fromYear = leave.getFromDate().getYear();
		int toYear = leave.getToDate().getYear();
		
		if (fromYear == toYear) {
			updateLeaveRecordwithJITInject(leave.getEmployee(), leave.getLeaveType(), fromYear, effectiveLeaveDuration);
		} else {
			LocalDate lastDay = LocalDate.of(fromYear, 12, 31);
			LocalDate firstDay = LocalDate.of(toYear, 1, 1);
			
			double leaveY1;
			double leaveY2;
			
			long totalCalendarSpan = ChronoUnit.DAYS.between(leave.getFromDate(), leave.getToDate()) + 1;
			
			if (totalCalendarSpan > 14 || "Medical".equalsIgnoreCase(leave.getLeaveType().getLeaveType())) {
				leaveY1 = (double) ChronoUnit.DAYS.between(leave.getFromDate(), lastDay) + 1;
				leaveY2 = effectiveLeaveDuration - leaveY1;
			} else {
				leaveY1 = calcLeaveDeductibles(leave.getFromDate(), lastDay);
				leaveY2 = calcLeaveDeductibles(firstDay, leave.getToDate());
			}
			
			updateLeaveRecordwithJITInject(leave.getEmployee(), leave.getLeaveType(), fromYear, leaveY1);
			updateLeaveRecordwithJITInject(leave.getEmployee(), leave.getLeaveType(), toYear, leaveY2);
		}
	}

	/*
	 * g. Helper Method: updateLeaveRecordwithJITInject --> Update the Leave Record DB
	 */
	private void updateLeaveRecordwithJITInject(Employee employee, LeaveType leaveType, int year, double leaveDays) {
		LeaveRecord lr = lrRepo.findByEmployeeIdAndLeaveTypeIdAndCalendarYear(employee.getId(), leaveType.getId(), year)
				.orElseGet(() -> initializeNewYearLeaveRecord(employee, leaveType, year));
		
		double availableLeave = lr.getEntitledDays() - lr.getConsumedDays();
		if (availableLeave < leaveDays) {
			throw new RuntimeException("Insufficient balance for " + year + ". Available: " + availableLeave);
		}
		
		lr.setConsumedDays(lr.getConsumedDays() + leaveDays);
		lrRepo.save(lr);
	}
	
	/*
	 * h. Helper Method: initializeNewYearLeaveRecord --> JIT Creation of Leave Record for the following Year
	 */
	private LeaveRecord initializeNewYearLeaveRecord(Employee employee, LeaveType leaveType, int year) {
		LeaveRecord newLeaveRecord = new LeaveRecord();
		newLeaveRecord.setEmployee(employee);
		newLeaveRecord.setLeaveType(leaveType);
		newLeaveRecord.setCalendarYear(year);
		newLeaveRecord.setConsumedDays(0.0);
		
		double entitledDays = 0;
		String typeName = leaveType.getLeaveType();
		
		if ("Annual".equalsIgnoreCase(typeName)) {
			// Try to retrieve from previous year
			LeaveRecord prevLeaveRecord = lrRepo
				.findByEmployeeIdAndLeaveTypeIdAndCalendarYear(
					employee.getId(), 1L, year - 1)
				.orElse(null);

			if (prevLeaveRecord != null) {
				entitledDays = prevLeaveRecord.getEntitledDays();
			} else {
				if (employee.getRank() == EmployeeRank.PROFESSIONAL) {
					entitledDays = 18.0;
				} else {
					entitledDays = 14.0;
				}
			}
		} else if ("Medical".equalsIgnoreCase(typeName)) {
			entitledDays = 60.0;
		}
		
		newLeaveRecord.setEntitledDays(entitledDays);
		return lrRepo.save(newLeaveRecord);		
	}

	/*
	 * i. Helper Method: calculateEffectiveDurationForRestore --> Re-runs the
	 * business rules to find the original deduction value.
	 */
	private double calculateEffectiveDurationForRestore(LeaveApplication leave) {
		String leaveType = leave.getLeaveType().getLeaveType();
		long currentLeaveDuration = ChronoUnit.DAYS.between(leave.getFromDate(), leave.getToDate()) + 1;

		if ("Medical".equalsIgnoreCase(leaveType)) {
			return (double) currentLeaveDuration;
		}

		if ("Annual".equalsIgnoreCase(leaveType)) {
			long totalLeaveChainSpan = getCombinedLeaveChainSpan(leave, currentLeaveDuration);

			if (currentLeaveDuration > 14) {
				return (double) currentLeaveDuration;
			} else if (totalLeaveChainSpan > 14) {
				return (double) totalLeaveChainSpan - getAlreadyDeductionInChain(leave);
			}
		}

		return calcLeaveDeductibles(leave.getFromDate(), leave.getToDate());
	}

	/*
	 * j. Helper Method: reverseDeduction --> Restores balance to LeaveRecord, handling crossover years.
	 */
	private void reverseDeduction(LeaveApplication leave, double leaveDays) {
		int fromYear = leave.getFromDate().getYear();
		int toYear = leave.getToDate().getYear();

		if (fromYear == toYear) {
			restoreBalance(leave.getEmployee(), leave.getLeaveType(), fromYear, leaveDays);
		} else {
			LocalDate lastDay = LocalDate.of(fromYear, 12, 31);

			double restoreY1;
			double restoreY2;

			long totalLeaveDuration = ChronoUnit.DAYS.between(leave.getFromDate(), leave.getToDate()) + 1;

			if (totalLeaveDuration > 14 || "Medical".equalsIgnoreCase(leave.getLeaveType().getLeaveType())) {
				restoreY1 = (double) ChronoUnit.DAYS.between(leave.getFromDate(), lastDay) + 1;
				restoreY2 = leaveDays - restoreY1;
			} else {
				restoreY1 = calcLeaveDeductibles(leave.getFromDate(), lastDay);
				restoreY2 = leaveDays - restoreY1;
			}

			restoreBalance(leave.getEmployee(), leave.getLeaveType(), fromYear, restoreY1);
			restoreBalance(leave.getEmployee(), leave.getLeaveType(), toYear, restoreY2);
		}
	}
	
	private void restoreBalance(Employee employee, LeaveType leaveType, int year, double leaveDays) {
		LeaveRecord lr = lrRepo.findByEmployeeIdAndLeaveTypeIdAndCalendarYear(employee.getId(), leaveType.getId(), year)
				.orElseThrow(() -> new RuntimeException("Leave Record not found for reversal."));
		
		lr.setConsumedDays(lr.getConsumedDays() - leaveDays);
		lrRepo.save(lr);
	}

	// --- Dash-Board Builder Methods ---
	// 1. Gets Recent Leave Applications
	@Transactional(readOnly=true)
	public List<LeaveApplication> getRecentLeaveApplications(Long employeeId) {
		
		return laRepo.findTop5ByEmployeeIdOrderByUpdatedAtDesc(employeeId);
	}
	
	// 2. Retrieve Leave Applications by employee ID
	@Transactional(readOnly = true)
	public List<LeaveApplication> getLeaveApplicationsforEmployee(Long employeeId) {
		return laRepo.findAllByEmployeeId(employeeId);
	}

	// 3. Retrieve Leave Application by Leave Application ID
	@Transactional(readOnly = true)
	public Optional<LeaveApplication> findLeaveById(Long id) {
		return laRepo.findById(id);
	}

	// 4. Validate if Leave Application ID exists
	@Transactional(readOnly = true)
	public boolean existsByLeaveId(Long id) {
		return laRepo.existsById(id);
	}
	
	// 5. The Retrieval Logic (Sharing the count into the entity)
	@Transactional(readOnly = true)
	public List  <LeaveApplication> getEmployeeLeaveHistory(Long employeeId ) {
		// 1. Get current year 
	    int currentYear = LocalDate.now().getYear();
		List<LeaveApplication> history = laRepo.findByEmployeeIdAndYear(employeeId, currentYear);
		// List<LocalDate> holidays = holRepo.findAllHolidayDates();

		for (LeaveApplication leave : history) {
			// Calculate the count
			double days = calcLeaveDeductibles(leave.getFromDate(), leave.getToDate());

			// "Share" the count into the duration field in the entity
			leave.setDuration(days);
		}
		return history;

	}

}