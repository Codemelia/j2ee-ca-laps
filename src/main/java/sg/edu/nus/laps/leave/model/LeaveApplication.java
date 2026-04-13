package sg.edu.nus.laps.leave.model;


import java.time.LocalDate;
// import java.time.LocalDateTime;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sg.edu.nus.laps.common.util.SetCreatedUpdated;
import sg.edu.nus.laps.employee.model.Employee;

@Entity
@Table(name = "leave_applications")

public class LeaveApplication extends SetCreatedUpdated {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @NotNull(message = "Start date is mandatory")
    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @NotNull(message = "End date is mandatory")
    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @Column(name = "proof", nullable = true)
    private String proof; // For medical proof url (Nullable)

    @NotBlank(message = "Reason is mandatory")
    @Column(nullable = false, length = 255)
    private String reason;

    
    @Column(name = "work_dissemination")
    private String workDissemination;

    @Column(name = "contact_details")
    private String contactDetails;

    
    @Column(name = "manager_comment")
    private String managerComment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status = LeaveStatus.DRAFT;

    // Set via common/util/SetCreatedUpdated.java
    // @Column(name = "created_at")
    // private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // @PrePersist
    // protected void onCreate() {
    //    this.createdAt = LocalDateTime.now();
    //    if (this.status == null) this.status = LeaveStatus.DRAFT;
    // }


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
    public LeaveType getLeaveType() 							{ return leaveType; }
    public void setLeaveType(LeaveType leaveType) 				{ this.leaveType = leaveType; }
    public Employee getEmployee() 								{ return employee; }
    public void setEmployee(Employee employee) 					{ this.employee = employee; }
	public String getWorkDissemination() 						{ return workDissemination; }
	public void setWorkDissemination(String workDissemination) 	{ this.workDissemination = workDissemination; }
	public String getContactDetails() 							{ return contactDetails; }
	public void setContactDetails(String contactDetails) 		{ this.contactDetails = contactDetails; }
    public String getProof() 									{ return this.proof; }
    public void setProof(String proof) 							{ this.proof = proof; }
    
}
