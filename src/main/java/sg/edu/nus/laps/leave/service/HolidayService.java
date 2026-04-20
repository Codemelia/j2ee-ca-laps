package sg.edu.nus.laps.leave.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
// import org.springframework.boot.context.event.ApplicationReadyEvent;
// import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import sg.edu.nus.laps.leave.dto.DataGovResponse;
import sg.edu.nus.laps.leave.model.Holiday;
import sg.edu.nus.laps.leave.repository.HolidayRepository;

/**
 * HolidayService fetches public holiday data from an open-source API and syncs it
 * with a database, ensuring the database is updated annually at the beginning of the new year.
 */
@Service
public class HolidayService {
	
	private HolidayRepository holidayRepo;
	public HolidayService(HolidayRepository holidayRepo) { this.holidayRepo = holidayRepo; }
	
	/* 
	 * 1. API URL Link --> Open-source API via data.gov.sg.
	 * 		Source : Ministry of Manpower. (2025). Public Holidays for 2026 (2025) 
	 * 		[DataSet]. data.gov.sg from https://data.gov.sg/datasets/d_149b61ad0a22f61c09dc80f2df5bbec8
	 */
	private final String DATASET_ID = "d_149b61ad0a22f61c09dc80f2df5bbec8";
	private final String BASE_URL = "https://data.gov.sg/api/action/datastore_search?resource_id=" + DATASET_ID;
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	// // 2. Validate if Holiday DB is Empty, otherwise Fetch Data via API
	// @PostConstruct
	// public void init() {
	// 	if (holidayRepo.count() == 0) {
	// 		fetchAndSyncHolidays();
	// 	}
	// }
	
	/*
	 * 3. @Scheduled Annotation --> Attempt to mimic system-like behaviour of annually refresh Holiday DB at the beginning 
	 * 		of the new year. Does not include unscheduled public holidays such as Polling Day. 
	 */
	@Transactional
	// @EventListener(ApplicationReadyEvent.class) // Run once every time app starts (seeding)
	@Scheduled(cron = "0 0 0 1 1 ?")
	public void fetchAndSyncHolidays() {
		try {
			// a. FETCH JSON Data from data.gov.sg
			DataGovResponse response = restTemplate.getForObject(BASE_URL, DataGovResponse.class);
			
			// b. MAP Holiday to LEAVE RECORDS DB
			if (response != null && response.result() != null) {
				response.result().records().forEach(record -> {
					if (!holidayRepo.existsByDate(record.date())) {
						Holiday h = new Holiday();
						h.setName(record.holiday());
						h.setDate(record.date());
						h.setLocation("Singapore");
						holidayRepo.save(h);
					}
				});
			}
		} catch (Exception a) {
			System.err.println("Unable to Update Public Holidays in DB: " + a.getMessage());
		}
	}
}
