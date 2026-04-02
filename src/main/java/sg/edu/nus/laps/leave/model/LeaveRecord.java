package sg.edu.nus.laps.leave.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import sg.edu.nus.laps.employee.model.Employee;
import sg.edu.nus.laps.shared.util.SetCreatedUpdated;

/* 
 * Successful Transactional Leave Application
 * 
 *  Association: 
 *  	@ManyToOne 	-> Employee	(Multiple Leave Records belong to One Employee)
 *  	@ManyToOne	-> LeaveType (Multiple Leave Records belong to One Leave Type)
 *  
 *  Data Types:
 *  	Long			- id
 *  	Long			- employeeId
 *  	Long			- leave_typeId
 *  	Double			- entitledDays
 *  	Double			- consumedDays
 *  	LocalDateTime	- createdAt
 *  	LocalDateTime	- updatedAt 
 */

@Entity
@Table(name = "leave_records")
public class LeaveRecord extends SetCreatedUpdated {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// 1. Setting the Attributes
	@Column(name = "entitled_days", nullable = false)
	@NotNull(message = "Entitled Days is Mandatory")
	@PositiveOrZero(message = "Entitled Days must be Positive")
	private Double entitledDays;
	
	@Column(name = "consumed_days", nullable = false)
	@NotNull(message = "Consumed Days is Mandatory")
	@PositiveOrZero(message = "Consumed Days must be Positive")
	private Double consumedDays;
	
	// 2. Setting the Associations
	// @ManyToOne 	-> Employee	(Multiple Leave Records belong to One Employee)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = false)
	@NotNull(message = "Employee is Mandatory")
	private Employee employee;
	
	// @ManyToOne	-> LeaveType (Multiple Leave Records belong to One Leave Type)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "leave_type_id", nullable = false)
	@NotNull(message = "Leave Type is Mandatory")
	private LeaveType leaveType;
	
	// 3. Setting the Constructors
	public LeaveRecord() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LeaveRecord(Double entitledDays, Double consumedDays, Employee employee, LeaveType leaveType) {
		this.entitledDays = entitledDays;
		this.consumedDays = consumedDays;
		this.employee = employee;
		this.leaveType = leaveType;
	}
	
	// 4. Setting Setters & Getters
	public Long getId() 								{ return id; }
	public Double getEntitledDays() 					{ return entitledDays; }
	public Double getConsumedDays() 					{ return consumedDays; }
	public Employee getEmployee() 						{ return employee; }
	public LeaveType getLeaveType() 					{ return leaveType; }

	public void setEntitledDays(Double entitledDays) 	{ this.entitledDays = entitledDays; }
	public void setConsumedDays(Double consumedDays) 	{ this.consumedDays = consumedDays; }
	public void setEmployee(Employee employee) 			{ this.employee = employee; }
	public void setLeaveType(LeaveType leaveType) 		{ this.leaveType = leaveType; }
	
	// 5. Override ToString Method
	@Override
	public String toString() {
		return "LeaveRecord ["
				+ "id=" + id 
				+ ", entitledDays=" + entitledDays 
				+ ", consumedDays=" + consumedDays
				+ ", employee=" + employee 
				+ ", leaveType=" + leaveType 
				+ "]";
	}	
}
