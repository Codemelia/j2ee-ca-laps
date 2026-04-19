package sg.edu.nus.laps.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequestDTO {
    
    // Light validation for login

	@NotBlank(message = "Company email is required")
	@Email(message = "Company email is invalid")
	@Size(max = 256, message = "Company email must not be more than 256 characters") // Prevent large payload
	private String email;

	@NotBlank(message = "Password is required")
	@Size(max = 100, message = "Password must not be more than 100 characters") // Prevent large payload
	private String password;

	public String getEmail() { return this.email; }
	public void setEmail(String email) { this.email = email; }
	public String getPassword() { return this.password; }
	public void setPassword(String password) { this.password = password; }

	public LoginRequestDTO() {}
	public LoginRequestDTO(String email, String password) {
		this.email = email;
		this.password = password;
	}

}
