package sg.edu.nus.laps.leave.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import sg.edu.nus.laps.leave.model.Holiday;

public record HolidayRecordDto(
		@JsonProperty("Date")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		LocalDate date,
		@JsonProperty("Holiday") String holiday,
		@JsonProperty("Day") String day) {
	
	// 1. Convert DTO to JPA Entity
	public Holiday toEntity() {
		return new Holiday(this.holiday, this.date);
	}

}
