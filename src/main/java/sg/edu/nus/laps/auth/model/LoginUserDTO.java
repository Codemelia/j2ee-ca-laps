package sg.edu.nus.laps.auth.model;

import jakarta.validation.constraints.NotBlank;

public class LoginUserDTO {

	// Basic validation here for form intake

    // ATTRIBUTES

	@NotBlank(message = "User email is required")
	private String email;

	@NotBlank(message = "Password is required")
	private String password;

    // GETTERS SETTERS

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }

    // CONSTRUCTORS

    public LoginUserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
	public LoginUserDTO(String email) {
		this.email = email;
	}
    public LoginUserDTO() {}

    // TO STRING

    @Override
    public String toString() {
        return "{" +
            " email='" + getEmail() + "'" +
            ", password='" + getPassword() + "'" +
            "}";
    }

}
