package Model;

import java.io.*;
import java.util.*;
import java.util.Date;

import java.sql.*;
import javax.sql.DataSource;

import java.text.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
 
/**
 * 读取考勤Excel表格 提供MySQL数据库读写的实现
 */
public class AttendanceDAOImplement implements AttendanceDAO {
	private Attendance attendance;
	private DataSource dataSource;
	private String normalTableName;
	private String abnormalTableName;
	private String defaultFolderPath;
	
	/**
	 * 	/** 读取filePath的excel表格 将数据储存在attendance实例中并
	 * 	初始化 AttendanceDAOImplement 实例
	 *
	 * @param dataSource	
	 * @param defaultFolderPath 	要读取的excel的文件路径
	 * @param dataSource			建立sql连接所需资源实例
	 * @param normalTableName	正常考勤记录数据库表名
	 * @param abnormalTableName	非正常考勤记录数据库表名
	 */
	public AttendanceDAOImplement(String defaultFolderPath, DataSource dataSource, 
			String normalTableName, String abnormalTableName) {
		this.normalTableName = normalTableName;
		this.abnormalTableName = abnormalTableName;
		this.defaultFolderPath = defaultFolderPath;
		this.dataSource = dataSource;
		attendance = buildAttendance(defaultFolderPath);
		writeDataBase();
	}
	
	
	/**	从数据库中取出某员工的正常出勤记录
	 * 
	 * @param 	员工姓名
	 * @return 	员工查询到的出勤记录
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
	
	/**	从数据库中取出某员工的异常(一天打卡一次)出勤记录
	 * 
	 * @param name
	 * @return
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
	
	/**	将attendance中的考勤记录以以下格式写入数据库：
	 * 		日期 姓名 第一次打卡时间 最后一次打卡时间
	 *	如当日仅有一次打卡记录， 则以以下格式写入另一数据库
	 *		日期 姓名 打卡时间
	 */
	private void writeDataBase() {
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
	
	/**	读取考勤Excel表格 将数据储存在attendance对象中
	 * 
	 * @param 	folderName 	记录打卡的excel文档文件夹名
	 * @param 	attendance	组织数据的数据结构
	 * @return 	attendance	建立好的打卡数据结构
	 * @throws 	IOException 
	 * @throws 	FileNotFoundException 
	 */
	private Attendance buildAttendance(String folderPath) {
		Attendance attendance = new Attendance();
		File files = null;
		String[] paths;
		try {
			files = new File(defaultFolderPath);
			// list of files and directories
			paths = files.list();
			// for each name in the path array
			for (String path : paths) {
				if (path.endsWith(".xlsx")) {
					FileInputStream file = new FileInputStream(new File(defaultFolderPath + "/" + path));
					XSSFWorkbook workbook = new XSSFWorkbook(file);
					XSSFSheet sheet = workbook.getSheetAt(0);
					Iterator<Row> rowIterator = sheet.iterator();
					
					while (rowIterator.hasNext()) {
						Row row = rowIterator.next();
						Iterator<Cell> cellIterator = row.cellIterator();
						while (cellIterator.hasNext()) {
							String name = cellIterator.next().getStringCellValue() ;
		                		String dateAndTime = cellIterator.next().getStringCellValue();
		                		if (!name.isEmpty() && !dateAndTime.isEmpty()) {            		
		    	                		Date[] dateArray = getDateAndTime(dateAndTime);
			                		if (dateArray != null) {
				                		Date date = dateArray[0];
				                		Date time = dateArray[1];
				                		attendance.add(name, date, time);
			                		}
		                		}
						}
					}
					workbook.close();
					file.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attendance;
	}
	
	/**	将日期和时间从string转化为Date实例
	 * @param 	dateAndTime 日期和时间的String形式
	 * @return	一个储存了日期和时间Date实例的Array
	 * 			null 如果单元格为空
	 * @throws 	ParseException
	 */
	private static Date[] getDateAndTime(String dateAndTime) throws ParseException {
		if (!dateAndTime.isEmpty()) {
			// 提取time
			String timeString = dateAndTime.substring(dateAndTime.indexOf("午") + 1).trim();
			// 提取date
			String dateString;
			if (dateAndTime.indexOf("上") != -1) {
				dateString = dateAndTime.substring(0, dateAndTime.indexOf("上")).trim();
			} else if (dateAndTime.indexOf("下") != -1){
				dateString = dateAndTime.substring(0, dateAndTime.indexOf("下")).trim();
				// 将time转换为24小时制 
				String hourString = timeString.substring(0, 2);
				int hour = Integer.parseInt(hourString) + 12;
				hourString = Integer.toString(hour);
				timeString = hourString + timeString.substring(2);
			} else {
				dateString = null;
			}
			
			if (dateString != null) {
				// 将date转化为符合SimpleDateFormat的格式
				if (dateString.charAt(dateString.indexOf("-") + 2) == '-') {
					dateString = dateString.substring(0, dateString.indexOf("-") + 1) 
								 + "0" +  dateString.substring(dateString.indexOf("-") + 1);
				}
				if ((dateString.lastIndexOf("-") + 2) >= dateString.length()) {
					dateString = dateString.substring(0, dateString.lastIndexOf("-") + 1) 
							 + "0" +  dateString.substring(dateString.lastIndexOf("-") + 1);
				}
				// Parse date
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = sdf.parse(dateString);
				
				// Parse time
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date time = sdf.parse(dateString + " " + timeString);
				
				Date[] result = new Date[2];
				result[0] = date;
				result[1] = time;	
				return result;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
}
