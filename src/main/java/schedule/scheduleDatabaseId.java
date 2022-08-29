package schedule;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class scheduleDatabaseId implements Serializable {
    private LocalDate date;
    private long empId;
    private LocalTime startTime;
    private LocalTime endTime;
    
    public scheduleDatabaseId() {
    }

    public scheduleDatabaseId(LocalDate date, long empId, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.empId = empId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        scheduleDatabaseId databaseId = (scheduleDatabaseId) o;
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
