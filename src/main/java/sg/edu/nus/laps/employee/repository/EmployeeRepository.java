package sg.edu.nus.laps.employee.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import sg.edu.nus.laps.employee.model.Employee;
import org.springframework.data.repository.query.Param;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	
	Optional<Employee> findByUser_Email(String email);
	
	Optional<Employee> findById(Long id);
	
	@Query("select count(e.Id) from Employee e")
	Integer countId();
	
	Page<Employee> findAll(Pageable pageable);

	@Query("""
		    SELECT e FROM Employee e
		    WHERE 
		        (:search IS NULL OR 
		            LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
		            LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR
		            LOWER(e.user.email) LIKE LOWER(CONCAT('%', :search, '%'))
		        )
		    AND
		        (:role IS NULL OR e.user.role.name = :role)
		""")
		Page<Employee> searchAndFilter(
		        @Param("search") String search,
		        @Param("role") String role,
		        Pageable pageable);
}
