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
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.hostmanager.IllegalOperationException;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;

/**
 * This interface contains methods that provide access to the data collected by Load Monitors running
 * on hosts. Several methods that can calculate simple statistics of data sampled by the Monitors are
 * provided.
 *
 * @author Branislav Repcek
 */
public interface HostDataStatisticianInterface extends Remote {

	/**
	 * Get list of samples from given interval in time.
	 * 
	 * @param start Start of the time interval specified as a number of 100 ns intervals since the 
	 *        start of the Windows epoch. Use <tt>null</tt> for negative infinity.
	 * @param end End of the time interval specified as a number of 100 ns intervals since the
	 *        start of the Windows epoch. Use <tt>null</tt> for positive infinity.
	 *        
	 * @return List of all samples from given interval. List may be empty.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InvalidArgumentException If time interval is invalid (if start is later than end).
	 * @throws InputParseException If an error occurred while parsing input files.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	List< LoadSample > getSamples(Long start, Long end) throws RemoteException,
			InvalidArgumentException, InputParseException, IllegalOperationException;

	/**
	 * Get list of samples from given interval in time.
	 * 
	 * @param start Time of the starting point of the interval. Use <tt>null</tt> for negative
	 *        infinity.
	 *        
	 * @param end Time of the ending point of the interval. Use <tt>null</tt> for positive
	 *        infinity.
	 *        
	 * @return List of all samples from given interval. List may be empty.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InvalidArgumentException If time interval is invalid (if start is later than end).
	 * @throws InputParseException If an error occurred while parsing input files.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	List< LoadSample > getSamples(Date start, Date end) throws RemoteException,
			InvalidArgumentException, InputParseException, IllegalOperationException;

	/**
	 * Retrieve all events from given time interval.
	 * 
	 * @param start Start of the time interval specified as a number of 100 ns intervals since the 
	 *        start of the Windows epoch. Use <tt>null</tt> for negative infinity.
	 * @param end End of the time interval specified as a number of 100 ns intervals since the
	 *        start of the Windows epoch. Use <tt>null</tt> for positive infinity.
	 * 
	 * @return List containing al events from given interval.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InvalidArgumentException If start is after end.
	 * @throws InputParseException If an error occurred while reading input files.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	List< LoadMonitorEvent > getEvents(Long start, Long end) throws RemoteException,
			InvalidArgumentException, InputParseException, IllegalOperationException;

	/**
	 * Retrieve all events from given time interval.
	 * 
	 * @param start Time of the beginning of the interval. Use <tt>null</tt> fo negative infinity.
	 * @param end Time of the end of the interval. Use <tt>null</tt> for positive infinity.
	 * 
	 * @return List containing al events from given interval.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InvalidArgumentException If start is after end.
	 * @throws InputParseException If an error occurred while reading input files.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	List< LoadMonitorEvent > getEvents(Date start, Date end) throws RemoteException,
			InvalidArgumentException, InputParseException, IllegalOperationException;

	/**
	 * Retrieve statistics about number of processes from given time interval.
	 * 
	 * @param start Start of the time interval specified as a number of 100 ns intervals since the 
	 *        start of the Windows epoch. Use <tt>null</tt> for negative infinity.
	 * @param end End of the time interval specified as a number of 100 ns intervals since the
	 *        start of the Windows epoch. Use <tt>null</tt> for positive infinity.
	 *        
	 * @return Statistics about number of processes run on the host in given time interval.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while reading input file.
	 * @throws InvalidArgumentException If specified time interval is invalid (start is after end).
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	ValueStatistics getProcessCountStats(Long start, Long end) throws RemoteException,
			InputParseException, InvalidArgumentException, IllegalOperationException;

	/**
	 * Get statistics about process queue length from given interval in time.
	 * 
	 * @param start Start of the time interval specified as a number of 100 ns intervals since the 
	 *        start of the Windows epoch. Use <tt>null</tt> for negative infinity.
	 * @param end End of the time interval specified as a number of 100 ns intervals since the
	 *        start of the Windows epoch. Use <tt>null</tt> for positive infinity.
	 * 
	 * @return Statistics about processor queue length during given interval.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while reading input file.
	 * @throws InvalidArgumentException If specified time interval is invalid (start is after end).
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	ValueStatistics getProcessQueueLengthStats(Long start, Long end) throws RemoteException,
			InputParseException, InvalidArgumentException, IllegalOperationException;

	/**
	 * Get statistics about free memory during specified time interval.
	 * 
	 * @param start Start of the time interval specified as a number of 100 ns intervals since the 
	 *        start of the Windows epoch. Use <tt>null</tt> for negative infinity.
	 * @param end End of the time interval specified as a number of 100 ns intervals since the
	 *        start of the Windows epoch. Use <tt>null</tt> for positive infinity.
	 * 
	 * @return Statistics about free memory during given interval.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while reading input file.
	 * @throws InvalidArgumentException If specified time interval is invalid (start is after end).
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	ValueStatistics getFreeMemoryStats(Long start, Long end) throws RemoteException,
			InputParseException, InvalidArgumentException, IllegalOperationException;

	/**
	 * Generate statistics about incoming and outgoing network traffic during given time period.
	 * 
	 * @param start Start of the time interval specified as a number of 100 ns intervals since the 
	 *        start of the Windows epoch. Use <tt>null</tt> for negative infinity.
	 * @param end End of the time interval specified as a number of 100 ns intervals since the
	 *        start of the Windows epoch. Use <tt>null</tt> for positive infinity.
	 *        
	 * @return List which contains instance {@link cz.cuni.mff.been.common.Pair} class with data
	 *         about specific network interface. Interfaces are listed in the order they appear in
	 *         the sample data. First item of each pair contains statistics about the incoming
	 *         traffic, second item contains data about outgoing traffic.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while reading input file.
	 * @throws InvalidArgumentException If time interval is invalid (start is after end).
	 * @throws IllegalOperationException If hardware configuration was changed during specified
	 *         interval or if input files have been closed via {@link #close()} method..
	 */
	List< Pair< ValueStatistics, ValueStatistics > > getNetworkStats(Long start, Long end)
			throws RemoteException, InputParseException, InvalidArgumentException,
			IllegalOperationException;

	/**
	 * Generate statistics about disk drive reads and writes during given time period.
	 * 
	 * @param start Start of the time interval specified as a number of 100 ns intervals since the 
	 *        start of the Windows epoch. Use <tt>null</tt> for negative infinity.
	 * @param end End of the time interval specified as a number of 100 ns intervals since the
	 *        start of the Windows epoch. Use <tt>null</tt> for positive infinity.
	 *        
	 * @return List which contains instance {@link cz.cuni.mff.been.common.Pair} class with data
	 *         about specific disk drive. Drives are listed in the order they appear in
	 *         the sample data. First item of each pair contains statistics about the disk reads,
	 *         second item contains data about drive writes.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while reading input file.
	 * @throws InvalidArgumentException If time interval is invalid (start is after end).
	 * @throws IllegalOperationException If hardware configuration was changed during specified
	 *         interval or if input files have been closed with the {@link #close()} method.
	 */
	List< Pair< ValueStatistics, ValueStatistics > > getDriveStats(Long start, Long end)
			throws RemoteException, InputParseException, InvalidArgumentException,
			IllegalOperationException;

	/**
	 * Get statistics about processor usage during specified time interval.
	 * 
	 * @param start Start of the time interval specified as a number of 100 ns intervals since the 
	 *        start of the Windows epoch. Use <tt>null</tt> for negative infinity.
	 * @param end End of the time interval specified as a number of 100 ns intervals since the
	 *        start of the Windows epoch. Use <tt>null</tt> for positive infinity.
	 *        
	 * @return List which contains statistics about each processor. Processors are listed in
	 *         order they apper in the sample data.
	 *         
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while reading input file.
	 * @throws InvalidArgumentException If time interval is invalid (start is after end).
	 * @throws IllegalOperationException If hardware configuration was changed during specified
	 *         interval or if input files have been closed with the {@link #close()} method..
	 */
	List< ValueStatistics > getProcessorStats(Long start, Long end) throws RemoteException,
			InputParseException, InvalidArgumentException, IllegalOperationException;

	/**
	 * Find hardware description with timestamp that is closest to the specified time.
	 * 
	 * @param timestamp Starting point of the search. Time is specified as a number of 100 ns 
	 *        intervals since the start of the Windows epoch.
	 *        
	 * @return Hardware description or <tt>null</tt> if no such event has been found.
	 *         
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while parsing input file.
	 * @throws ValueNotFoundException If no hardware description exists for given timestamp.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	HardwareDescription getHardwareDescription(long timestamp)
		throws InputParseException, RemoteException, ValueNotFoundException, IllegalOperationException;

	/**
	 * Get hardware description retrieved by the Load Monitor prior to the specified time.
	 *  
	 * @param date Date..
	 * 
	 * @return Hardware description with the biggest timestamp smaller than the date specified
	 *         in argument.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while parsing input files.
	 * @throws ValueNotFoundException If no hardware description exists for given date and time.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	HardwareDescription getHardwareDescription(Date date) throws RemoteException,
			InputParseException, ValueNotFoundException, IllegalOperationException;

	/**
	 * Find hardware description that is just before specified timestamp.
	 *  
	 * @param timestamp Starting point of the search. Time is specified as a number of 100 ns 
	 *        intervals since the start of the Windows epoch.
	 *        
	 * @return Hardware description just before the specified point in time. <tt>null</tt> is
	 *         returned if no description with timestamp smaller than the one specified has been found.
	 *         
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while parsing input file.
	 * @throws ValueNotFoundException If no hw description has been found prior to the specified time.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	HardwareDescription getPreviousHardwareDescription(long timestamp)
		throws InputParseException, RemoteException, ValueNotFoundException, IllegalOperationException;
	
	/**
	 * Find hardware description that is just before specified time.
	 *
	 * @param date Date and time which specified starting point of the search.
	 *        
	 * @return Hardware description just before the specified point in time. <tt>null</tt> is
	 *         returned if no description with timestamp smaller than the one specified has been found.
	 *         
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while parsing input file.
	 * @throws ValueNotFoundException If no hw description has been found prior to the specified time.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	HardwareDescription getPreviousHardwareDescription(Date date)
		throws InputParseException, RemoteException, ValueNotFoundException, IllegalOperationException;

	/**
	 * Get list of event timestamps from given time interval.
	 * 
	 * @param start Start of the time interval specified as a number of 100 ns intervals since the 
	 *        start of the Windows epoch. Use <tt>null</tt> for negative infinity.
	 * @param end End of the time interval specified as a number of 100 ns intervals since the
	 *        start of the Windows epoch. Use <tt>null</tt> for positive infinity.
	 *        
	 * @return List of longs. Each value is timestamp of the event in the load file. Timestamps
	 *         are in the ascending order.
	 *         
	 * @throws InputParseException If an error occurred while reading input file.
	 * @throws RemoteException If RMI error occurred.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	List< Long > getTimestamps(Long start, Long end)
		throws InputParseException, RemoteException, IllegalOperationException;
	
	/**
	 * Get list of event timestamps from given time interval. Only timestamps of events of specific
	 * type are returned.
	 * 
	 * @param start Start of the time interval specified as a number of 100 ns intervals since the 
	 *        start of the Windows epoch. Use <tt>null</tt> for negative infinity.
	 * @param end End of the time interval specified as a number of 100 ns intervals since the
	 *        start of the Windows epoch. Use <tt>null</tt> for positive infinity.
	 * @param type Type of events to search for. If this is <tt>null</tt> all events will be returned.
	 *        
	 * @return List of longs. Each value is timestamp of the event in the load file. Timestamps
	 *         are in the ascending order.
	 *         
	 * @throws InputParseException If an error occurred while reading input file.
	 * @throws RemoteException If RMI error occurred.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	List< Long > getTimestamps(Long start, Long end, LoadMonitorEvent.EventType type)
		throws InputParseException, RemoteException, IllegalOperationException;
	
	/**
	 * Get information about events from specified interval.
	 * 
	 * @param start Start of the time interval specified as a number of 100 ns intervals since the 
	 *        start of the Windows epoch. Use <tt>null</tt> for negative infinity.
	 * @param end End of the time interval specified as a number of 100 ns intervals since the
	 *        start of the Windows epoch. Use <tt>null</tt> for positive infinity.
	 *        
	 * @return List of pairs with details about each event from specified time interval. First
	 *         element of the pair is event's timestamp, second element is type of the event.
	 * 
	 * @throws InputParseException If an error occurred while parsing input files.
	 * @throws RemoteException If RMI error occurred.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	List< Pair< Long, LoadMonitorEvent.EventType > > getEventInfo(Long start, Long end)
		throws InputParseException, RemoteException, IllegalOperationException;
	
	/**
	 * Get event from given time.
	 * 
	 * @param timestamp Timestamp of the event to retrieve.
	 * 
	 * @return Event with given timestamp.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while reading input files.
	 * @throws ValueNotFoundException If no event with given timestamp has been found.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	LoadMonitorEvent getEvent(long timestamp) 
		throws RemoteException, InputParseException, ValueNotFoundException, IllegalOperationException;

	/**
	 * Get event from given time.
	 * 
	 * @param time Time of the event to retrieve.
	 * 
	 * @return Event with given time.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while reading input files.
	 * @throws ValueNotFoundException If no event with given timestamp has been found.
	 * @throws InvalidArgumentException If given date is <tt>null</tt>.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	LoadMonitorEvent getEvent(Date time) 
		throws RemoteException, InputParseException, ValueNotFoundException, 
		InvalidArgumentException, IllegalOperationException;
	
	/**
	 * Get timestamp of the last event that is cached in the instance of the implementing class.
	 * This basically denotes how old the data are. If you want more recent data, you have to call
	 * {@link #refresh()} method.
	 * 
	 * @return Timestamp of the last event that is in cache. If cache is empty <tt>null</tt> is returned.
	 *         Time is measured as a number of 100 ns intervals since start of the Windows epoch
	 *         (1. 1. 1601). 
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while parsing input files.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	Long getLastTimestamp() throws RemoteException, InputParseException, IllegalOperationException;
	
	/**
	 * Get timestamp of the last event of given type.
	 *  
	 * @param eventType Type of event to search for.
	 * 
	 * @return Timestamp of requested event or <tt>null</tt> if no such event has been found.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while parsing input files.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 * @throws InvalidArgumentException If event type is <tt>null</tt>.
	 */
	Long getLastTimestamp(LoadMonitorEvent.EventType eventType) 
		throws RemoteException, IllegalOperationException, InputParseException, InvalidArgumentException;
	
	/**
	 * Re-parse all files that are stored in the cache.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InputParseException If an error occurred while parsing input files.
	 * @throws IllegalOperationException If input files have been closed with the {@link #close()}
	 *         method.
	 */
	void refresh() throws RemoteException, InputParseException, IllegalOperationException;
	
	/**
	 * Close all input files. After call to close you should not attempt to call any methods on the
	 * object. Multiple calls do not have any effect.
	 *  
	 * @throws RemoteException If RMI error occurred.
	 * @throws IOException If an error occurred when closing input file.
	 */
	void close() throws RemoteException, IOException;
}
