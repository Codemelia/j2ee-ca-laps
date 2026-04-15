package sg.edu.nus.laps.approval;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveStatus;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<LeaveApplication, Long> {
    
    /**
     * Find all leave applications for a manager's team with specific status
     */
    @Query("SELECT la FROM LeaveApplication la WHERE la.employee.manager.id = :managerId AND la.status = :status")
    List<LeaveApplication> findByManagerAndStatus(@Param("managerId") Long managerId, @Param("status") LeaveStatus status);
    
    /**
     * Find all pending leave applications (APPLIED or UPDATED status)
     */
    @Query("SELECT la FROM LeaveApplication la WHERE la.employee.manager.id = :managerId AND (la.status = 'APPLIED' OR la.status = 'UPDATED') ORDER BY la.fromDate DESC")
    List<LeaveApplication> findPendingByManager(@Param("managerId") Long managerId);
    
    /**
     * Find complete leave history for a subordinate
     */
    List<LeaveApplication> findByEmployeeIdOrderByFromDateDesc(Long employeeId);
    
    /**
     * Find leave applications by employee and status
     */
    List<LeaveApplication> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);
}