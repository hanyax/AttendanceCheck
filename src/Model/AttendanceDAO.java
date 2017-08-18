import java.util.TreeSet;

public interface AttendanceDAO {
	TreeSet<Record> getAttendance(String name);
}
