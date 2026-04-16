package sg.edu.nus.laps.leave.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import sg.edu.nus.laps.leave.model.LeaveRecord;
import sg.edu.nus.laps.leave.repository.LeaveRecordRepository;

@Service
public class LeaveRecordService {

    private final LeaveRecordRepository lrRepo;

    public LeaveRecordService(LeaveRecordRepository lrRepo) {
        this.lrRepo = lrRepo;
    }

    // Gets leave balances (all leave types for this employee)
    public List<LeaveRecord> getLeaveRecords(Long employeeId) {
        return lrRepo.findByEmployeeId(employeeId);
    }
    
    public Optional<LeaveRecord> findByEmployeeIdAndLeaveTypeIdAndCalendarYear(Long employeeId, Long leaveTypeId, Integer calendarYear) {
    	Optional<LeaveRecord> leaveRecordOpt = lrRepo.findByEmployeeIdAndLeaveTypeIdAndCalendarYear(employeeId, leaveTypeId, calendarYear);
    	
    	if(leaveRecordOpt.isPresent()) {
    		return leaveRecordOpt;
    	}
    	return Optional.empty();
    
    }
    
}
