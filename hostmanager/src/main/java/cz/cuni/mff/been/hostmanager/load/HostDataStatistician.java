/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Branislav Repcek
 *
 *  GNU Lesser General Public License Version 2.1
 *  ---------------------------------------------
 *  Copyright (C) 2004-2006 Distributed Systems Research Group,
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

package cz.cuni.mff.been.hostmanager.load;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.common.util.MiscUtils;
import cz.cuni.mff.been.common.util.TimeUtils;
import cz.cuni.mff.been.hostmanager.IllegalOperationException;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;

/**
 * This class provides access to the data collected by Load Monitors on hosts in the network.
 * It contains various methods that can be used to calculate various simple statistics about
 * the data stored in database.
 *
 * @author Branislav Repcek
 */
class HostDataStatistician extends UnicastRemoteObject implements HostDataStatisticianInterface {
	
	private static final long	serialVersionUID	= 6017228168293642931L;

	/**
	 * Parser for the event file.
	 */
	private LoadFileParser< LoadMonitorEvent > eventParser;

	/**
	 * Entries from the load map file.
	 */
	private List< LoadMapFile.FileEntry > loadMapEntries;
	
	/**
	 * Name of the load map file.
	 */
	private String mapFile;
	
	/**
	 * Event file name.
	 */
	private String eventFile;
	
	/**
	 * Is load map file parsed?
	 */
	private boolean parsed;
	
	/**
	 * Are input files closed?
	 */
	private boolean closed;
	
	/**
	 * Create new data provider.
	 * 
	 * @param mapFile Map file.
	 * @param eventFile File with events.
	 * 
	 * @throws IOException If an error occurred while opening input files. 
	 * @throws RemoteException If RMI exception occurred.
	 * @throws InvalidArgumentException If some filename is empty string or <tt>null</tt>. 
	 */
	HostDataStatistician(String mapFile, String eventFile) 
		throws IOException, RemoteException, InvalidArgumentException {
		
		MiscUtils.verifyStringParameterBoth(mapFile, "mapFile");
		MiscUtils.verifyStringParameterBoth(eventFile, "eventFile");
		
		this.mapFile = mapFile;
		this.eventFile = eventFile;
		
		eventParser = new LoadFileParser< LoadMonitorEvent >(this.eventFile, false, LoadMonitorEvent.class);
		
		closed = false;
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getHardwareDescription(long)
	 */
	public synchronized HardwareDescription getHardwareDescription(long timestamp)
		throws RemoteException, InputParseException, ValueNotFoundException, IllegalOperationException {
		
		parseData();

		int prev = findPreviousEventByTimeAndType(timestamp, 
		                                          LoadMonitorEvent.EventType.MONITOR_HW_DESCRIPTION);
		int next = findNextEventByTimeAndType(timestamp, 
		                                      LoadMonitorEvent.EventType.MONITOR_HW_DESCRIPTION);
		int res = -1;
		
		if (prev >= 0) { 
			if (next >= 0) {
				long dp = timestamp - loadMapEntries.get(prev).getTimeStamp();
				long dn = loadMapEntries.get(next).getTimeStamp() - timestamp;
				
				if (dp <= dn) {
					res = prev;
				} else {
					res = next;
				}
			} else {
				res = prev;
			}
		} else {
			if (next >= 0) {
				res = next;
			} else {
				throw new ValueNotFoundException("No hardware description available for "
						+ "specified timestamp.");
			}
		}
		
		long filePos = loadMapEntries.get(res).getPosition();
		
		try {
			eventParser.seek(filePos);
			LoadMonitorEvent event = eventParser.getNext();
			
			if (!event.hasHardwareDescription()) {
				throw new InputParseException("Load map file is out of sync with event file.");
			}
			
			return event.getHardwareDescription();
		} catch (IOException e) {
			throw new InputParseException("Error reading input file.", e);
		} catch (IllegalOperationException e) {
			throw new InputParseException("Error reading input file.", e);
		}
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getHardwareDescription(java.util.Date)
	 */
	public synchronized HardwareDescription getHardwareDescription(Date date)
		throws RemoteException, InputParseException, ValueNotFoundException, IllegalOperationException {
		
		MiscUtils.verifyParameterIsNotNull(date, "date");
		
		return getHardwareDescription(TimeUtils.convertJavaDateToWindowsTime(date));
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getPreviousHardwareDescription(long)
	 */
	public synchronized HardwareDescription getPreviousHardwareDescription(long timestamp)
		throws InputParseException, RemoteException, ValueNotFoundException, IllegalOperationException {
		
		parseData();

		int prev = findPreviousEventByTimeAndType(timestamp, 
		                                          LoadMonitorEvent.EventType.MONITOR_HW_DESCRIPTION);
		
		if (prev == -1) {
			throw new ValueNotFoundException("No hardware description available for "
					+ "specified timestamp.");
		}
		
		long filePos = loadMapEntries.get(prev).getPosition();
		
		try {
			eventParser.seek(filePos);
			LoadMonitorEvent event = eventParser.getNext();
			
			if (!event.hasHardwareDescription()) {
				throw new InputParseException("Load map file is out of sync with event file.");
			}
			
			return event.getHardwareDescription();
		} catch (IOException e) {
			throw new InputParseException("Error reading input file.", e);
		} catch (IllegalOperationException e) {
			throw new InputParseException("Error reading input file.", e);
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getPreviousHardwareDescription(java.util.Date)
	 */
	public synchronized HardwareDescription getPreviousHardwareDescription(Date date)
		throws InputParseException, RemoteException, ValueNotFoundException, IllegalOperationException {
		
		MiscUtils.verifyParameterIsNotNull(date, "date");
		
		return getPreviousHardwareDescription(TimeUtils.convertJavaDateToWindowsTime(date));
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getSamples(java.lang.Long, java.lang.Long)
	 */
	public synchronized List< LoadSample > getSamples(Long start, Long end)
		throws RemoteException, InvalidArgumentException, InputParseException, IllegalOperationException {
		
		parseData();
		
		long s = 0;
		long e = 0;
		
		if (start == null) {
			s = loadMapEntries.get(0).getTimeStamp();
		} else {
			s = start;
		}
		
		if (end == null) {
			e = loadMapEntries.get(loadMapEntries.size() - 1).getTimeStamp();
		} else {
			e = end;
		}
		
		if (s > e) {
			throw new InvalidArgumentException("Invalid time interval.");
		}
		
		ArrayList< LoadSample > result = new ArrayList< LoadSample >();
		
		int spos = findPreviousEventByTime(s);

		if (spos < 0) {
			spos = 0;
		}
		
		synchronized (eventParser) {
			try {
				eventParser.seek(loadMapEntries.get(spos).getPosition());
			} catch (IOException f) {
				throw new InputParseException("Unable to seek in the event file.", f);
			}
			
			try {
				while (eventParser.hasNext()) {
					
					LoadMonitorEvent event = eventParser.getNext();
					
					if (event.hasSample()) {
						LoadSample sample = event.getSample();
						
						// Original version in which we compared time stamp of the sample
//						if (sample.getTimeStamp() >= s) {
//							if (sample.getTimeStamp() <= e) {
//								result.add(sample);
//							} else {
//								break;
//							}
//						}
						if (event.getTimestamp() >= s) {
							if (event.getTimestamp() <= e) {
								result.add(sample);
							} else {
								// Too big time stamp -> no need to read more samples.
								break;
							}
						}
					} else {
						// This event does not contain sample, but we can still check if we need
						// to bother reading another one.
						if (event.getTimestamp() > e) {
							break;
						}
					}
				}
			} catch (IOException f) {
				throw new InputParseException("Unable to read input file.", f);
			} catch (IllegalOperationException f) {
				throw new InputParseException("Unable to read input file.", f);
			}
		}
		
		return result;
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getSamples(java.util.Date, java.util.Date)
	 */
	public synchronized List< LoadSample > getSamples(Date start, Date end)
		throws RemoteException, InvalidArgumentException, InputParseException, IllegalOperationException {
		
		Long s = null;
		Long e = null;
		
		if (start != null) {
			s = TimeUtils.convertJavaDateToWindowsTime(start);
		}
		
		if (end != null) {
			TimeUtils.convertJavaDateToWindowsTime(end);
		}
		
		return getSamples(s, e);
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getEvents(java.lang.Long, java.lang.Long)
	 */
	public synchronized List< LoadMonitorEvent > getEvents(Long start, Long end)
		throws RemoteException, InvalidArgumentException, InputParseException, IllegalOperationException {
		
		parseData();
		
		long s = 0;
		long e = 0;
		
		if (start == null) {
			s = loadMapEntries.get(0).getTimeStamp();
		} else {
			s = start;
		}
		
		if (end == null) {
			e = loadMapEntries.get(loadMapEntries.size() - 1).getTimeStamp();
		} else {
			e = end;
		}
		
		if (s > e) {
			throw new InvalidArgumentException("Invalid time interval.");
		}
		
		ArrayList< LoadMonitorEvent > result = new ArrayList< LoadMonitorEvent >();
		
		int spos = findPreviousEventByTime(s);

		if (spos < 0) {
			spos = 0;
		}
		
		synchronized (eventParser) {
			try {
				eventParser.seek(loadMapEntries.get(spos).getPosition());
			} catch (IOException f) {
				throw new InputParseException("Unable to seek in the event file.", f);
			}
			
			try {
				while (eventParser.hasNext()) {
					
					LoadMonitorEvent event = eventParser.getNext();

					long timestamp = event.getTimestamp();

					if (timestamp >= s) {
						if (timestamp <= e) {
							result.add(event);
						}
					}
				}
			} catch (IOException f) {
				throw new InputParseException("Unable to read input file.", f);
			} catch (IllegalOperationException f) {
				throw new InputParseException("Unable to read input file.", f);
			}
		}
		
		return result;
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getEvents(java.util.Date, java.util.Date)
	 */
	public synchronized List< LoadMonitorEvent > getEvents(Date start, Date end)
		throws RemoteException, InvalidArgumentException, InputParseException, IllegalOperationException {
		
		Long s = null;
		Long e = null;
		
		if (start != null) {
			s = TimeUtils.convertJavaDateToWindowsTime(start);
		}
		
		if (end != null) {
			TimeUtils.convertJavaDateToWindowsTime(end);
		}
		
		return getEvents(s, e);
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getProcessCountStats(java.lang.Long, java.lang.Long)
	 */
	public synchronized ValueStatistics getProcessCountStats(Long start, Long end)
		throws RemoteException, InputParseException, InvalidArgumentException, IllegalOperationException {
		
		parseData();
		
		List< LoadSample > samples = getSamples(start, end);
		
		ValueStatistics stats = new ValueStatistics("Process count", new Long(0), (Long) null);
		
		for (LoadSample s: samples) {
			stats.addSamplePoint(s.getProcessCount());
		}
		
		return stats;
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getProcessQueueLengthStats(java.lang.Long, java.lang.Long)
	 */
	public synchronized ValueStatistics getProcessQueueLengthStats(Long start, Long end)
		throws RemoteException, InputParseException, InvalidArgumentException, IllegalOperationException {
		
		parseData();
		
		List< LoadSample > samples = getSamples(start, end);
		
		ValueStatistics stats = new ValueStatistics("Process queue length", new Long(0), (Long) null);
		
		for (LoadSample s: samples) {
			stats.addSamplePoint(s.getProcessQueueLength());
		}
		
		return stats;
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getFreeMemoryStats(java.lang.Long, java.lang.Long)
	 */
	public synchronized ValueStatistics getFreeMemoryStats(Long start, Long end)
		throws RemoteException, InputParseException, InvalidArgumentException, IllegalOperationException {

		parseData();
		
		List< LoadSample > samples = getSamples(start, end);

		if (samples.isEmpty()) {
			return new ValueStatistics("Free physical memory.", new Long(0), null);
		}
		
		Long upperBound = null;
		
		try {
			long s = 0;
			
			if (start == null) {
				s = loadMapEntries.get(0).getTimeStamp();
			} else {
				s = start;
			}
			
			HardwareDescription desc = getHardwareDescription(s);
			
			upperBound = desc.getMemorySize();
		} catch (ValueNotFoundException e) {
			upperBound = null;
		}
				
		ValueStatistics stats = new ValueStatistics("Free physical memory", new Long(0), upperBound);
		
		for (LoadSample s: samples) {
			stats.addSamplePoint(s.getMemoryFree());
		}
		
		return stats;
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getNetworkStats(java.lang.Long, java.lang.Long)
	 */
	public synchronized List< Pair< ValueStatistics, ValueStatistics > > getNetworkStats(Long start, Long end)
		throws RemoteException, InputParseException, InvalidArgumentException, IllegalOperationException {
		
		parseData();
		
		List< LoadSample > samples = getSamples(start, end);
		ArrayList< Pair< ValueStatistics, ValueStatistics > > result = 
			new ArrayList< Pair< ValueStatistics, ValueStatistics > >();
		
		if (samples.isEmpty()) {
			// No data -> No stats
			return result;
		}
			
		try {
			long s = 0;
			
			if (start == null) {
				s = loadMapEntries.get(0).getTimeStamp();
			} else {
				s = start;
			}
			
			HardwareDescription desc = getHardwareDescription(s);
			
			if (desc.getAdapterCount() == 0) {
				// No adapters -> no stats
				return result;
			} else {
				for (int i = 0; i < desc.getAdapterCount(); ++i) {
					ValueStatistics read = new ValueStatistics(desc.getAdapters()[i], 
					                                           new Long(0),
					                                           (Long) null); 
					ValueStatistics write = new ValueStatistics(desc.getAdapters()[i], 
					                                            new Long(0), 
					                                            (Long) null);
					
					result.add(new Pair< ValueStatistics, ValueStatistics >(read, write));
				}
			}
		} catch (ValueNotFoundException e) {
			// No description found -> generate names automatically
			LoadSample first = samples.get(0);
		
			if (first.getNetworkInterfaceCount() == 0) {
				// No network interfaces -> no stats
				return result;
			}
			
			for (int i = 0; i < first.getNetworkInterfaceCount(); ++i) {
				ValueStatistics read = new ValueStatistics("Network " + i, 
				                                           new Long(0),
				                                           (Long) null); 
				ValueStatistics write = new ValueStatistics("Network " + i,
				                                            new Long(0),
				                                            (Long) null);
				
				result.add(new Pair< ValueStatistics, ValueStatistics >(read, write));
			}
		}
		
		int lastNetCount = samples.get(0).getNetworkInterfaceCount();
		
		if (lastNetCount == 0) {
			return result;
		}
		
		for (LoadSample s: samples) {
			int []reads = s.getNetworkBytesReceivedPerSecond();
			int []writes = s.getNetworkBytesSentPerSecond();
			
			if (reads.length != lastNetCount) {
				throw new IllegalOperationException("Number of network adapters changed "
						+ "while evaluating data (" + lastNetCount + "->" + reads.length + ").");
			}
			
			for (int i = 0; i < reads.length; ++i) {
				result.get(i).getKey().addSamplePoint(reads[i]);
				result.get(i).getValue().addSamplePoint(writes[i]);
			}
		}
		
		return result;
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getDriveStats(java.lang.Long, java.lang.Long)
	 */
	public synchronized List< Pair< ValueStatistics, ValueStatistics > > getDriveStats(Long start, Long end)
		throws RemoteException, InputParseException, InvalidArgumentException, IllegalOperationException {

		parseData();
		
		List< LoadSample > samples = getSamples(start, end);
		ArrayList< Pair< ValueStatistics, ValueStatistics > > result = 
			new ArrayList< Pair< ValueStatistics, ValueStatistics > >();
		
		if (samples.isEmpty()) {
			// No data -> no stats
			return result;
		}
		
		try {
			long s = 0;
			
			if (start == null) {
				s = loadMapEntries.get(0).getTimeStamp();
			} else {
				s = start;
			}
			
			HardwareDescription desc = getHardwareDescription(s);
			
			if (desc.getDriveCount() == 0) {
				// No drives -> no stats
				return result;
			} else {
				for (int i = 0; i < desc.getDriveCount(); ++i) {
					ValueStatistics read = new ValueStatistics(desc.getDrives()[i], 
					                                           new Long(0),
					                                           (Long) null); 
					ValueStatistics write = new ValueStatistics(desc.getDrives()[i], 
					                                            new Long(0), 
					                                            (Long) null);
					
					result.add(new Pair< ValueStatistics, ValueStatistics >(read, write));
				}
			}
		} catch (ValueNotFoundException e) {
			// No description found -> generate names automatically
			LoadSample first = samples.get(0);
		
			if (first.getDriveCount() == 0) {
				// No drives -> no stats
				return result;
			}
			
			for (int i = 0; i < first.getDriveCount(); ++i) {
				ValueStatistics read = new ValueStatistics("Drive " + i, 
				                                           new Long(0),
				                                           (Long) null); 
				ValueStatistics write = new ValueStatistics("Drive " + i,
				                                            new Long(0),
				                                            (Long) null);
				
				result.add(new Pair< ValueStatistics, ValueStatistics >(read, write));
			}
		}
		
		int lastDriveCount = samples.get(0).getDriveCount();
		
		if (lastDriveCount == 0) {
			return result;
		}
		
		for (LoadSample s: samples) {
			long []reads = s.getDiskReadBytesPerSecond();
			long []writes = s.getDiskWriteBytesPerSecond();
			
			if (reads.length != lastDriveCount) {
				throw new IllegalOperationException("Number of disk drives changed while "
						+ "evaluating data (" + lastDriveCount + "->" + reads.length + ").");
			}
			
			for (int i = 0; i < reads.length; ++i) {
				result.get(i).getKey().addSamplePoint(reads[i]);
				result.get(i).getValue().addSamplePoint(writes[i]);
			}
		}
		
		return result;
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getProcessorStats(java.lang.Long, java.lang.Long)
	 */
	public synchronized List< ValueStatistics > getProcessorStats(Long start, Long end)
		throws RemoteException, InputParseException, InvalidArgumentException, IllegalOperationException {

		parseData();
		
		List< LoadSample > samples = getSamples(start, end);
		ArrayList< ValueStatistics > result = new ArrayList< ValueStatistics >();
		
		if (!samples.isEmpty()) {
			// There's no HW desc for processors, just the count, but we can determine that easily
			// from the sample.
			LoadSample first = samples.get(0);
			
			if (first.getProcessorCount() == 0) {
				// Well, this should not happen, but if we don't have CPU (veeery strange computer)
				// we will return empty stats
				return result;
			}
			
			for (int i = 0; i < first.getProcessorCount(); ++i) {
				ValueStatistics cpu = new ValueStatistics("Processor " + i, 
				                                          new Long(0),
				                                          new Long(100)); 
				
				result.add(cpu);
			}
			
			int lastCpuCount = samples.get(0).getProcessorCount();
			
			if (lastCpuCount == 0) {
				return result;
			}
			
			for (LoadSample s: samples) {
				short []usage = s.getProcessorUsage();
				
				if (usage.length != lastCpuCount) {
					throw new IllegalOperationException("Number of processors changed while "
							+ "evaluating data (" + lastCpuCount + "->" + usage.length + ").");
				}
				
				for (int i = 0; i < usage.length; ++i) {
					result.get(i).addSamplePoint(usage[i]);
				}
			}
		}
		
		return result;
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getTimestamps(java.lang.Long, java.lang.Long, cz.cuni.mff.been.hostmanager.load.LoadMonitorEvent.EventType)
	 */
	public synchronized List< Long > getTimestamps(Long start, Long end, LoadMonitorEvent.EventType type)
		throws InputParseException, RemoteException, IllegalOperationException {
		
		parseData();
		
		ArrayList< Long > result = new ArrayList< Long >();
		
		long s = 0;
		long e = 0;
		
		if (start == null) {
			s = loadMapEntries.get(0).getTimeStamp();
		} else {
			s = start;
		}
		
		if (end == null) {
			e = loadMapEntries.get(loadMapEntries.size() - 1).getTimeStamp();
		} else {
			e = end;
		}
		
		if (s > e) {
			throw new InvalidArgumentException("Invalid time interval.");
		}

		int posFirst = findNextEventByTime(s);
		int posLast = findPreviousEventByTime(e);
		
		if (type == null) {
			for (int i = posFirst; i < posLast; ++i) {
				result.add(loadMapEntries.get(i).getTimeStamp());
			}
		} else {
			for (int i = posFirst; i < posLast; ++i) {
				LoadMapFile.FileEntry entry = loadMapEntries.get(i);
				
				if (entry.getType() == type) {
					result.add(entry.getTimeStamp());
				}
			}
		}
		
		return result;
	}

	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getTimestamps(java.lang.Long, java.lang.Long)
	 */
	public synchronized List< Long > getTimestamps(Long start, Long end)
		throws InputParseException, RemoteException, IllegalOperationException {
		
		return getTimestamps(start, end, null);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getEventInfo(java.lang.Long, java.lang.Long)
	 */
	public synchronized List< Pair< Long, LoadMonitorEvent.EventType >> getEventInfo(Long start, Long end)
		throws InputParseException, RemoteException, IllegalOperationException {
		
		parseData();
		
		ArrayList< Pair< Long, LoadMonitorEvent.EventType > > result = 
			new ArrayList< Pair< Long, LoadMonitorEvent.EventType > >();

		long s = 0;
		long e = 0;
		
		if (start == null) {
			s = loadMapEntries.get(0).getTimeStamp();
		} else {
			s = start;
		}
		
		if (end == null) {
			e = loadMapEntries.get(loadMapEntries.size() - 1).getTimeStamp();
		} else {
			e = end;
		}
		
		if (s > e) {
			throw new InvalidArgumentException("Invalid time interval.");
		}

		int posFirst = findNextEventByTime(s);
		int posLast = findPreviousEventByTime(e);
		
		for (int i = posFirst; i < posLast; ++i) {
			LoadMapFile.FileEntry entry = loadMapEntries.get(i);
			result.add(new Pair< Long, LoadMonitorEvent.EventType >(entry.getTimeStamp(), 
					entry.getType()));
		}
		
		return result;
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getEvent(java.util.Date)
	 */
	public synchronized LoadMonitorEvent getEvent(Date time)
		throws RemoteException, InputParseException, ValueNotFoundException,
		InvalidArgumentException, IllegalOperationException {
		
		MiscUtils.verifyParameterIsNotNull(time, "time");
		
		return getEvent(TimeUtils.convertJavaDateToWindowsTime(time));
	}

	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getEvent(long)
	 */
	public synchronized LoadMonitorEvent getEvent(long timestamp)
		throws RemoteException, InputParseException, ValueNotFoundException, IllegalOperationException {
		
		parseData();
		
		LoadMapFile.FileEntry entry = new LoadMapFile.FileEntry(timestamp, 0, 
				LoadMonitorEvent.EventType.MONITOR_UNKNOWN);
		
		int index = Collections.binarySearch(loadMapEntries, entry, new ET1Comparator());
		
		if (index >= 0) {
			long filePos = loadMapEntries.get(index).getPosition();
			
			try {
				eventParser.seek(filePos);
				
				LoadMonitorEvent event = eventParser.getNext();
				
				return event;
			} catch (IOException e) {
				throw new InputParseException("Unable to seek in the event file.", e);
			} catch (IllegalOperationException e) {
				throw new InputParseException("Unable to read event file.", e);
			}
		} else {
			throw new ValueNotFoundException("No event found for given timestamp.");
		}
	}

	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#getLastTimestamp()
	 */
	public synchronized Long getLastTimestamp()
		throws RemoteException, InputParseException, IllegalOperationException {
		
		parseData();
		
		if (loadMapEntries.size() == 0) {
			return null;
		} else {
			return loadMapEntries.get(loadMapEntries.size() - 1).getTimeStamp();
		}
	}

	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataStatisticianInterface#getLastTimestamp(cz.cuni.mff.been.hostmanager.load.LoadMonitorEvent.EventType)
	 */
	public synchronized Long getLastTimestamp(LoadMonitorEvent.EventType eventType)
		throws RemoteException, IllegalOperationException, InputParseException, InvalidArgumentException {
		
		MiscUtils.verifyParameterIsNotNull(eventType, "eventType");
		
		parseData();
		
		if (loadMapEntries.size() == 0) {
			return null;
		} else {
			for (int i = loadMapEntries.size() - 1; i >= 0; --i) {
				LoadMapFile.FileEntry entry = loadMapEntries.get(i);
				if (entry.getType().equals(eventType)) {
					return entry.getTimeStamp();
				}
			}
			
			return null;
		}
	}

	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#refresh()
	 */
	public synchronized void refresh()
		throws RemoteException, InputParseException, IllegalOperationException {
		
		parsed = false;
		
		parseData();
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.HostDataProviderInterface#close()
	 */
	public void close() throws RemoteException, IOException {
		
		loadMapEntries.clear();
		eventParser.close();
	}

	/**
	 * Find event of given type with time stamp smaller or equal to the one specified.
	 * 
	 * @param timestamp Time stamp limit.
	 * @param type Type of the event to find.
	 * 
	 * @return Index of the event of given type with time stamp <= specified time stamp. 
	 *         If no such event exists, -1 is returned.
	 *         
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while parsing input file.
	 */
	private int findPreviousEventByTimeAndType(long timestamp, LoadMonitorEvent.EventType type)
		throws InputParseException, RemoteException, IllegalOperationException {

		parseData();

		int index = findPreviousEventByTime(timestamp);
		
		if (index == -1) {
			return -1;
		}
		
		for (int i = index; i > 0; --i) {
			if (loadMapEntries.get(i).getType() == type) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Find index of the entry which has the largest time stamp smaller than or equal to the specified 
	 * one.
	 * 
	 * @param timestamp Time stamp limit.
	 * 
	 * @return Index of the event with time stamp <= specified time stamp. If no such event exists,
	 *         -1 is returned.
	 *         
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while parsing input file.
	 */
	private int findPreviousEventByTime(long timestamp)
		throws RemoteException, InputParseException, IllegalOperationException {
		
		parseData();
		
		if (loadMapEntries.size() == 0) {
			return -1;
		}
		
		LoadMapFile.FileEntry entry = new LoadMapFile.FileEntry(timestamp, 0, 
				LoadMonitorEvent.EventType.MONITOR_UNKNOWN);
		
		int result = Collections.binarySearch(loadMapEntries, entry, new ET1Comparator());
		
		if (result >= 0) {
			return result;
		} else {
			return -result - 2;
		}
	}
	
	/**
	 * Find next event with time stamp at least as big as the specified one.
	 * 
	 * @param timestamp Time stamp to search for.
	 * 
	 * @return Event with time stamp >= specified time stamp.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while reading input files.
	 */
	private int findNextEventByTime(long timestamp)
		throws RemoteException, InputParseException, IllegalOperationException {
		
		parseData();
		
		if (loadMapEntries.size() == 0) {
			return -1;
		}
		
		LoadMapFile.FileEntry entry = 
			new LoadMapFile.FileEntry(timestamp, 0, LoadMonitorEvent.EventType.MONITOR_UNKNOWN);

		int result = Collections.binarySearch(loadMapEntries, entry, new ET1Comparator());
		
		if (result >= 0) {
			return result;
		} else {
			return -result - 1;
		}
	}
	
	/**
	 * Find first event that has correct type and its time stamp is at least as big as
	 * the one specified.
	 * 
	 * @param timestamp Time stamp limit.
	 * @param type Type of event to look for.
	 * 
	 * @return Index of the event of requested type with time stamp >= given time stamp or -1 if no
	 *         such event has been found.
	 *         
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while parsing input file.
	 */
	private int findNextEventByTimeAndType(long timestamp, LoadMonitorEvent.EventType type) 
		throws RemoteException, InputParseException, IllegalOperationException {

		int index = findNextEventByTime(timestamp);
		
		if (index == -1) {
			return -1;
		}
		
		for (int i = index; i < loadMapEntries.size(); ++i) {
			if (loadMapEntries.get(i).getType() == type) {
				return i;
			}
		}
		
		return -1; 
	}

	/**
	 * Comparator which compares entries from the map file according to their time stamps.
	 *
	 * @author Branislav Repcek
	 */
	private class ET1Comparator implements Comparator< LoadMapFile.FileEntry > {

		/* 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(LoadMapFile.FileEntry o1, LoadMapFile.FileEntry o2) {
			
			if (o1.getTimeStamp() > o2.getTimeStamp()) {
				return 1;
			} else if (o1.getTimeStamp() < o2.getTimeStamp()) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	/**
	 * Read load map file.
	 * 
	 * @throws InputParseException If and error occurred while reading input file.
	 * @throws RemoteException If RMI error occurred.
	 */
	private synchronized void parseData()
		throws InputParseException, RemoteException, IllegalOperationException {

		if (closed) {
			throw new IllegalOperationException("Input files have been closed.");
		}
		
		if (!parsed) {

			LoadMapFile loadMap = null;
			try {
				loadMap = new LoadMapFile(mapFile, false);
			} catch (Exception e) {
				throw new InputParseException("Unable to open load map file.", e);
			}
			
			try {
				int count = loadMap.getCount();
				loadMapEntries = loadMap.readFrom(0, count);
			} catch (Exception e) {
				throw new InputParseException("Unable to parse load map file.", e);
			} finally {
				try {
					loadMap.close();
				} catch (IOException e) {
					throw new InputParseException("Unable to close load map file.", e);
				}
			}
			
			parsed = true;
		}
	}
}
