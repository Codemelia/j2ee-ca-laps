// package sg.edu.nus.laps.approval;

// import java.util.List;
// import java.util.Optional;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import sg.edu.nus.laps.leave.model.LeaveApplication;
// import sg.edu.nus.laps.leave.model.LeaveStatus;
// import sg.edu.nus.laps.leave.repository.LeaveApplicationRepository;
// import sg.edu.nus.laps.leave.service.LeaveService;

/**
 * ApprovalService handles manager-specific leave approval workflow.
 * It delegates actual leave processing to LeaveService to avoid code duplication.
 * 
 * Responsibilities:
 * - Retrieve pending leave requests for a manager's team
 * - Retrieve subordinate leave history
 * - Delegate approval/rejection to LeaveService
 * - Display leave details with team conflict information
 */
// @Service
// public class ApprovalService {

//     private final LeaveApplicationRepository leaveRepo;
//     private final LeaveService leaveService;

//     public ApprovalService(LeaveApplicationRepository leaveRepo, 
//                           LeaveService leaveService) {
//         this.leaveRepo = leaveRepo;
//         this.leaveService = leaveService;
//     }

//     /**
//      * Retrieves all pending leave applications for a manager's subordinates.
//      * Status: APPLIED or UPDATED
//      * 
//      * @param managerId the manager's employee ID
//      * @return list of pending leave applications
//      */
//     public List<LeaveApplication> getPendingRequests(Long managerId) {
//         List<LeaveApplication> applied = leaveRepo.findByEmployeeManagerIdAndStatus(managerId, LeaveStatus.APPLIED);
//         List<LeaveApplication> updated = leaveRepo.findByEmployeeManagerIdAndStatus(managerId, LeaveStatus.UPDATED);
//         applied.addAll(updated);
//         return applied;
//     }

//     /**
//      * Retrieves the complete leave history for a specific subordinate.
//      * All records are sorted by from date in descending order.
//      * 
//      * @param employeeId the subordinate's employee ID
//      * @return list of all leave applications for the employee
//      */
//     public List<LeaveApplication> getSubordinateHistory(Long employeeId) {
//         return leaveRepo.findByEmployeeIdOrderByFromDateDesc(employeeId);
//     }

//     /**
//      * Finds a specific leave application by its ID.
//      * 
//      * @param id the leave application ID
//      * @return Optional containing the leave application if found
//      */
//     public Optional<LeaveApplication> findLeaveById(Long id) {
//         return leaveService.findLeaveById(id);
//     }

//     /**
//      * Approves a leave application.
//      * Delegates to LeaveService to handle complex business rules including:
//      * - Leave duration calculation
//      * - Leave balance deduction
//      * - Year crossover handling
//      * - Back-to-back leave chain calculation
//      * 
//      * @param leaveId the leave application ID to approve
//      * @throws RuntimeException if approval fails
//      */
//     @Transactional
//     public void approveRequest(Long leaveId) {
//         leaveService.processApproveOrRejectLeave(leaveId, LeaveStatus.APPROVED, null);
//     }

//     /**
//      * Rejects a leave application with a manager's comment.
//      * Comment is mandatory and will be visible to the employee.
//      * 
//      * @param leaveId the leave application ID to reject
//      * @param comment mandatory reason for rejection
//      * @throws RuntimeException if rejection fails or comment is blank
//      */
//     @Transactional
//     public void rejectRequest(Long leaveId, String comment) {
//         leaveService.processApproveOrRejectLeave(leaveId, LeaveStatus.REJECTED, comment);
//     }

//     /**
//      * Retrieves conflicting/overlapping leave applications for the manager's team.
//      * This helps the manager see how many team members are on leave during a specific period.
//      * 
//      * @param managerId the manager's ID
//      * @param fromDate leave start date
//      * @param toDate leave end date
//      * @param excludeId leave application ID to exclude from results
//      * @return list of approved leave applications that overlap the specified date range
//      */
//     public List<LeaveApplication> getConflictingLeaves(Long managerId, 
//                                                         java.time.LocalDate fromDate, 
//                                                         java.time.LocalDate toDate, 
//                                                         Long excludeId) {
//         return leaveRepo.findConflictingLeaves(managerId, fromDate, toDate, excludeId);
//     }
// }