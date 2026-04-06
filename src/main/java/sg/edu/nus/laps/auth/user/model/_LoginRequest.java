package sg.edu.nus.laps.auth.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// UNUSED SINCE USING SPRING SECURITY
// DTO: takes in user login credentials
public class _LoginRequest {

    // VARIABLES

    @NotBlank(message = "Company is required")
    @Email(message = "Company is invalid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters") // 72 is BCrypt effective input limit
    private String password;

    // CONSTRUCTORS

    public _LoginRequest() {}
    public _LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // GETTERS SETTERS

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }

}
