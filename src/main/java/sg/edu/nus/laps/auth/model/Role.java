package sg.edu.nus.laps.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "roles")
public class Role {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 10)
	@NotBlank(message = "User role cannot be null or blank")
	private String name;

	@Column(length = 100)
	private String description;

	public Role() {}
	public Role(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public Long getId() { return this.id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return this.name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return this.description; }
	public void setDescription(String description) { this.description = description; }
	
	@Override
	public String toString() {
		return "{" +
			" id='" + getId() + "'" +
			", name='" + getName() + "'" +
			", description='" + getDescription() + "'" +
			"}";
	}

}
