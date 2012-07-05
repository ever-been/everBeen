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

package cz.cuni.mff.been.hostmanager;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface provides access to the configuration of the Host Manager and Load Server.
 *
 * @author Branislav Repcek
 */
public interface HostManagerOptionsInterface extends Remote {

	/**
	 * Names of various options.
	 *
	 * @author Branislav Repcek
	 */
	public enum Option {
		
		/**
		 * Activity monitor interval.
		 */
		ACTIVITY_MONITOR_INTERVAL("activityMonitorInterval", 5000),
		
		/**
		 * Dead host timeout.
		 */
		DEAD_HOST_TIMEOUT("deadHostTimeout", 90000),
		
		/**
		 * Host detection timeout.
		 */
		HOST_DETECTION_TIMEOUT("hostDetectionTimeout", 300000),
		
		/**
		 * Pending hosts refresh interval.
		 */
		PENDING_HOSTS_REFRESH_INTERVAL("pendingHostRefreshInterval", 5000),
		
		/**
		 * Brief mode interval.
		 */
		BRIEF_MODE_INTERVAL("briefModeInterval", 10000),
		
		/**
		 * Detailed mode interval.
		 */
		DEFAULT_DETAILED_MODE_INTERVAL("defaultDetailedModeInterval", 1000);
		
		/**
		 * Property name.
		 */
		private String name;
		
		/**
		 * Default value of the option.
		 */
		private long defaultValue;
		
		/**
		 * Create new option.
		 * 
		 * @param name Name of the property which stores option.
		 */
		private Option(String name, long defaultValue) {
			
			this.name = name;
			this.defaultValue = defaultValue;
		}
		
		/**
		 * @return Name of the property.
		 */
		public String getName() {
			
			return name;
		}
		
		/**
		 * @return Default value for given option.
		 */
		public long getDefaultValue() {
			
			return defaultValue;
		}
	}

	/**
	 * This interface provides callback method which is called every time value of some configuration
	 * option is changed.
	 *
	 * @author Branislav Repcek
	 */
	public interface ValueChangeListener extends Remote {
		
		/**
		 * Callback which is called when some options changes its value.
		 * 
		 * @param option Option which has been modified.
		 * @param newValue New value of the option.
		 * 
		 * @throws HostManagerException Thrown if callee failed to apply new value of the option.
		 * @throws RemoteException If RMI error occurred.
		 */
		void valueChanged(Option option, long newValue) throws HostManagerException, RemoteException;
	}

	/**
	 * @return Interval at which Activity Monitor checks if some hosts crashed in milliseconds.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	long getActivityMonitorInterval() throws RemoteException;

	/**
	 * @param activityMonitorInterval Interval at which Activity Monitor checks if some hosts crashed.
	 *        Interval is in milliseconds. Minimum value is 1 ms. Note that setting too small value
	 *        will slow down computer on which Host Manager is running.
	 *        
	 * @throws IllegalArgumentException If interval is too small (less than 1 ms).
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostManagerException If an error occurred while saving configuration or applying
	 *         new settings. In this case, value is not changed.
	 */
	void setActivityMonitorInterval(long activityMonitorInterval) throws IllegalArgumentException,
			RemoteException, HostManagerException;

	/**
	 * @return Time after which host will be marked as crashed in milliseconds. Timeout is measured
	 *         since last sample received from the host.
	 *         
	 * @throws RemoteException If RMI error occurred.
	 */
	long getDeadHostTimeout() throws RemoteException;

	/**
	 * @param deadHostTimeout New time after which host will be marked as crashed. Timeout is measured
	 *        in milliseconds since last sample received from the host. Value has to be greater than
	 *        brief mode interval.
	 *        
	 * @throws IllegalArgumentException If interval is smaller than brief mode interval.
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostManagerException If an error occurred while saving configuration or applying
	 *         new settings. In this case, value is not changed.
	 */
	void setDeadHostTimeout(long deadHostTimeout) throws IllegalArgumentException, RemoteException,
			HostManagerException;

	/**
	 * @return Timeout after which Host Manager will not accept data from the detector. Timeout
	 *         is measured in milliseconds since start of the task.
	 *         
	 * @throws RemoteException If RMI error occurred.
	 */
	long getHostDetectionTimeout() throws RemoteException;

	/**
	 * @param hostDetectionTimeout Timeout after which Host Manager will not accept data from the 
	 *        detector. Timeout is measured in milliseconds since start of the task. It has to be 
	 *        at least 500 milliseconds.
	 *        
	 * @throws IllegalArgumentException If timeout is too small (less than or equal to 500 ms).
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostManagerException If an error occurred while saving configuration or applying
	 *         new settings. In this case, value is not changed.
	 */
	void setHostDetectionTimeout(long hostDetectionTimeout) throws IllegalArgumentException,
			RemoteException, HostManagerException;

	/**
	 * @return Interval at which Host Manager verifies if some detector does not violate Host detection
	 *         timeout. Interval is specified in milliseconds.
	 *         
	 * @throws RemoteException If RMI error occurred.
	 */
	long getPendingRefreshInterval() throws RemoteException;

	/**
	 * @param pendingRefreshInterval Interval at which Host Manager verifies if some detector does 
	 *        not violate Host detection timeout. Interval is specified in milliseconds. Minimum 
	 *        value is 1 ms, but it is recommended to use values of at least 500 ms. Also this 
	 *        interval should be smaller than Host detection timeout, otherwise all data from 
	 *        detectors will be rejected.
	 *        
	 * @throws IllegalArgumentException If interval is smaller than 1 ms.
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostManagerException If an error occurred while saving configuration or applying
	 *         new settings. In this case, value is not changed.
	 */
	void setPendingRefreshInterval(long pendingRefreshInterval) throws IllegalArgumentException,
			RemoteException, HostManagerException;

	/**
	 * @return Interval at which samples are measured in Brief Mode by the Load Monitor on all 
	 *         hosts in the network. Interval is specified in milliseconds.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	long getBriefModeInterval() throws RemoteException;

	/**
	 * @param briefModeInterval Interval at which samples are measured in Brief Mode by the 
	 *        Load Monitor on all hosts in the network. Interval is specified in milliseconds.
	 *        Minimum value is 1 ms, however this interval should be bigger than default detailed
	 *        mode interval. Smaller intervals produce more precise data about host utilisation,
	 *        but may create quite big network traffic and slow down all computers in the network.
	 *        
	 * @throws IllegalArgumentException If interval is too small.
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostManagerException If an error occurred while saving configuration or applying
	 *         new settings. In this case, value is not changed.
	 */
	void setBriefModeInterval(long briefModeInterval) throws IllegalArgumentException,
			RemoteException, HostManagerException;

	/**
	 * @return Default sampling rate of the Load Monitor when in detailed mode. This interval may
	 *         be overridden on a per-task basis via Task Descriptor. Interval is specified in
	 *         milliseconds.
	 *         
	 * @throws RemoteException If RMI error occurred.
	 */
	long getDefaultDetailedModeInterval() throws RemoteException;

	/**
	 * @param defaultDetailedModeInterval Default sampling rate of the Load Monitor when in detailed 
	 *        mode. This interval may be overridden on a per-task basis via Task Descriptor. 
	 *        Interval is specified in milliseconds. Minimum value is 1 ms. This interval should
	 *        be smaller than brief mode interval. Note that too small interval may slow down
	 *        computer and will distort results of your benchmark.
	 *        
	 * @throws IllegalArgumentException If interval is too small.
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostManagerException If an error occurred while saving configuration or applying
	 *         new settings. In this case, value is not changed.
	 */
	void setDefaultDetailedModeInterval(long defaultDetailedModeInterval)
			throws IllegalArgumentException, RemoteException, HostManagerException;

	/**
	 * Register new listener which will receive messages when value of some property is changed.
	 * 
	 * @param listener Listener to register. Each listener can be registered only once. Listener
	 *        cannot be <tt>null</tt>.
	 *        
	 * @throws IllegalArgumentException If listener is already registered or if it is <tt>null</tt>.
	 * @throws RemoteException If RMI error occurred.
	 */
	void registerValueChangeListener(ValueChangeListener listener)
		throws IllegalArgumentException, RemoteException;

	/**
	 * Unregister listener previously registered via
	 * {@link #registerValueChangeListener(cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface.ValueChangeListener)}. 
	 * 
	 * @param listener Listener to unregister.
	 * 
	 * @throws IllegalArgumentException If listener is <tt>null</tt> or if it is not registered.
	 * @throws RemoteException If RMI error occurred.
	 */
	void unregisterValueChangeListener(ValueChangeListener listener)
		throws IllegalArgumentException, RemoteException;

}
