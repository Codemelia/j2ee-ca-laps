package sg.edu.nus.laps.claim;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.employee.repository.EmployeeRepository;
import sg.edu.nus.laps.leave.model.LeaveRecord;
import sg.edu.nus.laps.leave.model.LeaveType;
import sg.edu.nus.laps.leave.repository.LeaveRecordRepository;
import sg.edu.nus.laps.leave.repository.LeaveTypeRepository;

@Service
public class OvertimeClaimService {

    private static final String COMPENSATION_LEAVE_TYPE = "Compensation";
    private static final double HALF_DAY_COMP_UNITS = 0.5;
    private static final double FULL_DAY_COMP_UNITS = 1.0;

    private final OvertimeClaimRepository claimRepo;
    private final LeaveTypeRepository leaveTypeRepo;
    private final LeaveRecordRepository leaveRecordRepo;
    private final EmployeeRepository empRepo;
    public OvertimeClaimService(
        OvertimeClaimRepository claimRepo,
        LeaveTypeRepository leaveTypeRepo,
        LeaveRecordRepository leaveRecordRepo,
        EmployeeRepository empRepo) {
        this.claimRepo = claimRepo;
        this.leaveTypeRepo = leaveTypeRepo;
        this.leaveRecordRepo = leaveRecordRepo;
        this.empRepo = empRepo;
    }

    // submit claim for approval
    @Transactional
    public OvertimeClaim submitClaim(Long empId, OvertimeClaim claim) {
        if (claim == null) {
            throw new IllegalArgumentException("Overtime claim must not be null");
        }

        validateClaimUnits(claim.getClaimedUnits());

        // Find employee from repo
        Optional<Employee> optEmp = empRepo.findById(empId);
        if (optEmp.isEmpty()) {
            throw new IllegalArgumentException("Employee not found");
        }

        claim.setEmployee(optEmp.get()); // Employee exists
        claim.setStatus(OvertimeClaimStatus.APPLIED); // Set default APPLIED
        return claimRepo.save(claim);
    }

    // Retrieve employee claim history
    @Transactional(readOnly = true)
    public List<OvertimeClaim> retrieveClaimHistory(Long employeeId) {
        if (employeeId == null) { return List.of(); }
        return claimRepo.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
    }

    // Retrieve manager's team claims by status
    @Transactional(readOnly = true)
    public List<OvertimeClaim> retrieveTeamClaims(Long managerId, List<OvertimeClaimStatus> statuses) {
        if (managerId == null || statuses == null || statuses.size() < 1) { return List.of(); }
        return claimRepo.findByEmployeeManagerIdAndStatusInOrderByCreatedAtDesc(
            managerId, statuses);
    }

    // Manager: approve/reject claim
    // Inserts entitled days into leave record
    @Transactional
    public OvertimeClaim processApproveOrRejectClaim(
        Long claimId,
        Long managerId,
        OvertimeClaimStatus targetStatus) {

        // Null checks
        if (claimId == null || managerId == null || targetStatus == null) {
            throw new IllegalArgumentException("Claim ID, manager ID, and target status are required");
        }

        // Retrieve claim by id
        OvertimeClaim claim = claimRepo.findById(claimId)
            .orElseThrow(() -> new IllegalArgumentException("Overtime claim not found"));

        // Null check and whether manager is employee's manager
        if (claim.getEmployee() == null || !managerId.equals(claim.getEmployee().getManagerId())) {
            throw new IllegalArgumentException("Unauthorized to process this overtime claim");
        }

        // Ensure claim is not already processed
        if (claim.getStatus() != OvertimeClaimStatus.APPLIED) {
            throw new IllegalStateException("Only applied claims can be processed");
        }

        // Set status to claim and update
        claim.setStatus(targetStatus);
        OvertimeClaim savedClaim = claimRepo.save(claim);

        // If approved, credit comp entitlement to employee leave record
        if (targetStatus == OvertimeClaimStatus.APPROVED) { creditCompensationLeave(savedClaim); }
        return savedClaim;
    }

    // Helper: credit compensation entitled days to employee leave record
    private void creditCompensationLeave(OvertimeClaim claim) {

        // Retrieve compensation leave type
        LeaveType compensationType = leaveTypeRepo.findByLeaveType(COMPENSATION_LEAVE_TYPE)
            .orElseThrow(() -> new IllegalStateException("Compensation leave type not found"));

        // Retrieve employee id and year
        Long employeeId = claim.getEmployee().getId();
        int claimYear = claim.getWorkedDate().getYear();

        // If current leave record does not exist,
        // Create new leave record
        LeaveRecord leaveRecord = leaveRecordRepo
            .findByEmployeeIdAndLeaveTypeIdAndCalendarYear(employeeId, compensationType.getId(), claimYear)
            .orElseGet(() -> 
                new LeaveRecord(claimYear, 0.0, 0.0, 
                    claim.getEmployee(), compensationType));

        // Convert hours to days and save to record
        double creditedDays = claim.getClaimedUnits();
        leaveRecord.setEntitledDays(leaveRecord.getEntitledDays() + creditedDays);
        leaveRecordRepo.save(leaveRecord);
    }

    private void validateClaimUnits(double claimedUnits) {
        if (claimedUnits != HALF_DAY_COMP_UNITS && claimedUnits != FULL_DAY_COMP_UNITS) {
            throw new IllegalArgumentException("Claimed compensation units must be 0.5 or 1.0 (in days)");
        }
    }

}
