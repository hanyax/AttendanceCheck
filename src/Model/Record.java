package Model;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Record implements Comparable<Object> {
	private String name;
	private Date date;
	private Date arriveTime;
	private Date departTime;
	private long totalTime;
	private boolean isEightHour;
	
	public Record(String name, Date date, Date arriveTime, Date departTime) {
		this(name, date, arriveTime);
		this.departTime = departTime;
		this.totalTime = getDateDiff(arriveTime, departTime,TimeUnit.MINUTES);
		this.isEightHour = (totalTime > (8 * 60));
	}
	
	public Record(String name, Date date, Date arriveTime) {
		this.name = name;
		this.date = date;
		this.arriveTime = arriveTime;
		this.departTime = null;
		this.totalTime = 0;
		this.isEightHour = false;
	}
	
	public String getName() {
		return name;
	}
	
	public Date getDate() {
		return date;
	}
	
	public Date getArriveTime() {
		return arriveTime;
	}
	
	public Date getDepartTime() {
		return departTime;
	}
	
	public boolean isEightHour() {
		return this.isEightHour;
	}
	
	public long getTotalTime() {
		return this.totalTime;
	}
	
	/**
	 * Get time difference between two dates in minutes
	 * @param date1 the oldest date
	 * @param date2 the newest date
	 * @param timeUnit the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	private long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof Record) {
			return this.getDate().compareTo(((Record) o).getDate());
		} else {
			return 1;
		}
	}
}
