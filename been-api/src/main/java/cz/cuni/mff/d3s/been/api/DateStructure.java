package cz.cuni.mff.d3s.been.api;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Kuba Brecka
 */
public class DateStructure {
	private int year;
	private int month;
	private int day;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public DateStructure() { }

	public Date toDate() {
		GregorianCalendar calendar = new GregorianCalendar(year, month - 1, day);
		return calendar.getTime();
	}
}
