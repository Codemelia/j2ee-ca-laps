package sg.edu.nus.laps.leave.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import sg.edu.nus.laps.leave.dto.DataGovResponse;
import sg.edu.nus.laps.leave.model.Holiday;
import sg.edu.nus.laps.leave.repository.HolidayRepository;

@Service
public class HolidayService {
	
	@Autowired
	private HolidayRepository holidayRepo;
	
	private final String DATASET_ID = "d_149b61ad0a22f61c09dc80f2df5bbec8";
	private final String BASE_URL = "https://data.gov.sg/api/action/datastore_search?resource_id=" + DATASET_ID + "&limit=100";
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	@PostConstruct
	public void init() {
		if (holidayRepo.count() == 0) {
			fetchAndSyncHolidays();
		}
	}
	
	@Transactional
	@Scheduled(cron = "0 0 0 1 1 ?")
	public void fetchAndSyncHolidays() {
		try {
			// 1. FETCH JSON from data.gov.sg
			DataGovResponse response = restTemplate.getForObject(BASE_URL, DataGovResponse.class);
			
			// 2. Attempt to MAP Holiday RECORDS
			if (response != null && response.result() != null) {
				response.result().records().forEach(record -> {
					if (!holidayRepo.existsByDate(record.date())) {
						Holiday h = new Holiday();
						h.setName(record.holiday());
						h.setDate(record.date());
						holidayRepo.save(h);
					}
				});
			}
		} catch (Exception a) {
			System.err.println("Failed to Sync Holidays: " + a.getMessage());
		}
	}
}
