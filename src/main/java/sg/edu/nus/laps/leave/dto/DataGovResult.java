package sg.edu.nus.laps.leave.dto;

import java.util.List;

import sg.edu.nus.laps.leave.model.Holiday;

public record DataGovResult(List<HolidayRecordDto> records) {

	public List<Holiday> getHolidayEntities() {
		return records.stream()
				.map(HolidayRecordDto::toEntity)
				.toList();
	}
}
