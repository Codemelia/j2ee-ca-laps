package sg.edu.nus.laps.employee.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.employee.repository.EmployeeRepository;

@Service
public class EmployeeService {
	
	private final EmployeeRepository eRepo;

	public EmployeeService(EmployeeRepository eRepo) {
		super();
		this.eRepo = eRepo;
	};
	
	public List<Employee> findAll() {
		return eRepo.findAll();
	}
	
	public Optional<Employee> findByEmail() {
		Optional<Employee> emOpt = eRepo.findByEmail();
		if (emOpt.isPresent()) {
			return emOpt;
		}
		return emOpt.empty();
	}
}
