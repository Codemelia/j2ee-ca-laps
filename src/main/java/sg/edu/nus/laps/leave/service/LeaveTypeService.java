package sg.edu.nus.laps.leave.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import sg.edu.nus.laps.leave.model.LeaveType;
import sg.edu.nus.laps.leave.repository.LeaveTypeRepository;

@Service
public class LeaveTypeService {

    private final LeaveTypeRepository ltRepo;
    public LeaveTypeService(LeaveTypeRepository ltRepo) {
        this.ltRepo = ltRepo;
    }

    public List<LeaveType> findAllLeaveTypes() {
        return ltRepo.findAll();
    }

    public Optional<LeaveType> findLeaveTypeById(Long id) {
        return ltRepo.findById(id);
    }

}
