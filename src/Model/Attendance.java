package Model;

import java.util.*;

/**
 * Attendance represent employee's attendance records organized in following way:
 * <name1 <date1 time1 time2 time3>>
 * <		  <date2 time1 time2 time3>>
 * <name2 <date1 time1 time2 time3>>
 * <		  <date2 time1 time2 time3>>
 * 
 * Attendance 储存了员工的出勤记录 数据组织如下：
 * <姓名1 <日期1 打卡时间1 打卡时间2 打卡时间3>>
 * <		 <日期2 打卡时间1 打卡时间2 打卡时间3>>
 * <	姓名2 <日期1 打卡时间1 打卡时间2 打卡时间3>>
 * <		 <日期2 打卡时间1 打卡时间2 打卡时间3>>
 * 
 * Name is not ordered, date and time is ordered by natural ordering
 * 姓名无顺序，日期按自然时间排序，打卡时间按自然时间排序
 * 
 * There is no duplicate in Name and Time in attendance 
 * 在attendance对象中，姓名、日期、打卡时间保证不存在重复，不存在null值
 * 
 */

public class Attendance {
	
	// Representation Invariant:
	//  attendance != null &&  
	//	attendance.keySet().contains(null) == false &&
	//	日期 != null && 打卡时间 != null 
	
	private HashMap<String, TreeMap<Date, SortedSet<Date>>> attendance; 
	
	/**
	 * Check if the representation invariant holds (if any).
	 */
	public static boolean CHECK_EXPENSIVE = false;
	
	private void checkRep() {
		assert(attendance != null): "Attendance can not be null.";
		if (CHECK_EXPENSIVE) {
			for (String name : attendance.keySet()) {
				assert(name != null): "Name in attendance can not be null";
				Map<Date, SortedSet<Date>> dates = attendance.get(name);
				for (Date date : dates.keySet()) {
					assert(date != null): "Date in attendance can not be null";
					Set<Date> punchTimes = dates.get(date);
					for(Date punchTime : punchTimes) {
						assert(punchTime != null): "PunchTime can not be null";
					}
				}
			} 
		}
	}
	
	/**
	 * 新建一个空attendance实例
	 * Initialize an empty attendance instance
	 */
	public Attendance() {
		attendance = new HashMap<String, TreeMap<Date, SortedSet<Date>>>();
		checkRep();
	}
	
	/**
	 * Add a attendance record with name, date and time
	 * name, date and time can not be null
	 * No duplication of name, date, time combination will be add
	 * 
	 * 在attendance实例中加入一次由姓名，日期和时间组成的出勤记录
	 * 保证重复记录不会被加入实例
	 * 姓名，日期，时间不能为null
	 * @param name	出勤人姓名 
	 * @param date	出勤日期
	 * @param time	出勤时间
	 */
	public void add(String name, Date date, Date time) {
		assert(name != null): "Name in attendance can not be null";
		assert(date != null): "Date in attendance can not be null";
		assert(time != null): "PunchTime can not be null";
		if (attendance.containsKey(name)) {
			Map<Date, SortedSet<Date>> dates = attendance.get(name);
			if (dates.containsKey(date)) {
				dates.get(date).add(time);
			} else {
				Set<Date> times = new TreeSet<Date>();
				times.add(time);
				dates.put(date, (SortedSet<Date>) times);
			}
		} else {
			TreeMap<Date, SortedSet<Date>> dates = new TreeMap<Date, SortedSet<Date>>();
			TreeSet<Date> times = new TreeSet<Date>();
			times.add(time);
			dates.put(date, times);
			attendance.put(name, dates);
		}
		
		checkRep();
	}
	
	/**
	 * Delete all attendance records from certain date if name, date exist
	 * Otherwise, do nothing
	 * Name, date and time can not be null
	 * 
	 * 在attendance实例中删除某雇员某天的记录
	 * 如该雇员不存在 || 需删除日期不存在，不执行任何操作
	 * 
	 * @param name	雇员姓名
	 * @param date	需删除日期
	 * @param time 	需删除时间
	 */
	public void deleteDate(String name, Date date) {
		assert(name != null): "Name in attendance can not be null";
		assert(date != null): "Date in attendance can not be null";
		
		if (attendance.containsKey(name) && attendance.get(name).containsKey(date)) {
			attendance.get(name).remove(date);
		}
	}
	
	/**
	 * Delete an attendance records from certain date and time
	 * if name, date and time exist
	 * Otherwise, do nothing
	 * 
	 * 在attendance实例中删除某雇员某天某时间的记录
	 * 如该雇员不存在 || 需删除日期不存在 || 需删除时间不存在，不执行任何操作
	 * 
	 * @param name	雇员姓名
	 * @param date	需删除日期
	 * @param time	需删除时间
	 */
	public void deleteDate(String name, Date date, Date time) {
		assert(name != null): "Name in attendance can not be null";
		assert(date != null): "Date in attendance can not be null";
		assert(time != null): "PunchTime can not be null";
		
		if (attendance.containsKey(name) && attendance.get(name).containsKey(date)
			&& attendance.get(name).get(date).contains(time)) {
			attendance.get(name).get(date).remove(time);
		}
	}
	
	/**
	 * Delete all records about certain employee if the name exist
	 * Otherwise do nothing
	 * 
	 * 在attendance实例中删除某雇员的记录
	 * 如该雇员不存在，不执行任何操作
	 * 
	 * @param name 雇员姓名
	 */
	public void deletePerson(String name) {
		assert(name != null): "Name in attendance can not be null";
		
		if (attendance.containsKey(name)) {
			attendance.remove(name);
		}
	}
	
	/**
	 * Get all attendance records about certain name
	 * 得到某员工在该月的考勤记录
	 * 
	 * @param 	name
	 * @return	null 如果这个员工不在记录中 if the name does not exist 
	 * 			map  代表该员工该月出勤记录 	a map represent all the records in the following format:
	 * 			map的key为日期Date实例 :	map key is the dates of the records
	 * 				[Weekday Month Day 00:00:00 CST yyyy]
	 * 			例如：
	 * 				Fri May 26 00:00:00 CST 2017
	 * 			map的值为一个按时间排序的Date实例的set:	map values is a ordered set of records
	 * 				[Weekday Month Day hh:mm:ss CST yyyy]
	 * 			例如：
	 * 				[Fri May 26 08:07:13 CST yyyy]
	 */
	public TreeMap<Date, SortedSet<Date>> getAttendance(String name) {
		if (attendance.containsKey(name)) {
			return attendance.get(name);
		} else {
			return null;
		}
	}
	
	/**
	 * Get all names that is in attendance instance
	 * 得到所有打卡员工的姓名
	 * @return	String 所有打卡员工的名字 A set of String with names
	 */
	public Set<String> getAllNames() {
		return attendance.keySet();
	}
	
	/**
	 * string representation of Attendance in following format:
	 * 用以下方式返回该实例的String表述
	 * 
	 * date1   name1   time1 time2 time3
	 * date2   name1   time1 time2 time3
	 * date3   name1   time1 time2 time3
	 * date1   name2   time1 time2 time3
	 * date2   name2   time1 time2 time3
	 * date3   name2   time1 time2 time3
	 */
	@Override
	public String toString() {
		String result = "";
		int n = 0;
		for (String name : attendance.keySet()) {
			Map<Date, SortedSet<Date>> dates = attendance.get(name);
			for (Date date : dates.keySet()) {
				n += 1;
				Set<Date> times = dates.get(date);
				String newLine = date + "   " + name + "   " + times + "	" + n + "\n";
				result += newLine;
			}
		} 
		return result;
	}
}
