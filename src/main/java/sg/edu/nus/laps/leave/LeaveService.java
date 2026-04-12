package sg.edu.nus.laps.leave;

import java.util.Optional;

import org.springframework.stereotype.Service;

import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.repository.LeaveApplicationRepository;

/*
    LeaveService handles all leave CRUD operations (Employee)

                    SERVICE SCOPE
    ------------------------------------------------
    -- READ --
    findAllByEmployeeId(employeeId) - Retrieve list of all leaves by Employee ID
    findByLeaveId(id)               - Retrieve leave application by Leave ID

    -- CREATE / UPDATE --
    save(leaveApplication)          - Create or update a leave application (JPA maps by ID)

    -- CANCEL --
    cancel(id)                      - Cancel a leave application
*/
@Service
public class LeaveService {

    private final LeaveApplicationRepository laRepo;

    public LeaveService(LeaveApplicationRepository laRepo) {
        this.laRepo = laRepo;
    }

    // TEST leave-details.html - DELETE when updated
    public Optional<LeaveApplication> findLeaveById(Long id) {
        return laRepo.findById(id);
    }

    // Check if ID exists
    public boolean existsByLeaveId(Long id) {
        return laRepo.existsById(id);
    }

}
