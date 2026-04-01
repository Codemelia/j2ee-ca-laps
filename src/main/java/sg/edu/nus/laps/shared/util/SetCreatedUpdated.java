package sg.edu.nus.laps.shared.util;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@MappedSuperclass // Declare as non-entity, mappings will be inherited by child classes
public abstract class SetCreatedUpdated {

	@Column(name = "created_at", nullable = false, updatable = false)
	protected LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	protected LocalDateTime updatedAt;

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	@PrePersist // set NOW() in MySQL on create
	private void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate // set NOW() in MySQL on update
	private void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

}
