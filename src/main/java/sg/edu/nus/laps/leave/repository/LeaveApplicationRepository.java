package sg.edu.nus.laps.leave.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sg.edu.nus.laps.employee.model.Employee;
import jakarta.transaction.Transactional;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveStatus;
import sg.edu.nus.laps.leave.model.LeaveType;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

	// 1. Read-Method: Find By Leave Type
	List<LeaveApplication> findByLeaveType(LeaveType leaveType);
	
	// 2. Read-Method: Find Leave History within Date Range
	List<LeaveApplication> findByEmployeeAndFromDateBetween(Employee employee, LocalDate fromDate, LocalDate toDate);
	
	// 3. Read-Method: Find Leave History by Status
	List<LeaveApplication> findByEmployeeAndStatus(Employee employee, LeaveStatus status);
	
	// 4. Read-Method: Manager's Open Leave Application
	List<LeaveApplication> findByEmployeeManagerIdAndStatus(Long managerId, LeaveStatus status);
	
	// 5. Read-Method: Manager's Team History
	List<LeaveApplication> findByEmployeeManagerId(Long managerId);
	
	// 6. Read-Method:Find Team's Leave History within Date Range
	List<LeaveApplication> findByFromDateBetweenAndStatus(LocalDate fromDate, LocalDate toDate, LeaveStatus status);
	
	// 7. Custom-Query: Overlap Date Check
	@Query("SELECT l from LeaveApplication l WHERE l.employee = :emp "
			+ "AND l.status NOT IN (sg.edu.nus.laps.leave.model.LeaveStatus.REJECTED, "
			+ "sg.edu.nus.laps.leave.model.LeaveStatus.CANCELLED, "
			+ "sg.edu.nus.laps.leave.model.LeaveStatus.DELETED)"
			+ "AND (:fromDate <= l.toDate AND :toDate >= l.fromDate)")
	List<LeaveApplication> findOverlappingApplication(
			@Param("emp") Employee employee,
			@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate
			);
	
	// 8. Custom-Query: Manager's Team Leave Check
	@Query("SELECT l from LeaveApplication l WHERE l.status = sg.edu.nus.laps.leave.model.LeaveStatus.APPROVED"
			+ "AND l.employee.managerID = :managerId"
			+ "AND :today BETWEEN l.fromDate AND l.toDate")
	List<LeaveApplication> findTeamActiveLeaves(
			@Param("managerId") Long managerId,
			@Param("today") LocalDate today
			);
	
	// 1. Read-Method: Find by LeaveApplication by Employee ID
	List<LeaveApplication> findAllByEmployeeId(Long employeeId);
	List<LeaveApplication> findByEmployeeIdOrderByFromDateDesc(Long employeeId);
	// 2. update-method: Cancel a leave application
	@Modifying
	@Transactional
	@Query("UPDATE LeaveApplication l SET l.status = 'CANCELLED' WHERE l.id = :id")
	void cancelLeave(@Param("id") Long id);
}
