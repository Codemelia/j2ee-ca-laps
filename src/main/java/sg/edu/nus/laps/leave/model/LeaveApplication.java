package sg.edu.nus.laps.leave.model;


import java.time.LocalDate;
// import java.time.LocalDateTime;

import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
// import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sg.edu.nus.laps.common.util.SetCreatedUpdated;
import sg.edu.nus.laps.employee.model.Employee;

@Entity
@Table(name = "leave_applications")
public class LeaveApplication extends SetCreatedUpdated {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Start date is mandatory")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @NotNull(message = "End date is mandatory")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @URL(message="Proof URL must be in a valid URL format")
    @Size(max = 2048, message = "Proof URL cannot exceed 2048 characters")
    @Column(name = "proof", nullable = true)
    private String proof; // For medical proof url (Nullable)

    @NotBlank(message = "Reason is mandatory")
    @Column(nullable = false, length = 255)
    private String reason;

    @Size(max = 255, message = "Work dissemination cannot exceed 255 characters")
    @Column(name = "work_dissemination", nullable = true)
    private String workDissemination;

    @Size(max = 255, message = "Contact details cannot exceed 255 characters")
    @Column(name = "contact_details", nullable = true)
    private String contactDetails;
    
    @Size(max = 255, message = "Manager comment cannot exceed 255 characters")
    @Column(name = "manager_comment", nullable = true)
    private String managerComment;

    @Column(name = "is_half_day")
    private boolean isHalfDay;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status = LeaveStatus.DRAFT;
    
    @Transient // This tells JPA not to create a column in MySQL
    private double duration;


    public double getDuration() { return duration;	}
	public void setDuration(double duration) {this.duration = duration;}
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;

    @Transient
    private Long leaveTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public LeaveApplication() {}

    public LeaveApplication(LocalDate fromDate, LocalDate toDate, String reason, LeaveType type, Employee emp) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
        this.leaveType = type;
        this.employee = emp;
    }

    public Long getId() 										{ return id; }
    public void setId(Long id) 									{ this.id = id; }
    public LocalDate getFromDate() 								{ return fromDate; }
    public void setFromDate(LocalDate fromDate) 				{ this.fromDate = fromDate; }
    public LocalDate getToDate() 								{ return toDate; }
    public void setToDate(LocalDate toDate) 					{ this.toDate = toDate; }
    public String getReason() 									{ return reason; }
    public void setReason(String reason) 						{ this.reason = reason; }
    public LeaveStatus getStatus() 								{ return status; }
    public void setStatus(LeaveStatus status) 					{ this.status = status; }
    public String getManagerComment() 							{ return managerComment; }
    public void setManagerComment(String comment) 				{ this.managerComment = comment; }
    public boolean isHalfDay() 									{ return isHalfDay; }
	public void setHalfDay(boolean isHalfDay) 					{ this.isHalfDay = isHalfDay; }
	public LeaveType getLeaveType() 							{ return leaveType; }
    public void setLeaveType(LeaveType leaveType) 				{ this.leaveType = leaveType; }
    public Long getLeaveTypeId() 								{ return leaveTypeId; }
    public void setLeaveTypeId(Long leaveTypeId) 				{ this.leaveTypeId = leaveTypeId; }
    public Employee getEmployee() 								{ return employee; }
    public void setEmployee(Employee employee) 					{ this.employee = employee; }
	public String getWorkDissemination() 						{ return workDissemination; }
	public void setWorkDissemination(String workDissemination) 	{ this.workDissemination = workDissemination; }
	public String getContactDetails() 							{ return contactDetails; }
	public void setContactDetails(String contactDetails) 		{ this.contactDetails = contactDetails; }
    public String getProof() 									{ return this.proof; }
    public void setProof(String proof) 							{ this.proof = proof; }
    
}
