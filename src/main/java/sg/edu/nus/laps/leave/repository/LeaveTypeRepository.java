package sg.edu.nus.laps.leave.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sg.edu.nus.laps.leave.model.LeaveType;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

	// 1. Read-Method: Find Leave Type by Name
	Optional<LeaveType> findByLeaveType(String leaveType);
	
	// 2. Read-Method: Validate Leave Type by Name
	boolean existsByLeaveType(String leaveType);
	
	// 3. Read-Method: Find Leave Type by Partial Name
	List<LeaveType> findByLeaveTypeContainingIgnoreCase(String leaveType);
	
	// 4. Read-Method: Find Leave Type by Description
	List<LeaveType> findByLeaveDescriptionContainingIgnoreCase(String description);

}
