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
import java.net.UnknownHostException;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import java.rmi.server.UnicastRemoteObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.common.id.OID;

import cz.cuni.mff.been.hostmanager.HostDatabaseException;
import cz.cuni.mff.been.hostmanager.HostManagerApplicationData;
import cz.cuni.mff.been.hostmanager.HostManagerException;
import cz.cuni.mff.been.hostmanager.HostManagerLogger;
import cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;

import cz.cuni.mff.been.hostmanager.database.HostDatabaseEngine;

import cz.cuni.mff.been.hostmanager.util.MiscUtils;

/**
 * Implementation of Load Server. Load Server manages all load data collected by Load Monitor on hosts.
 *
 * @author Branislav Repcek
 */
public class LoadServerImplementation extends UnicastRemoteObject
	implements LoadServerInterface, HostManagerOptionsInterface.ValueChangeListener {

	private static final long	serialVersionUID	= 1533525426129779591L;

	/**
	 * Application settings.
	 */
	private HostManagerApplicationData appData;
	
	/**
	 * Database management.
	 */
	private HostDatabaseEngine database;

	/**
	 * Logger.
	 */
	private HostManagerLogger logger;
	
	/**
	 * Configuration.
	 */
	private HostManagerOptionsInterface configuration;
	
	/**
	 * Maps listeners to their IDs.
	 */
	private ConcurrentHashMap< OID, LoadMonitorEventListener > listeners;

	/**
	 * Event queue.
	 */
	private EventQueue eventQueue;
	
	/**
	 * Event receiver.
	 */
	private EventReceiver eventReceiver;
	
	/**
	 * Cache manager.
	 */
	private ActivityMonitorCache cache;
	
	/**
	 * Activity monitor.
	 */
	private ActivityMonitor activityMonitor;
	
	/**
	 * Listener which stores data in the database.
	 */
	private EventStorageListener eventStorageListener;
	
	/**
	 * Global brief mode interval.
	 */
	private long globalBriefModeInterval;
	
	/**
	 * Global detailed mode interval.
	 */
	private long globalDetailedModeInterval;
	
	/**
	 * Create new Load Server.
	 * 
	 * @param appData Application data provided by the service.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws LoadMonitorException If an error occurred while initialising components
	 *         of the Load Server.
	 */
	public LoadServerImplementation(HostManagerApplicationData appData)
		throws RemoteException, LoadMonitorException {
		
		super();
		
		this.appData = appData;
		this.logger = appData.getLogger();
		this.configuration = appData.getConfiguration();
		
		this.logger.logInfo("Initializing Load Server.");
		this.database = appData.getDatabase();

		try {
			this.globalBriefModeInterval = configuration.getBriefModeInterval();
			this.globalDetailedModeInterval = configuration.getDefaultDetailedModeInterval();
		} catch (RemoteException e) {
			throw new LoadMonitorException("Unable to query LS settings.");
		}
		
		try {
			this.configuration.registerValueChangeListener(this);
		} catch (IllegalArgumentException e) {
			throw new LoadMonitorException("Unable to register configuration listener.", e);
		}
		
		listeners = new ConcurrentHashMap< OID, LoadMonitorEventListener >();
		eventQueue = new EventQueue();
		eventReceiver = new EventReceiver(eventQueue, listeners, logger);
		
		logger.logInfo("Creating Cache Manager.");
		cache = new ActivityMonitorCache();
		logger.logInfo("Cache Manager created successfully.");
		
		logger.logInfo("Creating ActivityMonitor.");
		try {
			activityMonitor = new ActivityMonitor(this, appData, cache);
		} catch (LoadMonitorException e) {
			throw new LoadMonitorException("Unable to initialize Activity Monitor.", e);
		}
		logger.logInfo("ActivityMonitor created successfully.");
		
		logger.logInfo("Creating EventStorageListener.");
		eventStorageListener = new EventStorageListener(appData);
		
		try {
			registerEventListener(eventStorageListener);
		} catch (RemoteException e) {
			logger.logFatal("Unable to register EventStorageListener.", e);
			throw new LoadMonitorException("Unable to register EventStorageListener.", e);
		} catch (InvalidArgumentException e) {
			assert false : "EventStorageListener is null.";
		}
		
		logger.logInfo("EventStorageListener registered successfully.");
	}
	
	/**
	 * Terminate Load Server. This will free all resources and close all opened files.
	 * 
	 * @throws HostManagerException If some error occurred.
	 */
	public void terminate() throws HostManagerException {

		logger.logInfo("Load Server is shutting down.");

		try {
			appData.getConfiguration().unregisterValueChangeListener(this);
		} catch (IllegalArgumentException e) {
			logger.logError("HM1: Unable to unregister configuration listener.", e);
		} catch (RemoteException e) {
			logger.logError("HM2: Unable to unregister configuration listener.", e);
		}
		
		eventReceiver.stop();
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadServerInterface#clearHostLoadData(java.lang.String)
	 */
	public void clearHostLoadData(String hostName) throws RemoteException, HostDatabaseException {

		database.deleteLoadFiles(hostName);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadServerInterface#getLastSample(java.lang.String)
	 */
	public LoadSample getLastSample(String hostName) throws RemoteException, ValueNotFoundException {
		
		synchronized (database) {
			if (!database.isHost(hostName)) {
				throw new ValueNotFoundException("Host \"" + hostName + "\" is not in database.");
			}
			
			ActivityMonitorCache.CacheElement elem = cache.getCacheElement(hostName);
			
			if (elem == null) {
				return null;
			} else {
				return elem.getLastSample();
			}
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadServerInterface#getLastHardwareDescription(java.lang.String)
	 */
	public HardwareDescription getLastHardwareDescription(String hostName) throws RemoteException, ValueNotFoundException {

		synchronized (database) {
			if (!database.isHost(hostName)) {
				throw new ValueNotFoundException("Host \"" + hostName + "\" is not in database.");
			}
			
			ActivityMonitorCache.CacheElement elem = cache.getCacheElement(hostName);
			
			if (elem == null) {
				return null;
			} else {
				return elem.getLastDescription();
			}
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadServerInterface#isHostOnline(java.lang.String)
	 */
	public boolean isHostOnline(String hostName)
		throws RemoteException, ValueNotFoundException, UnknownHostException {
		
		String canonicalName;
		
		try {
			canonicalName = MiscUtils.getCanonicalHostName(hostName);
		} catch (UnknownHostException e) {
			if (!database.isHost(hostName)) {
				throw e;
			} else {
				// host is in database, but its name cannot be resolved -> it is offline
				return false;
			}
		}
		
		if (!database.isHost(canonicalName)) {
			throw new ValueNotFoundException("Host \"" + canonicalName + "\" is not in database.");
		}

		LoadMonitorInterface loadMonitor = null;
		
		try {
			loadMonitor = (LoadMonitorInterface) Naming.lookup(
					"//" + hostName + ":" + RMI.REGISTRY_PORT + "/" + LoadMonitorInterface.RMI_NAME
			);
		} catch (Exception e) {
			// Unable to connect to the Load Monitor -> host is offline or crashed
			return false;
		}
		
		try {
			loadMonitor.ping();
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.LoadServerInterface#getHostStatus(java.lang.String)
	 */
	public HostStatus getHostStatus(String hostName) throws RemoteException, ValueNotFoundException {
		
		synchronized (database) {
			if (!database.isHost(hostName)) {
				throw new ValueNotFoundException("Host \"" + hostName + "\" is not in database.");
			}
			
			ActivityMonitorCache.CacheElement elem = cache.getCacheElement(hostName);
			
			if (elem == null) {
				return HostStatus.OFFLINE;
			} else {
				return elem.getStatus();
			}
		}
	}

	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.LoadServerInterface#getHostStatusMap()
	 */
	public Map< String, HostStatus > getHostStatusMap() throws RemoteException {

		HashMap< String, HostStatus > result = new HashMap< String, HostStatus >();
		
		synchronized (database) {

			for (Iterator< String > it = database.getHostNamesIterator(); it.hasNext(); ) {
				String hostName = it.next();
				
				ActivityMonitorCache.CacheElement elem = cache.getCacheElement(hostName);
				
				if (elem == null) {
					result.put(hostName, HostStatus.OFFLINE);
				} else {
					result.put(hostName, elem.getStatus());
				}
			}
		}
		
		return result;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadServerInterface#reportEvent(cz.cuni.mff.been.hostmanager.load.LoadMonitorEvent)
	 */
	public void reportEvent(LoadMonitorEvent event) throws RemoteException {
		
		logger.logTrace("Event: [" + MiscUtils.formatDate(event.getTime()) + "] " + event.getHostName());
		eventQueue.add(event);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadServerInterface#reportEvents(cz.cuni.mff.been.hostmanager.load.LoadMonitorEvent[])
	 */
	public void reportEvents(LoadMonitorEvent []events) throws RemoteException {
		
		eventQueue.addAll(events);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadServerInterface#reportEvents(java.util.Collection)
	 */
	public void reportEvents(Collection< LoadMonitorEvent > events) throws RemoteException {
		
		eventQueue.addAll(events);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadServerInterface#registerEventListener(cz.cuni.mff.been.hostmanager.load.LoadMonitorEventListener)
	 */
	public OID registerEventListener(LoadMonitorEventListener listener) 
		throws RemoteException, InvalidArgumentException {

		MiscUtils.verifyParameterIsNotNull(listener, "listener");

		OID newID = appData.getNextID(OID.class);
		
		listeners.put(newID, listener);
		
		return newID;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadServerInterface#unregisterEventListener(cz.cuni.mff.been.common.id.OID)
	 */
	public void unregisterEventListener(OID listenerID) 
		throws RemoteException, ValueNotFoundException, InvalidArgumentException {

		MiscUtils.verifyParameterIsNotNull(listenerID, "listenerID");
		
		if (listeners.containsKey(listenerID)) {
			listeners.remove(listenerID);
		} else {
			throw new ValueNotFoundException("Listener with given ID is not registered.");
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadServerInterface#getEventListener(cz.cuni.mff.been.common.id.OID)
	 */
	public LoadMonitorEventListener getEventListener(OID listenerID)
		throws RemoteException, ValueNotFoundException, InvalidArgumentException {
		
		MiscUtils.verifyParameterIsNotNull(listenerID, "listenerID");
		
		if (listeners.containsKey(listenerID)) {
			return listeners.get(listenerID);
		} else {
			throw new ValueNotFoundException("Unable to find listener with given ID.");
		}
	}

	/**
	 * This method is called automatically by the HM when new host connects to the network.
	 * 
	 * @param hostName Name of the host.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws LoadMonitorException If other error occurred.
	 */
	public void newHostConnected(String hostName) throws RemoteException, LoadMonitorException {

		LoadMonitorInterface result = null;

		String canonicalName = hostName;
		
		try {
			canonicalName = MiscUtils.getCanonicalHostName(hostName);
		} catch (UnknownHostException e) {
			logger.logError("Unable to resolve host \"" + hostName + "\".", e);
			throw new LoadMonitorException("Unable to resolve host.", e);
		}
		
		try {
			result = (LoadMonitorInterface) Naming.lookup(
					"//" + hostName + ":" + RMI.REGISTRY_PORT + "/" + LoadMonitorInterface.RMI_NAME
			);
		} catch (NotBoundException e) {
			logger.logError("Load Monitor not bound on host \"" + hostName + "\".");
			throw new LoadMonitorException(e);
		} catch (Exception e) {
			logger.logError("Unable to connect to the Load Monitor on \"" + hostName + "\".");
			throw new LoadMonitorException(e);
		}
		
		try {
			cache.addCacheElement(canonicalName, result);
		} catch (InvalidArgumentException e) {
			logger.logError("Unable to add \"" + canonicalName + "\" to the cache.", e);
			throw new LoadMonitorException("Unable to add \"" + canonicalName + "\" to the cache.", e);
		}
		
		try {
			result.initialize(hostName, 
			                  this, 
			                  configuration.getDefaultDetailedModeInterval(),
			                  configuration.getBriefModeInterval());
		} catch (LoadMonitorException e) {
			logger.logError("Unable to initialize Load Monitor on \"" + hostName + "\".", e);
			cache.removeCacheElement(canonicalName);
			throw new LoadMonitorException(e);
		} catch (RemoteException e) {
			logger.logError("Error initializing Load Monitor on \"" + hostName + "\".", e);
			cache.removeCacheElement(canonicalName);
			throw new LoadMonitorException(e);
		}
		
		activityMonitor.newHostConnected(canonicalName);
	}
	
	/**
	 * Method called automatically by the Host Manager when host is disconnected from network.
	 * 
	 * @param hostName Name of the host that is disconnected.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 */
	public void hostDisconnected(String hostName) throws RemoteException {
		
		// empty space provided by the CocaCola company.
	}

	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.LoadServerInterface#getConfiguration()
	 */
	public HostManagerOptionsInterface getConfiguration() throws RemoteException {
		
		return appData.getConfiguration();
	}

	/* 
	 * @see cz.cuni.mff.been.hostmanager.HostManagerOptions.ValueChangeListener#valueChanged(cz.cuni.mff.been.hostmanager.HostManagerOptions.Option, long)
	 */
	public void valueChanged(HostManagerOptionsInterface.Option option, long newValue)
		throws HostManagerException, RemoteException {
		
		switch (option) {
			
			case ACTIVITY_MONITOR_INTERVAL:
				logger.logInfo("Value of ACTIVITY_MONITOR_INTERVAL changed to " + newValue);
				
				activityMonitor.reloadOptions();
				break;
				
			case BRIEF_MODE_INTERVAL:
				logger.logInfo("Value of BRIEF_MODE_INTERVAL changed to " + newValue);
				globalBriefModeInterval = newValue;
				try {
					Map< String, HostStatus > statuses = getHostStatusMap();
					
					for (Map.Entry< String, HostStatus > entry: statuses.entrySet()) {
						String name = entry.getKey();
						
						
						ActivityMonitorCache.CacheElement elem = cache.getCacheElement(name);
						
						if (elem != null) {
							if (elem.getStatus() == HostStatus.ONLINE) {
								logger.logTrace("Setting brief interval on: " + name);

								LoadMonitorInterface lm = elem.getLoadMonitor();
								lm.setBriefInterval(globalBriefModeInterval);
							}
						}
					}
				} catch (Exception e) {
					throw new HostManagerException("Unable to set global brief mode interval.");
				}
				break;
				
			case DEFAULT_DETAILED_MODE_INTERVAL:
				logger.logInfo("Value of DEFAULT_DETAILED_MODE_INTERVAL changed to " + newValue);
				globalDetailedModeInterval = newValue;
				try {
					Map< String, HostStatus > statuses = getHostStatusMap();
					
					for (Map.Entry< String, HostStatus > entry: statuses.entrySet()) {
						String name = entry.getKey();
						
						ActivityMonitorCache.CacheElement elem = cache.getCacheElement(name);
						
						if (elem != null) {
							if (elem.getStatus() == HostStatus.ONLINE) {
								logger.logTrace("Setting default detailed mode interval on: " + name);

								LoadMonitorInterface lm = elem.getLoadMonitor();
								lm.setDefaultDetailedInterval(globalDetailedModeInterval);
							}
						}
					}
				} catch (Exception e) {
					throw new HostManagerException("Unable to set global detailed mode interval.");
				}
				break;
				
			case DEAD_HOST_TIMEOUT:
				logger.logInfo("Value of DEAD_HOST_TIMEOUT changed to " + newValue);
				activityMonitor.reloadOptions();
				break;
				
			default:
				// Other options are HM only...
		}
	}

	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.LoadServerInterface#getHostLoadDataProvider(java.lang.String)
	 */
	public HostDataStatistician getStatsProvider(String hostName)
		throws RemoteException, LoadMonitorException, ValueNotFoundException {

		if (database.isHost(hostName)) {
			String mapFile = database.getLoadMapFilePath(hostName);
			String loadFile = database.getLoadFilePath(hostName);
			try {
				return new HostDataStatistician(mapFile, loadFile);
			} catch (IOException e) {
				throw new LoadMonitorException("Unable to create data provider.", e);
			}
		} else {
			throw new ValueNotFoundException("Host \"" + hostName + "\" is not in database.");
		}
	}
}
