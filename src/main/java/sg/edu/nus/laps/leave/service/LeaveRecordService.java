package sg.edu.nus.laps.leave.service;

import java.util.List;

import sg.edu.nus.laps.leave.model.LeaveRecord;
import sg.edu.nus.laps.leave.repository.LeaveRecordRepository;

public class LeaveRecordService {

    private final LeaveRecordRepository lrRepo;

    public LeaveRecordService(LeaveRecordRepository lrRepo) {
        this.lrRepo = lrRepo;
    }

    // Gets leave balances (all leave types for this employee)
    public List<LeaveRecord> getLeaveBalances(Long employeeId) {
        return lrRepo.findByEmployeeId(employeeId);
    }

}
