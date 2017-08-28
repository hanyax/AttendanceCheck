package Model;

import java.util.*;
import java.util.Date;
import java.sql.*;
import javax.sql.DataSource;
 
/**
 * Build attendance from excel file and Database IO for MySQL database
 * 读取考勤Excel表格 提供MySQL数据库读写的实现
 */
public class AttendanceDAOImplement implements AttendanceDAO {
	private DataSource dataSource;
	private String normalTableName;
	private String abnormalTableName;
	
	/**
	 *	Build attendance instance form excel file specified by fildPath
	 *	and output all record to database
	 *
	 * 	读取filePath的excel表格 将数据储存在attendance实例中并
	 * 	初始化 AttendanceDAOImplement 实例
	 *
	 * @param defaultFolderPath 	要读取的excel的文件路径	
	 * @param dataSource			建立sql连接所需资源实例	dataSource for mysql connection
	 * @param normalTableName	正常考勤记录数据库表名 	mysql table name for normal records
	 * @param abnormalTableName	非正常考勤记录数据库表名	mysql table name for abnormal records
	 */
	public AttendanceDAOImplement(String defaultFolderPath, DataSource dataSource, 
			String normalTableName, String abnormalTableName) {
		this.normalTableName = normalTableName;
		this.abnormalTableName = abnormalTableName;
		this.dataSource = dataSource;
		Attendance attendance = ExcelParcer.buildAttendance(defaultFolderPath);
		writeDataBase(attendance);
	}
	
	/**
	 * Get all normal attendance records from database about certain employee 
	 * specified by name parameter
	 * 	
	 * 从数据库中取出某员工的正常出勤记录
	 * 
	 * @param 	员工姓名
	 * @return 	员工查询到的出勤记录 	TreeSet of Records for this employee 
	 * 							 	Empty TreeSet if the name does not exist in database
	 */
	@Override
	public TreeSet<Record> getAttendance(String name) {
		Connection conn = null;
		PreparedStatement stmt = null;
		SQLException ex = null;
		TreeSet<Record> records = new TreeSet<Record>();
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("SELECT date, name, arrive, depart FROM "
					+ normalTableName + " WHERE name = ?");
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				records.add(new Record(rs.getString("name"), 
						rs.getDate("date"), rs.getTimestamp("arrive"), rs.getTimestamp("depart")));
			}
		} catch (SQLException e) {
			ex = e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					ex = e;
				}
			}
			
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					ex = e;
				}
			}
			
			if (ex != null) {
				throw new RuntimeException(ex);
			}
		}
		return records;
	}
	
	/**	
	 * Get all abnormal attendance records(one record for a date) from database about certain employee 
	 * specified by name parameter
	 * 
	 * 从数据库中取出某员工的异常(一天打卡一次)出勤记录 	
	 * 
	 * @param 	name 员工姓名
	 * @return	员工查询到的出勤记录	TreeSet of Records for this employee 
	 * 							 	Empty TreeSet if the name does not exist in database
	 */
	public TreeSet<Record> getAbnormalAttendance(String name) {
		Connection conn = null;
		PreparedStatement stmt = null;
		SQLException ex = null;
		TreeSet<Record> records = new TreeSet<Record>();
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("SELECT date, name, time FROM " + 
					abnormalTableName + " WHERE name = ?");
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				records.add(new Record(rs.getString("name"), 
						rs.getDate("date"), rs.getTimestamp("time")));
			}
		} catch (SQLException e) {
			ex = e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					ex = e;
				}
			}
			
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					ex = e;
				}
			}
			
			if (ex != null) {
				throw new RuntimeException(ex);
			}
		}
		return records;
	}
	
	/**
	 * Write all normal attendance records in attendance object 
	 * to MySQL table in the following format:
	 * 		Date Name First-time Last-time
	 * 
	 * Write all abnormal(one record for a date) attendance records in attendance object 
	 * to MySQL table in the following format:
	 * 		Date Name Time
	 * 	
	 * 将attendance中的考勤记录以以下格式写入数据库：
	 * 	日期 姓名 第一次打卡时间 最后一次打卡时间
	 * 如当日仅有一次打卡记录， 则以以下格式写入另一数据库
	 *	日期 姓名 打卡时间
	 */
	@Override
	public void writeDataBase(Attendance attendance) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("can not find class");
		}
		
		Connection conn = null;
		PreparedStatement stmt = null;
		PreparedStatement abnormal = null;
		SQLException ex = null;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("INSERT IGNORE INTO " + normalTableName + "(date, name, arrive, depart) VALUES(?,?,?,?)");	//
			abnormal = conn.prepareStatement("INSERT IGNORE INTO " + abnormalTableName + "(date, name, time) VALUES(?,?,?)");	// 
			for (String name : attendance.getAllNames()) {
				TreeMap<Date, SortedSet<Date>> records = attendance.getAttendance(name);
				for (Date date : records.keySet()) {
					SortedSet<Date> times = records.get(date);
					if (times.size() > 1) {
						Date arrive = times.first();
						Date depart = times.last();
						stmt.setDate(1, new java.sql.Date(date.getTime()));
						stmt.setString(2, name);
						stmt.setTimestamp(3, new Timestamp(arrive.getTime()));
						stmt.setTimestamp(4, new Timestamp(depart.getTime()));
						stmt.addBatch();
					} else if (times.size() == 1){
						Date time = times.first();
						abnormal.setDate(1, new java.sql.Date(date.getTime()));
						abnormal.setString(2, name);
						abnormal.setTimestamp(3, new Timestamp(time.getTime()));
						abnormal.addBatch();
					}
				}
			}
			stmt.executeBatch();
			abnormal.executeBatch();
		} catch (SQLException e) {
			ex = e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					ex = e;
				}
			}
			
			if (abnormal != null) {
				try {
					abnormal.close();
				} catch (SQLException e) {
					ex = e;
				}
			}
			
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					ex = e;
				}
			}
			
			if (ex != null) {
				throw new RuntimeException(ex);
			}
			
		}
	}
}
