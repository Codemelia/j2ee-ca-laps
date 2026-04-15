package sg.edu.nus.laps.approval;

import sg.edu.nus.laps.leave.model.LeaveStatus;
import java.time.LocalDate;

public class ApprovalDTO {
    
    private Long leaveApplicationId;
    private Long employeeId;
    private String employeeName;
    private String leaveType;
    private LocalDate fromDate;
    private LocalDate toDate;
    private double daysDuration;
    private String reason;
    private LeaveStatus status;
    private String managerComment;
    private LocalDate appliedDate;
    
    // Constructors
    public ApprovalDTO() {}
    
    public ApprovalDTO(Long leaveApplicationId, Long employeeId, String employeeName, 
                       String leaveType, LocalDate fromDate, LocalDate toDate, 
                       double daysDuration, String reason, LeaveStatus status) {
        this.leaveApplicationId = leaveApplicationId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.leaveType = leaveType;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.daysDuration = daysDuration;
        this.reason = reason;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getLeaveApplicationId() {
        return leaveApplicationId;
    }
    
    public void setLeaveApplicationId(Long leaveApplicationId) {
        this.leaveApplicationId = leaveApplicationId;
    }
    
    public Long getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getEmployeeName() {
        return employeeName;
    }
    
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    
    public String getLeaveType() {
        return leaveType;
    }
    
    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }
    
    public LocalDate getFromDate() {
        return fromDate;
    }
    
    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }
    
    public LocalDate getToDate() {
        return toDate;
    }
    
    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
    
    public double getDaysDuration() {
        return daysDuration;
    }
    
    public void setDaysDuration(double daysDuration) {
        this.daysDuration = daysDuration;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LeaveStatus getStatus() {
        return status;
    }
    
    public void setStatus(LeaveStatus status) {
        this.status = status;
    }
    
    public String getManagerComment() {
        return managerComment;
    }
    
    public void setManagerComment(String managerComment) {
        this.managerComment = managerComment;
    }
    
    public LocalDate getAppliedDate() {
        return appliedDate;
    }
    
    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }
}