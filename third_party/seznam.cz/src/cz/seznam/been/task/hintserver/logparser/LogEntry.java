/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jiri Tauber
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2010 Distributed Systems Research Group,
 *  Faculty of Mathematics and Physics, Charles University in Prague
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License version 2.1, as published by the Free Software Foundation.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *  MA  02111-1307  USA
 */
package cz.seznam.been.task.hintserver.logparser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Data class for log parser. It represents one entry in the log that is
 * of some interest for further processing.
 * 
 * @author Jiri Tauber
 */
class LogEntry {

	/**
	 * Comparator class for sorting entries by date in ascending order
	 *
	 * @author Jiri Tauber
	 */
	public static class dateComparator implements Comparator<LogEntry> {
		@Override
		public int compare(LogEntry o1, LogEntry o2) {
			return o1.date.compareTo(o2.date);
		}
	}

	public static final String VALID_LOG_LINE_REGEX = "(\\d{4}/\\d{2}/\\d{2} \\d{1,2}:\\d{2}:\\d{2})"+
			"I3: \\[\\d+\\]:[^']+'([^']+)'.+status=(\\d+).+time=([\\d.,]+).+client=([\\d.:]+).*";

	private static final Pattern validLogLinePattern = Pattern.compile(VALID_LOG_LINE_REGEX);
	/** Format of the date in log */
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat();
	{
		dateFormat.applyPattern("yyyy/MM/dd HH:mm:ss");
	}

	private Date date;
	private String client;
	private String request;
	private int status;
	private float duration;


	public LogEntry(Date date, String client, String request, int status, float length) {
		super();
		this.date = date;
		this.client = client;
		this.request = request;
		this.status = status;
		this.duration = length;
	}


	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	/**
	 * Decodes one log line into log entry. If the log line doesn't match the
	 * regexp then it is not parsed and decode will return null.
	 * 
	 * @param line Single log line
	 * @return LogEntry representing the line or null
	 * @throws ParseException
	 */
	public static LogEntry decode(String line) throws ParseException {
		Matcher matcher = validLogLinePattern.matcher(line);
		if (!matcher.matches()) {
			return null;
		}

		Date date = dateFormat.parse(matcher.group(1));
		String client = matcher.group(5);
		String request = matcher.group(2);
		int status = Integer.decode(matcher.group(3));
		float duration = Float.valueOf(matcher.group(4));
		return new LogEntry(date, client, request, status, duration);
	}
}
