package sg.edu.nus.laps.auth.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import sg.edu.nus.laps.employee.model.Employee;

@Entity
@Table(name = "users")
public class User {
	
	@Id
	private String email;

}
