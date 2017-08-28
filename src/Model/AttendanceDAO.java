package Model;

import java.util.TreeSet;

/**
 *	An interface specified all methods that are required for database IO
 */
public interface AttendanceDAO {
	void writeDataBase(Attendance attendance);
	TreeSet<Record> getAttendance(String name);
	TreeSet<Record> getAbnormalAttendance(String name);
}
