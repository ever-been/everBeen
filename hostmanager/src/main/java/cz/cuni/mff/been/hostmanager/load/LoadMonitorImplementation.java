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

import java.io.File;
import java.io.IOException;

import java.net.UnknownHostException;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.common.util.MiscUtils;
import cz.cuni.mff.been.common.util.TimeUtils;
import cz.cuni.mff.been.common.util.XMLHelper;

import cz.cuni.mff.been.hostmanager.HostManagerException;
import cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface;
import cz.cuni.mff.been.hostmanager.IllegalOperationException;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.OutputWriteException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;

import cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface;


/**
 * Implementation of the Load Monitor interface.
 *
 * @author Branislav Repcek
 */
public class LoadMonitorImplementation extends UnicastRemoteObject implements LoadMonitorInterface {

	private static final long	serialVersionUID	= 4836469016925937068L;

	/**
	 * Extension of the files with the sample data.
	 */
	private static final String SAMPLE_FILE_EXTENSION = ".bsample";

	/**
	 * Name of the load database file.
	 */
	private static final String LOAD_DATABASE_INDEX_FILE = "load-database.index";
	
	/**
	 * Name of the root node of the index file.
	 */
	private static final String INDEX_FILE_NODE_NAME = "loadIndex";
	
	/**
	 * Working directory.
	 */
	private String workingDirectory;
	
	/**
	 * Path to the library directory.
	 */
	private String libraryDirectory;
	
	/**
	 * Sampling interval for detailed mode.
	 */
	private long defaultDetailedInterval;
	
	/**
	 * Current sampling interval in detailed mode.
	 */
	private long currentDetailedInterval;
	
	/**
	 * Sampling interval for brief mode.
	 */
	private long briefInterval;
	
	/**
	 * Current mode.
	 */
	private LoadMonitorMode mode;
	
	/**
	 * Name of the host on which Load Server is running.
	 */
	private String loadServerHostname;
	
	/**
	 * Load server which will receive all events generated on this host.
	 */
	private LoadServerInterface loadServer;
	
	/**
	 * Adapter for the native library.
	 */
	private LoadMonitorNative monitor;
	
	/**
	 * Name of this computer.
	 */
	private String hostName;

	/**
	 * Timer which runs daemon thread.
	 */
	private Timer timer;
	
	/**
	 * Flag which shows if native library has been successfully loaded.
	 */
	private boolean nativeLibrary;
	
	/**
	 * Writer used to serialise detailed mode data to the disk.
	 */
	private LoadFileParser< LoadSample > detailedModeWriter;
	
	/**
	 * Map which stores all load files in data directory.
	 */
	private ConcurrentHashMap< Pair< String, String >, LoadDatabaseEntry > loadEntries;
	
	/**
	 * Event queue.
	 */
	private EventQueue eventQueue;
	
	/**
	 * Number of events with samples processed in the detailed mode since last event 
	 * report to the Load Server.
	 */
	private int eventCount;
	
	/**
	 * Number of events with samples that have to be processed in detailed mode before one event is 
	 * sent to the Load Server.
	 */
	private long briefModeMark;
	
	/**
	 * Flag which determines if brief mode will be started again when detailed mode is stopped.
	 */
	private boolean reenableBriefMode;
	
	/**
	 * Event dispatcher.
	 */
	private EventDispatcher eventDispatcher;
	
	/**
	 * Init OK?
	 */
	private boolean init;
	
	/**
	 * Create new instance of Load Monitor. Only one instance should run on each host.
	 * 
	 * @param libraryDir Directory in which libraries are located.
	 * @param workingDir Working directory for Load Monitor. All data will be saved there.
	 * 
	 * @throws RemoteException If RMI error occurred.
	 * @throws LoadMonitorException If other error occurred during initialisation.
	 */
	public LoadMonitorImplementation(String libraryDir, String workingDir)
		throws RemoteException, LoadMonitorException {
		
		super();

		defaultDetailedInterval = HostManagerOptionsInterface.Option.DEFAULT_DETAILED_MODE_INTERVAL.getDefaultValue();
		currentDetailedInterval = defaultDetailedInterval;
		briefInterval = HostManagerOptionsInterface.Option.BRIEF_MODE_INTERVAL.getDefaultValue();
		briefModeMark = briefInterval / defaultDetailedInterval;
		
		eventQueue = new EventQueue();
		
		try {
			hostName = MiscUtils.getCanonicalHostName("localhost");
		} catch (UnknownHostException e) {
			throw new LoadMonitorException("Unable to resolve localhost.", e);
		}

		logInfo("Creating Load Monitor. Computer name is \"" + hostName + "\".");
		
		workingDirectory = workingDir;
		libraryDirectory = libraryDir;
		
		logInfo("Load Monitor working directory: " + workingDirectory);
		
		File wdf = new File(workingDirectory);
		
		if (!wdf.exists()) {
			logInfo("Working directory dies not exist. Creating...");
			if (!wdf.mkdirs()) {
				logError("Unable to create working directory.");
				throw new LoadMonitorException("Unable to create working directory \""
						+ workingDirectory + "\".");
			}
		} else {
			if (!wdf.isDirectory()) {
				logError("Working directory path does not point to the directory.");
				throw new LoadMonitorException("Working directory path does not point to the directory.");
			}
		}
		
		loadEntries = new ConcurrentHashMap< Pair< String, String >, LoadDatabaseEntry >();
		
		try {
			loadDatabaseIndex();
		} catch (HostManagerException e) {
			throw new LoadMonitorException("Error loading index file.", e);
		}
		
		logInfo("Binding Load Monitor's RMI interface.");
		// Bind us to the RMI
		try {
			Naming.rebind(RMI.URL_PREFIX + "/" + LoadMonitorInterface.RMI_NAME, this);
		} catch (Exception e) {
			logError("Unable to register RMI interface.");
			
			throw new LoadMonitorException("Unable to register RMI interface.", e);
		}

		try {
			monitor = new LoadMonitorNative(!libraryDirectory.endsWith(File.separator)
					? libraryDirectory + File.separator : libraryDirectory);
			
			nativeLibrary = true;
			
		} catch (Exception e) {
			logError("Unable to initialize native library. Message: " + e.getMessage());
			nativeLibrary = false;
		}

		if (nativeLibrary) {
			nativeLibrary = monitor.initialize();
		}
		
		eventDispatcher = new EventDispatcher(eventQueue);
		
		logInfo("Load Monitor created successfully.");
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#getBriefInterval()
	 */
	public synchronized long getBriefInterval() throws RemoteException {
		
		return briefInterval;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#getDefaultDetailedInterval()
	 */
	public synchronized long getDefaultDetailedInterval() throws RemoteException {
		
		return defaultDetailedInterval;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#getMode()
	 */
	public LoadMonitorMode getMode() throws RemoteException {
		
		return mode;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#setBriefInterval(int)
	 */
	public synchronized void setBriefInterval(long interval) 
		throws RemoteException, InvalidArgumentException, LoadMonitorException {

		if (interval <= 0) {
			throw new InvalidArgumentException("Invalid brief mode interval.");
		}
		
		logInfo("New brief mode interval: " + interval);
		
		briefInterval = interval;
		briefModeMark = briefInterval / currentDetailedInterval;
		
		if (mode == LoadMonitorMode.MODE_BRIEF) {
			try {
				stopBriefMode();
				startBriefMode();
			} catch (IllegalOperationException e) {
				assert false : "Weeell, this is strange. This should never happen."; 
			}
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#setDetailedInterval(int)
	 */
	public synchronized void setDefaultDetailedInterval(long interval) 
		throws RemoteException, InvalidArgumentException, LoadMonitorException {

		if (interval <= 0) {
			throw new InvalidArgumentException("Invalid detailed mode interval.");
		}
		
		logInfo("New default detailed mode interval: " + interval);
		
		defaultDetailedInterval = interval;
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#startBriefMode()
	 */
	public void startBriefMode()
		throws RemoteException, LoadMonitorException, IllegalOperationException {

		if (mode != LoadMonitorMode.MODE_NONE) {
			throw new IllegalOperationException("Monitor is already in brief or detailed mode.");
		}
		
		timer = new Timer(true);
		try {
			timer.scheduleAtFixedRate(new MonitorThread(), 0, briefInterval);
		} catch (IllegalStateException e) {
			mode = LoadMonitorMode.MODE_NONE;
			throw new LoadMonitorException("Unable to schedule brief mode thread.", e);
		}
		
		mode = LoadMonitorMode.MODE_BRIEF;
		
		processEvent(new LoadMonitorEvent(LoadMonitorEvent.EventType.MONITOR_START, hostName));
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#stopBriefMode()
	 */
	public void stopBriefMode() throws RemoteException, IllegalOperationException {

		if (mode != LoadMonitorMode.MODE_BRIEF) {
			throw new IllegalOperationException("Monitor is not in brief mode.");
		}
		
		if (timer != null) {
			timer.cancel();
		}
		
		timer = null;
		
		mode = LoadMonitorMode.MODE_NONE;
		
		processEvent(new LoadMonitorEvent(LoadMonitorEvent.EventType.MONITOR_STOP, hostName));
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#setLoadServerHostname(java.lang.String)
	 */
	public void initialize(String lsHostName, LoadServerInterface loadServer, 
			long defaultDetailedModeInterval, long briefModeInterval) 
			throws RemoteException, InvalidArgumentException, LoadMonitorException {

		logInfo("Initializing Load Monitor.");
	
		this.loadServerHostname = lsHostName;
		this.loadServer = loadServer;
		
		logInfo("Registered with Load Server on \"" + loadServerHostname + "\".");
				
		eventDispatcher = new EventDispatcher(eventQueue, this.loadServer);

		mode = LoadMonitorMode.MODE_NONE;
		
		logInfo("Load Monitor initialized successfully.");
		
		processEvent(new LoadMonitorEvent(LoadMonitorEvent.EventType.MONITOR_START_UP, hostName));
		
		if (nativeLibrary) {
			HardwareDescription description = monitor.getHardwareDescription();
			processEvent(new LoadMonitorEvent(description, hostName));
		} else {
			processEvent(new LoadMonitorEvent(new HardwareDescription(), hostName));
		}

		init = true;
		
		this.defaultDetailedInterval = defaultDetailedModeInterval;
		this.currentDetailedInterval = this.defaultDetailedInterval;
		this.briefInterval = briefModeInterval;
		
		// Now start taking samples in brief mode
		try {
			startBriefMode();
		} catch (IllegalOperationException e) {
			throw new LoadMonitorException("Unable to start brief mode.");
		}
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#terminate()
	 */
	public synchronized void terminate() throws RemoteException {
		
		logInfo("Terminating Load Monitor.");

		if (!init) {
			return;
		}
		
		if (mode == LoadMonitorMode.MODE_BRIEF) {
			try {
				stopBriefMode();
			} catch (IllegalOperationException e) {
				// whis should never happen
				assert false : "Unable to stop brief mode - illegal op.";
			}
		} else if (mode == LoadMonitorMode.MODE_DETAILED) {
			// set this, so brief mode is not enabled again
			reenableBriefMode = false;
			
			try {
				stopDetailedMode();
			} catch (IllegalOperationException e) {
				// this should never happen
				assert false : "Unable to stop detailed mode - illegal op.";
			} catch (LoadMonitorException e) {
				// same goes for this
				assert false : "Unable to stop detailed mode - LME.";
			}
		}
		
		processEvent(new LoadMonitorEvent(LoadMonitorEvent.EventType.MONITOR_SHUT_DOWN, hostName));

		if (nativeLibrary) {
			monitor.terminate();
		}
		
		eventDispatcher.stop();
		
		init = false;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#clearDetailedModeData()
	 */
	public void clearDetailedModeData() throws RemoteException, IllegalOperationException {

		if (mode == LoadMonitorMode.MODE_DETAILED) {
			throw new IllegalOperationException("Load monitor is currently in detailed mode.");
		}
	
		synchronized (loadEntries) {
			if (!loadEntries.isEmpty()) {
				for (LoadDatabaseEntry entry: loadEntries.values()) {
					String fileName = getFullFileName(entry.getFileName());
					
					File file = new File(fileName);
					
					try {
						if (!file.delete()) {
							// Failed for some reason, delete when VM exits.
							file.deleteOnExit();
						}
					} catch (SecurityException e) {
						logError("Unable to delete data file \"" + fileName
								+ "\". Message: " + e.getMessage());
						// Do nothing, the file will just sit there...
					}
				}
				
				loadEntries.clear();
				
				try {
					saveDatabaseIndex();
				} catch (OutputWriteException e) {
					logError("Unable to save index after removing all db entries."
							+ " Message: " + e.getMessage());
				}
			}
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#clearDetailedModeData(java.lang.String)
	 */
	public void clearDetailedModeData(String contextId, String taskId)
		throws RemoteException, IllegalOperationException, InvalidArgumentException, ValueNotFoundException {

		if (mode == LoadMonitorMode.MODE_DETAILED) {
			throw new IllegalOperationException("Load monitor is currently in detailed mode.");
		}
		
		MiscUtils.verifyStringParameterBoth(contextId, "contextId");
		MiscUtils.verifyStringParameterBoth(taskId, "taskId");

		synchronized (loadEntries) {
		
			Pair< String, String > key = new Pair< String, String >(contextId, taskId);
			LoadDatabaseEntry entry = loadEntries.get(key);
			
			if (entry == null) {
				throw new ValueNotFoundException("Unable to find database entry for " + key);
			}
			
			String fileName = getFullFileName(entry.getFileName());
			
			File file = new File(fileName);
			
			try {
				if (!file.delete()) {
					// Failed for some reason, delete when VM exits.
					file.deleteOnExit();
				}
			} catch (SecurityException e) {
				logError("Unable to delete data file \"" + fileName + "\". Message: " + e.getMessage());
				// Do nothing, the file will just sit there...
			}
			
			loadEntries.remove(key);
			
			try {
				saveDatabaseIndex();
			} catch (OutputWriteException e) {
				logError("Unable to save index after removing entry for " + key + ". Message: " + e.getMessage());
			}
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#clearDetailedModeData(java.lang.String)
	 */
	public void clearDetailedModeData(String contextId)
		throws RemoteException, IllegalOperationException, ValueNotFoundException {
		
		if (mode == LoadMonitorMode.MODE_DETAILED) {
			throw new IllegalOperationException("Load monitor is currently in detailed mode.");
		}
		
		MiscUtils.verifyStringParameterBoth(contextId, "contextId");
		
		boolean r = false;
		
		synchronized (loadEntries) {
			
			for (Iterator< Pair< String, String > > it = loadEntries.keySet().iterator(); it.hasNext(); ) {
				Pair< String, String > current = it.next();
				
				if (current.getKey().equals(contextId)) {
					clearDetailedModeData(current.getKey(), current.getValue());
					r = true;
				}
			}
		}
		
		if (!r) {
			throw new ValueNotFoundException("No data for context \"" + contextId + "\" found.");
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#startDetailedMode
	 */
	public void startDetailedMode(String contextId, String taskId)
		throws RemoteException, IllegalOperationException, LoadMonitorException {

		startDetailedMode(contextId, taskId, defaultDetailedInterval);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#startDetailedMode(java.lang.String, java.lang.String, long)
	 */
	public void startDetailedMode(String contextId, String taskId, long interval)
		throws RemoteException, IllegalOperationException, LoadMonitorException, InvalidArgumentException {

		MiscUtils.verifyStringParameterBoth(contextId, "contextId");
		MiscUtils.verifyStringParameterBoth(taskId, "taskId");
		
		if (interval < 1) {
			throw new InvalidArgumentException("Detailed mode interval has to be greater than"
					+ " or equal to 1 ms."); 
		}
		
		logInfo("Starting detailed mode on request by [" + contextId + ":" + taskId + "]."
				+ " Interval is: " + interval);
		
		reenableBriefMode = false;
		
		if (mode == LoadMonitorMode.MODE_BRIEF) {
			stopBriefMode();
			reenableBriefMode = true;
		} else if (mode == LoadMonitorMode.MODE_DETAILED) {
			throw new IllegalOperationException("Monitor is already in detailed mode.");
		}
		
		// Generate new file name and entry for the database
		String fileName = null;
		
		// If such entry already exists, remove old file
		if (loadEntries.containsKey(new Pair< String, String >(contextId, taskId))) {
			fileName = loadEntries.get(new Pair< String, String >(contextId, taskId)).getFileName();
			
			// :(
			boolean del = false;
			
			try {
				File file = new File(getFullFileName(fileName));
				
				del = file.delete();
			} catch (Exception e) {
				del = false;
			}
			
			if (!del) {
				// Unable to delete old file -> generate new name
				fileName = generateFileName(contextId, taskId);
			}
		} else {
			fileName = generateFileName(contextId, taskId);
		}
		
		LoadDatabaseEntry loadDbEntry = new LoadDatabaseEntry(contextId, taskId, fileName);
		
		//Now create new data file
		LoadFileWritable loadFile = getLoadFile(loadDbEntry);
		
		try {
			detailedModeWriter = LoadFileWritable.createSampleWriter(loadFile);
		} catch (Exception e) {
			logError("Unable to create new data writer: " + e.getMessage());
			throw new LoadMonitorException("Unable to create new data writer.", e);
		}
		
		currentDetailedInterval = interval;
		briefModeMark = briefInterval / currentDetailedInterval;
		
		// Start measuring
		timer = new Timer(true);
		try {
			timer.scheduleAtFixedRate(new MonitorThread(), 0, currentDetailedInterval);
		} catch (Exception e) {
			mode = LoadMonitorMode.MODE_NONE;
			throw new LoadMonitorException("Unable to schedule detailed mode thread.");
		}
		
		mode = LoadMonitorMode.MODE_DETAILED;
		
		processEvent(new LoadMonitorEvent(LoadMonitorEvent.EventType.MONITOR_START_DETAILED, hostName));
		
		// And store entry in the database and save it (in case we crash)
		loadEntries.put(new Pair< String, String >(contextId, taskId), loadDbEntry);
		try {
			saveDatabaseIndex();
		} catch (OutputWriteException e) {
			logError("Unable to save index when switching to detailed mode: " + e.getMessage());
			stopDetailedMode();
			throw new LoadMonitorException("Unable to save new database index.");
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#stopDetailedMode()
	 */
	public void stopDetailedMode()
		throws RemoteException, IllegalOperationException, LoadMonitorException {

		if (mode != LoadMonitorMode.MODE_DETAILED) {
			throw new IllegalOperationException("Monitor is not in detailed mode.");
		}
		
		logInfo("Stopping detailed mode.");
		
		timer.cancel();

		processEvent(new LoadMonitorEvent(LoadMonitorEvent.EventType.MONITOR_STOP_DETAILED, hostName));
		
		mode = LoadMonitorMode.MODE_NONE;
		
		try {
			detailedModeWriter.close();
		} catch (IOException e) {
			logError("Unable to close detailed mode data writer: " + e.getMessage());
		}

		detailedModeWriter = null;
		
		if (reenableBriefMode) {
			startBriefMode();
		}
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#getDetailedModeLoadData(java.lang.String, java.lang.String)
	 */
	public LoadFile getDetailedModeLoadData(String contextId, String taskId)
		throws RemoteException, InvalidArgumentException, ValueNotFoundException {

		MiscUtils.verifyStringParameterBoth(contextId, "contextId");
		MiscUtils.verifyStringParameterBoth(taskId, "taskId");

		LoadDatabaseEntry entry = loadEntries.get(new Pair< String, String >(contextId, taskId));
		
		if (entry == null) {
			throw new ValueNotFoundException("No load data found for ["
					+ contextId + ":" + taskId + "].");
		}
		
		LoadFile loadFile = getLoadFile(entry);
		
		return loadFile;
	}

	/*
	 *  @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#hasDetailedModeData(java.lang.String, java.lang.String)
	 */
	public boolean hasDetailedModeData(String contextId, String taskId)
		throws RemoteException, InvalidArgumentException {
		
		MiscUtils.verifyStringParameterBoth(contextId, "contextId");
		MiscUtils.verifyStringParameterBoth(taskId, "taskId");

		LoadDatabaseEntry entry = loadEntries.get(new Pair< String, String >(contextId, taskId));

		return (entry != null);
	}

	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#hasNativeSupport()
	 */
	public boolean hasNativeSupport() throws RemoteException {
		
		return nativeLibrary;
	}
	
	/*
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#ping()
	 */
	public long ping() throws RemoteException {
		
		return TimeUtils.now();
	}
	
	/* 
	 * @see cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface#getHardwareDescription()
	 */
	public synchronized HardwareDescription getHardwareDescription() throws RemoteException {
		
		return monitor.getHardwareDescription();
	}

	/**
	 * Write error message to the stdout.
	 * 
	 * @param message Message to output.
	 */
	private void logError(String message) {

		if ("true".equalsIgnoreCase(System.getenv("BEEN_HOSTRUNTIME_DEBUG"))) {
			System.out.println(TimeUtils.nowFormated() + " ERROR " + message);
		}
	}
	
	/**
	 * Write info message to the stdout.
	 * 
	 * @param message Message to output.
	 */
	private void logInfo(String message) {
		
		if ("true".equalsIgnoreCase(System.getenv("BEEN_HOSTRUNTIME_DEBUG"))) {
			System.out.println(TimeUtils.nowFormated() + " INFO " + message);
		}
	}
	
	/**
	 * Process event. This method is actually a workhorse of the whole system as it will automatically
	 * decide whether event should be saved or sent to the Load Server based on current settings
	 * and mode detector is operating in.
	 * <br/>
	 * <br/>
	 * Events that contain samples are processed like this:
	 * <ul>
	 *  <li>Brief mode:<br/>
	 *      If Load Monitor has has been successfully initialised by the Load Server, all events will
	 *      be sent to the server. If no valid reference to the Load Server exists, events are thrown
	 *      away.
	 *  </li>
	 *  <li>Detailed mode:<br/>
	 *      All samples are automatically saved to the load file via <tt>detailedModeWriter</tt>.
	 *      If Load Server reference is valid, some events will be sent to the Load Server to 
	 *      simulate brief mode samples (so the Load Server does not think we are down). 
	 *      Which samples are sent to the Load Server depends on the intervals for brief 
	 *      and detailed mode. 
	 *  </li>
	 * </ul>
	 * If event does not contain sample, it is always sent to the Load Server (if valid reference
	 * exists).
	 * 
	 * @param event Event to process.
	 */
	private void processEvent(LoadMonitorEvent event) {
		
		if (mode == LoadMonitorMode.MODE_DETAILED) {
			
			if (event.getSample() != null) {
				
				eventCount += 1;
				
				try {
					detailedModeWriter.append(event.getSample());
				} catch (Exception e) {
					// Error writing data.
					logError("Error writing sample data: " + e.getMessage());
				}
			}
			
			if ((eventCount >= briefModeMark)
				|| (event.getType() != LoadMonitorEvent.EventType.MONITOR_SAMPLE)) {
				
				if (eventDispatcher.isRunning()) {
					eventQueue.add(event);
				}
			}
			
			if (eventCount >= briefModeMark) {
				eventCount = 0;
			}
		} else {
			eventCount = 0;
			if (eventDispatcher.isRunning()) {
				eventQueue.add(event);
			}
		}
	}
	
	/**
	 * Parse index file which contains entries for load files that had been previously written
	 * by the Load Monitor. If file does not exist, no entries will be loaded. New file will then
	 * be created when monitor is switched to detailed mode.
	 * 
	 * @throws LoadMonitorException If index file is directory.
	 * @throws InputParseException If an error occurred when parsing file.
	 */
	private void loadDatabaseIndex() throws LoadMonitorException, InputParseException {
		
		logInfo("Loading index file.");
		
		File indexFile = new File(getFullFileName(LOAD_DATABASE_INDEX_FILE));
		
		if (indexFile.isDirectory()) {
			logError("Unable to open index file. It is directory.");
			throw new LoadMonitorException("Unable to open index file. It is directory.");
		}
		
		if (!indexFile.isFile()) {
			logInfo("Load database index not found. New one will be created when needed.");
			return;
		}
		
		DocumentBuilder builder = null;
		
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logError("Unable to create document builder: " + e.getMessage());
			throw new InputParseException("Unable to create document builder.", e);
		}
		
		Document document = null;
		
		try {
			document = builder.parse(indexFile);
		} catch (Exception e) {
			logError("Unable to parse index file: " + e.getMessage());
			throw new InputParseException("Unable to parse index file.", e);
		}
		
		Node indexNode = XMLHelper.getSubNodeByName(INDEX_FILE_NODE_NAME, document);
		
		ArrayList< Node > entryNodes = 
			XMLHelper.getChildNodesByName(LoadDatabaseEntry.XML_NODE_NAME, indexNode);
		
		for (Node node: entryNodes) {
			LoadDatabaseEntry entry = new LoadDatabaseEntry(node);
			
			loadEntries.put(new Pair< String, String >(entry.getContextId(), entry.getTaskId()), 
					entry);
		}
		
		logInfo("Index file loaded successfully. " + loadEntries.size() + " entr"
				+ (loadEntries.size() == 1 ? "y" : "ies") + " parsed successfully.");
	}
	
	/**
	 * Save index of the load database to the file. Index is saved every time it is modified (that is,
	 * every time monitor is switched to the detailed mode).
	 * 
	 * @throws OutputWriteException If an error occurred while writing data.
	 */
	private void saveDatabaseIndex() throws OutputWriteException {
		
		DocumentBuilder builder = null;
		
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new OutputWriteException("Unable to create document builder.", e);
		}
		
		Document document = builder.newDocument();
		
		Element indexNode = document.createElement(INDEX_FILE_NODE_NAME);
		
		document.appendChild(indexNode);
		
		for (LoadDatabaseEntry entry: loadEntries.values()) {
			indexNode.appendChild(entry.exportAsElement(document));
		}
		
		try {
			XMLHelper.saveDocument(document, getFullFileName(LOAD_DATABASE_INDEX_FILE));
		} catch (Exception e) {
			throw new OutputWriteException("Unable to save load monitor index.", e);
		}
	}
	
	/**
	 * Get load file corresponding to the given database entry.
	 * 
	 * @param entry Entry from the database.
	 * 
	 * @return Load file corresponding to the database entry.
	 */
	private LoadFileWritable getLoadFile(LoadDatabaseEntry entry) {
		
		return new LoadFileWritable(entry.contextId, entry.taskId, getFullFileName(entry.fileName));
	}
	
	/**
	 * Generate file name from the name of the working directory and IDs of context and task.
	 * If such file already exists, new filename is generated automatically (1000 different
	 * names are tested).
	 * 
	 * @param contextId Id of the context.
	 * @param taskId Id of the task.
	 * 
	 * @return Name of the file (not full path!).
	 */
	private String generateFileName(String contextId, String taskId) {
		
		String nameNoExt = makeFSFriendly(contextId) + "-" + makeFSFriendly(taskId);
		String pathNoExt = MiscUtils.concatenatePath(workingDirectory, nameNoExt);
		
		String index = "";
		int i = 0;
		
		for ( ; i < 1000; ++i) {
			File file = new File(pathNoExt + index + SAMPLE_FILE_EXTENSION);
			
			if (!file.isFile()) {
				break;
			}
			
			index = String.valueOf(i);
		}
		
		if (i == 1000) {
			// too many files, first one is the oldest, so overwrite it
			// This is pretty horrible way of solving this, but whoever has 1000 files in
			// one directory (and those files are all generated by LM) is probably asking for
			// trouble anyway :)
			index = "";
			File file = new File(pathNoExt + index + SAMPLE_FILE_EXTENSION);
			
			try {
				file.delete();
			} catch (Exception e) {
				// OK, we were unable to delete file, but we will use it anyway...
			}
		}
		
		return nameNoExt + index + SAMPLE_FILE_EXTENSION;
	}
	
	/**
	 * Replace all non-alphanumeric characters with the underscore.
	 * 
	 * @param s Input string.
	 * 
	 * @return String with all non-alphanumeric characters replaced with the underscore.
	 */
	private String makeFSFriendly(String s) {

		return s.replaceAll("[^a-zA-Z0-9]", "_");
	}
	
	/**
	 * Get full path to the file.
	 * 
	 * @param name Name of the file.
	 * 
	 * @return Absolute path to the file in the working directory.
	 */
	private String getFullFileName(String name) {
		
		return MiscUtils.concatenatePath(workingDirectory, name);
	}
	
	/**
	 * One entry in the database which contains data collected when monitor was in detailed mode.
	 * 
	 * @author Branislav Repcek
	 */
	private class LoadDatabaseEntry implements XMLSerializableInterface {
		
		/**
		 * Name of the node in the XML file.
		 */
		public static final String XML_NODE_NAME = "loadEntry";
		
		/**
		 * Name of the attribute with the context id.
		 */
		private static final String ATTRIBUTE_CONTEXT_ID = "context"; 
		
		/**
		 * Name of the attribute with the task id.
		 */
		private static final String ATTRIBUTE_TASK_ID = "task";
		
		/**
		 * Name of the attribute with the file name.
		 */
		private static final String ATTRIBUTE_FILE_NAME = "file";
		
		/**
		 * Context id.
		 */
		private String contextId;
		
		/**
		 * Task id.
		 */
		private String taskId;
		
		/**
		 * File name (not full path).
		 */
		private String fileName;
		
		/**
		 * Create new database entry.
		 * 
		 * @param contextId Id of the context.
		 * @param taskId Id of the task.
		 * @param fileName Name of the file. This has to be relative to the database directory.
		 * 
		 * @throws InvalidArgumentException If some argument is <tt>null</tt> or empty string.
		 */
		public LoadDatabaseEntry(String contextId, String taskId, String fileName)
			throws InvalidArgumentException {
			
			MiscUtils.verifyStringParameterBoth(contextId, "contextId");
			MiscUtils.verifyStringParameterBoth(taskId, "taskId");
			MiscUtils.verifyStringParameterBoth(fileName, "fileName");
			
			this.contextId = contextId;
			this.taskId = taskId;
			this.fileName = fileName;
		}
		
		/**
		 * Create new database entry from the XML file node.
		 * 
		 * @param node Node to parse.
		 * 
		 * @throws InputParseException If an error occurred while parsing data.
		 */
		public LoadDatabaseEntry(Node node) throws InputParseException {
			
			parseXMLNode(node);
		}

		/**
		 * @return Context id.
		 */
		public String getContextId() {
			
			return contextId;
		}

		/**
		 * @return name of the file (not full path!).
		 */
		public String getFileName() {
			
			return fileName;
		}

		/**
		 * @return Id of the task.
		 */
		public String getTaskId() {
			
			return taskId;
		}

		/*
		 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#exportAsElement(org.w3c.dom.Document)
		 */
		public Element exportAsElement(Document document) {

			Element element = document.createElement(getXMLNodeName());
			
			element.setAttribute(ATTRIBUTE_CONTEXT_ID, contextId);
			element.setAttribute(ATTRIBUTE_TASK_ID, taskId);
			element.setAttribute(ATTRIBUTE_FILE_NAME, fileName);
			
			return element;
		}

		/*
		 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#getXMLNodeName()
		 */
		public String getXMLNodeName() {

			return XML_NODE_NAME;
		}

		/*
		 * @see cz.cuni.mff.been.hostmanager.database.XMLSerializableInterface#parseXMLNode(org.w3c.dom.Node)
		 */
		public void parseXMLNode(Node node) throws InputParseException {
			
			contextId = XMLHelper.getAttributeValueByName(ATTRIBUTE_CONTEXT_ID, node);
			taskId = XMLHelper.getAttributeValueByName(ATTRIBUTE_TASK_ID, node);
			fileName = XMLHelper.getAttributeValueByName(ATTRIBUTE_FILE_NAME, node);
		}
	}
	
	/**
	 * This class contains code that is run in periodic intervals and collects data from the native
	 * library.
	 *
	 * @author Branislav Repcek
	 */
	private class MonitorThread extends TimerTask {
		
		/*
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {

			LoadSample sample = monitor.getSample();
			
			LoadMonitorEvent event = new LoadMonitorEvent(sample, hostName);
			
			processEvent(event);
		}
	}
}
