package sg.edu.nus.laps.employee.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import sg.edu.nus.laps.auth.model.User;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveRecord;
import sg.edu.nus.laps.shared.util.SetCreatedUpdated;

@Entity
@Table(name = "employees")
public class Employee extends SetCreatedUpdated {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Remove bc this should be FK
	// @Column(nullable = false, unique = true, length = 256) // JPA - MySQL constraints
	// @NotBlank(message = "Company email is required")
	// @Email(message = "Please enter a valid email address") // check invalid email
	// @Size(min = 10, max = 256, message = "Email must be between 10 to 256 characters")
	// private String email;

	@Column(name = "first_name", nullable = false, length = 50) // JPA - MySQL constraints
	@NotBlank(message = "First name is required")
	@Size(min = 2, max = 50, message = "First name must be between 2 to 50 characters")
	@Pattern(regexp = "^[A-Za-z '-]+$", message = "First name contains invalid characters") // Validate no special characters
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 50) // JPA - MySQL constraints
	@NotBlank(message = "Last name is required")
	@Size(min = 2, max = 50, message = "Last name must be between 2 to 50 characters")
	@Pattern(regexp = "^[A-Za-z '-]+$", message = "Last name contains invalid characters") // Validate no special characters
	private String lastName;

	@Column(name = "contact_number", nullable = false, length = 15) // JPA - MySQL constraints
	@NotBlank(message = "Contact number is required")
	@Size(min = 8, max = 15, message = "Contact number must be between 8 to 15 characters")
	@Pattern(regexp = "^\\+?\\d{8,15}$", message = "Enter a valid phone number") // Validate phone number regexp - \\s () \\d . not allowed for now
	private String contactNumber;

	@Enumerated(EnumType.STRING) // JPA store ENUM as String in DB
	@Column(nullable = false, length = 30) // JPA - MySQL constraints
	@NotNull(message = "Please select employee's rank")
	private EmployeeRank rank;
	
	// Admin will need to key in manager's id directly
	@Column(name = "manager_id", nullable = false) // JPA - MySQL constraints
	@NotNull(message = "Manager's id is required")
	@Positive(message = "Manager's id must be a positive number") // Validate admin input
	private Long managerId;

	// @Column(name = "created_at", nullable = false, updatable = false) // JPA - MySQL constraints
	// private LocalDateTime createdAt;
	
	// @Column(name = "updated_at", nullable = false) // JPA - MySQL constraints
	// private LocalDateTime updatedAt;
	
	// Employee to User: One to One
	// Employee as Owning Side bc User may not be Employee, but Employee must be User
	// CascadeType.REMOVE - On delete employee, delete associated user
	// orphanRemoval=true - On setUser(null), detaches old user and deletes it
	@OneToOne(optional = false, fetch = FetchType.LAZY, 
		cascade = CascadeType.REMOVE, orphanRemoval = true) // optional to reinforce Employee must be User
	@JoinColumn(name = "email", referencedColumnName = "email",
		nullable = false, unique = true)
	private User user;
	
	// Employee to LeaveApplication: One to Many
	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
	private List<LeaveApplication> leaveApplications = new ArrayList<>();
	
	// Employee to LeaveRecord: One to Many
	@OneToMany(mappedBy= "employee", fetch = FetchType.LAZY)
	private List<LeaveRecord> leaveRecords = new ArrayList<>();
	
	public Employee() {
		super();
	}

	public Employee(Long id, String firstName, String lastName, 
			String contactNumber, EmployeeRank rank, Long managerId) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.contactNumber = contactNumber;
		this.rank = rank;
		this.managerId = managerId;
		// Not needed, auto update via JPA/MySQL
		// this.createdAt = createdAt;
		// this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

//	public void setId(Long id) {
//		this.id = id;
//	}

	// public String getEmail() {
	// 	return email;
	// }

	// public void setEmail(String email) {
	// 	this.email = email;
	// }

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public EmployeeRank getRank() {
		return rank;
	}

	public void setRank(EmployeeRank rank) {
		this.rank = rank;
	}

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	// public LocalDateTime getCreatedAt() {
	// 	return createdAt;
	// }

	// // public void setCreatedAt(LocalDateTime createdAt) {
	// // 	this.createdAt = createdAt;
	// // }

	// public LocalDateTime getUpdatedAt() {
	// 	return updatedAt;
	// }

	// // public void setUpdatedAt(LocalDateTime updatedAt) {
	// // 	this.updatedAt = updatedAt;
	// // }

	// @PrePersist // set NOW() in MySQL on create
	// private void onCreate() {
	// 	LocalDateTime now = LocalDateTime.now();
	// 	this.createdAt = now;
	// 	this.updatedAt = now;
	// }

	// @PreUpdate // set NOW() in MySQL on update
	// private void onUpdate() {
	// 	this.updatedAt = LocalDateTime.now();
	// }

}
