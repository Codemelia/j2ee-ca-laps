package sg.edu.nus.laps.leave.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sg.edu.nus.laps.leave.model.LeaveRecord;

@Repository
public interface LeaveRecordRepository extends JpaRepository<LeaveRecord, Long> {

}
