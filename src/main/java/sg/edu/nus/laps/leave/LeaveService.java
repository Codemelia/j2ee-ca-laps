package sg.edu.nus.laps.leave;

import java.util.Optional;

import org.springframework.stereotype.Service;

import sg.edu.nus.laps.employee.repository.EmployeeRepository;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.repository.HolidayRepository;
import sg.edu.nus.laps.leave.repository.LeaveApplicationRepository;
import sg.edu.nus.laps.leave.repository.LeaveRecordRepository;
import sg.edu.nus.laps.leave.repository.LeaveTypeRepository;

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
	private final LeaveTypeRepository ltRepo;
	private final LeaveRecordRepository lrRepo;
	private final HolidayRepository holRepo; 
	private final EmployeeRepository empRepo;

	public LeaveService(
			LeaveApplicationRepository laRepo,
			LeaveTypeRepository ltRepo,
			LeaveRecordRepository lrRepo,
			HolidayRepository holRepo,
			EmployeeRepository empRepo) {
		this.laRepo = laRepo;
		this.ltRepo = ltRepo;
		this.lrRepo = lrRepo;
		this.holRepo = holRepo;
		this.empRepo = empRepo;
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
