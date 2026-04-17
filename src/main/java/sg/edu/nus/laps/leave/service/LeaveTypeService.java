package sg.edu.nus.laps.leave.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import sg.edu.nus.laps.leave.model.LeaveType;
import sg.edu.nus.laps.leave.repository.LeaveTypeRepository;

/**
 * LeaveTypeService provides methods to interact with leave types through repository.
 */
@Service
public class LeaveTypeService {
    private final LeaveTypeRepository ltRepo;
    public LeaveTypeService(LeaveTypeRepository ltRepo) { this.ltRepo = ltRepo; }

    public List<LeaveType> findAllLeaveTypes() {
        List<LeaveType> types = ltRepo.findAll();
        return types != null ? types : new ArrayList<>();
    }

    public Optional<LeaveType> findLeaveTypeById(Long id) {
        return ltRepo.findById(id);
    }
    
    public Optional<LeaveType> findByLeaveType(String leaveType) {
    	return ltRepo.findByLeaveType(leaveType);
    }
}
