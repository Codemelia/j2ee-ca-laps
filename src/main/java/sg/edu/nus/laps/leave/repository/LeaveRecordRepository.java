package sg.edu.nus.laps.leave.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import sg.edu.nus.laps.leave.model.LeaveRecord;

@Repository
public interface LeaveRecordRepository extends JpaRepository<LeaveRecord, Long> {

	// 1. Read-Method: Find by Employee ID
	List<LeaveRecord> findByEmployeeId(Long employeeId);
	
	// 2. Read-Method: Find by Employee ID, Leave Type ID, Calendar Year
	Optional<LeaveRecord> findByEmployeeIdAndLeaveTypeIdAndCalendarYear(Long employeeId, Long leaveTypeId, Integer calendarYear);
	
	// 3. Read-Method:  Check if a Record Exist by Employee ID, Leave Type ID, Calendar Year
	boolean existsByEmployeeIdAndLeaveTypeIdAndCalendarYear(Long employeeId, Long leaveTypeId, Integer calendarYear);
	
	// 4. Read-Method: Find by Leave Type ID, Calendar Year
	List<LeaveRecord> findByLeaveTypeIdAndCalendarYear(Long leaveTypeId, Integer calendarYear);
	
	// 5. Read-Method: Find by Manager ID
	List<LeaveRecord> findByEmployeeManagerId(Long managerId);
	
	// 6. Custom Query: Check for Deficit Records
	@Query("SELECT lr FROM LeaveRecord lr WHERE (lr.entitledDays - lr.consumedDays) < 0")
	List<LeaveRecord> findDeficitRecords();

}
