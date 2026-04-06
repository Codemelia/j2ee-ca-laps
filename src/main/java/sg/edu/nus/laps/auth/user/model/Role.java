package sg.edu.nus.laps.auth.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Role {

	// No Bean Validation bc roles are seed data only
	// App never creates/ updates roles for now
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 10) // JPA - MySQL constraints
	private String name;

	@Column(length = 100) // default nullable
	private String description;

	// CONSTRUCTORS

	public Role() {}
	public Role(String name, String description) {
		this.name = name;
		this.description = description;
	}

	// GETTERS SETTERS

	public Long getId() { return this.id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return this.name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return this.description; }
	public void setDescription(String description) { this.description = description; }

	// TO STRING
	
	@Override
	public String toString() {
		return "{" +
			" id='" + getId() + "'" +
			", name='" + getName() + "'" +
			", description='" + getDescription() + "'" +
			"}";
	}


}
