package sg.edu.nus.laps.claim;

import java.time.LocalDate;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import sg.edu.nus.laps.common.util.SetCreatedUpdated;
import sg.edu.nus.laps.employee.model.Employee;

@Entity
@Table(name = "overtime_claims")
public class OvertimeClaim extends SetCreatedUpdated {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Worked date is mandatory")
    @FutureOrPresent(message = "Worked date must not be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "worked_date", nullable = false)
    private LocalDate workedDate;

    @NotNull(message = "Claimed compensation units are mandatory")
    @PositiveOrZero(message = "Claimed compensation units must be positive or zero")
    @Column(name = "claimed_days", nullable = false)
    private double claimedDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OvertimeClaimStatus status = OvertimeClaimStatus.APPLIED; // Default to APPLIED

    // Many overtime claims to one employee
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public OvertimeClaim() {}
    public OvertimeClaim(Long id, LocalDate workedDate, double claimedDays,
        OvertimeClaimStatus status, Employee employee) {
        this.id = id;
        this.workedDate = workedDate;
        this.claimedDays = claimedDays;
        this.status = status;
        this.employee = employee;
    }

    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getWorkedDate() { return this.workedDate; }
    public void setWorkedDate(LocalDate workedDate) { this.workedDate = workedDate; }
    public double getClaimedDays() { return this.claimedDays; }
    public void setClaimedDays(double claimedDays) { this.claimedDays = claimedDays; }
    public OvertimeClaimStatus getStatus() { return this.status; }
    public void setStatus(OvertimeClaimStatus status) { this.status = status; }
    public Employee getEmployee() { return this.employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    @AssertTrue(message = "Claimed compensation units must be in increments of 0.5")
    private boolean isValidClaimUnits() {
        if (claimedDays < 0) { return false; }
        // Check whether units are in increments of 0.6
        double multi = claimedDays * 2; // Always integer (expected)
        return Math.abs(multi - Math.round(multi)) < 1e-9; // Checks whether value is extremely close to integer
    }

}
