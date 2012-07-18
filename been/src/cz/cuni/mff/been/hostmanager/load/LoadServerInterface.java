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

import java.net.UnknownHostException;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.Collection;
import java.util.Map;

import cz.cuni.mff.been.common.id.OID;

import cz.cuni.mff.been.hostmanager.HostDatabaseException;
import cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface;
import cz.cuni.mff.been.hostmanager.HostManagerService;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;

/**
 * This interface provides methods to access load data collected by the load monitors on the hosts
 * in the environment.
 * 
 * @author Branislav Repcek
 */
public interface LoadServerInterface extends Remote {

	/**
	 * Interface URL.
	 */
	String URL = "/been/hostmanager/" + HostManagerService.REMOTE_INTERFACE_LOAD_SERVER;

	/**
	 * Get most recent sample received from the host. Note that this will not open data files on
	 * the disk and therefore host must be registered on the Load Server in this session.
	 * 
	 * @param hostName Fully qualified network name of the host.
	 * 
	 * @return Most recent sample received from the host. <tt>null</tt> is returned if the host 
	 *         is not registered on the Load Server or if no samples have been received yet. 
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws ValueNotFoundException If host is not in database.
	 */
	LoadSample getLastSample(String hostName) throws RemoteException, ValueNotFoundException;
	
	/**
	 * Retrieve most recent data about hardware configuration as reported by the Load Monitor.
	 * Description is read from the cache and therefore host has to be registered on the Load Server
	 * in this session.
	 * 
	 * @param hostName Fully qualified network name of the host to retrieve data about.
	 * 
	 * @return Most recent hardware configuration as reported by the Load Server. <tt>null</tt> is
	 *         returned if no hardware configuration is in cache or if host is not registered
	 *         on the Load Server.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws ValueNotFoundException If host is not in database.
	 */
	HardwareDescription getLastHardwareDescription(String hostName)
		throws RemoteException, ValueNotFoundException;
	
	/**
	 * Remove all load data for given host.
	 * 
	 * @param hostName Name of the host.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostDatabaseException If an error occurred while updating database.
	 */
	void clearHostLoadData(String hostName) throws RemoteException, HostDatabaseException;
	
	/**
	 * Test is given host is online and that Host Runtime is still running on it. This method will
	 * try to connect to the Load Monitor on the host and call ping method. RMI binding for the
	 * Load Monitor is not retrieved from the cache.
	 * 
	 * @param hostName Name of the host to test.
	 * 
	 * @return <tt>true</tt> if host is online and Load Monitor is running on it, <tt>false</tt>
	 *         otherwise.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws ValueNotFoundException If host is not in database.
	 * @throws UnknownHostException If host cannot be found on the network.
	 */
	boolean isHostOnline(String hostName) 
		throws RemoteException, ValueNotFoundException, UnknownHostException;
	
	/**
	 * Retrieve status of the host. Note that this method does not attempt to connect to the host and
	 * therefore status may not be accurate. To see if host is responding, use {@link #isHostOnline(String)}
	 * method.
	 * 
	 * @param hostName Fully qualified network name of the host.
	 * 
	 * @return Status of the host.
	 * 
	 * @throws RemoteException If RMI exception occurred.
	 * @throws ValueNotFoundException If host is not in database.
	 */
	HostStatus getHostStatus(String hostName) throws RemoteException, ValueNotFoundException;
	
	/**
	 * Get map which maps host name to its last known status.
	 * 
	 * @return Map containing status of each host in the host database.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	Map< String, HostStatus > getHostStatusMap() throws RemoteException;
	
	/**
	 * Report new event that occurred on the host.
	 * 
	 * @param event Event to report.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	void reportEvent(LoadMonitorEvent event) throws RemoteException;
	
	/**
	 * Report events that occurred on the host.
	 * 
	 * @param events Events to report.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	void reportEvents(LoadMonitorEvent []events) throws RemoteException;
	
	/**
	 * Report multiple events that occurred on the host.
	 * 
	 * @param events Event to report.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	void reportEvents(Collection< LoadMonitorEvent > events) throws RemoteException;
	
	/**
	 * Registers new event listener. Event listener will then receive any events its filter will accept.
	 * 
	 * @param listener Listener to register.
	 * 
	 * @return ID of the listener. This ID can later be used to unregister listener. Note that this
	 *         ID is only way of accessing listener after it has been registered, so don't throw it 
	 *         away :).
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws InvalidArgumentException If listener is <tt>null</tt>
	 */
	OID registerEventListener(LoadMonitorEventListener listener) 
		throws RemoteException, InvalidArgumentException;
	
	/**
	 * Unregister previously registered event listener. After unregistering, listener immediately 
	 * stops receiving events.
	 * 
	 * @param listenerID ID of the listener returned when listener has been registered via 
	 *        registerEventListener.
	 *        
	 * @throws RemoteException If RMI error occurred.
	 * @throws ValueNotFoundException If listener with given ID has not been found.
	 * @throws InvalidArgumentException If ID is <tt>null</tt>.
	 */
	void unregisterEventListener(OID listenerID) 
		throws RemoteException, ValueNotFoundException, InvalidArgumentException;
	
	/**
	 * Get previously registered listener.
	 * 
	 * @param listenerID ID of the listener to retrieve.
	 * 
	 * @return Requested listener.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws ValueNotFoundException If no listener exists with given ID.
	 * @throws InvalidArgumentException If ID is <tt>null</tt>.
	 */
	LoadMonitorEventListener getEventListener(OID listenerID)
		throws RemoteException, ValueNotFoundException, InvalidArgumentException;

	/**
	 * Retrieve object which contains configuration of the Host Manager and Load Server.
	 * This object can be used to query or change configuration of the HM and LS.
	 * 
	 * @return Configuration manager for the Host Manager and Load Monitor.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	HostManagerOptionsInterface getConfiguration() throws RemoteException;
	
	/**
	 * Retrieve object which allows various queries on data collected by the Load Monitor on specified
	 * host.
	 * 
	 * @param hostName Name of the host to retrieve data about.
	 * 
	 * @return Object which provides methods to access data collected by the Load Monitor.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws LoadMonitorException If an error occurred while reading input files.
	 * @throws ValueNotFoundException If host is not in database.
	 */
	HostDataStatisticianInterface getStatsProvider(String hostName)
		throws RemoteException, LoadMonitorException, ValueNotFoundException;
}
