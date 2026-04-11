package sg.edu.nus.laps.leave.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "leave_applications")

public class LeaveApplication {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @NotNull(message = "Start date is mandatory")
    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @NotNull(message = "End date is mandatory")
    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;


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
    private LeaveStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

  
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;



    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = LeaveStatus.APPLIED;
    }

   

    public LeaveApplication() {}

    public LeaveApplication(LocalDate fromDate, LocalDate toDate, String reason, LeaveType type, Employee emp) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
        this.leaveType = type;
        this.employee = emp;
    }

    

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }

    public String getManagerComment() { return managerComment; }
    public void setManagerComment(String comment) { this.managerComment = comment; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
}
