package sg.edu.nus.laps.approval;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveStatus;
import sg.edu.nus.laps.leave.repository.LeaveApplicationRepository;

@Service
public class ApprovalService {

    private final LeaveApplicationRepository leaveRepo;

    public ApprovalService(LeaveApplicationRepository leaveRepo) {
        this.leaveRepo = leaveRepo;
    }

    /**
     * Retrieves all pending leave applications for a manager's subordinates 
     * (Status: APPLIED or UPDATED).
     */
    public List<LeaveApplication> getPendingRequests(Long managerId) {
        // Fetch applications with status APPLIED and UPDATED separately and merge them
        List<LeaveApplication> applied = leaveRepo.findByEmployeeManagerIdAndStatus(managerId, LeaveStatus.APPLIED);
        List<LeaveApplication> updated = leaveRepo.findByEmployeeManagerIdAndStatus(managerId, LeaveStatus.UPDATED);
        applied.addAll(updated);
        return applied;
    }

    /**
     * Retrieves the complete leave history for a specific subordinate.
     */
    public List<LeaveApplication> getSubordinateHistory(Long employeeId) {
        return leaveRepo.findByEmployeeIdOrderByFromDateDesc(employeeId);
    }

    /**
     * Finds a specific leave application by its ID.
     */
    public Optional<LeaveApplication> findLeaveById(Long id) {
        return leaveRepo.findById(id);
    }

    /**
     * Updates the leave application status to APPROVED.
     */
   @Transactional
public void approveRequest(Long leaveId) {
    leaveRepo.findById(leaveId).ifPresent(l -> {
        // 1. Update Status
        l.setStatus(LeaveStatus.APPROVED);
        
        // 2. Logic: Update Leave Balance (Consumed Days)
        // Retrieve the record for this specific year and leave type
        int year = l.getFromDate().getYear();
        Optional<LeaveRecord> recordOpt = lrRepo.findByEmployeeIdAndLeaveTypeIdAndCalendarYear(
            l.getEmployee().getId(), l.getLeaveType().getId(), year);

        if (recordOpt.isPresent()) {
            LeaveRecord record = recordOpt.get();
            double daysToDeduct = calculateActualLeaveDays(l);
            record.setConsumedDays(record.getConsumedDays().add(BigDecimal.valueOf(daysToDeduct)));
            lrRepo.save(record);
        }
        
        leaveRepo.save(l);
    });
}

// Helper method to implement the <= 14 days rule
private double calculateActualLeaveDays(LeaveApplication l) {
    long totalDays = ChronoUnit.DAYS.between(l.getFromDate(), l.getToDate()) + 1;
    
    // Requirement: If <= 14 days, exclude weekends/public holidays
    if (totalDays <= 14) {
        // call a utility here that checks against your 'holidays' table and skips Saturdays/Sundays
        return countWorkingDays(l.getFromDate(), l.getToDate());
    }
    return (double) totalDays;
}

    /**
     * Updates the leave application status to REJECTED and adds a manager's comment.
     */
    @Transactional
    public void rejectRequest(Long leaveId, String comment) {
        leaveRepo.findById(leaveId).ifPresent(l -> {
            l.setStatus(LeaveStatus.REJECTED);
            l.setManagerComment(comment);
            leaveRepo.save(l);
        });
    }
}
