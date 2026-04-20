package sg.edu.nus.laps.claim;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface OvertimeClaimRepository extends JpaRepository<OvertimeClaim, Long> {

	List<OvertimeClaim> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);

	List<OvertimeClaim> findByEmployeeManagerIdAndStatusInOrderByCreatedAtDesc(
		Long managerId, List<OvertimeClaimStatus> statusList);

	List<OvertimeClaim> findByIdInAndEmployeeManagerId(
		List<Long> ids, Long managerId);

	// Retrieve claims within curr year by manager
	@Query("SELECT c from OvertimeClaim c " +
		"WHERE c.employee.managerId = :mgrId " +
		"AND FUNCTION('YEAR', c.workedDate) = :year " +
		"AND c.status IN :statusList " +
		"ORDER BY c.createdAt DESC")
	List<OvertimeClaim> findTeamClaimsByYearAndStatusIn(
		@Param("mgrId") Long mgrId,
		@Param("year") int year,
		@Param("statusList") List<OvertimeClaimStatus> statusList
	);
}
