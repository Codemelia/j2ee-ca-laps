package sg.edu.nus.laps.me;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import sg.edu.nus.laps.leave.model.LeaveRecord;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.repository.LeaveRecordRepository;
import sg.edu.nus.laps.leave.repository.LeaveApplicationRepository;
import java.util.List;

/*
    Me Service handles user/employee-facing functions (Authenticated users only)
    
                    SERVICE SCOPE
    ------------------------------------------------
    -- READ --
    getDashboardData(employeeId)        - Retrieve recent leave requests and leave balances by Employee ID
    getProfile(employeeId)              - Retrieve employee profile by Employee ID
    getNotifications(employeeId)        - Retrieve notifications by Employee ID

    -- UPDATE --
    updateProfile(employeeId, profile)  - Update employee profile details
*/
@Service
public class MeService {

    @Autowired
    private LeaveRecordRepository leaveRecordRepo;

    @Autowired
    private LeaveApplicationRepository leaveAppRepo;

    // Gets leave balances (all leave types for this employee)
    public List<LeaveRecord> getLeaveBalances(Long employeeId) {
        return leaveRecordRepo.findByEmployeeId(employeeId);
    }
    
    // Gets recent leave requests
    public List<LeaveApplication> getRecentLeaveRequests(Long employeeId) {
        return leaveAppRepo
            .findByEmployeeIdOrderByFromDateDesc(employeeId);
    }
} 
