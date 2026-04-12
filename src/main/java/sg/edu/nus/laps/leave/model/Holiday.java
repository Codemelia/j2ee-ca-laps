package sg.edu.nus.laps.leave.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "holidays")
public class Holiday {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	// Default Constructor
	public Holiday() {
	}

	// Parameterized Constructor
	public Holiday(String name, LocalDate date) {
		this.name = name;
		this.date = date;
	}

	// Getters and Setters
	public Long getId() 					{ return id; }
	public String getName() 				{ return name; }
	public void setName(String name) 		{ this.name = name; }
	public LocalDate getDate() 				{ return date; }
	public void setDate(LocalDate date) 	{ this.date = date; }
}
