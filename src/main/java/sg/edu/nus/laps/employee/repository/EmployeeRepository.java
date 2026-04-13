package sg.edu.nus.laps.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import sg.edu.nus.laps.employee.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	
	Optional<Employee> findByUser_Email(String email);
	
	Optional<Employee> findById(Long id);
	
	@Query("select count(e.Id) from Employee e")
	Integer countId();
	

}
