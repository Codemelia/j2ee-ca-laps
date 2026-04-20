package sg.edu.nus.laps.claim;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OvertimeClaimRepository extends JpaRepository<OvertimeClaim, Long> {

	List<OvertimeClaim> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);

	List<OvertimeClaim> findByEmployeeManagerIdAndStatusInOrderByCreatedAtDesc(
		Long managerId, List<OvertimeClaimStatus> statuses);

}
