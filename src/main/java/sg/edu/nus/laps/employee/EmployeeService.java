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
	
	// Partially update existing employee
	// Propagation.REQUIRED: Keep user and employee saves in same transaction
	// Isolation.READ_COMMITTED: Prevent dirty reads
	@Transactional(propagation = Propagation.REQUIRED, 
		isolation = Isolation.READ_COMMITTED)
	public void updateEmployee(Employee employee) {
		checkEmployeeValid(employee); // Check if employee valid
		checkEmployeeExistsForUpdate(employee.getId()); // Check if employee exists

		// Retrieve existing employee from DB (For full fields)
		Employee existEmployee = eRepo.findById(employee.getId()).get();

		// Map new (editable) fields to existing employee
		// Prevents unintended overwrites
		mapEditableFields(employee, existEmployee);

		// Retrieve existing user account
		// Check if valid and exists
		User existUser = existEmployee.getUser();
		checkUserValidAndExists(existUser);

		// Update role if valid and changed
		if (!employee.getRoleName().isBlank()) {
			Role newRole = findRoleByName(employee.getRoleName());

			if (!existUser.getRole().equals(newRole)) {
				existUser.setRole(newRole); // JPA will update users automatically
			}
		}

		// Update employee - updates user in DB too
		eRepo.save(existEmployee);
	}

	// Save new employee and user
	// Propagation.REQUIRED: Keep user and employee saves in same transaction
	// Isolation.READ_COMMITTED: Prevent dirty reads
	@Transactional(propagation = Propagation.REQUIRED, 
		isolation = Isolation.READ_COMMITTED)
	public void saveNewEmployee(Employee employee) {
		checkEmployeeValid(employee); // Check if employee valid
		checkEmployeeExistsForNew(employee.getId()); // Check if employee already has ID

		String roleName = employee.getRoleName();
		checkRoleValid(roleName); // Check if selected role is valid

		// Retrieve employee name and generate email
		// Replace all non-letter characters
		String firstName = employee.getFirstName()
			.replaceAll("[^A-Za-z]", "")
			.toLowerCase();
		String lastName = employee.getLastName()
			.replaceAll("[^A-Za-z]", "")
			.toLowerCase();

		// Check if first/last name valid for email generation
		// Covers edge cases of names containing only spaces, ', -
		checkEmailValidForNew(firstName, lastName);

		String email = String.format("%s.%s@%s", 
			firstName, lastName, EMAIL_DOMAIN);

		// Check if email already exists
		checkEmailExistsForNew(email);

		// Auto generate employee password - 15 chars
		// Hash for storage
		String passwordRaw = UUID.randomUUID()
			.toString()
			.replace("-", "")
			.substring(0, 15);
		String passwordHash = encoder.encode(passwordRaw);

		// Retrieve role
		Role newRole = findRoleByName(roleName);

		// Create and save new user with enabled account and assigned role
		// Fields: email, passwordHash, enabled, role
		User user = new User(email, passwordHash, true, newRole);

		// Set employee's user account and save
		employee.setUser(user); // JPA maps to User and saves
		eRepo.save(employee);
	}
	
	// Delete employee and user account
	// Propagation.REQUIRED: Keep user and employee deletes in same transaction
	// Isolation.READ_COMMITTED: Prevent dirty reads
	@Transactional(propagation = Propagation.REQUIRED, 
		isolation = Isolation.READ_COMMITTED)
	public void delete(Employee employee) {
		checkEmployeeValid(employee); // Check if employee valid
		checkEmployeeExistsForUpdate(employee.getId()); // Check if employee exists

		// Delete employee - cascades to User
		eRepo.delete(employee);
	}

	// HELPERS
	// Check employee validity
	private void checkEmployeeValid(Employee employee) {
		if (employee == null || employee.getUser() == null) {
			throw new InvalidEmployeeException();
		}
	}

	// Check if employee exists for update
	private void checkEmployeeExistsForUpdate(Long employeeId) {
		if (!eRepo.existsById(employeeId)) {
			throw new InvalidEmployeeException("Employee not found");
		}
	}

	// Check if employee exists for new save
	private void checkEmployeeExistsForNew(Long employeeId) {
		if (employeeId != null) {
			throw new InvalidEmployeeException("New employee should not have ID");
		}
	}

	// Check if employee user email validity
	private void checkUserValidAndExists(User user) {
		if (user == null 
			|| user.getEmail() == null 
			|| !uRepo.existsByEmail(user.getEmail())) {
			throw new InvalidEmployeeException("Employee has invalid user account");
		}
	}

	// Check if user role is valid
	private void checkRoleValid(String roleName) {
		if (roleName.isBlank()) {
			throw new InvalidEmployeeException("Invalid role");
		}
	}

	// Retrieve valid user role
	private Role findRoleByName(String roleName) {
		return rRepo.findByName(roleName)
				.orElseThrow(() -> 
					new InvalidEmployeeException("User Role is invalid"));
	}

	// Map only fields that admin can update
	private void mapEditableFields(Employee employee, Employee existEmployee) {
		if (employee.getFirstName() != null) { existEmployee.setFirstName(employee.getFirstName()); }
		if (employee.getLastName() != null) { existEmployee.setLastName(employee.getLastName()); }
		if (employee.getContactNumber() != null) { existEmployee.setContactNumber(employee.getContactNumber()); }
		if (employee.getRank() != null) { existEmployee.setRank(employee.getRank()); }
		if (employee.getManagerId() != null) { existEmployee.setManagerId(employee.getManagerId()); }
		if (employee.getTeamName() != null) { existEmployee.setTeamName(employee.getTeamName()); }
		if (employee.getRoleName() != null) { existEmployee.setRoleName(employee.getRoleName()); }
	}

	// Check new email valid
	private void checkEmailValidForNew(String firstName, String lastName) {
		if (firstName.isBlank() || lastName.isBlank()) {
			throw new InvalidEmployeeException("Invalid name for email generation");
		}
	}

	// Check new email already exists
	private void checkEmailExistsForNew(String email) {
		if (uRepo.existsById(email)) {
			throw new InvalidEmployeeException(
				"Employee with same name already exists"
			);
		}
	}

	// Saves details in record for later retrieval
	@SuppressWarnings("unused")
	private NewEmployeeRecord getNewEmployeeRecord(
		Employee employee, String email, String passwordRaw) {
		return new NewEmployeeRecord(employee, email, passwordRaw);
	}

}
