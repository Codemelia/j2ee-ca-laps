package sg.edu.nus.laps.leave.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sg.edu.nus.laps.common.util.SetCreatedUpdated;

@Entity
@Table(name = "leave_types")
public class LeaveType extends SetCreatedUpdated {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="leaveType" ,nullable=false,length = 20)
	@NotNull(message = "Leave type is mandatory")
	@Size(max = 20, message = "Leave type cannot exceed 20 characters")
	private String leaveType;
	
	@Column(name="leaveDescription", length = 200)
	@Size(max = 200, message = "Description cannot exceed 200 characters")
	private String leaveDescription;
	
	@OneToMany(mappedBy = "leaveType", fetch = FetchType.LAZY)
	private List<LeaveRecord> leaveRecords = new ArrayList<>();
	
	@OneToMany(mappedBy = "leaveType", fetch = FetchType.LAZY)
	private List<LeaveApplication> leaveApplication = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public String getLeaveDescription() {
		return leaveDescription;
	}

	public void setLeaveDescription(String leaveDescription) {
		this.leaveDescription = leaveDescription;
	}

	public List<LeaveRecord> getLeaveRecords() {
		return leaveRecords;
	}

	public void setLeaveRecords(List<LeaveRecord> leaveRecords) {
		this.leaveRecords = leaveRecords;
	}

	public List<LeaveApplication> getLeaveApplication() {
		return leaveApplication;
	}

	public void setLeaveApplication(List<LeaveApplication> leaveApplication) {
		this.leaveApplication = leaveApplication;
	}

	public LeaveType(Long id,
		@NotNull(message = "Leave type is mandatory") @Size(max = 20, message = "Leave type cannot exceed 20 characters") String leaveType,
		@Size(max = 200, message = "Description cannot exceed 200 characters") String leaveDescription,
		List<LeaveRecord> leaveRecords, List<LeaveApplication> leaveApplication) {
		super();
		this.id = id;
		this.leaveType = leaveType;
		this.leaveDescription = leaveDescription;
		this.leaveRecords = leaveRecords;
		this.leaveApplication = leaveApplication;
	}

	public LeaveType() {
		super();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	LeaveType other = (LeaveType) obj;
		return Objects.equals(id, other.id);
	}
	@Override
	public String toString() {
		return "LeaveType [id=" + id + ", leaveType=" + leaveType + ", leaveDescription=" + leaveDescription + "]";
	}
}
