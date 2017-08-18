package Model;

import java.util.Date;

public class Record implements Comparable<Object> {
	private String name;
	private Date date;
	private Date arriveTime;
	private Date departTime;
	
	public Record(String name, Date date, Date arriveTime, Date departTime) {
		this(name, date, arriveTime);
		this.departTime = departTime;
	}
	
	public Record(String name, Date date, Date arriveTime) {
		this.name = name;
		this.date = date;
		this.arriveTime = arriveTime;
		this.departTime = null;
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

	@Override
	public int compareTo(Object o) {
		if (o instanceof Record) {
			return this.getDate().compareTo(((Record) o).getDate());
		} else {
			return 1;
		}
	}
}
