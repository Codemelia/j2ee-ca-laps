package sg.edu.nus.laps.employee;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.employee.repository.EmployeeRepository;

/*
    EmployeeService handles all employee CRUD operations (Admin-only)

                    SERVICE SCOPE
    ------------------------------------------------
    -- READ --
    findAll()          - Retrieve list of all employees
    findByEmail(email) - Retrieve employee by email
    findById(id)       - Retrieve employee by ID

    -- CREATE / UPDATE --
    save(employee)     - Create or update employee record (JPA maps by ID)

    -- DELETE --
    delete(employee)   - Delete employee record
*/
@Service
public class EmployeeService {
	
	private final EmployeeRepository eRepo;

	public EmployeeService(EmployeeRepository eRepo) {
		super();
		this.eRepo = eRepo;
	}
	
	@Transactional(readOnly=true)
	public List<Employee> findAll() {
		return eRepo.findAll();
	}
	
	@Transactional(readOnly=true)
	public Optional<Employee> findByEmail(String email) {
		Optional<Employee> emOpt = eRepo.findByEmail(email);
		if (emOpt.isPresent()) {
			return emOpt;
		}
		return Optional.empty();
	}
	
	@Transactional(readOnly=true)
	public Optional<Employee> findById(Long id) {
		Optional<Employee> empOpt = eRepo.findById(id);
		if (empOpt.isPresent()) {
			return empOpt;
		}
		return Optional.empty();
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
	public void save(Employee employee) {
		eRepo.save(employee);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
	public void delete(Employee employee) {
		eRepo.delete(employee);
	}
}
