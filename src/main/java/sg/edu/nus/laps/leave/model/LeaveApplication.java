package sg.edu.nus.laps.leave.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import sg.edu.nus.laps.employee.model.Employee;

@Entity
@Table(name = "leave_applications")
public class LeaveApplication {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// ASSOCIATIONS

	// Writing first to avoid application run errors
	// Please remove comment after model is updated
	@ManyToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;

	// @ManyToOne	-> LeaveType (Multiple Leave Applications belong to One Leave Type)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "leave_type_id", nullable = false)
	@NotNull(message = "Leave Type is Mandatory")
	private LeaveType leaveType;

}
