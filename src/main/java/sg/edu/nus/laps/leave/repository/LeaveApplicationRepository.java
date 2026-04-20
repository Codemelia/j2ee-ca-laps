package sg.edu.nus.laps.leave.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveStatus;
import sg.edu.nus.laps.leave.model.LeaveType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

	// Read-Method: Manager's team leaves
	List<LeaveApplication> findByEmployeeManagerIdAndStatusIn(Long managerId, List<LeaveStatus> statusList);
	
	// 5. Read-Method: Manager's Team History
	List<LeaveApplication> findByEmployeeManagerId(Long managerId);
	
	// 6. Read-Method:Find Team's Leave History within Date Range
	List<LeaveApplication> findByFromDateBetweenAndStatus(LocalDate fromDate, LocalDate toDate, LeaveStatus status);
	
	// 7. Custom-Query: Overlap Date Check
	@Query("SELECT l from LeaveApplication l WHERE l.employee = :emp "
			+ "AND l.status NOT IN (:excStatusList) "
			+ "AND (:fromDate <= l.toDate AND :toDate >= l.fromDate)")
	List<LeaveApplication> findOverlappingApplication(
			@Param("excStatusList") List<LeaveStatus> excStatusList,
			@Param("emp") Employee employee,
			@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate
			);
	
	// 8. Custom-Query: Manager's Team Leave Check
	@Query("SELECT l from LeaveApplication l WHERE l.status = :approveStatus "
			+ "AND l.employee.managerId = :managerId "
			+ "AND :today BETWEEN l.fromDate AND l.toDate")
	List<LeaveApplication> findTeamActiveLeaves(
			@Param("approveStatus") LeaveStatus approveStatus,
			@Param("managerId") Long managerId,
			@Param("today") LocalDate today
			);
	
	// 9. Read-Method: Find Leave Application by Employee ID
	List<LeaveApplication> findAllByEmployeeId(Long employeeId);
	
	// 10. For Dashboard: Limit leave applications to top 5
	List<LeaveApplication> findTop5ByEmployeeIdOrderByUpdatedAtDesc(Long employeeId);

	// 11. Display leave history (fromDate or toDate) containing current year
	List<LeaveApplication> findByEmployeeIdOrderByFromDateDesc(Long employeeId);
	@Query("SELECT l FROM LeaveApplication l WHERE l.employee.id = :empId " +
		"AND (FUNCTION('YEAR', l.fromDate) = :year OR FUNCTION('YEAR', l.toDate) = :year) " +
		"AND l.status <> 'DELETED' " +
		"ORDER BY l.fromDate DESC")
	List<LeaveApplication> findByEmployeeIdAndYear(
		@Param("empId") Long empId, 
		@Param("year") int year
		/* Pageable pageable */
	);

  	@Query("SELECT l FROM LeaveApplication l WHERE l.employee.managerId = :managerId " +
		"AND l.status in ('APPLIED', 'UPDATED', 'APPROVED')" +
		"AND l.id != :excludeId " +
		"AND l.fromDate <= :toDate AND l.toDate >= :fromDate")
	List<LeaveApplication> findConflictingLeaves(
		@Param("managerId") Long managerId,
		@Param("fromDate") LocalDate fromDate,
		@Param("toDate") LocalDate toDate,
		@Param("excludeId") Long excludeId);
    
    // Find leave applications by employee and status
    List<LeaveApplication> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);
    
    // Retrieve leave applications for Movement Register filtered by month/year with pagination
    @Query("SELECT l FROM LeaveApplication l WHERE MONTH(l.fromDate) = :month AND YEAR(l.fromDate) = :year")
    Page<LeaveApplication> findByMonth(@Param("month") int month,
                                       @Param("year") int year,
                                       Pageable pageable);
    
}
