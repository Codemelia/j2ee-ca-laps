package sg.edu.nus.laps.employee.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sg.edu.nus.laps.auth.model.User;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.leave.model.LeaveRecord;

@Entity
@Table(name = "employees")
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank(message = "Company email is required")
	@Size(min = 10, max = 256, message = "Email must be between 10 to 256 characters")
	private String email;
	@NotBlank(message = "First name is required")
	@Size(min = 2, max = 50, message = "First name must be between 2 to 50 characters")
	private String first_name;
	@NotBlank(message = "Last name is required")
	@Size(min = 2, max = 50, message = "First name must be between 2 to 50 characters")
	private String last_name;
	@NotBlank(message = "Contact number is required")
	@Size(min = 8, max = 15, message = "Contact number must be between 8 to 15 digits")
	private String contact_number;
	@NotNull(message = "Please select employee's rank")
	private EmployeeRank rank;
	
	// Will Create Employee Form ask for manager's name and then extract id? 
	// Or require Admin to key in manager's id directly?
	@NotBlank(message = "Manager's id is required") 
	private Integer manager_id;
	private LocalDateTime created_at;
	private LocalDateTime updated_at;
	
	// Employee to User: One to One
	@OneToOne(mappedBy = "employee", fetch = FetchType.LAZY)
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

	public Employee(Long id,
			@NotBlank(message = "Company email is required") @Size(min = 10, max = 256, message = "Email must be between 10 to 256 characters") String email,
			@NotBlank(message = "First name is required") @Size(min = 2, max = 50, message = "First name must be between 2 to 50 characters") String first_name,
			@NotBlank(message = "Last name is required") @Size(min = 2, max = 50, message = "First name must be between 2 to 50 characters") String last_name,
			@NotBlank(message = "Contact number is required") @Size(min = 8, max = 15, message = "Contact number must be between 8 to 15 digits") String contact_number,
			@NotNull(message = "Please select employee's rank") EmployeeRank rank,
			@NotBlank(message = "Manager's id is required") Integer manager_id, LocalDateTime created_at,
			LocalDateTime updated_at) {
		super();
		this.id = id;
		this.email = email;
		this.first_name = first_name;
		this.last_name = last_name;
		this.contact_number = contact_number;
		this.rank = rank;
		this.manager_id = manager_id;
		this.created_at = created_at;
		this.updated_at = updated_at;
	}

	public Long getId() {
		return id;
	}

//	public void setId(Long id) {
//		this.id = id;
//	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getContact_number() {
		return contact_number;
	}

	public void setContact_number(String contact_number) {
		this.contact_number = contact_number;
	}

	public EmployeeRank getRank() {
		return rank;
	}

	public void setRank(EmployeeRank rank) {
		this.rank = rank;
	}

	public Integer getManager_id() {
		return manager_id;
	}

	public void setManager_id(Integer manager_id) {
		this.manager_id = manager_id;
	}

	public LocalDateTime getCreated_at() {
		return created_at;
	}

	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}

	public LocalDateTime getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(LocalDateTime updated_at) {
		this.updated_at = updated_at;
	}
	
	
	
	

}
