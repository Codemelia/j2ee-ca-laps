package sg.edu.nus.laps.leave.dto;

import java.util.List;

public record DataGovResponse(DataGovResult result) {

	public List<HolidayRecordDto> getRecords() {
		return (result != null) ? result.records() : List.of();
	}
}
