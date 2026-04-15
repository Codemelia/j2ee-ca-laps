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
     * 获取经理下属所有待处理的申请（APPLIED 或 UPDATED）
     */
    public List<LeaveApplication> getPendingRequests(Long managerId) {
        // 分别获取状态为 APPLIED 和 UPDATED 的申请并合并
        List<LeaveApplication> applied = leaveRepo.findByEmployeeManagerIdAndStatus(managerId, LeaveStatus.APPLIED);
        List<LeaveApplication> updated = leaveRepo.findByEmployeeManagerIdAndStatus(managerId, LeaveStatus.UPDATED);
        applied.addAll(updated);
        return applied;
    }

    /**
     * 获取下属的完整历史
     */
    public List<LeaveApplication> getSubordinateHistory(Long employeeId) {
        return leaveRepo.findByEmployeeIdOrderByFromDateDesc(employeeId);
    }

    public Optional<LeaveApplication> findLeaveById(Long id) {
        return leaveRepo.findById(id);
    }

    @Transactional
    public void approveRequest(Long leaveId) {
        leaveRepo.findById(leaveId).ifPresent(l -> {
            l.setStatus(LeaveStatus.APPROVED);
            leaveRepo.save(l);
        });
    }

    @Transactional
    public void rejectRequest(Long leaveId, String comment) {
        leaveRepo.findById(leaveId).ifPresent(l -> {
            l.setStatus(LeaveStatus.REJECTED);
            l.setManagerComment(comment);
            leaveRepo.save(l);
        });
    }
}
