package sg.edu.nus.laps.employee.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.employee.repository.EmployeeRepository;

@Service
public class EmployeeService {
	
	private final EmployeeRepository eRepo;

	public EmployeeService(EmployeeRepository eRepo) {
		super();
		this.eRepo = eRepo;
	}
	
	public List<Employee> findAll() {
		return eRepo.findAll();
	}
	
	public Optional<Employee> findByEmail(String email) {
		Optional<Employee> emOpt = eRepo.findByEmail(email);
		if (emOpt.isPresent()) {
			return emOpt;
		}
		return Optional.empty();
	}
	
	public Optional<Employee> findById(Long id) {
		Optional<Employee> empOpt = eRepo.findById(id);
		if (empOpt.isPresent()) {
			return empOpt;
		}
		return Optional.empty();
	}
	
	public void save(Employee employee) {
		eRepo.save(employee);
	}
	
	public void delete(Employee employee) {
		eRepo.delete(employee);
	}
}
