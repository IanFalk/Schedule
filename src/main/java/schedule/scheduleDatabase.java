package schedule;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/*  Example of database structure
 *  _________________________________
 * | 1 | 2022-08-29 | 08:00 | 14:00 |
 * | 2 | 2022-08-30 | 09:00 | 12:30 |
 * |________________________________|
 */
@Entity
@Table(name="DailySchedule")
@IdClass(scheduleDatabaseId.class)
public class scheduleDatabase implements Serializable {
    @Id
    private LocalDate date;
    @Id
    private long empId;
    private LocalTime startTime;
    private LocalTime endTime;

    protected scheduleDatabase() {}

    public scheduleDatabase(LocalDate date, long empId, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.empId = empId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getEmpId() {
        return empId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getShift() {
        return getStartTime() + "-" + getEndTime();
    }

    public String toString() {
        return "Date: " + date
        + ", empId: " + empId 
        + ", startTime: " + startTime
        + ", endTime: " + endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        scheduleDatabase databaseId = (scheduleDatabase) o;
        return date.equals(databaseId.date) &&
                startTime.equals(databaseId.startTime) &&
                endTime.equals(databaseId.endTime) &&
                empId == databaseId.empId;

    }

    @Override
    public int hashCode() {
        return Objects.hash(date, empId, startTime, endTime);
    }
}
