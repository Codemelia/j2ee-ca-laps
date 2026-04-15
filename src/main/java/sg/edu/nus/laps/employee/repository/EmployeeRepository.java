package sg.edu.nus.laps.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.employee.model.EmployeeRank;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	
	Optional<Employee> findByUser_Email(String email);
	
	Optional<Employee> findById(Long id);
	
	@Query("select count(e.Id) from Employee e")
	Integer countId();
	
	Page<Employee> findAll(Pageable pageable);
	
	List<Employee> findByRank(EmployeeRank rank);
	
}
