package Model;

public class SQLTest {
	public static void main(String[] args) {
		AttendanceDAOImplement attendance = new AttendanceDAOImplement("/Users/shawn-xu/Desktop/5.26-6.25.xlsx");
		System.out.println(attendance.getAttendance("啦啦啦"));
	}
}
