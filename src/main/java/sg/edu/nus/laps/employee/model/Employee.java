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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import sg.edu.nus.laps.auth.model.User;
import sg.edu.nus.laps.common.util.SetCreatedUpdated;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveRecord;

@Entity
@Table(name = "employees")
public class Employee extends SetCreatedUpdated {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "first_name", nullable = false, length = 50)
	@NotBlank(message = "First name is required")
	// @Size(min = 2, max = 50, message = "First name must be between 2 to 50 characters")
	@Pattern(regexp = "^[A-Za-z '-]{2,50}$", message = "First name must be 2 to 50 characters without special characters")
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 50)
	@NotBlank(message = "Last name is required")
	// @Size(min = 2, max = 50, message = "Last name must be between 2 to 50 characters")
	@Pattern(regexp = "^[A-Za-z '-]{2,50}$", message = "Last name must be 2 to 50 characters without special characters")
	private String lastName;

	@Column(name = "contact_number", nullable = false, length = 15)
	@NotBlank(message = "Contact number is required")
	// @Size(min = 8, max = 15, message = "Contact number must be between 8 to 15 characters")
	@Pattern(regexp = "^\\+?\\d{8,15}$", message = "Contact number must be valid with 8 to 15 digits") // Validate phone number regexp - \\s () \\d . not allowed for now
	private String contactNumber;

	@Enumerated(EnumType.STRING) // JPA store ENUM as String in DB
	@Column(name = "`rank`", nullable = false, length = 30)
	@NotNull(message = "Please select employee's rank")
	private EmployeeRank rank;
	
	// Admin will need to key in manager's id directly
	@Column(name = "manager_id", nullable = true)
	@NotBlank(message = "Please state employee's Manager's ID")
	@Positive(message = "Manager's id must be a positive number") // Validate admin input
	private Long managerId;

	// ADDED for display
	@Column(nullable=false)
	@NotBlank(message = "Please state employee's team name")
	@Size(max = 50, message = "Team name must be 50 characters and below")
	private String teamName;

	@Column(nullable=false)
	@NotBlank(message = "Please state employee's job title")
	@Size(max = 50, message = "Job title must be 50 characters and below")
	private String jobTitle;
	
	// ADDED for form binding
	@Transient //Don't persist in db
	@NotNull(message = "Annual Leave Entitlement cannot be blank")
	@DecimalMin(value="14.0", inclusive=true, message="")
	@DecimalMax(value="21.0", inclusive=true, message="")
	private Double annualLeave;

	@Transient
	@NotBlank(message = "Role must not be blank")
	private String roleName;

	// Employee to User: One to One
	// Employee as Owning Side bc User may not be Employee, but Employee must be User
	// CascadeType.REMOVE - On delete employee, delete associated user
	// CascadeType.PERSIST - Saves User on Save Employee
	// CascadeType.MERGE - Updates User on Update Employee
	// orphanRemoval=true - On setUser(null), detaches old user and deletes it
	@OneToOne(optional = false, 
		fetch = FetchType.LAZY, 
		cascade = { CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE }, 
		orphanRemoval = true)
	@JoinColumn(name = "email", referencedColumnName = "email",
		nullable = false, unique = true)
	private User user;
	
	// Employee to LeaveApplication: One to Many
	// orphanRemoval - detach leaveApplication to allow delete
	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY,
		orphanRemoval = true)
	private List<LeaveApplication> leaveApplications = new ArrayList<>();
	
	// Employee to LeaveRecord: One to Many
	// CascadeType.PERSIST, CascadeType.MERGE - Allow Employee modification to update leave records
	// orphanRemoval - detach leaveRecord to allow delete
	@OneToMany(mappedBy= "employee", fetch = FetchType.LAZY, 
		cascade = {CascadeType.PERSIST, CascadeType.MERGE},
		orphanRemoval = true)
	private List<LeaveRecord> leaveRecords = new ArrayList<>();
	
	public Employee() {}
	public Employee(Long id, String firstName, String lastName, String contactNumber,
		EmployeeRank rank, Long managerId, String teamName, String jobTitle, Double annualLeave,
		String roleName, User user, List<LeaveApplication> leaveApplications, List<LeaveRecord> leaveRecords) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.contactNumber = contactNumber;
		this.rank = rank;
		this.managerId = managerId;
		this.teamName = teamName;
		this.jobTitle = jobTitle;
		this.annualLeave = annualLeave;
		this.roleName = roleName;
		this.user = user;
		this.leaveApplications = leaveApplications;
		this.leaveRecords = leaveRecords;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }
	public String getContactNumber() { return contactNumber; }
	public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
	public EmployeeRank getRank() { return rank; }
	public void setRank(EmployeeRank rank) { this.rank = rank; }
	public Long getManagerId() { return managerId; }
	public void setManagerId(Long managerId) { this.managerId = managerId; }
	public User getUser() { return user; }
	public void setUser(User user) { this.user = user; }
	public String getTeamName() { return this.teamName; }
	public void setTeamName(String teamName) { this.teamName = teamName; }
	public String getJobTitle() { return this.jobTitle; }
	public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
	public List<LeaveApplication> getLeaveApplications() { return this.leaveApplications; }
	public void setLeaveApplications(List<LeaveApplication> leaveApplications) { this.leaveApplications = leaveApplications; }
	public List<LeaveRecord> getLeaveRecords() { return this.leaveRecords; }
	public void setLeaveRecords(List<LeaveRecord> leaveRecords) { this.leaveRecords = leaveRecords; }
	public String getRoleName() { return this.roleName; }
	public void setRoleName(String roleName) { this.roleName = roleName; }
	public Double getAnnualLeave() { return annualLeave; }
	public void setAnnualLeave(Double annualLeave) { this.annualLeave = annualLeave; }

	@Override
	public String toString() {
		return "Employee [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", contactNumber="
				+ contactNumber + ", rank=" + rank + ", managerId=" + managerId + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + ", annualLeave=" + annualLeave + ", user=" + user + ", leaveApplications=" + leaveApplications
				+ ", leaveRecords=" + leaveRecords + "]";
	}
	
}
