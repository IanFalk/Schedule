package schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface scheduleDataRepository extends JpaRepository<scheduleDatabase, scheduleDatabaseId>{
  List<scheduleDatabase> findByDate(LocalDate date);
  List<scheduleDatabase> findByEmpId(long empId);
  List<scheduleDatabase> findByStartTime(LocalTime startTime);
  List<scheduleDatabase> findByEndTime(LocalTime endTime);
}