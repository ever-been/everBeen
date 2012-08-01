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

import java.io.File;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.common.id.OID;
import cz.cuni.mff.been.hostmanager.database.DatabaseManagerInterface;
import cz.cuni.mff.been.hostmanager.database.HostDatabaseEngine;
import cz.cuni.mff.been.hostmanager.database.HostGroup;
import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;
import cz.cuni.mff.been.hostmanager.database.PropertyDescription;
import cz.cuni.mff.been.hostmanager.database.PropertyDescriptionTable;
import cz.cuni.mff.been.hostmanager.database.PropertyTreeInterface;
import cz.cuni.mff.been.hostmanager.database.RestrictionInterface;
import cz.cuni.mff.been.hostmanager.database.SimpleHostInfo;
import cz.cuni.mff.been.hostmanager.database.SoftwareAliasDefinition;
import cz.cuni.mff.been.hostmanager.load.LoadServerInterface;
import cz.cuni.mff.been.hostmanager.util.MiscUtils;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.taskmanager.HostRuntimeRegistrationListener;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper;

/**
 * This class implements all interfaces that form Host Manager.
 * 
 * NOTE: This is not the most memory efficient implementation because it always keeps whole database 
 * in memory. However, this should not be an issue since memory is cheap :)
 * 
 * @author Branislav Repcek
 */
public class HostManagerImplementation extends UnicastRemoteObject 
	implements HostManagerInterface, DatabaseManagerInterface, HostRuntimeRegistrationListener,
		HostManagerOptions.ValueChangeListener {

	private static final long	serialVersionUID	= -7475870081483310519L;

	/**
	 * Name of the file with descriptions of properties.
	 */
	public static final String PROPERTY_DESCRIPTION_FILE_NAME = "property.desc";
	
	/**
	 * App data.
	 */
	private HostManagerApplicationData appData;
	
	/**
	 * Path to HM data directory. This directory contains all HM related data (including host database).
	 */
	private String dataPath;

	/**
	 * Descriptions of all properties and objects in database.
	 */
	private PropertyDescriptionTable descriptions;

	/**
	 * Handle to HM task.
	 */
	private Task task;
	
	/**
	 * Logger.
	 */
	private HostManagerLogger logger;
	
	/**
	 * Configuration.
	 */
	private HostManagerOptionsInterface configuration;

	/**
	 * ID number of the next detector task.
	 */
	private long taskIDNum;
	
	/**
	 * List of hosts from which updated data from detectors has not been received yet.
	 * Key in the map is fully qualified name of the host, value is info about given host.
	 */
	private ConcurrentHashMap< String, PendingHostInfo > hostsPending;
	
	/**
	 * Cache which stores status of each add/refresh operation.
	 */
	private ConcurrentHashMap< OperationHandle, HostOperationStatus > operationCache;
	
	/**
	 * Last handle assigned to operation (counter).
	 */
	private OperationHandle currentHandle;
	
	/**
	 * Local database manager.
	 */
	private HostDatabaseEngine database;
	
	/**
	 * Timer used for watching timeouts.
	 */
	private Timer timer;
	
	/**
	 * How often will be queue of pending hosts examined.
	 */
	private long pendingHostsCheckPeriod;
	
	/**
	 * How long to wait for the data from detector.
	 */
	private long hostDetectionTimeout;
	
	/**
	 * List of the active listeners.
	 */
	private ConcurrentHashMap< OID, HostManagerEventListener > listeners;
	
	/**
	 * Create new HostManagerImplementation object.
	 *
	 * @param appData Application data initialised by the service.
	 * @param task Task which is used to access TaskManager which will be used for logging.
	 * 
	 * @throws RemoteException if RMI error occurred.
	 * @throws HostManagerException if an error occurred during service initialisation.
	 */
	public HostManagerImplementation(HostManagerApplicationData appData, Task task)
		throws RemoteException, HostManagerException {
		
		super();
	
		taskIDNum = 0;
		
		this.appData = appData;
		this.task = task;
		this.logger = appData.getLogger();
		this.configuration = appData.getConfiguration();
		this.database = appData.getDatabase();
	
		logger.logInfo("Starting Host Manager.");
		
		dataPath = appData.getWorkingDirectory();
		
		if (!dataPath.endsWith(File.separator)) {
			dataPath += File.separator;
		}
		
		listeners = new ConcurrentHashMap< OID, HostManagerEventListener >();
		
		try {
			appData.getConfiguration().registerValueChangeListener(this);
		} catch (IllegalArgumentException e) {
			throw new HostManagerException("Unable to register configuration listener.", e);
		}
		
		initialize();
		logger.logInfo("Host Manager started successfully.");
	}
	
	/**
	 * Initialise instance of Host Manager. This method should be called only once.
	 * 
	 * @throws HostManagerException if an error occurred.
	 */
	private void initialize() throws HostManagerException {
	
		logger.logInfo("Loading property description file.");
		try {
			descriptions = new PropertyDescriptionTable(dataPath + PROPERTY_DESCRIPTION_FILE_NAME);
		} catch (Exception e) {
			logger.logFatal("Error loading property descriptions.", e);
			throw new HostManagerException(e);
		}
		logger.logInfo("Property descriptions loaded successfully.");
		
		hostsPending = new ConcurrentHashMap< String, PendingHostInfo >();
		operationCache = new ConcurrentHashMap< OperationHandle, HostOperationStatus >();
		currentHandle = new OperationHandle();

		try {
			pendingHostsCheckPeriod = configuration.getPendingRefreshInterval();
			hostDetectionTimeout = configuration.getHostDetectionTimeout();
		} catch (RemoteException e) {
			throw new HostManagerException(e);
		}

		restartWatchDog();
		
		try {
			task.getTasksPort().registerEventListener(this);
		} catch (RemoteException e) {
			throw new HostManagerException("Unable to register event listener on the TM.", e);
		}
	}
	
	/**
	 * Free all used resources. Always call this when Host Manager is terminating.
	 * 
	 * @throws HostManagerException if some error occurred.
	 */
	public void terminate() throws HostManagerException {

		logger.logInfo("Host Manager is shutting down.");

		try {
			task.getTasksPort().unregisterEventListener(this);
		} catch (RemoteException e) {
			logger.logError("Unable to unregister event listener on the TM.", e);
		}
		
		timer.cancel();
		database.terminate();

		try {
			appData.getConfiguration().unregisterValueChangeListener(this);
		} catch (IllegalArgumentException e) {
			logger.logError("HM1: Unable to unregister configuration listener.", e);
		} catch (RemoteException e) {
			logger.logError("HM2: Unable to unregister configuration listener.", e);
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#addHost(String)
	 */
	public OperationHandle addHost(String hostName) throws UnknownHostException, RemoteException {
	
		logger.logDebug("Adding host \"" + hostName + "\" to the database.");
		
		String canonicalName = MiscUtils.getCanonicalHostName(hostName);
		OperationHandle handle = getNextOperationHandle();

		operationCache.put(handle, buildUnknownStatus(canonicalName));
		
		String key = handle.toString();

		// Is host already in db?
		if (isHostInDatabase(canonicalName)) {
			
			logger.logError("Host \"" + canonicalName + "\" is already in database.");
			String message = "Unable to add host. Host is already in the database.";
			operationCache.put(handle, buildFailedStatus(message, canonicalName));
			
			postEvent(new HostManagerEvent(HostManagerEvent.EventType.HOST_ADD,
			                               HostManagerEvent.Status.FAILED,
			                               canonicalName,
			                               message));
			
			return handle;
		}

		boolean scheduleResult = false;

		try {
			scheduleResult = runDetectorOnHost(canonicalName, key);
		} catch (Exception e) {
			String message = "Unable to schedule detector on host, reason: \"" + e.getMessage() + "\".";
			
			logger.logError("Unable to schedule detector on \"" + canonicalName + "\".", e);
			
			operationCache.put(handle, buildFailedStatus(message, canonicalName));
			
			postEvent(new HostManagerEvent(HostManagerEvent.EventType.HOST_ADD,
			                               HostManagerEvent.Status.FAILED,
			                               canonicalName,
			                               message));
			
			return handle;
		}
		
		// schedule detector task on the host
		if (scheduleResult) {
			hostsPending.put(key, new PendingHostInfo(canonicalName, key, new Date(), false, handle));
			String message = "Detector has been successfully scheduled. Waiting for data.";
			operationCache.put(handle, buildPendingStatus(message, canonicalName));
			
			return handle;
		} else {
			String message = "Unable to schedule detector on the host.";
            operationCache.put(handle, buildFailedStatus(message, canonicalName));
            
            logger.logError("Unable to schedule detector on \"" + canonicalName + "\".");
            
            postEvent(new HostManagerEvent(HostManagerEvent.EventType.HOST_ADD,
                                           HostManagerEvent.Status.FAILED,
                                           canonicalName,
                                           message));
            
            return handle;
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.database.DatabaseManagerInterface#uploadHostData(String, String, String, String)
	 */
	public void uploadHostData(String hostName, String hostKey, String data, String outEncoding) 
		throws RemoteException, HostManagerException { 
		
		synchronized (hostsPending) {
			
			if (!hostsPending.containsKey(hostKey)) {
				String message = "Attempted to upload data for non-scheduled host \""
					+ hostName + "\".";
	
				logger.logWarning(message);
	
				throw new HostManagerException("Unable to upload data for non-scheduled host."
						+ " Host name :\"" + hostName + "\".");
			}
			
			PendingHostInfo pendingData = hostsPending.get(hostKey); 
			
			hostsPending.remove(hostKey);
			
			if (!pendingData.isRefresh()) {
				// we are adding new host
				try {
					database.addHost(hostName, data, outEncoding);
				} catch (Exception e) {
					logger.logError("Error adding host \"" + hostName + "\" to the database.");
					String message = "Host was not added, reason: \"" + e.getMessage() + "\".";
					
					operationCache.put(pendingData.getHandle(), buildFailedStatus(message, hostName));
					
					postEvent(new HostManagerEvent(HostManagerEvent.EventType.HOST_ADD,
					                               HostManagerEvent.Status.FAILED,
					                               hostName,
					                               message));
					
					throw new HostManagerException("Error adding host \"" + hostName
							+ "\" to the database.", e);
				}
				
				logger.logInfo("Host \"" + hostName + "\" successfully added to the database.");
				
				String message = "Host was successfully added to the database.";
				
				operationCache.put(pendingData.getHandle(), buildSuccessfulStatus(message, hostName));
				
				postEvent(new HostManagerEvent(HostManagerEvent.EventType.HOST_ADD,
				                               HostManagerEvent.Status.SUCCEEDED,
				                               hostName,
				                               message));
			} else {
				PropertyTreeInterface userProperties = 
					database.findHost(hostName).getUserPropertiesObject();
				
				// this is refresh
				try {
					database.refreshHost(hostName, data, outEncoding);
				} catch (Exception e) {
					logger.logError("Error adding data for host \"" + hostName
							+ "\" to the database.", e);
					
					String message = "Refresh failed, reason \"" + e.getMessage() + "\".";
					
					operationCache.put(pendingData.getHandle(), buildFailedStatus(message, hostName));
					
					postEvent(new HostManagerEvent(HostManagerEvent.EventType.HOST_REFRESH,
					                               HostManagerEvent.Status.FAILED,
					                               hostName,
					                               message));
					
					throw new HostManagerException("Error adding data for host \""
							+ hostName + "\" to the database.", e);
				}
				
				logger.logInfo("Refresh of the host \"" + hostName + "\" successful.");
				
				String message = "Refresh successful.";
				
				operationCache.put(pendingData.getHandle(), buildSuccessfulStatus(message, hostName));
	
				postEvent(new HostManagerEvent(HostManagerEvent.EventType.HOST_REFRESH,
				                               HostManagerEvent.Status.SUCCEEDED,
				                               hostName,
				                               message));
				
				try {
					database.updateUserProperties(hostName, userProperties);
				} catch (InvalidArgumentException e) {
					assert false : "What? Host has disappeared on us boys...";
				} catch (HostDatabaseException e) {
					throw new HostDatabaseException("Error updating user properties.", e);
				}
			}
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#removeHost(String)
	 */
	public void removeHost(String hostName) 
		throws RemoteException, ValueNotFoundException, HostDatabaseException {
		
		logger.logDebug("Removing host \"" + hostName + "\" from the database.");
		
		database.removeHost(hostName);
		
		logger.logInfo("Host \"" + hostName + "\" has been successfully removed from the database.");
		
		postEvent(new HostManagerEvent(HostManagerEvent.EventType.HOST_REMOVE,
		                               HostManagerEvent.Status.SUCCEEDED,
		                               hostName));
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#refreshHost(String)
	 */
	public OperationHandle refreshHost(String hostName) 
		throws RemoteException, UnknownHostException, ValueNotFoundException {
		
		logger.logDebug("Refreshing host \"" + hostName + "\".");
		
		String canonicalName = hostName;
		
		try {
			canonicalName = MiscUtils.getCanonicalHostName(hostName);
		} catch (UnknownHostException e) {
			if (!database.isHost(hostName)) {
				logger.logError("RefreshHost: host \"" + hostName + "\" is not in database.");
				
				throw new ValueNotFoundException("Host \"" + hostName + "\" is not in database.");
			} else {
				
				logger.logError("RefreshHost: unable to resolve host \"" + hostName + "\".");
				
				throw e;
			}
		}
		
		OperationHandle handle = getNextOperationHandle();
		
		operationCache.put(handle, buildUnknownStatus(canonicalName));
		
		String key = handle.toString();
		
		// test if host is in database, if not, it can't be refreshed
		if (!isHostInDatabase(canonicalName)) {
			String message = "Host is not in database and can't be refreshed.";
			operationCache.put(handle, buildFailedStatus(message, canonicalName));
			
			postEvent(new HostManagerEvent(HostManagerEvent.EventType.HOST_REFRESH,
			                               HostManagerEvent.Status.FAILED,
			                               canonicalName,
			                               message));
			
			return handle;
		} else {			
			boolean scheduleResult = false;
			
			try {
				// schedule detector task on host
				scheduleResult = runDetectorOnHost(canonicalName, key);
			} catch (Exception e) {
				String message = "Unable to schedule detector on host, reason: \""
					+ e.getMessage() + "\".";
				
				operationCache.put(handle, buildFailedStatus(message, canonicalName));
				
				logger.logError("Unable to schedule detector on \"" + canonicalName + "\".", e);
				
				postEvent(new HostManagerEvent(HostManagerEvent.EventType.HOST_REFRESH,
				                               HostManagerEvent.Status.FAILED,
				                               canonicalName,
				                               message));
				
				return handle;
			}
			
			
			if (scheduleResult) {
				String message = "Detector has been successfully scheduled on the host.";
				
				hostsPending.put(key, new PendingHostInfo(canonicalName, key, new Date(), true, handle));
				operationCache.put(handle, buildPendingStatus(message, canonicalName));
				
				return handle;
			} else {
				String message = "Unable to schedule detector on host.";
				
				logger.logError("Unable to schedule detector on \"" + canonicalName + "\".");
				
				operationCache.put(handle, buildFailedStatus(message, canonicalName));
				
				postEvent(new HostManagerEvent(HostManagerEvent.EventType.HOST_REFRESH,
				                               HostManagerEvent.Status.FAILED,
				                               canonicalName,
				                               message));
				
				return handle;
			}
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#refreshAll()
	 */
	public Map< String, OperationHandle > refreshAll() throws RemoteException {
		
		HashMap< String, OperationHandle > result = new HashMap< String, OperationHandle >();
		String []hostNames = database.getHostNames();
		
		for (String currentHost: hostNames) {
			OperationHandle handle = null;
			try {
				handle = refreshHost(currentHost);
			} catch (Exception e) {
				handle = getNextOperationHandle();
				String message = "Unable to refresh host, reason: \"" + e.getMessage() + "\".";
				operationCache.put(handle, buildFailedStatus(message, currentHost));
			}
			
			result.put(currentHost, handle);
		}
		
		return result;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#isHostInDatabase(String)
	 */
	public boolean isHostInDatabase(String hostName) throws RemoteException, UnknownHostException {
		
		String canonicalName = MiscUtils.getCanonicalHostName(hostName);
		return database.isHost(canonicalName);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getHostInfo(String)
	 */
	public HostInfoInterface getHostInfo(String hostName) throws RemoteException, ValueNotFoundException {
		
		return database.findHost(hostName);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getSimpleHostInfo(String)
	 */
	public SimpleHostInfo getSimpleHostInfo(String name) throws RemoteException, ValueNotFoundException {
		
		return new SimpleHostInfo(database.findHost(name));
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getHostCount()
	 */
	public int getHostCount() throws RemoteException {
		
		return database.getHostCount();
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getHostHistoryDates(String)
	 */
	public Date[] getHostHistoryDates(String hostName) throws RemoteException, ValueNotFoundException {
		
		Date []result = database.getHostHistoryDates(hostName).toArray(new Date[0]); 
		
		return result;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getHostHistoryEntry(String, Date)
	 */
	public HostInfoInterface getHostHistoryEntry(String hostName, Date date) 
		throws RemoteException, ValueNotFoundException, HostDatabaseException {
		
		return database.getHostHistoryEntry(hostName, date);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#removeHostHistoryEntry(String, Date)
	 */
	public void removeHostHistoryEntry(String hostName, Date date) 
		throws RemoteException, ValueNotFoundException, HostManagerException, HostDatabaseException {
		
		database.removeHostHistoryEntry(hostName, date);
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getGroupCount()
	 */
	public int getGroupCount() throws RemoteException {
		
		return database.getGroupCount();
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getGroup(String)
	 */
	public HostGroup getGroup(String name) throws RemoteException, ValueNotFoundException {
		
		return database.findGroup(name);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#addGroup(HostGroup)
	 */
	public void addGroup(HostGroup group) 
		throws RemoteException, InvalidArgumentException, HostDatabaseException {
		
		try {
			database.addGroup(group);
		} catch (InvalidArgumentException e) {
			postEvent(new HostManagerEvent(HostManagerEvent.EventType.GROUP_CREATE,
			                               HostManagerEvent.Status.FAILED,
			                               group.getName(),
			                               e.getMessage()));
			
			throw e;
		} catch (HostDatabaseException e) {
			postEvent(new HostManagerEvent(HostManagerEvent.EventType.GROUP_CREATE,
			                               HostManagerEvent.Status.FAILED,
			                               group.getName(),
			                               e.getMessage()));
			
			throw e;
		}
		
		postEvent(new HostManagerEvent(HostManagerEvent.EventType.GROUP_CREATE,
		                               HostManagerEvent.Status.SUCCEEDED,
		                               group.getName()));
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#isGroup(String)
	 */
	public boolean isGroup(String name) throws RemoteException {
		
		return database.isGroup(name);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#createGroup(RestrictionInterface[], String)
	 */
	public HostGroup createGroup(RestrictionInterface []conditions, String groupName)
		throws RemoteException, ValueNotFoundException, ValueTypeIncorrectException, HostManagerException {
		
		HostGroup result = new HostGroup(groupName);
		
		result.addHosts(database.queryHosts(conditions));
		
		return result; 
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#createGroup(HostQueryCallbackInterface, String)
	 */
	public HostGroup createGroup(HostQueryCallbackInterface query, String name) 
		throws RemoteException, Exception {
		
		HostGroup result = new HostGroup(name);
		
		result.addHosts(database.queryHosts(query));
		
		return result; 
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#removeGroup(String)
	 */
	public HostGroup removeGroup(String name) 
		throws RemoteException, ValueNotFoundException, HostDatabaseException {
		HostGroup result;
		
		try {
			result = database.removeGroup(name);
		} catch (ValueNotFoundException e) {
			postEvent(new HostManagerEvent(HostManagerEvent.EventType.GROUP_REMOVE,
			                               HostManagerEvent.Status.FAILED,
			                               name,
			                               e.getMessage()));
			
			throw e;
		} catch (HostDatabaseException e) {
			postEvent(new HostManagerEvent(HostManagerEvent.EventType.GROUP_REMOVE,
			                               HostManagerEvent.Status.FAILED,
			                               name,
			                               e.getMessage()));
			
			throw e;
		}
		
		postEvent(new HostManagerEvent(HostManagerEvent.EventType.GROUP_REMOVE,
		                               HostManagerEvent.Status.SUCCEEDED,
		                               name));
		return result;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#updateGroup(HostGroup)
	 */
	public void updateGroup(HostGroup group) 
		throws RemoteException, HostManagerException, ValueNotFoundException, InvalidArgumentException {
	
		try {
			database.updateGroup(group);
		} catch (InvalidArgumentException e) {
			postEvent(new HostManagerEvent(HostManagerEvent.EventType.GROUP_CHANGE,
			                               HostManagerEvent.Status.FAILED,
			                               group.getName(),
			                               e.getMessage()));
			
			throw e;
		} catch (ValueNotFoundException e) {
			postEvent(new HostManagerEvent(HostManagerEvent.EventType.GROUP_CHANGE,
			                               HostManagerEvent.Status.FAILED,
			                               group.getName(),
			                               e.getMessage()));
			
			throw e;
		} catch (HostManagerException e) {
			postEvent(new HostManagerEvent(HostManagerEvent.EventType.GROUP_CHANGE,
			                               HostManagerEvent.Status.FAILED,
			                               group.getName(),
			                               e.getMessage()));
			
			throw e;
		}
		
		postEvent(new HostManagerEvent(HostManagerEvent.EventType.GROUP_CHANGE,
		                               HostManagerEvent.Status.SUCCEEDED,
		                               group.getName()));
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#renameGroup(String, String)
	 */
	public void renameGroup(String oldName, String newName) 
		throws RemoteException, ValueNotFoundException, InvalidArgumentException, HostDatabaseException {

		try {
			database.renameGroup(oldName, newName);
		} catch (InvalidArgumentException e) {
			postEvent(new HostManagerEvent(HostManagerEvent.EventType.GROUP_CHANGE,
			                               HostManagerEvent.Status.FAILED,
			                               oldName,
			                               e.getMessage()));
			
			throw e;
		} catch (ValueNotFoundException e) {
			postEvent(new HostManagerEvent(HostManagerEvent.EventType.GROUP_CHANGE,
			                               HostManagerEvent.Status.FAILED,
			                               oldName,
			                               e.getMessage()));
			
			throw e;
		} catch (HostDatabaseException e) {
			postEvent(new HostManagerEvent(HostManagerEvent.EventType.GROUP_CHANGE,
			                               HostManagerEvent.Status.FAILED,
			                               oldName,
			                               e.getMessage()));
			
			throw e;
		}
		
		postEvent(new HostManagerEvent(HostManagerEvent.EventType.GROUP_CHANGE,
		                               HostManagerEvent.Status.SUCCEEDED,
		                               "\"" + oldName + "\" renamed to \"" + newName + "\"."));
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#queryHosts(HostQueryCallbackInterface)
	 */
	public HostInfoInterface []queryHosts(HostQueryCallbackInterface hq) throws RemoteException, Exception {
		
		ArrayList< String > matchingHosts = database.queryHosts(hq);
		
		HostInfoInterface []result = new HostInfoInterface[matchingHosts.size()];
		
		int i = 0;
		for (String hName: matchingHosts) {
			
			result[i] = database.findHost(hName);
		}
		
		return result;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#queryHosts(RestrictionInterface[])
	 */
	public HostInfoInterface []queryHosts(RestrictionInterface []restrictions) 
		throws RemoteException, ValueNotFoundException, ValueTypeIncorrectException, HostManagerException {
		
		ArrayList< String > matchingHosts = database.queryHosts(restrictions);
		
		HostInfoInterface []result = new HostInfoInterface[matchingHosts.size()];
		
		for (int i = 0; i < matchingHosts.size(); i++) {
			result[i] = database.findHost(matchingHosts.get(i));
		}
		
		return result;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getAllPropertyDescriptions()
	 */
	public List< PropertyDescription > getPropertyDescriptionsList() throws RemoteException {
		
		return descriptions.getAllDescriptions();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getPropertyDescription(java.lang.String)
	 */
	public PropertyDescription getPropertyDescription(String path) 
		throws ValueNotFoundException, InvalidArgumentException, RemoteException {
		
		return descriptions.getDescription(path);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getPropertyDescriptionTable()
	 */
	public PropertyDescriptionTable getPropertyDescriptionTable() throws RemoteException {
		
		return descriptions;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#updateUserProperties(cz.cuni.mff.been.hostmanager.database.HostInfoInterface)
	 */
	public void updateUserProperties(HostInfoInterface host) 
		throws InvalidArgumentException, HostDatabaseException, RemoteException {
		
		String name = host.getHostName();

		database.updateUserProperties(name, host.getUserPropertiesObject());
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getHostNames()
	 */
	public String[] getHostNames() throws RemoteException {

		return database.getHostNames();
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getGroupNames()
	 */
	public String[] getGroupNames() throws RemoteException {
		
		return database.getGroupNames();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getOperationStatus(cz.cuni.mff.been.hostmanager.OperationHandle)
	 */
	public HostOperationStatus getOperationStatus(OperationHandle handle) 
		throws RemoteException, IllegalArgumentException {
		
		HostOperationStatus status = operationCache.get(handle);
		
		if (status == null) {
			throw new IllegalArgumentException("Requested operation handle was not found in cache.");
		} else {
			return status;
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#removeOperationStatus(cz.cuni.mff.been.hostmanager.OperationHandle)
	 */
	public HostOperationStatus removeOperationStatus(OperationHandle handle)
		throws RemoteException, IllegalArgumentException {
		
		HostOperationStatus status = operationCache.remove(handle);
		
		if (status == null) {
			throw new IllegalArgumentException("Requested operation handle was not found in cache.");
		} else {
			return status;
		}		
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#addAliasDefinition(cz.cuni.mff.been.hostmanager.database.SoftwareAliasDefinition)
	 */
	public int addAliasDefinition(SoftwareAliasDefinition alias) 
		throws RemoteException, HostDatabaseException {
		
		return database.addAliasDefinition(alias);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#addAliasDefinitionList(java.lang.Iterable)
	 */
	public void addAliasDefinitionList(Iterable< SoftwareAliasDefinition > aliases) 
		throws RemoteException, HostDatabaseException {
		
		database.addAliasDefinitionList(aliases);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getAliasDefinition(int)
	 */
	public SoftwareAliasDefinition getAliasDefinition(int i) 
		throws RemoteException, ValueNotFoundException {
		
		return database.getAliasDefinitition(i);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getAliasDefinitionCount()
	 */
	public int getAliasDefinitionCount() throws RemoteException {
		
		return database.getAliasDefinitionCount();
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#rebuildAliasTableForAllHosts()
	 */
	public void rebuildAliasTableForAllHosts() throws RemoteException, HostDatabaseException {
		
		try {
			database.rebuildAliasTableForAllHosts();
		} catch (HostDatabaseException e) {
			postEvent(new HostManagerEvent(HostManagerEvent.EventType.DATABASE_REBUILDING,
			                               HostManagerEvent.Status.FAILED,
			                               "database",
			                               e.getMessage()));
			
			throw e;
		}
		
		postEvent(new HostManagerEvent(HostManagerEvent.EventType.DATABASE_REBUILDING,
		                               HostManagerEvent.Status.SUCCEEDED,
		                               "database"));
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#removeAliasDefinition(int)
	 */
	public void removeAliasDefinition(int i) 
		throws RemoteException, ValueNotFoundException, HostDatabaseException {
		
		database.removeAliasDefinition(i);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#removeAllAliasDefinitions()
	 */
	public void removeAllAliasDefinitions() throws RemoteException, HostDatabaseException {
		
		database.removeAllAliasDefinitions();
	}

	/**
	 * @return Database engine.
	 */
	public HostDatabaseEngine getDatabaseEngine() {
		
		return database;
	}

	/* 
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getConfiguration()
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
			
			case HOST_DETECTION_TIMEOUT:
				logger.logInfo("Value of HOST_DETECTION_TIMEOUT changed to " + newValue);
				hostDetectionTimeout = newValue;
				break;
				
			case PENDING_HOSTS_REFRESH_INTERVAL:
				logger.logInfo("Value of PENDING_HOST_REFRESH_INTERVAL changed to " + newValue);
				pendingHostsCheckPeriod = newValue;
				restartWatchDog();
				break;
				
			default:
				// Ignore other options (they are not for HM).
		}
	}

	/**
	 * Start watch dog thread with current pendingHostCheckPeriod.
	 */
	private void restartWatchDog() throws HostManagerException {

		if (timer != null) {
			timer.cancel();
		}
		
		try {
			timer = new Timer(true);
			timer.schedule(new WatchDog(), 0, pendingHostsCheckPeriod);
		} catch (Exception e) {
			throw new HostManagerException("Unable to start new Watch Dog thread.", e);
		}
	}
	
	/**
	 * This method is called when new Host Runtime connects to the Task Manager.
	 * Host will be automatically added to the database if necessary and its hardware and software 
	 * configuration will be scanned. All global settings will be also communicated 
	 * to the Load Monitor running on the host.
	 * <br>
	 * Note that this method is asynchronous. That is, it will return immediately and only way to
	 * test its progress is using {@link #getOperationStatus(OperationHandle)} method with
	 * the {@link OperationHandle} returned.
	 * <br>
	 * Operation that is performed consists of multiple steps:
	 * <ol>
	 *  <li>Scan the host using appropriate detector.</li>
	 *  <li>Add host to the database.</li>
	 *  <li>Connect to the host and upload settings to the Load Monitor.</li>
	 *  <li>Initialise Load Monitor on the host.</li>
	 * </ol>
	 * Should any of the steps above fail, whole operation will be marked as failed.
	 * 
	 * @param hostName Name of the new host. This does not need to be fully qualified host name.
	 * @return Handle of the operation that is to be performed. This handle can be used to check
	 *         status of the operation via {link {@link #getOperationStatus(OperationHandle)}
	 *         method.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws UnknownHostException If host name cannot be resolved.
	 */
	public OperationHandle newHostConnected(String hostName)
		throws RemoteException, UnknownHostException {

		String canonicalName = MiscUtils.getCanonicalHostName(hostName);

		logger.logInfo("New host connected: \"" + canonicalName + "\".");
		
		OperationHandle newHostOpHandle = getNextOperationHandle();
		OperationHandle addHandle = null;
		
		operationCache.put(newHostOpHandle, buildPendingStatus("Connecting to the host.", canonicalName));
		
		synchronized (database) {
			if (database.isHost(canonicalName)) {
				// Host is already in db -> we need to refresh its config
				logger.logDebug("Host \"" + canonicalName + "\" is already in database"
						+ " - refreshing configuration.");

				try {
					addHandle = refreshHost(canonicalName);
				} catch (ValueNotFoundException e) {
					logger.logError("Host \"" + canonicalName + "\" has been removed from database.");
					
					operationCache.put(newHostOpHandle, 
							buildFailedStatus("Host has been removed from database.", canonicalName));
					
					operationCache.remove(addHandle);
					
					return newHostOpHandle;
				}
			} else {
				// Host is not yet in db -> scan
				logger.logDebug("Host \"" + canonicalName + "\" is not in database - scanning.");
				
				addHandle = addHost(canonicalName);
			}
			
			synchronized (operationCache) {
				
				HostOperationStatus hose = operationCache.get(addHandle);
				
				if (hose.getStatus().equals(HostOperationStatus.Status.FAILED)) {
					operationCache.put(newHostOpHandle, buildFailedStatus(hose.getMessage(), canonicalName));
					operationCache.remove(addHandle);
					
					return newHostOpHandle;
				}
				
				try {
					registerEventListener(new NewHostEventListener(canonicalName, addHandle, newHostOpHandle));
				} catch (RemoteException e) {
					logger.logError("Unable to register listener.", e);
					
					operationCache.remove(addHandle);
					operationCache.put(newHostOpHandle, 
							buildFailedStatus("Unable to register listener.", canonicalName));
					
					return newHostOpHandle;
				}
				
				operationCache.put(newHostOpHandle, buildPendingStatus("Attempting to schedule detector"
						+ " on the host.", canonicalName));
			}
		}
		
		return newHostOpHandle;
	}
	
	/* 
	 * @see cz.cuni.mff.been.taskmanager.HostRuntimeRegistrationListener#hostRuntimeRegistered(java.lang.String)
	 */
	public void hostRuntimeRegistered(String hostname) throws RemoteException {
		
		try {
			newHostConnected(hostname);
		} catch (UnknownHostException e) {
			// This should not happen, since host connected to the TM, but bad things (tm) happen...
			logger.logError("Unable to connected to the \"" + hostname + "\".");
		}
	}

	/* 
	 * @see cz.cuni.mff.been.taskmanager.HostRuntimeRegistrationListener#hostRuntimeUnregistered(java.lang.String)
	 */
	public void hostRuntimeUnregistered(String hostname) throws RemoteException {
		
		appData.getLSI().hostDisconnected(hostname);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#registerEventListener(cz.cuni.mff.been.hostmanager.HostManagerEventListener)
	 */
	public OID registerEventListener(HostManagerEventListener listener)
		throws InvalidArgumentException, RemoteException {
		
		MiscUtils.verifyParameterIsNotNull(listener, "listener");

		OID id = appData.getNextID(OID.class);
		
		listeners.put(id, listener);
		
		return id;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#unregisterEventListener(cz.cuni.mff.been.common.id.OID)
	 */
	public void unregisterEventListener(OID listenerId)
		throws RemoteException, InvalidArgumentException, ValueNotFoundException {

		MiscUtils.verifyParameterIsNotNull(listenerId, "listenerId");
		
		synchronized (listeners) {
			
			if (!listeners.contains(listenerId)) {
				throw new ValueNotFoundException("Unable to find listener for id \""
						+ listenerId + "\".");
			}
			
			listeners.remove(listenerId);
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.HostManagerInterface#getLoadServer()
	 */
	public LoadServerInterface getLoadServer() throws RemoteException {

		return appData.getLSI();
	}

	/**
	 * Ask Task Manager names of all Host Runtimes and add/refresh configuration of each. This
	 * will then initialise Load Monitor on each registered HR.
	 */
	public void initializeHostRuntimes() {
		
		logger.logInfo("Initializing Host Runtimes registered on the Task Manager.");
		
		String []knownHosts = null;
		try {
			knownHosts = task.getTasksPort().getRegisteredHostRuntimes();
		} catch (RemoteException e) {
			logger.logError("Unable to retrieve known hosts from the Task Manager.", e);
			return;
		}
		
		for (String host: knownHosts) {
			logger.logDebug("Registering host \"" + host + "\".");
			try {
				hostRuntimeRegistered(host);
			} catch (RemoteException e) {
				logger.logError("Error registering \"" + host + "\".");
			}
		}
	}
	
	/**
	 * Process given event and notify all listeners that are currently registered. This method
	 * works asynchronously.
	 * 
	 * @param event Event to process.
	 */
	private void postEvent(final HostManagerEvent event) {
		
		Thread thread = new Thread() {
		
			/*
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {

				/*
				 * Visit all listeners and process current event.
				 */
				synchronized (listeners) {
					
					for (Map.Entry< OID, HostManagerEventListener > listenerEntry: listeners.entrySet()) {
						
						listenerEntry.getValue().processEvent(event);
						
						if (listenerEntry.getValue().remove()) {
							listeners.remove(listenerEntry);
						}
					}
				}
			}
		};
		
		thread.start();
	}
	
	/**
	 * Run detector on selected host. This method is "low-level", that is, it will only submit task
	 * descriptor to the Host Runtime.
	 * 
	 * @param canonicalName Full name of the host.
	 * @param key Key assigned to this operation by the Host Manager.
	 * 
	 * @return <code>true</code> on success, <code>false</code> otherwise.
	 * 
	 * @throws HostManagerException If an error occurred while submitting task to the Task Manager.
	 * @throws UnknownHostException If host was not found.
	 */
	@SuppressWarnings("deprecation")
    private synchronized boolean runDetectorOnHost(String canonicalName, String key) 
		throws UnknownHostException, HostManagerException {
		
		++taskIDNum;
		TaskDescriptor taskDesc = TaskDescriptorHelper.createDetector(
			DETECTOR_PREFIX + taskIDNum + "-" + System.currentTimeMillis(),
			canonicalName
		);
		TaskDescriptorHelper.addTaskProperties(taskDesc, Pair.pair("key", key));
		TaskDescriptorHelper.addDependencyCheckpoint(
			taskDesc,
			"hostmanager-tid",																		// TODO: MAGIC CONSTANT!!!
			Service.STATUS_CHECKPOINT,
			Service.STATUS_RUNNING
		);

		try {
			task.getTasksPort().runTask(taskDesc);
		} catch (RemoteException e) {
			throw new HostManagerException("Unable to start detector on \"" + canonicalName + "\".", e);
		}

		return true;
	}
	
	/**
	 * This listener is used a small workaround when new host is connected to the network. It is 
	 * needed because HostManager does not support "chaining" of the {@link HostOperationStatus}
	 * messages.  
	 *
	 * @author Branislav Repcek
	 */
	private class NewHostEventListener extends HostManagerEventListener {

		private static final long	serialVersionUID	= -8529653173475899021L;

		/**
		 * Name of the host we are waiting for.
		 */
		private String hostName;
		
		/**
		 * Handle of the operation we are waiting for. 
		 */
		private OperationHandle addHandle;
		
		/**
		 * Handle of the "new host connected" operation.
		 */
		private OperationHandle newHostOp;
		
		/**
		 * Create new event listener.
		 * 
		 * @param hostName Name of the host to wait for.
		 * @param addHandle Handle of the operation to wait for.
		 * @param newHostOp Handle of the whole "new host connected" operation.
		 */
		public NewHostEventListener(String hostName, OperationHandle addHandle,
				OperationHandle newHostOp) {
			
			this.hostName = hostName;
			this.addHandle = addHandle;
			this.newHostOp = newHostOp;
		}
		
		/* 
		 * @see cz.cuni.mff.been.hostmanager.HostManagerEventListener#processEvent(cz.cuni.mff.been.hostmanager.HostManagerEvent)
		 */
		@Override
		public void processEvent(HostManagerEvent event) {
			
			if (event.getObjectName().equals(hostName)) {
				synchronized (operationCache) {
					HostOperationStatus status = operationCache.get(addHandle);
					
					if (status == null) {
						removeMe();
					} else {
						if (status.getStatus().equals(HostOperationStatus.Status.SUCCESS)) {
							removeMe();
							operationCache.remove(addHandle);
							newHostSuccess(hostName, newHostOp);
						} else if (status.getStatus().equals(HostOperationStatus.Status.FAILED)) {
							removeMe();
							newHostFailure(hostName, addHandle, newHostOp);
						}
					}
				}
			}
		}
	}
	
	/**
	 * This method is called when data is received for the host that connected to the network.
	 * 
	 * @param hostName Name of the host.
	 * @param newHostOpHandle Handle of the whole "new host connected" operation.
	 */
	private void newHostSuccess(String hostName, OperationHandle newHostOpHandle) {
		
		try {
			appData.getLSI().newHostConnected(hostName);
		} catch (Exception e) {
			logger.logError("Unable to initialize Load Monitor on \"" + hostName + "\".", e);
			operationCache.put(newHostOpHandle, buildFailedStatus("Unable to initialize Load Monitor.",
					hostName));
			
			return;
		}
		
		operationCache.put(newHostOpHandle, buildSuccessfulStatus("Host connected successfully.",
				hostName));
	}
	
	/**
	 * This method is called when detector failed on the new host.
	 * 
	 * @param hostName Name of the host.
	 * @param addHandle Handle of the add or refresh operation on the host.
	 * @param newHostOpHandle Handle of the whole "new host connected" operation.
	 */
	private void newHostFailure(String hostName, OperationHandle addHandle,
			OperationHandle newHostOpHandle) {
		
		HostOperationStatus status = operationCache.get(addHandle);
		
		operationCache.put(newHostOpHandle, buildFailedStatus("Error acquiring data: "
				+ status.getMessage(), hostName));
		operationCache.remove(addHandle);
	}
	
	/**
	 * Information about host that has some add/refresh operation pending.
	 *
	 * @author Branislav Repcek
	 */
	private class PendingHostInfo implements Serializable {
		
		private static final long	serialVersionUID	= -6669701997844439158L;

		/**
		 * Date operation has started.
		 */
		private Date date;
		
		/**
		 * Is the operation refresh?
		 */
		private boolean refresh;
		
		/**
		 * Handle to the operation.
		 */
		private OperationHandle handle;
		
		/**
		 * Name of the host.
		 */
		private String hostName;
		
		/**
		 * Create new PendingHostInfo.
		 * 
		 * @param hostName Name of the host.
		 * @param hostKey Host's key.
		 * @param newDate Date operation has started.
		 * @param newRefresh Is the operation refresh?
		 * @param newHandle Handle to the operation returned by method performing given operation.
		 */
		public PendingHostInfo(String hostName, String hostKey, Date newDate, boolean newRefresh, OperationHandle newHandle) {
			
			date = newDate;
			refresh = newRefresh;
			handle = newHandle;
			this.hostName = hostName;
		}

		/**
		 * Get date of the operation.
		 * 
		 * @return Get date operation has started.
		 */
		public Date getDate() {
			return date;
		}

		/**
		 * Get handle to the operation status.
		 * 
		 * @return Handle to the status of the operation.
		 */
		public OperationHandle getHandle() {
			return handle;
		}

		/**
		 * See if operation is refresh of add.
		 * 
		 * @return <code>true</code> if operation is refresh, <code>false</code> if it is add.
		 */
		public boolean isRefresh() {
			return refresh;
		}

		/**
		 * @return Name of the host.
		 */
		public String getHostName() {
			
			return hostName;
		}
	}
	
	/**
	 * Get handle to the next add/remove operation.
	 * 
	 * @return Next operation handle.
	 */
	private synchronized OperationHandle getNextOperationHandle() {
		
		currentHandle = currentHandle.getNext();
		return currentHandle;
	}
	
	/**
	 * Build status info for failed operation.
	 * 
	 * @param message Message.
	 * @param host Host on which operation has been performed.
	 * 
	 * @return HostOperationStatus object with specified status message.
	 */
	private HostOperationStatus buildFailedStatus(String message, String host) {
		
		return new HostOperationStatus(HostOperationStatus.Status.FAILED, message, host);
	}
	
	/**
	 * Build status info for operation with unknown status.
	 * 
	 * @param host Host in which operation has been performed.
	 * 
	 * @return HostOperationStatus object with specified status message.
	 */
	private HostOperationStatus buildUnknownStatus(String host) {
		
		return new HostOperationStatus(HostOperationStatus.Status.UNKNOWN, "", host);
	}
	
	/**
	 * Build status info for successfully completed operation.
	 * 
	 * @param message Message.
	 * @param host Host on which operation has been performed.
	 * 
	 * @return HostOperationStatus object with specified status message.
	 */
	private HostOperationStatus buildSuccessfulStatus(String message, String host) {
		
		return new HostOperationStatus(HostOperationStatus.Status.SUCCESS, message, host);
	}

	/**
	 * Build status info for pending operation.
	 * 
	 * @param message Message.
	 * @param host Host on which operation has been performed.
	 * 
	 * @return HostOperationStatus object with specified status message.
	 */
	private HostOperationStatus buildPendingStatus(String message, String host) {
		
		return new HostOperationStatus(HostOperationStatus.Status.PENDING, message, host);
	}

	/**
	 * This class is used as a thread watching hosts on which detector has been scheduled.
	 * Detector is required to finish in some fixed amount of time. If detector does not upload
	 * data in time it is considered failure and host is removed from list of pending hosts.
	 * Even if the detector sends data later, it will not be accepted.
	 *
	 * @author Branislav Repcek
	 */
	private class WatchDog extends TimerTask {

		/*
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			
			long currentDateTime = new Date().getTime();
			
			synchronized (hostsPending) {
				// check every item in list of pending hosts
				for (Iterator< Map.Entry< String, PendingHostInfo > > it = 
						hostsPending.entrySet().iterator();
				     it.hasNext(); ) {
					
					Map.Entry< String, PendingHostInfo > currentEntry = it.next();
					
					long hostsDateTime = currentEntry.getValue().getDate().getTime();
					
					// test if host is too old
					if (currentDateTime - hostsDateTime > hostDetectionTimeout) {
						String hostName = currentEntry.getValue().getHostName();
						
						task.logError("No data received for host \"" + hostName + "\" (timed out).");
	
						// add status message
						String message = "No data received for host (timed out).";
						
						operationCache.put(currentEntry.getValue().getHandle(), 
								buildFailedStatus(message, hostName));
						
						HostManagerEvent.EventType et = HostManagerEvent.EventType.HOST_REFRESH;
						
						if (!currentEntry.getValue().isRefresh()) {
							et = HostManagerEvent.EventType.HOST_ADD;
						}
						
						postEvent(new HostManagerEvent(et,
						                               HostManagerEvent.Status.FAILED,
						                               hostName,
						                               message));
						
						it.remove();
					}
				}
			}
		}
	}

	@Override
	public SoftwareAliasDefinition getAliasDefinitionByName( String name )
	throws ValueNotFoundException {
		return database.getAliasDefinitionByName( name );
	}

	@Override
	public SoftwareAliasDefinition[] getAliasDefinitions() {
		return database.getAliasDefinitions();
	}

	@Override
	public SoftwareAliasDefinition removeAliasDefinitionByName( String name )
	throws ValueNotFoundException, HostDatabaseException {
		return database.removeAliasDefinitionByName( name );
	}

	@Override
	public HostGroup[] getGroups() throws RemoteException {
		return database.getGroups();
	}

	@Override
	public HostInfoInterface[] getHostInfos() throws RemoteException {
		return database.getHostInfos();
	}
}
