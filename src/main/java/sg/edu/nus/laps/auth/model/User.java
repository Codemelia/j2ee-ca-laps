package sg.edu.nus.laps.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import sg.edu.nus.laps.common.util.SetCreatedUpdated;
import sg.edu.nus.laps.employee.model.Employee;

@Entity
@Table(name = "users")
public class User extends SetCreatedUpdated {
	
	// VARIABLES

	@Id
	@Column(nullable = false, unique = true, length = 256) // JPA - MySQL constraints
	@NotBlank(message = "User email is required")
	@Email(message = "User email is invalid") // Check invalid email
	@Size(min = 10, max = 256, message = "User email must be between 10 to 256 characters")
	private String email;

	// No raw password validation bc this is hashed password
	// Set length at 255 for flexibility in case of hash algorithm change
	@Column(name = "password_hash", nullable = false, length = 255) // JPA - MySQL constraints
	@NotBlank(message = "Password hash is required")
	@Size(max = 255, message = "Password hash must not be longer than 255 characters")
	private String passwordHash;

	// default to true - Avoid accidental pending state
	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
	private boolean enabled = true;

	// ASSOCIATIONS

	// User to Role: Many to One
	// User as Owning Side, stores FK role_id
	// optional = false - All Users must have a Role to be saved to DB
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id")
	private Role role;

	// User to Employee: One to One
	@OneToOne(optional = true, mappedBy = "user", fetch = FetchType.LAZY)
	private Employee employee;

	// LIFECYCLE

	// CONSTRUCTORS

	public User() {}
	public User(String email, String passwordHash, boolean enabled, Role role) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.enabled = enabled;
		this.role = role;
	}
	
	// GETTERS SETTERS

	// Restrictions:
	// Only admin can access setEmail() / setRole() / isEnabled() - enforced in service layer
	// NO setCreatedAt() / setUpdatedAt() to prevent corrupting timestamps

	public String getEmail() { return this.email; }
	public void setEmail(String email) { this.email = email; }
	public String getPasswordHash() { return this.passwordHash; }
	public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
	public boolean isEnabled() { return this.enabled; }
	public void setEnabled(boolean enabled) { this.enabled = enabled; }
	public Role getRole() { return this.role; }
	public void setRole(Role role) { this.role = role; }
	public Employee getEmployee() { return this.employee; }
	public void setEmployee(Employee employee) { this.employee = employee; }

	// TO STRING

	@Override
	public String toString() {
		return "{" +
			" email='" + getEmail() + "'" +
			", passwordHash='" + getPasswordHash() + "'" +
			", enabled='" + isEnabled() + "'" +
			", role='" + getRole() + "'" +
			"}";
	}

}
