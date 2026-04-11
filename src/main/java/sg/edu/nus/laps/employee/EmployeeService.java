package sg.edu.nus.laps.employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.laps.auth.user.model.Role;
import sg.edu.nus.laps.auth.user.model.User;
import sg.edu.nus.laps.auth.user.repository.RoleRepository;
import sg.edu.nus.laps.auth.user.repository.UserRepository;
import sg.edu.nus.laps.common.exception.employee.InvalidEmployeeException;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.employee.model.NewEmployeeRecord;
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
	private final UserRepository uRepo;
	private final RoleRepository rRepo;
	private final String EMAIL_DOMAIN;
	private final PasswordEncoder encoder;

	public EmployeeService(
		EmployeeRepository eRepo,
		UserRepository uRepo,
		RoleRepository rRepo,
		PasswordEncoder encoder,
		@Value("${app.email.domain}") String EMAIL_DOMAIN) {
		super();
		this.eRepo = eRepo;
		this.uRepo = uRepo;
		this.rRepo = rRepo;
		this.EMAIL_DOMAIN = EMAIL_DOMAIN;
		this.encoder = encoder;
	}
	
	@Transactional(readOnly=true)
	public List<Employee> findAll() {
		return eRepo.findAll();
	}
	
	@Transactional(readOnly=true)
	public Optional<Employee> findByEmail(String email) {
		Optional<Employee> emOpt = eRepo.findByUser_Email(email);
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
	
	// Update existing employee
	// Propagation.REQUIRED: Keep user and employee saves in same transaction
	// Isolation.READ_COMMITTED: Prevent dirty reads
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public void updateEmployee(Employee employee, Role role) {
		checkValidEmployee(employee); // Check if employee valid
		if (!eRepo.existsById(employee.getId())) { // Check if employee exists
			throw new InvalidEmployeeException();
		}

		// if admin has assigned new role, update user repo
		User user = employee.getUser();
		if (user.getRole() != role) {
			user.setRole(role);
			uRepo.save(user);
		}

		eRepo.save(employee); // Save employee
	}

	// Save new employee and user
	// Propagation.REQUIRED: Keep user and employee saves in same transaction
	// Isolation.READ_COMMITTED: Prevent dirty reads
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public void saveNewEmployee(Employee employee, Role role) {
		checkValidEmployee(employee); // Check if employee valid
		if (role == null 
			|| !rRepo.existsByName(role.getName())) { // Check if role null or invalid
			throw new InvalidEmployeeException("Invalid user account for employee"); 
		}

		// If employee exists and already linked to user, update it and exit
		if (eRepo.existsById(employee.getId())
			|| employee.getUser() != null) {
			updateEmployee(employee, role);
			return;
		}

		// Retrieve employee name and generate email
		// Replace all non-letter characters
		String firstName = employee.getFirstName()
			.replaceAll("[^A-Za-z]", "")
			.toLowerCase();
		String lastName = employee.getLastName()
			.replaceAll("[^A-Za-z]", "")
			.toLowerCase();
		String email = String.format("%s.%s@%s", 
			firstName, lastName, EMAIL_DOMAIN);

		// Auto generate employee password - 15 chars
		// Hash for storage
		String passwordRaw = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
		String passwordHash = encoder.encode(passwordRaw);

		// Create and save new user with enabled account and assigned role
		// Fields: email, passwordHash, enabled, role
		User user = new User(email, passwordHash, true, role);
		uRepo.save(user);

		// Set employee's user account and save
		employee.setUser(user);
		eRepo.save(employee);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
	public void delete(Employee employee) {
		eRepo.delete(employee);
	}

	// HELPERS
	// Check existing employee validity
	private void checkValidEmployee(Employee employee) {
		if (employee == null || employee.getUser() == null) {
			throw new InvalidEmployeeException();
		}
	}

	// Saves details in record for later retrieval
	@SuppressWarnings("unused")
	private NewEmployeeRecord getNewEmployeeRecord(Employee employee, String email, String passwordRaw) {
		return new NewEmployeeRecord(employee, email, passwordRaw);
	}

}
