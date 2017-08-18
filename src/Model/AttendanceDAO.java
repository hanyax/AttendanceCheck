package Model;

import java.util.TreeSet;

public interface AttendanceDAO {
	TreeSet<Record> getAttendance(String name);
	TreeSet<Record> getAbnormalAttendance(String name);
}
