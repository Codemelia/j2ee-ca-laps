package sg.edu.nus.laps.approval;

import sg.edu.nus.laps.leave.model.LeaveApplication;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "approvals")
public class ApprovalEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "leave_application_id", nullable = false)
    private LeaveApplication leaveApplication;
    
    @Column(name = "manager_comment", length = 500)
    private String managerComment;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    private ApprovalStatus approvalStatus;
    
    @Column(name = "approved_date")
    private LocalDate approvedDate;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public ApprovalEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public ApprovalEntity(LeaveApplication leaveApplication, ApprovalStatus approvalStatus) {
        this.leaveApplication = leaveApplication;
        this.approvalStatus = approvalStatus;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Lifecycle Methods
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LeaveApplication getLeaveApplication() {
        return leaveApplication;
    }
    
    public void setLeaveApplication(LeaveApplication leaveApplication) {
        this.leaveApplication = leaveApplication;
    }
    
    public String getManagerComment() {
        return managerComment;
    }
    
    public void setManagerComment(String managerComment) {
        this.managerComment = managerComment;
    }
    
    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }
    
    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
    
    public LocalDate getApprovedDate() {
        return approvedDate;
    }
    
    public void setApprovedDate(LocalDate approvedDate) {
        this.approvedDate = approvedDate;
    }
    
    public Long getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}