package sg.edu.nus.laps.leave;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveRecord;
import sg.edu.nus.laps.leave.model.LeaveStatus;
import sg.edu.nus.laps.leave.repository.*;

<<<<<<< Updated upstream
import java.util.Optional;

import org.springframework.stereotype.Service;

import sg.edu.nus.laps.employee.repository.EmployeeRepository;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.repository.HolidayRepository;
import sg.edu.nus.laps.leave.repository.LeaveApplicationRepository;
import sg.edu.nus.laps.leave.repository.LeaveRecordRepository;
import sg.edu.nus.laps.leave.repository.LeaveTypeRepository;
=======
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
>>>>>>> Stashed changes

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
	 @Autowired private LeaveApplicationRepository leaveApplicationRepository;
	 @Autowired private HolidayRepository holidayRepository;
	 
	public  List<LeaveApplication> getLeaveRecordsforEmployee(Long employeeId){
		return leaveApplicationRepository.findAllByEmployeeId(employeeId);
	}
	public LeaveApplication getLeaveById(Long id) {
	    // .findById() is built into JpaRepository by default
	    // .orElse(null) handles the case where the ID doesn't exist in the DB
	    return leaveApplicationRepository.findById(id).orElse(null);
	}
	public LeaveApplication save(LeaveApplication leaveApplication) {
	    
	    return leaveApplicationRepository.save(leaveApplication);
	}

<<<<<<< Updated upstream
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

=======
	// 1. The Calculation Logic
    public int calculateActualLeaveDays(LocalDate start, LocalDate end, List<LocalDate> holidays) {
        int count = 0;
        LocalDate curr = start;
        while (!curr.isAfter(end)) {
            DayOfWeek day = curr.getDayOfWeek();
            boolean isWeekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
            boolean isHoliday = holidays.contains(curr);
            if (!isWeekend && !isHoliday) {
                count++;
            }
            curr = curr.plusDays(1);
        }
        return count;
    }

    // 2. The Retrieval Logic (Sharing the count into the entity)
    public List<LeaveApplication> getEmployeeLeaveHistory(Long employeeId) {
        List<LeaveApplication> history = leaveApplicationRepository.findByEmployeeIdOrderByFromDateDesc(employeeId);
        List<LocalDate> holidays = holidayRepository.findAllHolidayDates();

        for (LeaveApplication leave : history) {
            // Calculate the count
            double days = calculateActualLeaveDays(leave.getFromDate(), leave.getToDate(), holidays);
            
            // "Share" the count into the duration field in the entity
            leave.setDuration(days); 
        }
        return history;
    
}
	// RULE: Only 'APPROVED' leaves can be 'CANCELLED'
    @Transactional
    public void cancelLeave(Long id) {
        LeaveApplication leaveApplication = leaveApplicationRepository.findById(id).orElse(null);
        if (leaveApplication != null && leaveApplication.getStatus() == LeaveStatus.APPROVED) {
        	leaveApplication.setStatus(LeaveStatus.CANCELLED);
            leaveApplicationRepository.save(leaveApplication);
        }
    } 
    
 // For deleting an 'Applied' leave
    @Transactional
    public void deleteLeave(Long id) {
        LeaveApplication leave = leaveApplicationRepository.findById(id).orElse(null);
        if (leave != null && leave.getStatus() == LeaveStatus.APPLIED) {
            leave.setStatus(LeaveStatus.DELETED);
            leaveApplicationRepository.save(leave);
        }
    }
    // update 'Applied' leave
    @Transactional
    public void updateLeave(LeaveApplication updatedLeave) {
        updatedLeave.setStatus(LeaveStatus.UPDATED);
        leaveApplicationRepository.save(updatedLeave);
    }
>>>>>>> Stashed changes
}
