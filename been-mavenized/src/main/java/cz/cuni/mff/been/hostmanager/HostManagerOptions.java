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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Configuration manager for both Host Manager and Load Server.
 *
 * @author Branislav Repcek
 */
class HostManagerOptions extends UnicastRemoteObject implements HostManagerOptionsInterface {

	private static final long	serialVersionUID	= 859825792109847658L;

	/**
	 * Default name of the configuration file.
	 * 
	 * NOTE change this in build.xml if modified.
	 */
	public static final String DEFAULT_FILE_NAME = "hostmanager.options";
	
	/**
	 * Host detection timeout (ms).
	 */
	private long hostDetectionTimeout;
	
	/**
	 * Refresh interval of the WatchDog thread (ms).
	 */
	private long pendingRefreshInterval;
	
	/**
	 * Activity Monitor refresh interval (ms).
	 */
	private long activityMonitorInterval;
	
	/**
	 * Dead host timeout (ms). 
	 */
	private long deadHostTimeout;
	
	/**
	 * Brief mode sampling interval (ms).
	 */
	private long briefModeInterval;
	
	/**
	 * Default detailed mode sampling interval.
	 */
	private long defaultDetailedModeInterval;
	
	/**
	 * Name of the config file.
	 */
	private String fileName;
	
	/**
	 * Data storage 2.
	 */
	private Properties properties;
	
	/**
	 * Listeners.
	 */
	private Set< ValueChangeListener > listeners;
	
	/**
	 * Load configuration from given file.
	 * 
	 * @param fileName Name of the configuration file.
	 * 
	 * @throws InputParseException If an error occurred while parsing input data.
	 * @throws RemoteException If RMI error occurred.
	 */
	public HostManagerOptions(String fileName) throws InputParseException, RemoteException {
		
		listeners = new HashSet< ValueChangeListener >();
		
		load(fileName);
	}

	/* @see cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface#getActivityMonitorInterval()
	 */
	public synchronized long getActivityMonitorInterval() throws RemoteException {
		
		return activityMonitorInterval;
	}

	/* @see cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface#setActivityMonitorInterval(long)
	 */
	public synchronized void setActivityMonitorInterval(long activityMonitorInterval) 
		throws IllegalArgumentException, RemoteException, HostManagerException {
		
		if (this.activityMonitorInterval == activityMonitorInterval) {
			return;
		}
		
		if (activityMonitorInterval < 1) {
			throw new IllegalArgumentException("Activity Monitor interval of \""
					+ activityMonitorInterval + " is too small. Minimum value is 1 ms.");
		}

		valueChanged(Option.ACTIVITY_MONITOR_INTERVAL, activityMonitorInterval);
		
		this.activityMonitorInterval = activityMonitorInterval;

		save();
	}

	/* @see cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface#getDeadHostTimeout()
	 */
	public synchronized long getDeadHostTimeout() throws RemoteException {
		
		return deadHostTimeout;
	}

	/* @see cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface#setDeadHostTimeout(long)
	 */
	public synchronized void setDeadHostTimeout(long deadHostTimeout)
		throws IllegalArgumentException, RemoteException, HostManagerException {
		
		if (this.deadHostTimeout == deadHostTimeout) {
			return;
		}
		
		if (deadHostTimeout <= getBriefModeInterval()) {
			throw new IllegalArgumentException("Dead host timeout is too small. It has to "
					+ "be greater than brief mode interval.");
		}

		valueChanged(Option.DEAD_HOST_TIMEOUT, deadHostTimeout);
		
		this.deadHostTimeout = deadHostTimeout;
		save();
	}

	/* @see cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface#getHostDetectionTimeout()
	 */
	public synchronized long getHostDetectionTimeout() throws RemoteException {
		
		return hostDetectionTimeout;
	}

	/* @see cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface#setHostDetectionTimeout(long)
	 */
	public synchronized void setHostDetectionTimeout(long hostDetectionTimeout)
		throws IllegalArgumentException, RemoteException, HostManagerException {
		
		if (this.hostDetectionTimeout == hostDetectionTimeout) {
			return;
		}
		
		if (hostDetectionTimeout <= 500) {
			throw new IllegalArgumentException("Host detection timeout has to be at least 500 ms.");
		}

		valueChanged(Option.HOST_DETECTION_TIMEOUT, hostDetectionTimeout);
		
		this.hostDetectionTimeout = hostDetectionTimeout;
		save();
	}

	/* @see cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface#getPendingRefreshInterval()
	 */
	public synchronized long getPendingRefreshInterval() throws RemoteException {
		
		return pendingRefreshInterval;
	}

	/* @see cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface#setPendingRefreshInterval(long)
	 */
	public synchronized void setPendingRefreshInterval(long pendingRefreshInterval)
		throws IllegalArgumentException, RemoteException, HostManagerException {
		
		if (pendingRefreshInterval == this.pendingRefreshInterval) {
			return;
		}
		
		if (pendingRefreshInterval < 1) {
			throw new IllegalArgumentException("Pending host refresh interval is too small."
					+ " Minimum value is 1 ms.");
		}
		
		valueChanged(Option.PENDING_HOSTS_REFRESH_INTERVAL, pendingRefreshInterval);
		
		this.pendingRefreshInterval = pendingRefreshInterval;
		save();
	}

	/* @see cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface#getBriefModeInterval()
	 */
	public synchronized long getBriefModeInterval() throws RemoteException {
		
		return briefModeInterval;
	}

	/* @see cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface#setBriefModeInterval(long)
	 */
	public synchronized void setBriefModeInterval(long briefModeInterval)
		throws IllegalArgumentException, RemoteException, HostManagerException {
		
		if (this.briefModeInterval == briefModeInterval) {
			return;
		}
		
		if (briefModeInterval < 1) {
			throw new IllegalArgumentException("Brief mode interval is too small."
					+ " Minimum value is 1 ms.");
		}
		
		valueChanged(Option.BRIEF_MODE_INTERVAL, briefModeInterval);
		
		this.briefModeInterval = briefModeInterval;
		save();
	}

	/* @see cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface#getDefaultDetailedModeInterval()
	 */
	public synchronized long getDefaultDetailedModeInterval() throws RemoteException {
		
		return defaultDetailedModeInterval;
	}

	/* @see cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface#setDefaultDetailedModeInterval(long)
	 */
	public synchronized void setDefaultDetailedModeInterval(long defaultDetailedModeInterval)
		throws IllegalArgumentException, RemoteException, HostManagerException {
		
		if (this.defaultDetailedModeInterval == defaultDetailedModeInterval) {
			return;
		}
		
		if (defaultDetailedModeInterval < 1) {
			throw new IllegalArgumentException("Default detailed mode interval is too small."
					+ " Minimum value is 1 ms.");
		}
		
		valueChanged(Option.DEFAULT_DETAILED_MODE_INTERVAL, defaultDetailedModeInterval);
		
		this.defaultDetailedModeInterval = defaultDetailedModeInterval;
		save();
	}
	
	/* @see cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface#registerValueChangeListener(cz.cuni.mff.been.hostmanager.HostManagerOptions.ValueChangeListener)
	 */
	public void registerValueChangeListener(ValueChangeListener listener)
		throws IllegalArgumentException, RemoteException {

		synchronized (listeners) {
			
			if (listener == null) {
				throw new IllegalArgumentException("null listeners are not allowed.");
			}
		
			if (listeners.contains(listener)) {
				throw new IllegalArgumentException("Listener is already registered.");
			}
			
			listeners.add(listener);
		}
	}

	/* @see cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface#unregisterValueChangeListener(cz.cuni.mff.been.hostmanager.HostManagerOptions.ValueChangeListener)
	 */
	public void unregisterValueChangeListener(ValueChangeListener listener)
		throws IllegalArgumentException, RemoteException {

		synchronized (listeners) {
			
			if (listener == null) {
				throw new IllegalArgumentException("Unable to unregister null listener.");
			}
			
			if (!listeners.contains(listener)) {
				throw new IllegalArgumentException("Listener is not registered.");
			}
			
			listeners.remove(listener);
		}
	}
	
	/**
	 * Load configuration from file.
	 * 
	 * @param fileName Name of the file which contains configuration.
	 * 
	 * @throws InputParseException If an error occurred while parsing data.
	 */
	private void load(String fileName) throws InputParseException {
		
		properties = new Properties();
		
		FileInputStream stream = null;
		
		try {
			stream = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			throw new InputParseException("Unable to open options file.", e);
		}
		try {
			properties.load(stream);
		} catch (IOException e) {
			throw new InputParseException("Error reading properties.", e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				throw new InputParseException("Unable to close input file.", e);
			}
		}
		
		this.fileName = fileName;
		
		hostDetectionTimeout = rStrProp2Long(Option.HOST_DETECTION_TIMEOUT.getName(), 500);
		pendingRefreshInterval = rStrProp2Long(Option.PENDING_HOSTS_REFRESH_INTERVAL.getName(), 1);
		activityMonitorInterval = rStrProp2Long(Option.ACTIVITY_MONITOR_INTERVAL.getName(), 1);
		briefModeInterval = rStrProp2Long(Option.BRIEF_MODE_INTERVAL.getName(), 1);
		deadHostTimeout = rStrProp2Long(Option.DEAD_HOST_TIMEOUT.getName(), briefModeInterval);
		defaultDetailedModeInterval = rStrProp2Long(Option.DEFAULT_DETAILED_MODE_INTERVAL.getName(), 1);
	}
	
	/**
	 * Convert value of given property to long.
	 *  
	 * @param propName name of property to convert.
	 * @param min Minimum allowed value of the property.
	 * 
	 * @return Value of property as long.
	 * 
	 * @throws InputParseException If an error occurred while converting value to long.
	 */
	private long rStrProp2Long(String propName, long min) 
		throws InputParseException {
		
		String value = properties.getProperty(propName);
		
		if (value == null) {
			throw new InputParseException("Unable to find property \"" + propName + "\".");
		}
		
		long res = 0;
		
		try {
			res = Long.valueOf(value).longValue();
		} catch (NumberFormatException e) {
			throw new InputParseException("Error convertin value of \"" + propName + "\" property.", e);
		}
		
		if (res < min) {
			throw new InputParseException("Value of property \"" + propName + "\" is too small."
					+ " Minimum value is " + min + ".");
		}
		
		return res;
	}
	
	/**
	 * Set value of given property.
	 * 
	 * @param value Value of the property.
	 * @param name Name of the property to set.
	 */
	private void sLong2StrProp(long value, String name) {
		
		properties.setProperty(name, String.valueOf(value));
	}
	
	/**
	 * Save configuration to the file.
	 * 
	 * @throws OutputWriteException If an error occurred while writing data.
	 */
	private void save() throws OutputWriteException {
		
		sLong2StrProp(hostDetectionTimeout, Option.HOST_DETECTION_TIMEOUT.getName());
		sLong2StrProp(pendingRefreshInterval, Option.PENDING_HOSTS_REFRESH_INTERVAL.getName());
		sLong2StrProp(activityMonitorInterval, Option.ACTIVITY_MONITOR_INTERVAL.getName());
		sLong2StrProp(deadHostTimeout, Option.DEAD_HOST_TIMEOUT.getName());
		sLong2StrProp(briefModeInterval, Option.BRIEF_MODE_INTERVAL.getName());
		sLong2StrProp(defaultDetailedModeInterval, Option.DEFAULT_DETAILED_MODE_INTERVAL.getName());
		
		FileOutputStream stream = null;
		
		try {
			stream = new FileOutputStream(fileName, false);
		} catch (FileNotFoundException e) {
			throw new OutputWriteException("Unable to create configuration file \""
					+ fileName + "\".", e);
		}
		try {
			properties.store(stream, "Host Manager Options");
		} catch (IOException e) {
			throw new OutputWriteException("Unable to save configuration.", e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				throw new OutputWriteException("Unable to close output file.", e);
			}
		}
	}
	
	/**
	 * Notify all listeners that some value has been changed.
	 * 
	 * @param option Value that has been changed.
	 * @param newValue Value to which option is changed.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws HostManagerException If an error occurred while applying new settings.
	 */
	private void valueChanged(Option option, long newValue)
		throws RemoteException, HostManagerException {
	
		synchronized (listeners) {

			for (ValueChangeListener listener: listeners) {
				listener.valueChanged(option, newValue);
			}
		}
	}
}
