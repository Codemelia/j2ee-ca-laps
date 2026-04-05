package sg.edu.nus.laps.employee.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.employee.repository.EmployeeRepository;

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
	@Modifying(clearAutomatically = true)
	public void save(Employee employee) {
		eRepo.save(employee);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
	@Modifying(clearAutomatically = true)
	public void delete(Employee employee) {
		eRepo.delete(employee);
	}
}
