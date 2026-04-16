package sg.edu.nus.laps.auth.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordDTO {

    // Validation for Password change

	// ATTRIBUTES

    // Content not validated - checked in Service + DB
	@NotBlank(message = "Current password cannot be null or blank")
	private String oldRawPassword;

	@NotBlank(message = "New password cannot be null or blank")
	@Size(min = 12, max = 16, message ="New password must be between 12 and 16 characters")
	@Pattern(
		regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{12,16}$",
		message = "New password must contain 12 to 16 characters, including uppercase, lowercase, number, and special characters"
	)
	private String newRawPassword;

	@NotBlank(message = "Confirm password cannot be null or blank")
    private String confirmPassword;

	// GETTERS SETTERS

	public String getOldRawPassword() { return this.oldRawPassword; }
	public void setOldRawPassword(String oldRawPassword) { this.oldRawPassword = oldRawPassword; }
	public String getNewRawPassword() { return this.newRawPassword; }
	public void setNewRawPassword(String newRawPassword) { this.newRawPassword = newRawPassword; }
	public String getConfirmPassword() { return this.confirmPassword; }
	public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    
	// CONSTRUCTORS

	public PasswordDTO() {}
	public PasswordDTO(String oldRawPassword, String newRawPassword, String confirmPassword) {
		this.oldRawPassword = oldRawPassword;
		this.newRawPassword = newRawPassword;
		this.confirmPassword = confirmPassword;
	}

	// TO STRING

	@Override
	public String toString() {
		return "{" +
			" oldRawPassword='" + getOldRawPassword() + "'" +
			", newRawPassword='" + getNewRawPassword() + "'" +
			", confirmPassword='" + getConfirmPassword() + "'" +
			"}";
	}

}
