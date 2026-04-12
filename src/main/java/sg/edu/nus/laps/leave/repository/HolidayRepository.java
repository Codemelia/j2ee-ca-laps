package sg.edu.nus.laps.leave.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sg.edu.nus.laps.leave.model.Holiday;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
	
	// 1. Read-Method: Check if Holiday is in DB
	boolean existsByDate(LocalDate date);
	
	// 2. Read-Method: Find a Specific Holiday by Date
	Optional<Holiday> findByDate(LocalDate date);
	
	// 3. Custom Query:List All Holiday by Year
	@Query("SELECT h FROM Holiday h WHERE YEAR(h.date) = :year")
	List<Holiday> findByYear(@Param("year") int year);
	
	// 4. Read-Methdo: List All Holiday within Specified Date Range
	List<Holiday> findByDateBetween(LocalDate fromDate, LocalDate toDate);
	
	//5. Calc-Method: Count No. of Holiday within Specified Date Range
	long countByDateBetween(LocalDate fromDate, LocalDate toDate);
}
