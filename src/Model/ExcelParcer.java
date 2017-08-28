package Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Parse records from excel file and store it into attendance object
 * @author ShawnXu
 *
 */
public class ExcelParcer {
	
	/**	
	 * Read excel file and store attendance records in attendance object
	 * 
	 * 读取考勤Excel表格 将数据储存在attendance对象中
	 * 
	 * @param 	folderName 	记录打卡的excel文档文件夹名
	 * @param 	attendance	组织数据的数据结构
	 * @return 	attendance	建立好的打卡数据结构
	 * @throws 	IOException 
	 * @throws 	FileNotFoundException 
	 */
	public static Attendance buildAttendance(String folderPath) {
		Attendance attendance = new Attendance();
		File files = null;
		String[] paths;
		try {
			files = new File(folderPath);
			// list of files and directories
			paths = files.list();
			// for each name in the path array
			for (String path : paths) {
				if (path.endsWith(".xlsx")) {
					FileInputStream file = new FileInputStream(new File(folderPath + "/" + path));
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
	
	/**	
	 * Turn date and time string to Date object
	 * 将日期和时间从string转化为Date实例
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
