/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Antonin Tomecek
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
package cz.cuni.mff.been.taskmanager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.common.anttasks.AntTaskException;
import cz.cuni.mff.been.common.anttasks.Delete;
import cz.cuni.mff.been.common.serialize.Deserialize;
import cz.cuni.mff.been.common.serialize.DeserializeException;
import cz.cuni.mff.been.hostmanager.HostManagerException;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import cz.cuni.mff.been.hostmanager.HostManagerService;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;
import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;
import cz.cuni.mff.been.hostmanager.database.Memory;
import cz.cuni.mff.been.hostmanager.database.RSLRestriction;
import cz.cuni.mff.been.hostmanager.database.RestrictionInterface;
import cz.cuni.mff.been.hostmanager.load.HostStatus;
import cz.cuni.mff.been.hostmanager.load.LoadServerInterface;
import cz.cuni.mff.been.hostmanager.util.MiscUtils;
import cz.cuni.mff.been.hostmanager.value.ValueInteger;
import cz.cuni.mff.been.hostruntime.HostRuntimeException;
import cz.cuni.mff.been.hostruntime.HostRuntimeInterface;
import cz.cuni.mff.been.hostruntime.TaskInterface;
import cz.cuni.mff.been.jaxb.BindingParser;
import cz.cuni.mff.been.jaxb.ConvertorException;
import cz.cuni.mff.been.jaxb.XSD;
import cz.cuni.mff.been.jaxb.td.Dependencies;
import cz.cuni.mff.been.jaxb.td.DependencyCheckPoint;
import cz.cuni.mff.been.jaxb.td.HostRuntimes;
import cz.cuni.mff.been.jaxb.td.Package;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.jaxb.td.TaskExclusivity;
import cz.cuni.mff.been.logging.FilesystemLogStorage;
import cz.cuni.mff.been.logging.LogLevel;
import cz.cuni.mff.been.logging.LogRecord;
import cz.cuni.mff.been.logging.LogStorage;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.logging.OutputHandle;
import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.softwarerepository.MatchException;
import cz.cuni.mff.been.softwarerepository.PackageMetadata;
import cz.cuni.mff.been.softwarerepository.RSLPackageQueryCallback;
import cz.cuni.mff.been.softwarerepository.SoftwareRepositoryInterface;
import cz.cuni.mff.been.softwarerepository.SoftwareRepositoryService;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.taskmanager.data.CheckPointEntry;
import cz.cuni.mff.been.taskmanager.data.ContextEntry;
import cz.cuni.mff.been.taskmanager.data.Data;
import cz.cuni.mff.been.taskmanager.data.HostRuntimeEntry;
import cz.cuni.mff.been.taskmanager.data.TaskData;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;
import cz.cuni.mff.been.taskmanager.data.TaskState;
import cz.cuni.mff.been.taskmanager.tasktree.IllegalAddressException;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTree;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeAddress;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeInput;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeQuery;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeReader;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeRecord;

/**
 * Implementation of Task Manager (main class).
 * 
 * @author Antonin Tomecek
 * @author Andrej Podzimek
 */
public class TaskManagerImplementation extends UnicastRemoteObject
implements TaskManagerInterface, HostRuntimesPortInterface {
	
	private static final long serialVersionUID = -4208900267250364137L;

	private static final String RESCUE_DIR_NAME = "rescue";

	/** 
	 * Default number of finished tasks kept in self cleaning context
	 */
	private static final int DEFAULT_FINISHED_TASKS_KEPT = 10;
	
	/** Path where TM's configuration file should be stored. Initialized by constructor. */
	private final File configurationFile;
	
	/** The task tree associated with this task manager */
	private final TaskTree taskTree;
	
	/** The default task tree reader for remote queries. */
	private final TaskTreeReader taskTreeReader;
	
	/** The parser used to read task descriptors. */
	private final BindingParser< TaskDescriptor > taskDescriptorParser;
	
	/** Object storing data of Task Manager. */
	private Data data;
	
	/** Path to the root directory for Task Manager's files. */
	private String rootDirectory = null;
	
	/** Log storage component */
	private LogStorage logStorage = null;
	
	/** Log level of the Task Manager. */
	private LogLevel logLevel;
	
	private LinkedList<ServiceEntry> serviceRegistry
		= new LinkedList<ServiceEntry>();
	
	private CheckPoint checkPointWaitingObject
		= new CheckPoint(null, null, null, null);

	private int lastTaskNumber = 0;
	/**
	 * Size limit of the Host Runtime's package cache.
	 */
	private long maxPackageCacheSize = DEFAULT_MAX_PACKAGE_CACHE_SIZE;
	
	/**
	 * Number of closed contexts, for which the Host Runtime should keep data on
	 * the disk.
	 */
	private int keptClosedContextCount = DEFAULT_KEPT_CLOSED_CONTEXT_COUNT;

	/**
	 * List of closed contexts that have not been deleted yet.
	 */
	private List<String> keptClosedContextsSync = new LinkedList<String> ();

	/**
	 * Listeners on Host Runtime registrations and unregistration events.
	 */
	private ArrayList<HostRuntimeRegistrationListener> registrationListeners;

	/** Listeners on Task events */
	private ArrayList<TaskEventListener> taskListeners;

//	/**
//	 * Delete file or directory (recursively) specified by <code>file</code>.
//	 * 
//	 * @param file File or directory to be removed.
//	 * @throws NullPointerException If input parameter is <code>null</code>.
//	 * @throws IllegalArgumentException If file is unknown thing...
//	 */
//	private static void deleteRecursive(File file) {
//		/* Check input parameters. */
//		if (file == null) {
//			throw new NullPointerException("directory is null");
//		}
//		
//		/* Delete (recursively). */
//		if (file.isFile()) {
//			file.delete();
//		} else if (file.isDirectory()) {
//			File[] items = file.listFiles();
//			for (File item : items) {
//				deleteRecursive(item);
//			}
//			file.delete();
//		}
//	}
	
	/**
	 * Store configuration to configuration file.
	 * 
	 * @param configurationFile XML file for storing configuration.
	 */
	private void storeConfiguration(File configurationFile) {
		Document document;
		try {
			document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Could not create Document for XML. "
					+ "This should not occur.", e);
		}
		
		/* Add root element. */
		Element configurationElement = document.createElement("configuration");
		document.appendChild(configurationElement);
		
		/* Add hostRuntimes element. */
		Element hostRuntimesElement
			= document.createElement("hostRuntimes");
		configurationElement.appendChild(hostRuntimesElement);
		/* ... set attributes for hostRuntimes element. */
		hostRuntimesElement.setAttribute("maxPackageCacheSize",
				String.valueOf(maxPackageCacheSize));
		hostRuntimesElement.setAttribute("keptClosedContextCount",
				String.valueOf(keptClosedContextCount));
		
		/* Store to file. */
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance()
				.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException("Could not create Transformer for XML. "
					+ "This should not occur.", e);
		}
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				OutputKeys.DOCTYPE_SYSTEM, "configuration.dtd");
		try {
			transformer.transform(new DOMSource(document),
					new StreamResult(configurationFile));
		} catch (TransformerException e) {
			System.err.println("Could not store configuration of Task "
					+ "Manager to XML file.");
		}
	}
	
	/**
	 * Load configuration from configuration file.
	 * 
	 * @param configurationFile XML file for loading configuration.
	 * @throws IllegalArgumentException If <code>configurationFile</code> could
	 * 	not be parsed for some reason.
	 */
	private void loadConfiguration(File configurationFile) {
		if (!configurationFile.exists() || !configurationFile.isFile()) {
			throw new IllegalArgumentException("Configuration file (\""
					+ configurationFile.getPath() + "\") not found");
		}
		
		DocumentBuilderFactory documentBuilderFactory
			= DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setValidating(true);
		
		Document document;
		try {
			document = documentBuilderFactory
				.newDocumentBuilder().parse(configurationFile);
		} catch (SAXException e) {
			throw new IllegalArgumentException("Parse error occured.", e);
		} catch (IOException e) {
			throw new IllegalArgumentException("IO error occurred.", e);
		} catch (ParserConfigurationException e) {
			throw new IllegalArgumentException("DocumentBuilder can not be "
					+ "created", e);
		}
		
		/* Process element configuration... */
		Element configurationElement = document.getDocumentElement();
		if (!configurationElement.getTagName().equals("configuration")) {
			logWarning("Unknown format of configuration file "
					+ "(will not be loaded).");
		}
		
		/* Process element hostRuntimes... */
		Element hostRuntimesElement = (Element)
			configurationElement.getElementsByTagName("hostRuntimes").item(0);
		/* ... attribute maxPackageCacheSize. */
		String attributeMaxPackageCacheSize
			= hostRuntimesElement.getAttribute("maxPackageCacheSize");
		if (!attributeMaxPackageCacheSize.equals("")) {
			maxPackageCacheSize
				= Long.parseLong(attributeMaxPackageCacheSize);
		}
		/* ... attribute keptClosedContextCount. */
		String attributeKeptClosedContextCount
			= hostRuntimesElement.getAttribute("keptClosedContextCount");
		if (!attributeKeptClosedContextCount.equals("")) {
			keptClosedContextCount
				= Integer.parseInt(attributeKeptClosedContextCount);
		}
	}
	
//	/**
//	 * This method should be called after <code>Data</code> object is rescued.
//	 * It fills (updates) remote interfaces for tasks.
//	 */
//	private void reinitializeDataWhenRescue() {
//		HostRuntimeEntry[] hostRuntimes = data.getHostRuntimes();
//		
//		for (HostRuntimeEntry hostRuntime : hostRuntimes) {
//			/* Construct URI of Host Runtime control interface. */
//			URI hostRuntimeUri;
//			try {
//				hostRuntimeUri = new URI("rmi", null, hostRuntime.getHostName(),
//						RMI.REGISTRY_PORT,
//						HostRuntimeInterface.URL, null, null);
//			} catch (URISyntaxException e) {
//				System.err.println("Could not construct hierarchical URI from "
//						+ "the given components (hostName \""
//						+ hostRuntime.getHostName() + "\"):" + e.getMessage());
//				continue;
//			}
//			
//			/* Obtain RMI reference to Host Runtime. */
//			HostRuntimeInterface hostRuntimeInterface;
//			try {
//				hostRuntimeInterface = (HostRuntimeInterface)
//				Naming.lookup(hostRuntimeUri.toString());
//			} catch (Exception e) {
//				System.err.println("Could not get RMI reference for host "
//						+ "runtime (URI \"" + hostRuntimeUri.toString()
//						+ "\")");
//				
//				/* Set all tasks of that hostRuntime to ABORTED and remove
//				 * hostRuntime */
//				TaskEntry[] tasks
//					= data.getTasksOnHost(hostRuntime.getHostName());
//				for (TaskEntry task : tasks) {
//					data.changeTaskState(
//							task.getTaskId(), task.getContextId(),
//							TaskState.ABORTED);
//				}
//				data.removeHostRuntime(hostRuntime);
//				
//				continue;
//			}
//			
//			/* Get all RMI interfaces to tasks from each Host Runtime and set
//			 * Task Manager's data. */
//			try {
//				TaskInterface[] taskInterfaces;
//				taskInterfaces = hostRuntimeInterface.getRunningTaskInterfaces();
//				for (TaskInterface taskInterface : taskInterfaces) {
//					String taskId = taskInterface.getTaskID();
//					String contextId = taskInterface.getContextID();
//					/* Set TaskInterface in data. */
//					try {
//						data.setTaskInterface(taskId, contextId,
//								taskInterface);
//					} catch (Exception e) {
//						System.err.println("Problem when setting "
//								+ "taskInterface: " + e.getMessage());
//					}
//				}
//			} catch (RemoteException e) {
//				System.out.println("Could not get interfaces of running tasks "
//						+ "on hostRuntime (URI \"" + hostRuntimeUri.toString()
//						+ "\"): " + e.getMessage());
//			}
//		}
//		
//	}
	
	/**
	 * Creates a new Task Manager instance and
	 * handles RemoteException thrown by implicit super constructor.
	 * 
	 * @param rootDirectory Root directory for Task Manager.
	 * @param level Log level of the Task Manager.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	protected TaskManagerImplementation(String rootDirectory, LogLevel level)
	throws RemoteException {
		Throwable throwable = null;
		
		this.taskTree = new TaskTree();		
		this.taskTreeReader = new TaskTreeReader(taskTree);
		try {
			this.taskDescriptorParser = XSD.TD.createParser(TaskDescriptor.class);
		} catch (SAXException exception) {
			throwable = exception;
			throw new RuntimeException();															// Pro forma, finally {} exits.
		} catch (JAXBException exception) {
			throwable = exception;
			throw new RuntimeException();															// Pro forma, finally {} exits.
		} finally {
			for (Throwable t = throwable; null != t; t = t.getCause()) {
				System.err.println();
				System.err.println(t.getMessage());
				t.printStackTrace(System.err);
			}
			if (null != throwable) {
				System.exit(1);
			}
		}
		
		/* Is Task Manager initialised from rescue? */
		//boolean rescued = false;
		
		this.rootDirectory = rootDirectory;
		this.logLevel = level;
		
		File rescueDirectory = new File(rootDirectory, RESCUE_DIR_NAME);
//		if (rescueDirectory.exists()) {
//			rescued = true;
//			/* Rescue... */
//			System.err.println("Reload to last state...");
//			File rescueDirectoryOld
//				= new File(rootDirectory, RESCUE_DIR_NAME + "_OLD");
//			rescueDirectory.renameTo(rescueDirectoryOld);
//			this.data = Rescue.rescueData(rescueDirectory, rescueDirectoryOld);
//			this.reinitializeDataWhenRescue();
//			deleteRecursive(rescueDirectoryOld);
//			System.err.println("Reloaded.");
//		} else {
			this.data = new Data(rescueDirectory, taskTree);
//		}
		
		this.registrationListeners = new ArrayList<HostRuntimeRegistrationListener>();
		this.taskListeners = new ArrayList<TaskEventListener>();
		
		/* Prepare and load configuration from XML file. */
		this.configurationFile = new File(rootDirectory, CONFIGURATION_FILE);
		try {
			this.loadConfiguration(configurationFile);
		} catch (IllegalArgumentException e) {
			System.err.println("Could not load configuration file. New "
					+ "configuration file will be created (with default values "
					+ "set).");
			this.storeConfiguration(configurationFile);
		}
		
		/* Create and initialise the log storage. */
		try {
			String logDirPath = rootDirectory + File.separator + "logs";
			File logDir = new File(logDirPath);
			if (logDir.exists()) {
				Delete.deleteDirectory(logDirPath);
			}
			
			logStorage = new FilesystemLogStorage(rootDirectory + File.separator + "logs"); 
		} catch (LogStorageException e) {
			System.err.println("Cannot create the log storage: " + e.getMessage());
			System.exit(1);
		} catch (AntTaskException e) {
			System.err.println("Cannot delete the log directory: " + e.getMessage());
			System.exit(1);
		}
		
//		if (!rescued) {
			/* Create system context. */
			this.newContext(SYSTEM_CONTEXT_ID,
					SYSTEM_CONTEXT_NAME, SYSTEM_CONTEXT_DESCRIPTION, null);

			try {
				this.logStorage.addTask(SYSTEM_CONTEXT_ID, TASKMANAGER_TASKNAME);
				this.logStorage.setTaskHostname(SYSTEM_CONTEXT_ID, TASKMANAGER_TASKNAME, 
						InetAddress.getLocalHost().getCanonicalHostName());
			} catch (Exception e) {
				logFatal("Cannot store Task Manager's logs: " + e.getMessage());
				System.exit(1);
			}
//		}
		
		/* If the Task Manager shuts down, do some work. */ 
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//			public void run() {
////				deleteRecursive(data.getRescueRootDir());
//			}
//		});
	}
	
	/**
	 * End working of Task Manager.
	 */
	public void stopTaskManager() throws RemoteException {
		System.exit(0);
	}
	
	/**
	 * End working of Task Manager and of all known Host Runtimes.
	 */
	public void stopTaskManagerAndHostRuntimes() throws RemoteException {
		HostRuntimeEntry[] hostRuntimes = data.getHostRuntimes();
		
		for (HostRuntimeEntry hostRuntime : hostRuntimes) {
			/* Construct URI of Host Runtime control interface. */
			URI hostRuntimeUri;
			try {
				hostRuntimeUri = new URI("rmi", null, hostRuntime.getHostName(),
						RMI.REGISTRY_PORT,
						HostRuntimeInterface.URL, null, null);
			} catch (URISyntaxException e) {
				throw new RemoteException("Could not construct URI of Host "
						+ "Runtime");
			}
			
			/* Obtain RMI reference to Host Runtime. */
			HostRuntimeInterface hostRuntimeInterface;
			try {
				hostRuntimeInterface = (HostRuntimeInterface)
				Naming.lookup(hostRuntimeUri.toString());
			} catch (Exception e) {
				throw new RemoteException("Could not connect to required Host "
						+ "Runtime (URI=\"" + hostRuntimeUri.toString()
						+ "\")");
			}
			
			/* Kill Host Runtime... */
			hostRuntimeInterface.terminate();
		}
		
		System.exit(0);
	}
	
	/**
	 * A simple "function pointer" definition for host load evaluation.
	 * 
	 * @author Andrej Podzimek
	 */
	private static interface LoadHandler {
		
		/**
		 * The decision making algorithm to find out whether the host can accept 
		 * 
		 * @param host Task Manager's local entry for the host.
		 * @param descriptor Descriptor for the task to launch.
		 * @return Whether the task can be accepted or not.
		 * @throws RemoteException When the Host Manager cannot be contacted.
		 */
		boolean canAcceptLoad(HostRuntimeEntry host, TaskDescriptor descriptor) throws RemoteException;
	}
	
	/**
	 * A special always-true load handler for the initial phase with no Host Manager running.
	 * 
	 * @author Andrej Podzimek
	 */
	private static final class NullLoadHandler implements LoadHandler {

		@Override
		public boolean canAcceptLoad(HostRuntimeEntry host, TaskDescriptor descriptor) {
			host.addLoad(
				descriptor.isSetLoadMonitoring() ?
					descriptor.getLoadMonitoring().getLoadUnits() :									// OK, has default value.
					DEFAULT_LOAD_UNITS
			);
			return true;
		}
	}
	
	/**
	 * A standard load handler that asks the Host Manager about the current load limit.
	 * 
	 * @author Andrej Podzimek
	 */
	private final class StandardLoadHandler implements LoadHandler {
		
		/** A reference to the Host Manager. */
		private final HostManagerInterface hostManager;
		
		/**
		 * Initializes a new handler instance with the supplied Host Manager reference.
		 * 
		 * @param hostManager A reference to the Host Manager.
		 */
		StandardLoadHandler(HostManagerInterface hostManager) {
			this.hostManager = hostManager;
		}

		@Override
		public boolean canAcceptLoad(HostRuntimeEntry host, TaskDescriptor descriptor)
		throws RemoteException {
			final HostInfoInterface hostInfo = hostManager.getHostInfo(host.getHostName());
			int limit;
			
			try {
				limit = (
					(ValueInteger)
					hostInfo.getUserPropertyValue(HostInfoInterface.Properties.LOAD_UNITS)
				).intValue();
			} catch (ValueNotFoundException exception) {
				limit = getDefaultLimit(hostInfo);
			} catch (ClassCastException exception) {
				logError("LOAD_UNITS has invalid type on host " + host.getHostName());
				limit = getDefaultLimit(hostInfo);
			}
			return host.acceptLoad(
				descriptor.isSetLoadMonitoring() ?
					descriptor.getLoadMonitoring().getLoadUnits() :									// OK, has default value.
					DEFAULT_LOAD_UNITS,
				limit
			);
		}
		
		/**
		 * Gets the default limit for a host runtime as configured by the Host Manager. If no such
		 * value is available, a (hopefully) sane default value will be computed.
		 * 
		 * @param hostInfo Host information obtained from the Host Manager.
		 * @return The default load units limit.
		 */
		private int getDefaultLimit(HostInfoInterface hostInfo) {
			int limit;
			try {
				limit = (
					(ValueInteger)
					hostInfo.getPropertyValue(HostInfoInterface.Properties.DEFAULT_LOAD_UNITS)
				).intValue();
			} catch (ValueNotFoundException exception1) {
				logError("DEFAULT_LOAD_UNITS is not set for host " + hostInfo.getHostName());
				limit = (int)
				(Memory.Properties.PHYSICAL_MEMORY_GUESS / Memory.Properties.BYTES_PER_UNIT);
			}																						// C.C.Exception is fatal here.
			return limit;
		}
	}
	
	/**
	 * Selects a host runtime from the list of applicable hosts. The hosts on the list are expected
	 * to meet all the statically defined conditions (RSL, asTask and the like). Furthermore, they
	 * are expected to be online. This method first selects hosts that meet dynamically changing
	 * conditions (such as host reservation) from the supplied list. Then one of the matching hosts
	 * is selected at random. When either no host matches the dynamic conditions or an empty list
	 * is supplied, null will be returned.
	 * 
	 * @param taskDescriptor The task descriptor to read data from.
	 * @param hostManager A reference to the Host Manager.
	 * @throws RemoteException When the Host Manager can't be contacted.
	 */
	private boolean selectHostRuntime(
		TaskDescriptor taskDescriptor,
		HostManagerInterface hostManager
	) throws RemoteException {
		final String taskId = taskDescriptor.getTaskId();
		final String contextId = taskDescriptor.getContextId();
		final List<String> hostNames = taskDescriptor.getHostRuntimes().getName();					// OK, we can modify the TD.
		final TaskExclusivity taskExclusivity = taskDescriptor.getExclusive();
		final Map< String, HostStatus > hostStatusMap;
		final boolean isSystem = SYSTEM_CONTEXT_ID.equals(contextId);
		final LoadHandler loadHandler;
		ListIterator<String> hostIt;
		String selectedHostName;
		
		/*
		 * When the HM is null, this is an early phase of HM startup when the detectors are run.
		 * We must rely on Task Manager's list of host runtimes.
		 */
		if (
			null == hostManager ||																	// We don't have a HM.
			(isSystem && taskId.startsWith(HostManagerInterface.DETECTOR_PREFIX))					// It's a detector. (Deadlock!)
		) {
			hostStatusMap = new HashMap< String, HostStatus >();
			for (HostRuntimeEntry entry : data.getHostRuntimes()) {									// For known host runtimes...
				hostStatusMap.put(entry.getHostName(), HostStatus.ONLINE);							// ...assume they are online.
			}
			loadHandler = new NullLoadHandler();
		} else {
			final LoadServerInterface loadServer = hostManager.getLoadServer();
			hostStatusMap = loadServer.getHostStatusMap();
			loadHandler = new StandardLoadHandler(hostManager);
		}
		
		Collections.shuffle(hostNames);																// Keep it random. :-)
		selectedHostName = null;
		hostIt = hostNames.listIterator();
		hostSelection:
		while (hostIt.hasNext()) {
			String hostName = hostIt.next();
			if (HostStatus.ONLINE != hostStatusMap.get(hostName)) {									// Either unknown or bad host.
				continue hostSelection;
			}
			HostRuntimeEntry hostRuntimeEntry = data.getHostRuntimeByName(hostName);
			if (hostRuntimeEntry != null) {
				String hostReservation = hostRuntimeEntry.getReservation();
				switch (taskExclusivity) {
					case NON_EXCLUSIVE:
						if (null == hostReservation) {												// No reservation? OK!
						} else if (hostReservation.equals(contextId)) {								// Our reservation? OK!
						} else {
							continue hostSelection;
						}
						break;
					case CONTEXT_EXCLUSIVE:
						if (null == hostReservation) {												// No reservation?
							TaskEntry[] tasksOnHost = data.getTasksOnHost(hostName);
							for (TaskEntry taskOnHost : tasksOnHost) {								// Check for other ctx first.
								switch (taskOnHost.getState()) {
									case RUNNING:
									case SLEEPING:													// Yes, fall through.
										if (!taskOnHost.getContextId().equals(contextId)) {
											continue hostSelection;									// Foe ctx found.
										}
										break;
									default:
										break;
								}
							}																		// All ctxs ours. We're OK.
						} else if (hostReservation.equals(contextId)) {								// Our reservation? OK!
						} else {
							continue hostSelection;
						}
						break;
					case EXCLUSIVE:
						if (null == hostReservation) {												// No reservation?
							TaskEntry[] tasksOnHost = data.getTasksOnHost(hostName);
							for (TaskEntry taskOnHost : tasksOnHost) {								// Then we want no other tasks.
								switch (taskOnHost.getState()) {
									case RUNNING:
									case SLEEPING:													// Yes, fall through.
										continue hostSelection;										// Foe task found.
									default:
										break;
								}
							}
						} else {
							continue hostSelection;
						}
						break;
				}
				if (loadHandler.canAcceptLoad(hostRuntimeEntry, taskDescriptor)) {
					selectedHostName = hostName;
					break hostSelection;
				}
			}
		}
		if (null == selectedHostName) {
			return false;
		} else {
			hostIt.remove();																		// Remove the selected host...
			hostNames.add(0, selectedHostName);														// ...and make it first.
			return true;
		}
	}
	
	/**
	 * Test if task is ready to run (exclusivity, dependencies, ...).
	 * 
	 * @param taskEntry <code>TaskEntry</code> of checked task.
	 * @param taskData <code>TaskData</code> of checked task.
	 * @return <code>true</code> if task is ready to run, <code>false</code>
	 * 	otherwise.
	 * @throws TaskManagerException When checkpoint data deserialization fails.
	 */
	private boolean isTaskReadyToRun(TaskEntry taskEntry, TaskData taskData)
	throws TaskManagerException {
		try {
			final TaskDescriptor taskDescriptor = taskData.getTaskDescriptor();
			final HostManagerInterface hostManager = (HostManagerInterface) serviceFind(
				HostManagerService.SERVICE_NAME,
				Service.RMI_MAIN_IFACE
			);
			
			prepareHostNames(taskDescriptor, hostManager);	
			if (!checkDependencies(taskData)) {														// Test for reached checkpoints.
				return false;
			}
			return selectHostRuntime(taskDescriptor, hostManager);
		} catch (RemoteException exception) {
			logError("Could not contact the Host Manager: " + exception.getMessage());
			return false;
		}
	}
	
	/**
	 * Test if task is ready to run and start it.
	 * 
	 * @param taskEntry <code>TaskEntry</code> of checked task.
	 * @param taskData <code>TaskData</code> of checked task.
	 * @return <code>true</code> if task started, <code>false</code> otherwise.
	 */
	private boolean runTaskIfReady(TaskEntry taskEntry, TaskData taskData) {
		boolean taskReady;		
		String taskId = taskEntry.getTaskId();
		String contextId = taskEntry.getContextId();
		
		synchronized (data) {
			try {
				taskReady = isTaskReadyToRun(taskEntry, taskData);
				if (taskReady) {
					data.changeTaskState(taskId, contextId, TaskState.SCHEDULED);
				}
			} catch (TaskManagerException exception) {												// This is fatal.
				logError(
					"Task cannot be started, giving up (" +  taskId + ", " + contextId + "): " +
					exception.getMessage()
				);
				try {
					taskReachedEnd(taskId, contextId, TaskState.ABORTED);
				} catch (RemoteException e) {
					logError(
						"Task could not reach state (" + taskId + ", " + contextId + "): " +
						e.getMessage()
					);
				}
				taskReady = false;
			}
		}
		if (taskReady) {
			try {
				taskStart(taskData);
			} catch (TaskManagerException exception) {												// This is non-fatal.
				taskReady = false;																	// BAD exception design.
			}
		}
		return taskReady;
	}
	
	/**
	 * Test all <code>SUBMITTED</code> tasks and run all ready of them.
	 */
	private void runAllReadyTasks() {
		synchronized (data) {
			TaskEntry[] tasksSubmitted = data.getTasksByState(TaskState.SUBMITTED);
			for (TaskEntry taskEntry : tasksSubmitted) {
				String taskId = taskEntry.getTaskId();
				String contextId = taskEntry.getContextId();
				TaskData taskData = data.getTaskData(taskId, contextId);
				runTaskIfReady(taskEntry, taskData);
			}
		}
	}
	
	/**
	 * Run (schedule) one or more new tasks specified by their task
	 * descriptors (XML form).
	 * 
	 * @param taskDescriptorPaths Array containing paths to the XML
	 * 	representation of Task Descriptors.
	 * @throws RemoteException If something failed during this operation.
	 */
	public void runTask(String ... taskDescriptorPaths)
	throws RemoteException {
		/* Paths to TaskDescriptors in Task Manager's root
		 * directory. */
		String[] taskDescriptors
			= new String[taskDescriptorPaths.length];
		
		/* Copy TaskDescriptor XML files to the Task Manager's
		 * root directory. */
		for (int i = 0; i < taskDescriptorPaths.length; i++) {
			try {
				File oldFile = new File(taskDescriptorPaths[i]);
				File newFile = new File(rootDirectory,
						oldFile.getName());
				taskDescriptors[i]
					= newFile.getCanonicalPath();

				// TODO Copying the content of a file should really be a library method !
				
				/* Transfer data to the newFile... */
				BufferedInputStream in
				= new BufferedInputStream(
						new FileInputStream(oldFile));
				BufferedOutputStream out
				= new BufferedOutputStream(
						new FileOutputStream(newFile));
				final int bufferSize = 1024;
				byte [] buffer = new byte[bufferSize];
				int len;
				while ((len = in.read(buffer, 0, bufferSize)) != -1) {
					out.write(buffer, 0, len);
				}
				
				in.close();
				out.close();
			} catch (Exception e) {
				throw new RemoteException("Cannot copy task descriptor", e);
			}
		}
		
		/* Create task for each Task Descriptor. */
		for (int i = 0; i < taskDescriptors.length; i++) {
			TaskDescriptor taskDescriptor = null;
			
			try {
				taskDescriptor = taskDescriptorParser.parse(new File(taskDescriptors[i]));
			} catch (JAXBException e) {
				throw new RemoteException("Could not parse XML task descriptor", e);				// TODO: Don't throw RE!!!
			} catch (ConvertorException e) {
				throw new RemoteException("Could not parse XML task descriptor's RSL sections.", e);// TODO: Don't throw RE!!!
			}
			
			runTask(taskDescriptor);
		}
	}

	/**
	 * Run (schedule) one new Task specified by its task descriptor.
	 * 
	 * @param taskDescriptor Task descriptor of new task to run.
	 * @throws RemoteException If something failed during this operation.
	 */
	public void runTask(TaskDescriptor taskDescriptor)
	throws RemoteException {
		TaskDescriptor originalTaskDescriptor;
		TaskDescriptor modifiedTaskDescriptor;
		HostRuntimes hostRuntimes;
		
		originalTaskDescriptor = TaskDescriptorHelper.clone(taskDescriptor);
		modifiedTaskDescriptor = TaskDescriptorHelper.clone(taskDescriptor);
		hostRuntimes = modifiedTaskDescriptor.getHostRuntimes();									// Always set.
		if (hostRuntimes.isSetAsTask()) {															// Prepare the extra dependency.
			TaskDescriptorHelper.addDependencyCheckpoint(											// Value will be null.
				modifiedTaskDescriptor,
				hostRuntimes.getAsTask(),
				Task.CHECKPOINT_NAME_STARTED
			);																						// OK, waiting arranged.
		} else if (hostRuntimes.isSetName()) {														// Resolve names from 'outside'.
			String name = null;
			List<String> names;
			ListIterator<String> it;
			
			names = hostRuntimes.getName();
			for (it = names.listIterator(); it.hasNext();) {
				try {
					name = MiscUtils.getCanonicalHostName(it.next());
					it.set(name);
				} catch (UnknownHostException e) {
					throw new RemoteException("Unable to resolve host \"" + name + "\".", e);		// TODO: NOT RemoteException!!!
				}
				if (null == data.getHostRuntimeByName(name)) {
					throw new RemoteException("Host runtime \"" + name + "\" not connected.");		// TODO: NOT RemoteException!!!
				}
			}
		}
		
		try {
			taskNew(modifiedTaskDescriptor, originalTaskDescriptor);
		} catch (TaskManagerException e) {
			throw new RemoteException("Cannot start task", e);										// TODO: NOT RemoteException!!!
		}
	}
	
	/**
	 * Run (schedule) one or more new tasks specified by their task
	 * descriptors.
	 * 
	 * @param taskDescriptors Task descriptors of new tasks to run.
	 * @throws RemoteException If something failed during this operation.
	 */
	public void runTask(TaskDescriptor ... taskDescriptors)
	throws RemoteException {
		synchronized (data) {
			for (int i = 0; i < taskDescriptors.length; i++) {
				runTask(taskDescriptors[i]);
			}
		}
	}
	
	/* Index of last context. */
	private static long lastContextIndex = 0;
	
	/**
	 * Create new ID for context.
	 * 
	 * @return New ID for context.
	 */
	private static String getNewContextId() {
		lastContextIndex++;
		
		return lastContextIndex + "_" + System.currentTimeMillis();
	}
	
	/**
	 * Create new context.
	 * 
	 * @param name Human readable name of context.
	 * @param description Human readable description of context.
	 * @param magicObject Some magic object (Serializable and Cloneable).
	 * @return ID of context.
	 * @throws RemoteException If something failed during this operation.
	 */
	public String newContext(String name, String description,
			Serializable magicObject)
	throws RemoteException {
		String id = getNewContextId();
		
		newContext(id, name, description, magicObject);
		
		return id;
	}
	
	/**
	 * Create new context. If context with specified ID exists then if it is
	 * already deactivated, it is deleted and newly created otherwise exception
	 * is thrown.
	 * This method creates non-self-cleaning context (all the finished 
	 * tasks in context will be kept forever).
	 * 
	 * @param id ID of context.
	 * @param name Human readable name of context.
	 * @param description Human readable description of context.
	 * @param magicObject Some magic object (Serializable and Cloneable).
	 * @throws IllegalStateException If context with specified ID already
	 * 	exists.
	 * @throws RemoteException If something failed during this operation.
	 */
	@Override
	public void newContext(String id, String name, String description,
			Serializable magicObject)
			throws RemoteException {

		newContext(id, name, description, magicObject, false);
	}
	
	/**
	 * Create new context. If context with specified ID exists then if it is
	 * already deactivated, it is deleted and newly created otherwise exception
	 * is thrown.
	 * This method support selfCleaning parameter which specifies 
	 * whether count of finished contexts should be limited to
	 * a predefined constant.
	 * 
	 * @param id ID of context.
	 * @param name Human readable name of context.
	 * @param description Human readable description of context.
	 * @param magicObject Some magic object (Serializable and Cloneable).
	 * @param selfCleaning whether new context should be self cleaning.
	 * @throws IllegalStateException If context with specified ID already
	 * 	exists.
	 * @throws RemoteException If something failed during this operation.
	 */
	public void newContext(String id, String name, String description,
			Serializable magicObject, boolean selfCleaning)
	throws RemoteException {
		/* Test if context currently exists. */
		ContextEntry currentContextEntry = data.getContextById(id);
		if (currentContextEntry != null) {
//			if (currentContextEntry.isOpen()) {
//				throw new IllegalStateException("Context with id \"" + id
//						+ "\" already exists and is still opened");
//			}
			throw new IllegalStateException("Context with id \"" + id
					+ "\" already exists");
		}
		
		ContextEntry contextEntry;
		
		if (selfCleaning) {
			contextEntry = new ContextEntry(id, name, description, magicObject, DEFAULT_FINISHED_TASKS_KEPT);
		} else {
			contextEntry = new ContextEntry(id, name, description, magicObject);
		}
		
		
		data.newContext(contextEntry);
		
		/* Add new context to log storage. */
		try {
			logStorage.addContext(id);
		} catch (LogStorageException e) {
			throw new RemoteException("Unable to add context to the log storage", e);
		}
	}
	
	/**
	 * Close context.
	 * 
	 * @param contextId ID of context.
	 * @throws RemoteException If something failed during this operation.
	 */
	public void closeContext(String contextId) throws RemoteException {
		data.closeContext(contextId);
	}

	/**
	 * Return informations about all tasks known inside Task Manager.
	 * 
	 * @return Array containing TaskEntry for each task.
	 * @throws RemoteException If something failed during this operation.
	 */
	public TaskEntry[] getTasks() throws RemoteException {
		return data.getTasks();
	}
	
	/**
	 * Return informations about all tasks known inside Task Manager as member
	 * of specified context.
	 * 
	 * @param contextId ID of requested context.
	 * @return Array containing TaskEntry for each convenient task.
	 * @throws RemoteException If something failed during this operation.
	 */
	public TaskEntry[] getTasksInContext(String contextId)
	throws RemoteException {
		return data.getTasksInContext(contextId);
	}
	
	/**
	 * Return informations about all tasks known inside Task Manager as
	 * scheduled on specified HostRuntime.
	 * 
	 * @param hostName URI of requested Host Runtime.
	 * @return Array containing TaskEntry for each convenient task.
	 * @throws RemoteException If something failed during this operation.
	 */
	public TaskEntry[] getTasksOnHost(String hostName)
	throws RemoteException {
		return data.getTasksOnHost(hostName);
	}
	
	/**
	 * Return informations about all contexts known inside Task Manager.
	 * 
	 * @return Array containing ContextEntry for each context.
	 * @throws RemoteException If something failed during this operation.
	 */
	public ContextEntry[] getContexts() throws RemoteException {
		return data.getContexts();
	}
	
	/**
	 * Return informations about one task specified by its ID.
	 * 
	 * @param taskId ID of requested task.
	 * @return TaskEntry filled in by informations about requested task (null
	 * 	if requested task not found).
	 * @throws RemoteException If something failed during this operation.
	 */
	public TaskEntry getTaskById(String taskId, String contextId)
	throws RemoteException {
		TaskEntry result = data.getTaskById(taskId, contextId);
		if (result == null) {
			throw new IllegalArgumentException("Invalid contextId (\"" + contextId					// TODO: !!! CHECKED exception !!!
				+ "\") or taskId (\"" + taskId + "\").");
		}
		return result;
	}
	
	/**
	 * Return informations about one context specified by its ID.
	 * 
	 * @param contextId ID of requested context.
	 * @return ContextEntry filled in by informations about requested context
	 * 	(null if requested task not found).
	 * @throws RemoteException If something failed during this operation.
	 */
	public ContextEntry getContextById(String contextId)
	throws RemoteException {
		ContextEntry result = data.getContextById(contextId); 
		if (result == null) {
			throw new IllegalArgumentException("Invalid contextId (\"" + contextId + "\").");
		}
		return result;
	}
	
	/**
	 * Kill task specified by its ID.
	 * 
	 * @param taskId ID of task.
	 * @param contextId ID of context.
	 * @throws IllegalArgumentException If task not found.
	 * @throws RemoteException If something failed during this operation.
	 */
	public void killTaskById(String taskId, String contextId)
	throws RemoteException {
		TaskInterface taskInterface = null;
		
		synchronized (data) {
			TaskData taskData = data.getTaskData(taskId, contextId);
			if (taskData == null) {
				throw new IllegalArgumentException("task (taskId \"" + taskId
						+ "\", contextId \"" + contextId + "\") not found");
			}

			taskInterface = taskData.getTaskInterface();
			if (taskInterface == null) {
				taskReachedEnd(taskId, contextId, TaskState.ABORTED);
			}
		}
		
		if (taskInterface != null) {
			taskInterface.kill();
		}
		
		unregisterFinishedService(contextId, taskId);
	}
	
	/**
	 * Kill all tasks within specified context and that context.
	 * 
	 * @param contextId ID of context.
	 * @throws RemoteException If something failed during this operation.
	 */
	public void killContextById(String contextId) throws RemoteException {
		ContextEntry contextEntry = data.getContextById(contextId);
		if (contextEntry == null) {
			throw new IllegalArgumentException("context (contextId \""
					+ contextId + "\") not found");
		}
		
		TaskEntry[] taskEntries = data.getTasksInContext(contextId);
		for (TaskEntry taskEntry : taskEntries) {
			killTaskById(taskEntry.getTaskId(), taskEntry.getContextId());
		}
	}
	
	/**
	 * Used by Host Runtime for forwarding of check points reached by
	 * tasks.
	 * 
	 * @param name Name of checkPoint.
	 * @param value Value of checkPoint.
	 * @param taskId ID of task.
	 * @param contextId ID of context.
	 * @param hostName Name of host on which this checkPoint was reached.
	 * @param magicObject <code>MagicObject</code> of this checkPoint.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	public void checkPointReached(String name, String value,
			String taskId, String contextId, String hostName,
			Serializable magicObject)
	throws RemoteException {
		checkPointReached(new CheckPoint(taskId, contextId,
				name, value));
	}
	
	/**
	 * Used by Host Runtime for forwarding of checkpoint reached by task.
	 * 
	 * @param checkPoint CheckPoint representation of reached checkpoint.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	public void checkPointReached(CheckPoint checkPoint)
	throws RemoteException {
		logDebug("[" + checkPoint.getContextId() + ":" 
			+ checkPoint.getTaskId() + "] Checkpoint \"" + checkPoint.getName() 
			+ "\" set to \"" + checkPoint.getValue() + "\"");
		
		/* Add information about this checkpoint to the list of
		 * reached checkpoints. */
		String name = checkPoint.getName();
		String taskId = checkPoint.getTaskId();
		String contextId = checkPoint.getContextId();
		String hostName = checkPoint.getHostName();
		Serializable magicObject = checkPoint.getValue();
		CheckPointEntry checkPointEntry = new CheckPointEntry(name,
				taskId, contextId, hostName, magicObject);
		
		synchronized (data) {
			data.newCheckPointOver(checkPointEntry);
		}

		/* Test dependencies of all waiting tasks if they were
		 * already reached. */	
		runAllReadyTasks();
		
		/* Notify all tasks waiting for checkpoint in checkPointLook(). */
		synchronized (checkPointWaitingObject) {
			checkPointWaitingObject.notifyAll();
		}
	}
	
	/**
	 * Test if specified context is still open or if there is specified task and
	 * is not finished yet.
	 * 
	 * @param taskId ID of task.
	 * @param contextId ID of context.
	 * @return <code>true</code> if checkPoint with specified
	 * 	<code>taskId</code> and <code>contextId</code> can be still reached;
	 * 	<code>false</code> otherwise.
	 */
	private boolean isCheckPointReachPossible(String taskId, String contextId) {
		/* Test if specified context exists and is still open. */
		ContextEntry contextEntry = data.getContextById(contextId);
		if (contextEntry == null) {
			return false;
		}
		if (contextEntry.isOpen()) {
			return true;
		}
		
		/* If context is not open so check if there is not-ended task with
		 * specified taskId. */
		TaskEntry taskEntry = data.getTaskById(taskId, contextId);
		if (taskEntry == null) {
			return false;
		}
		if ((taskEntry.getState() == TaskState.FINISHED)
				|| (taskEntry.getState() == TaskState.ABORTED)) {
			return false;
		}
		
		/* If there is not-ended task (specified by its taskId and contextId) in
		 * closed context... */
		return true;
	}
	
	/**
	 * Returns array containing all reached checkpoints matching specified
	 * parameters (taskId, contextId, name).
	 * All filled in values must match. Values set to null are arbitrary.
	 * Calling of this method is non-blocking.
	 * 
	 * @param name Name of checkpoint.
	 * @param value Value of checkpoint.
	 * @param taskId ID of task which reached checkpoint.
	 * @param contextId ID of context in which checkpoint was reached.
	 * @return Array containing all checkpoint matching specified
	 * 	checkpointTemplate.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	private CheckPoint[] checkPointLook(String name, Serializable value,
			String taskId, String contextId)
	throws RemoteException {
		CheckPointEntry[] checkPointEntries
			= data.getCheckPoints(name, taskId, contextId, value);
		
		CheckPoint[] matchingCheckPoints
			= new CheckPoint[checkPointEntries.length];
		
		for (int i = 0; i < checkPointEntries.length; i++) {
			matchingCheckPoints[i] = new CheckPoint(
					checkPointEntries[i].getTaskId(),
					checkPointEntries[i].getContextId(),
					checkPointEntries[i].getName(),
					checkPointEntries[i].getMagicObject());
		}
		
		return matchingCheckPoints;
	}
	
	/**
	 * Returns array containing all reached checkpoints matching specified
	 * parameters (taskId, contextId, name).
	 * All filled in values must match. Values set to null are arbitrary.
	 * Calling of this method is blocking.
	 * 
	 * @param name Name of checkpoint.
	 * @param value Value of checkpoint.
	 * @param taskId ID of task which reached checkpoint.
	 * @param contextId ID of context in which checkpoint was reached.
	 * @param timeout Maximum time to wait in milliseconds.
	 * @return Array containing all checkpoint matching specified
	 * 	checkpointTemplate.
	 * @throws TaskManagerException If Required checkPoint can not be reached
	 * 	anyway
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	private CheckPoint[] checkPointLook(String name, Serializable value,
			String taskId, String contextId, long timeout)
	throws TaskManagerException, RemoteException {
		long startTimeNano = System.nanoTime();
		CheckPoint[] result = null;
		
		synchronized (checkPointWaitingObject) {
			while ((result = checkPointLook(
					name, value, taskId, contextId)).length < 1) {
				
				if (!isCheckPointReachPossible(taskId, contextId)) {
					throw new TaskManagerException("Required checkPoint can "
							+ "not be reached anyway");
				}
				
				if (timeout == INFINITE_TIME) {
					try {
						checkPointWaitingObject.wait();
					} catch (InterruptedException e) {
						// do nothing
					}
				} else {
					long elapsedTime
					= (System.nanoTime() - startTimeNano) / 1000000;
					long remainTime = timeout - elapsedTime;

					if (remainTime <= 0) {
						return new CheckPoint[0];
					}

					try {
						checkPointWaitingObject.wait(remainTime);
					} catch (InterruptedException e) {
						// do nothing
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Do check point lookup. Tasks should not call this.
	 * Returns last value of reached checkpoints matching specified taskId,
	 * contextId and name. All values must be non-null and must match.
	 * 
	 * Calling of this method is blocking. If timeout is set to zero, then
	 * return immediately.
	 * @param name Name of checkpoint.
	 * @param taskId ID of task which reached checkpoint.
	 * @param contextId ID of context in which checkpoint was reached.
	 * @param timeout Maximum time to wait in milliseconds.
	 * 
	 * @return Value of specified checkpoint (can be <code>null</code>).
	 * @throws NullPointerException If some input parameter is null.
	 * @throws IllegalArgumentException If checkpoint not found.
	 * @throws TaskManagerException If Required checkPoint can not be reached
	 * 	anyway
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	public Serializable checkPointLook(String name,
			String taskId, String contextId, long timeout)
	throws TaskManagerException, RemoteException {
		/* Check input parameters. */
		if (name == null) {
			throw new NullPointerException("name is null");
		}
		if (taskId == null) {
			throw new NullPointerException("taskId is null");
		}
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}
		
		CheckPoint[] foundCheckPoints
			= checkPointLook(name, null, taskId, contextId, timeout);
		
		if (foundCheckPoints.length == 0) {
			throw new IllegalArgumentException("specified checkpoint not foud");
		}
		
		return foundCheckPoints[0].getValue();
	}
	
	/**
	 * Used by Host Runtime for forwarding of lookup request from task.
	 * Returns array containing all reached checkpoints matching specified
	 * checkpointTemplate. All filled in values must match. Values set to
	 * null are arbitrary.
	 * Calling of this method is blocking. If timeout is set to zero, then
	 * return immediately.
	 * 
	 * @param checkPointTemplate Prepared template for checkpoint match.
	 * @param timeout Maximum time to wait in milliseconds.
	 * @return Array containing all checkpoint matching specified
	 * 	checkpointTemplate.
	 * @throws TaskManagerException If Required checkPoint can not be reached
	 * 	anyway
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	public CheckPoint[] checkPointLook(CheckPoint checkPointTemplate,
			long timeout)
	throws TaskManagerException, RemoteException {
		String name = checkPointTemplate.getName();
		Serializable value = checkPointTemplate.getValue();
		String taskId = checkPointTemplate.getTaskId();
		String contextId = checkPointTemplate.getContextId();
		
		return checkPointLook(name, value, taskId, contextId, timeout);
	}
	
	/**
	 * Used by Host Runtime for forwarding of log messages from tasks.
	 * 
	 * @param contextId Id of the task's context.
	 * @param taskId TID of task.
 	 * @param level log level of this log message 
	 * @param timestamp time stamp of this log message
	 * @param message Message to log.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	public void log(String contextId, String taskId, LogLevel level, Date timestamp,
			String message)
	throws RemoteException {
		if (!logLevel.isGreaterOrEqual(LogLevel.WARN)) {
			System.out.println("[" + contextId + ":" + taskId + "] " + level
					+ " " + message);
		}
		
		try {
			logStorage.log(contextId, taskId, timestamp, level, message);
		} catch (LogStorageException e) {
			throw new RemoteException("Cannot store the log message in the log "
					+ "storage", e);
		}
	}
	

	/**
	 * Used by Host Runtime for forwarding of task's request for new
	 * registration of service.
	 * 
	 * @param service Object describing service to register.
	 * @throws RemoteException If something failed during the execution
	 * 	of the remote method call.
	 * @throws IllegalArgumentException If <code>service</code> is not correctly
	 * 	filled in.
	 */
	public void serviceRegister(ServiceEntry service)
	throws RemoteException {
		/* Check all values in ServiceEntry. */
		if (service.getServiceName() == null) {
			throw new IllegalArgumentException("service.serviceName can not be "
					+ "null");
		}
		if (service.getInterfaceName() == null) {
			throw new IllegalArgumentException("service.InterfaceName can not "
					+ "be null");
		}
		if (service.getRmiAddress() == null) {
			throw new IllegalArgumentException("service.rmiAddress can not be "
					+ "null");
		}
//		if ((service.getServiceName() == null)
//				|| (service.getInterfaceName() == null)
//				|| (service.getRmiAddress() == null)) {
//			logWarning("Warning: Registered service entry must have "
//				+ "all fields filled in (i.e. serviceName, interfaceName, "
//				+ "rmiAddress, remoteInterface). This entry ["
//				+ service.getServiceName() + "," + service.getInterfaceName()
//				+ "," + service.getRmiAddress() + ","
//				+ service.getRemoteInterface() + "] won't be registered!!");
//			return;
//		}
		
		ServiceEntry newEntry = service.clone();
		
		synchronized (serviceRegistry) {
			serviceRegistry.add(newEntry);
		}
	}
	
	/**
	 * Used by Host Runtime for forwarding of task's request for 
	 * deregistration of services.
	 * All entries matching specified template are removed from the
	 * registry.
	 * 
	 * @param serviceTemplate Object describing services to remove (using
	 * 	regular expressions).
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	public void serviceUnregister(ServiceEntry serviceTemplate)
	throws RemoteException {
		/* Make patterns for regex matching. */
		Pattern serviceNamePattern = null;
		Pattern interfaceNamePattern = null;
		Pattern rmiAddressPattern = null;
		
		String serviceNameTemplate = serviceTemplate.getServiceName();
		if (serviceNameTemplate == null) {
			/* Match everything. */
			serviceNamePattern = Pattern.compile(".*");
		} else {
			serviceNamePattern
				= Pattern.compile(serviceNameTemplate);
		}
		
		String interfaceNameTemplate
			= serviceTemplate.getInterfaceName();
		if (interfaceNameTemplate == null) {
			interfaceNamePattern = Pattern.compile(".*");
		} else {
			interfaceNamePattern
				= Pattern.compile(interfaceNameTemplate);
		}
		
		URI rmiAddressTemplate = serviceTemplate.getRmiAddress();
		if (rmiAddressTemplate == null) {
			rmiAddressPattern = Pattern.compile(".*");
		} else {
			rmiAddressPattern = Pattern.compile(rmiAddressTemplate
					.normalize().toString());
					// normalised and converted to String
		}
		
		synchronized (serviceRegistry) {
			Iterator< ServiceEntry > registryIterator = serviceRegistry.iterator();	
			while (registryIterator.hasNext()) {
				ServiceEntry serviceEntry = registryIterator.next();
				
				/* Do matching... */
				Matcher serviceNameMatcher = serviceNamePattern
					.matcher(serviceEntry.getServiceName());
				if (!serviceNameMatcher.matches()) {
					continue;
				}
				
				Matcher interfaceNameMatcher = interfaceNamePattern
					.matcher(serviceEntry.getInterfaceName());
				if (!interfaceNameMatcher.matches()) {
					continue;
				}
				
				Matcher rmiAddressMatcher = rmiAddressPattern
					.matcher(serviceEntry.getRmiAddress()
							.normalize().toString());
				if (!rmiAddressMatcher.matches()) {
					continue;
				}
				
				/* If everything matches so remove this entry from
				 * registry. */
				registryIterator.remove();
			}
		}
	}
	
	/**
	 * Used by Host Runtime for forwarding of task's request for looking in
	 * service entries.
	 * Returns array containing all service entries matching specified
	 * template (using regular expressions).
	 * 
	 * @param serviceTemplate Object looked describing services (using
	 * 	regular expressions).
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	public ServiceEntry[] serviceLook(ServiceEntry serviceTemplate)
	throws RemoteException {
		LinkedList<ServiceEntry> matchingEntries
			= new LinkedList<ServiceEntry>();
		
		/* Make patterns for regex matching. */
		Pattern serviceNamePattern = null;
		Pattern interfaceNamePattern = null;
		Pattern rmiAddressPattern = null;
		
		String serviceNameTemplate = serviceTemplate.getServiceName();
		if (serviceNameTemplate == null) {
			/* Match everything. */
			serviceNamePattern = Pattern.compile(".*");
		} else {
			serviceNamePattern
				= Pattern.compile(serviceNameTemplate);
		}
		
		String interfaceNameTemplate
			= serviceTemplate.getInterfaceName();
		if (interfaceNameTemplate == null) {
			interfaceNamePattern = Pattern.compile(".*");
		} else {
			interfaceNamePattern
				= Pattern.compile(interfaceNameTemplate);
		}
		
		URI rmiAddressTemplate = serviceTemplate.getRmiAddress();
		if (rmiAddressTemplate == null) {
			rmiAddressPattern = Pattern.compile(".*");
		} else {
			rmiAddressPattern = Pattern.compile(rmiAddressTemplate
					.normalize().toString());
					// normalised and converted to String
		}
		
		String taskId = serviceTemplate.getTaskId();
		String contextId = serviceTemplate.getContextId();
		
		synchronized (serviceRegistry) {
			for (ServiceEntry serviceEntry : serviceRegistry) {
				
				/* Do matching... */
				Matcher serviceNameMatcher = serviceNamePattern
					.matcher(serviceEntry.getServiceName());
				if (!serviceNameMatcher.matches()) {
					continue;
				}
				
				Matcher interfaceNameMatcher = interfaceNamePattern
					.matcher(serviceEntry.getInterfaceName());
				if (!interfaceNameMatcher.matches()) {
					continue;
				}
				
				Matcher rmiAddressMatcher = rmiAddressPattern
					.matcher(serviceEntry.getRmiAddress()
							.normalize().toString());
				if (!rmiAddressMatcher.matches()) {
					continue;
				}
				
				if ((taskId != null) && (!taskId.equals(serviceEntry.getTaskId()))) {
					continue;
				}

				if ((contextId != null) && (!contextId.equals(serviceEntry.getContextId()))) {
					continue;
				}
				
				/* If everything matches so add clone of this entry to
				 * the created list. */
				ServiceEntry matchingEntry = serviceEntry.clone();
				matchingEntries.add(matchingEntry);
			}
		}
		
		return matchingEntries.toArray(new ServiceEntry[matchingEntries.size()]);
	}
	
	/**
	 * Used by tasks for finding some registered remote interface.
	 * Given names are compared for exact match (doesn't use regular
	 * expressions).
	 * 
	 * @param serviceName Name of service.
	 * @param interfaceName Name of service's interface.
	 * @return Remote representation of one from all matching interfaces
	 * 	or null if none.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	public Remote serviceFind(String serviceName, String interfaceName)
	throws RemoteException {
		/* Check input parameters (null is not allowed). */
		if ((serviceName == null) || (interfaceName == null)) {
			return null;
		}
		
		/* Do search. */
		ServiceEntry templateEntry = new ServiceEntry(
			Pattern.quote(serviceName),
			Pattern.quote(interfaceName), null, null, null, null);
		ServiceEntry[] matchingEntries
			= serviceLook(templateEntry);
		
		/* If found then return the first. */
		if (matchingEntries.length > 0) {
			return matchingEntries[0].getRemoteInterface();
		}
		
		/* Return null by default (if nothing found). */
		return null;
	}
	
	/**
	 * Used by tasks for finding some registered remote interface.
	 * Given names are compared for exact match (doesn't use regular
	 * expressions).
	 * 
	 * @param serviceName Name of service.
	 * @param interfaceName Name of service's interface.
	 * @return URI representation of one from all matching interfaces or
	 * 	null if none.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	public URI serviceFindURI(String serviceName, String interfaceName)
	throws RemoteException {
		/* Check input parameters (null is not allowed). */
		if ((serviceName == null) || (interfaceName == null)) {
			return null;
		}
		
		/* Do search. */
		ServiceEntry templateEntry = new ServiceEntry(
			Pattern.quote(serviceName),
			Pattern.quote(interfaceName), null, null, null, null);
		ServiceEntry[] matchingEntries
			= serviceLook(templateEntry);
		
		/* If found then return the first. */
		if (matchingEntries.length > 0) {
			return matchingEntries[0].getRmiAddress();
		}
		
		/* Return null by default (if nothing found). */
		return null;
	}

	@Override
	public LogRecord[] getLogsForTask(String context, String taskID) 
	throws RemoteException, LogStorageException, IllegalArgumentException, NullPointerException {
		return logStorage.getLogsForTask(context, taskID);
	}

	@Override
	public boolean isContextRegistered(String name)
	throws RemoteException, LogStorageException, IllegalArgumentException, NullPointerException {
		return logStorage.isContextRegistered(name);
	}

	@Override
	public boolean isTaskRegistered(String context, String taskID)
	throws RemoteException, LogStorageException, IllegalArgumentException, NullPointerException {
		return logStorage.isTaskRegistered(context, taskID);
	}

	@Override
	public void addErrorOutput(String context, String taskID, String output) 
	throws RemoteException, LogStorageException, IllegalArgumentException, NullPointerException {
		logStorage.addErrorOutput(context, taskID, output);
	}

	@Override
	public void addStandardOutput(String context, String taskID, String output) 
	throws RemoteException, LogStorageException, IllegalArgumentException, NullPointerException {
		logStorage.addStandardOutput(context, taskID, output);
	}

	@Override
	public OutputHandle getErrorOutput(String context, String taskID)
	throws RemoteException, LogStorageException, IllegalArgumentException, NullPointerException {
		return logStorage.getErrorOutput(context, taskID);
	}

	@Override
	public OutputHandle getStandardOutput(String context, String taskID)
	throws RemoteException, LogStorageException, IllegalArgumentException, NullPointerException {
		return logStorage.getStandardOutput(context, taskID);
	}
	
	/**
	 * Test if context is finished (i.e. closed and all its tasks are ended).
	 * 
	 * @param contextId ID of context.
	 * @return <code>true</code> if context is finished; <code>false</code>
	 * 	otherwise.
	 * @throws IllegalArgumentException If context with specified ID does not
	 * 	exist.
	 */
	private boolean isContextFinished(String contextId) {
		/* Check if system context. */
		if (SYSTEM_CONTEXT_ID.equals(contextId)) {
			return false;
		}
		
		synchronized (data) {
			/* Check if context exists. */
			ContextEntry contextEntry = data.getContextById(contextId);
			if (contextEntry == null) {
				throw new IllegalArgumentException("Context \"" + contextId + "\" "
						+ "does not exist");
			}

			/* Check if context is open. */
			if (contextEntry.isOpen()) {
				return false;
			}

			/* Check if some task with specified contextId is not ended (FINISHED or
			 * ABORTED). */
			TaskEntry[] taskEntries = data.getTasksInContext(contextId);
			for (TaskEntry taskEntry : taskEntries) {
				if ((taskEntry.getState() != TaskState.FINISHED)
						&& (taskEntry.getState() != TaskState.ABORTED)) {
					return false;
				}
			}
		}
		
		/* Otherwise context is finished... */
		return true;
	}
	
	/**
	 * Kills and removes the given context.
	 * 
	 * Forcefully kills all tasks in the context to make sure it can be removed.
	 * Then proceeds to delete the newly inactive context.
	 * 
	 * Does nothing if the specified context is system context of BEEN.
	 * 
	 * TODO The synchronization issues here are certainly unresolved !
	 */
	public void killAndDeleteContext (String contextId)
	{
		// System context is never removed.
		if (SYSTEM_CONTEXT_ID.equals (contextId)) return;

		// Kill all tasks in the context.
		// Note that the context can become deleted in callbacks.
		try { killContextById (contextId); }
		catch (RemoteException e) { System.err.println ("Error trying to kill tasks in context: " + e.getMessage()); }

		// Delete the thing.
		deleteInactiveContext (contextId);
	}
	
	/*
	 * Removes the given context assuming it is inactive.
	 *  
	 * Destroys all the associated task objects in the context.
	 * Then deletes the context from the host runtimes.
	 * And finally removes the context internally. 
	 * 
	 * Does nothing if the specified context is system context of BEEN.
	 * 
	 * TODO The synchronization issues here are certainly unresolved !
	 */
	private void deleteInactiveContext (String contextId)
	{
		// System context is never removed.
		if (SYSTEM_CONTEXT_ID.equals (contextId)) return;

		// Free all resources used by tasks from the deleted context.
		// Build the list of host runtimes as we go along.
		Set<String> hostRuntimesToClean = new HashSet <String> ();
		TaskEntry[] taskEntriesToRemove = data.getTasksInContext(contextId);
		for (TaskEntry removedTaskEntry : taskEntriesToRemove) {
			String removedContextId = removedTaskEntry.getContextId ();
			String removedTaskId = removedTaskEntry.getTaskId ();

			// TODO It is a bit illogical to have task entry and task data and search for one using the other.
			TaskData removedTaskData = data.getTaskData (removedTaskId, removedContextId);
			hostRuntimesToClean.add (removedTaskEntry.getHostName ());
			try
			{
				TaskInterface removedTaskInterface = removedTaskData.getTaskInterface ();
				if (removedTaskInterface != null)
				{
					removedTaskInterface.destroy ();
					logInfo ("Task ["+ removedContextId + ":" + removedTaskId + "] removed on context removal.");
				}
			}
			catch (TaskException e)
			{
				logError("Error removing task ["+ removedContextId + ":" + removedTaskId + "] from its hostruntime.");
				e.printStackTrace();
			}
			catch (RemoteException e) {
				logError("Error removing task ["+ removedContextId + ":" + removedTaskId + "] from its hostruntime.");
				e.printStackTrace();
			}
		}
		
		// Remove the context from all the host runtimes.
		for (String cleanedHostRuntime : hostRuntimesToClean)
		{
			if (cleanedHostRuntime != null)
			{
				// TODO A bit awkward way to get the host runtime reference.
				// If we already have references to tasks, why not have the same for host runtimes ?
				
				// Build the URI.
	            URI cleanedHostRuntimeUri;
				try {
					cleanedHostRuntimeUri = new URI ("rmi", null, cleanedHostRuntime, RMI.REGISTRY_PORT, HostRuntimeInterface.URL, null, null);
				}
				catch (URISyntaxException e)
				{
					logError ("Failed to construct the host runtime URI for \"" + cleanedHostRuntime + "\".");
					e.printStackTrace ();
					break;
				}
	
				// Find the reference.
				HostRuntimeInterface cleanedHostRuntimeInterface;
				try
				{
					cleanedHostRuntimeInterface = (HostRuntimeInterface) Naming.lookup (cleanedHostRuntimeUri.toString());
				}
				catch (Exception e)
				{
					logError ("Failed to obtain reference for \"" + cleanedHostRuntimeUri.toString () + "\".");
					e.printStackTrace ();
					break;
				}
				
				// Remove the context.
				try
				{
					cleanedHostRuntimeInterface.deleteContext (contextId);
				}
				catch (HostRuntimeException e) { logError ("Deleting of context on \"" + cleanedHostRuntime + "\" failed: " + e.getMessage()); }
				catch (RemoteException e) { logError("Remote exception when deleting context data: " + e.getMessage()); }
			}
		}

		// Remove the context from the log storage.
		try
		{
			logStorage.removeContext (contextId);
		}
		catch (LogStorageException e) { System.err.println ("Cannot remove context from the log storage: " + e.getMessage()); }
		
		// Finally forget the context data.
		this.data.delContextByForce(contextId);
	}

	/**
	 * Notification from Host Runtime, that specified task was restarted.
	 * 
	 * @param taskId ID of task.
	 * @param contextId ID of context.
	 * @throws RemoteException If something failed during the execution of
	 * 	the remote method call.
	 */
	public void taskRestarted(String taskId, String contextId)
	throws RemoteException {
		data.notifyTaskRestarted(taskId, contextId);
	}
			
	/**
	 * Used by Host Runtime for notify of task's reached state.
	 * 
	 * TODO Too much happens synchronously in this method, given that it is a callback.
	 * It would be better to move some of the cleanup stuff to an asynchronous handler.
	 * 
	 * @param taskId The identifier of the task.
	 * @param state The final state of the task.
	 * @throws RemoteException If something failed during the execution of the remote method call.
	 */
	public void taskReachedEnd(String taskId, String contextId, TaskState state) throws RemoteException
	{
		// This method should really be called only on finished or aborted tasks.
		// Failure to observe this rule used to be ignored silently before.
		if (state != TaskState.FINISHED && state != TaskState.ABORTED)
		{
			throw new AssertionError ("Task end notification called with invalid task state.");
		}
		
		final String hostName;
		final TaskEntry taskEntry;
	
		
		/* List of finished task that should be removed  by their hostruntimes because of their context
		 * limits number of finished tasks kept. This list is here because these tasks will be removed
		 * from task manager's data structures inside synchronized section and it would be dangerous
		 * to call hostruntimes inside the synchronized section, so they'll be removed later. 
		 */
		List<TaskEntry> hostruntimeTaskRemovalQueue = new ArrayList<TaskEntry>();
		
		synchronized (data) {
			
			// If this task registered any services, unregister them.
			unregisterFinishedService(contextId, taskId);
			
			// Update the state of the task in the task database.
			data.changeTaskState(taskId, contextId, state);

			// A task could have been aborted without setting its final checkpoint.
			// In case of abort, the checkpoint is therefore set by the task manager.
			if (state == TaskState.ABORTED) {
				checkPointReached(new CheckPoint(
						taskId,
						contextId,
						Task.CHECKPOINT_NAME_FINISHED,
						String.valueOf (Task.EXIT_CODE_ERROR)));
			}
			
			// Remove the reservation of the host runtime capacity for the task.
			taskEntry = data.getTaskById(taskId, contextId);
			final TaskExclusivity taskExclusivity = taskEntry.getExclusivity();
			
			hostName = taskEntry.getHostName();
			
			if (hostName != null) {
				switch (taskExclusivity) {
					case CONTEXT_EXCLUSIVE:
						// Check if there is no other exclusive task running or sleeping on the host.
						// If there is such, preserve host runtime reservation, otherwise drop it.
						unset_exclusivity: {
							TaskEntry[] tasksOnHost = data.getTasksOnHost(hostName);
							for (TaskEntry taskOnHost : tasksOnHost) {
								TaskState taskState = taskOnHost.getState();
								if ((taskState == TaskState.RUNNING) || (taskState == TaskState.SLEEPING)) {
									TaskExclusivity anotherTaskExclusivity = taskOnHost.getExclusivity();
									if (anotherTaskExclusivity != TaskExclusivity.NON_EXCLUSIVE) {
										break unset_exclusivity;
									}
								}
							}
							data.changeHostRuntimeReservation(hostName, null);
						}
						break;
					case EXCLUSIVE:
						// If this task was exclusive, we can be sure no other exclusive task is running.
						data.changeHostRuntimeReservation(hostName, null);
						break;
					default:
						// No work needs to be done.
						break;
				}
			}

			// Notify all listeners about task finish.
			for (TaskEventListener listener : taskListeners) {
				listener.TaskFinished(taskId, contextId);
			}
		
			// Contexts that execute infinitely long have a limit on the number of finished tasks that are kept around.
			// If this is one of those contexts, populate the list of tasks that will need to be removed.
			// Removal is done later to make sure it is not inside synchronized block.
			ContextEntry contextEntry = data.getContextById(contextId);
			if (contextEntry != null && contextEntry.getFinishedTasksKept() >= 0) {
				
				int finishedTasksKept = contextEntry.getFinishedTasksKept();
				
				List<TaskEntry> finishedTasks = new ArrayList<TaskEntry>();
				TaskEntry[] tasks = data.getTasksInContext(contextId);
				for (TaskEntry task : tasks) {
					if (TaskState.FINISHED.equals(task.getState())) {
						finishedTasks.add(task);
					}
				}

				// TODO It would be nice if the tasks were removed in the order of their completion time.

				/* check whether we have too many finished tasks in context */
				if (finishedTasksKept < finishedTasks.size()) {
					/* clean some tasks */
					int cleanCount = finishedTasks.size() - finishedTasksKept;
					
					/* put finished tasks to remove into queue, they'll
					 * be removed outside of synchronized block 
					 */
					for (int i = 0; i < cleanCount; i++) {
						/* remove task from taskmanagager here
						 * and postpone removal from hostruntime 
						 * until later */ 
						 
						TaskEntry toRemove = finishedTasks.get(i);
						hostruntimeTaskRemovalQueue.add( toRemove );
						data.delTask(toRemove.getTaskId(), toRemove.getContextId());
					}
				}
			}
		}

		// Remove reservation of load units on the host runtime.
		if (hostName != null)
		{
			final HostRuntimeEntry hostRuntimeEntry = data.getHostRuntimeByName(hostName);
			final TaskDescriptor taskDescriptor = taskEntry.getModifiedTaskDescriptor();
			hostRuntimeEntry.removeLoad(taskDescriptor.isSetLoadMonitoring() ? taskDescriptor.getLoadMonitoring().getLoadUnits() : DEFAULT_LOAD_UNITS);
		}
		
		// Rescan all dependencies and run tasks that can run.
		runAllReadyTasks();
		
		// The termination of a task could result in the context being finished (closed and nothing running).
		// There is a limit on the number of finished contexts kept around, contexts above that are removed.
		if (isContextFinished(contextId))
		{
			synchronized (keptClosedContextsSync)
			{
				if (!keptClosedContextsSync.contains (contextId)) keptClosedContextsSync.add (contextId);
				while (keptClosedContextsSync.size () > keptClosedContextCount)
				{
					String removedContextId = keptClosedContextsSync.remove (0);
					try { deleteInactiveContext (removedContextId); }
					catch (IllegalArgumentException e) {
						// Probably means the context was already deleted.
					}
				}
			}
		}
		
		// If we have created a list of tasks tasks to be removed from contexts that execute infinitely long,
		// now is the time to remove the tasks. The removal includes deleting the tasks from the host runtime. 
		for (TaskEntry removedTaskEntry : hostruntimeTaskRemovalQueue)
		{
			String removedTaskId = removedTaskEntry.getTaskId ();
			String removedContextId = removedTaskEntry.getContextId ();
			
			// TODO It is a bit illogical to have task entry and task data and search for one using the other.
			TaskData removedTaskData = data.getTaskData (removedTaskId, removedContextId);
			try
			{
				TaskInterface removedTaskInterface = removedTaskData.getTaskInterface ();
				if (removedTaskInterface != null)
				{
					removedTaskInterface.destroy ();
					logInfo ("Task ["+ removedContextId + ":" + removedTaskId + "] removed because finished task count limit has been reached.");
				}
			}
			catch (TaskException e)
			{
				logError("Error removing task ["+ removedContextId + ":" + removedTaskId + "] from its hostruntime.");
				e.printStackTrace();
			}
			catch (RemoteException e) {
				logError("Error removing task ["+ removedContextId + ":" + removedTaskId + "] from its hostruntime.");
				e.printStackTrace();
			}
			
			try {
				// Also remove the task entries from the log storage.
				logStorage.removeTask (removedContextId, removedTaskId);
				logInfo("Task ["+ removedContextId + ":" + removedTaskId + "] removed from log storage because finished task count limit has been reached.");
			} catch (LogStorageException e) {					
				logError("Error removing log storage for task ["+ removedContextId + ":" + removedTaskId + "].");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * For specified taskDescriptor prepare its list of usable hostRuntimes
	 * (based on RSL or asTask attribute) if needed.
	 * 
	 * @param taskDescriptor TaskDescriptor to process.
	 * @param hostManager A reference to the Host Manager.
	 * @throws TaskManagerException When no host specification is found in the task descriptor.
	 */
	private void prepareHostNames(TaskDescriptor taskDescriptor, HostManagerInterface hostManager)
	throws TaskManagerException {
		final List<String> hostNames;
		final HostRuntimes hostRuntimes = taskDescriptor.getHostRuntimes();							// Should be always set.
		if (hostRuntimes.isSetAsTask()) {															// First look at asTask.
			TaskEntry determiningTask = data.getTaskById(
				hostRuntimes.getAsTask(),
				taskDescriptor.getContextId()
			);
			hostNames = hostRuntimes.getName();
			hostNames.clear();																		// Start with a blank list.
			if (null != determiningTask) {															// This should ALWAYS hold...
				String hostName = determiningTask.getHostName();									// May be null! (Not run yet.)
				if (null != hostName) {
					hostNames.add(hostName);														// ...thanks to the dependency.
				}
			}
		} else if (hostRuntimes.isSetRSL()) {														// Then look at RSL.
			if (null == hostManager) {																// Hack for HM-less operation.
				throw new TaskManagerException("You need to run Host Manager for RSL support");
			}
			
			final RestrictionInterface[] restrictionInterfaces = new RestrictionInterface[] {
				new RSLRestriction(hostRuntimes.getRSL())
			};
			hostNames = hostRuntimes.getName();
			hostNames.clear();																		// Start with a blank list.
			try {
				HostInfoInterface[] hostInfoInterfaces = hostManager.queryHosts(					// Make a RSL query.
					restrictionInterfaces
				);
				for (HostInfoInterface hii : hostInfoInterfaces) {									// Set matching host names.
					hostNames.add(hii.getHostName());
				}
			} catch (RemoteException exception) {
				logError("Could not contact the Host Manager: " + exception.getMessage());			// OK, returns empty list.
			} catch (HostManagerException exception) {
				logError(
					"Could not resolve host names RSL (RSL '" + hostRuntimes.getRSL() + "'): " +
					exception.getMessage()
				);
				throw new TaskManagerException("Could not resolve host names RSL.", exception);		// Fatal. No way to recover!
			}
		} else if (hostRuntimes.isSetName()) {														// OK, nothing to do.
		} else {																					// This will NEVER happen...
			throw new IllegalArgumentException("Task descriptor without host specification");		// ...if called after validate().
		}
	}
	
	/**
	 * For specified taskDescriptor prepare its definition of package (based on
	 * RSL) if needed.
	 * 
	 * @param taskDescriptor TaskDescriptor to process.
	 * @throws TaskManagerException if anything goes wrong.
	 */
	private void preparePackageName(TaskDescriptor taskDescriptor) 
	throws TaskManagerException {
		Package pacKage;
		
		pacKage = taskDescriptor.getPackage();														// Always set.
		if (pacKage.isSetRSL()) {
			RSLPackageQueryCallback packageQueryCallback =
				new RSLPackageQueryCallback(pacKage.getRSL());
			
			try {
				SoftwareRepositoryInterface softwareRepositoryInterface =
					(SoftwareRepositoryInterface) serviceFind(
						SoftwareRepositoryService.SERVICE_NAME,
						Service.RMI_MAIN_IFACE
					);
				if (softwareRepositoryInterface == null) {
					throw new TaskManagerException("Software Repository not found");
				}
				
				PackageMetadata[] metadata =
					softwareRepositoryInterface.queryPackages(packageQueryCallback);
				
				/* Set taskDescriptor. */
				// use only the first... */
				if (metadata.length < 1) {
					throw new IllegalArgumentException(
						"Name of package not found: " + pacKage.getRSL()
					);
				}
				pacKage.setName((metadata[0].getFilename()));
			} catch (RemoteException e) {
				throw new TaskManagerException ("Could not find Software Repository service", e);
			} catch (MatchException e) {
				throw new IllegalArgumentException("Could not get package names", e);
			}
		}
	}
	
	/**
	 * Starts execution of specified task.
	 * 
	 * @param taskData Task to start.
	 * @throws TaskManagerException CAUTION! Thrown on non-fatal problems here, unlike other places.
	 */
	private void taskStart(TaskData taskData) throws TaskManagerException {
		URI hostRuntimeUri;
		HostRuntimeInterface hostRuntime;
		TaskDescriptor taskDescriptor = taskData.getTaskDescriptor();
		
		/* Get hostName. */
		String hostName;
		try {
			List<String> hostNames = taskDescriptor.getHostRuntimes().getName();
			hostName = MiscUtils.getCanonicalHostName(hostNames.get(0));							// Resolve once more. (?)
		} catch (UnknownHostException e) {
			throw new TaskManagerException("Getting of canonical host name failed", e);
		}
		
		if (data.getHostRuntimeByName(hostName) == null) {
			throw new TaskManagerException("Host runtime not registered: " + hostName);
		}
		
		try {
			hostRuntimeUri = new URI("rmi", null, hostName,
					RMI.REGISTRY_PORT,
					HostRuntimeInterface.URL, null, null);
		} catch (URISyntaxException e) {
			throw new TaskManagerException("Could not construct URI of Host Runtime");
		}
		
		try {
			hostRuntime = (HostRuntimeInterface)
				Naming.lookup(hostRuntimeUri.toString());
		} catch (Exception e) {
			throw new TaskManagerException("Could not connect to required Host "
				+ "Runtime (URI=\"" + hostRuntimeUri.toString() + "\")");
		}
		
		try {
			String taskId = taskData.getTaskDescriptor().getTaskId();
			String contextId = taskData.getTaskDescriptor().getContextId();
			
			/* Add task to logStorage. */
			logStorage.setTaskHostname(contextId, taskId, hostName);
			
			/* Initialise hostRuntime. */
			hostRuntime.initialize(this, maxPackageCacheSize);
			
			synchronized (data) {
				/* Set task's state. */
				data.changeTaskState(taskId, contextId, TaskState.RUNNING);
				
				/* Run task on Host Runtime. */
				taskData.setTaskInterface(hostRuntime.createTask(taskData.getTaskDescriptor()));
			
				/* Set HostRuntime for running task. */
				data.setTaskHostRuntime(taskId, contextId, hostName);
				
				/* Set reservation of Host Runtime if needed. */
				TaskExclusivity taskExclusivity = taskData.getTaskDescriptor().getExclusive();
				switch (taskExclusivity) {
					case CONTEXT_EXCLUSIVE:
						data.changeHostRuntimeReservation(hostName, contextId);
						break;
						
					case EXCLUSIVE:
						data.changeHostRuntimeReservation(hostName, "");
						break;
						
					default:
						break;  // No work needs to be done...
				}
			}
			
			/* Set task's directory paths. */
			String taskDirectory
				= taskData.getTaskInterface().getTaskDirectory();
			String workingDirectory
				= taskData.getTaskInterface().getWorkingDirectory();
			String temporaryDirectory
				= taskData.getTaskInterface().getTemporaryDirectory();
			data.setTaskDirectories(taskId, contextId,
					taskDirectory, workingDirectory, temporaryDirectory);
			
			logTrace("[" + taskData.getTaskDescriptor().getContextId()
					+ ":" + taskData.getTaskDescriptor().getTaskId() + "] "
					+ "Started.");
		} catch (Exception e) {
			throw new TaskManagerException(e);
		}
	}
	
	/**
	 * Tests if specified dependencyCheckpoint was already reached.
	 * 
	 * @param dependencyCheckpoint Tested CheckPoint.
	 * @return True if CheckPoint was already reached, false otherwise.
	 * @throws TaskManagerException When task dependency deserialization fails.
	 */
	private boolean checkDependency(DependencyCheckPoint dependencyCheckpoint, String contextId)
	throws TaskManagerException {
		final String type = dependencyCheckpoint.getType();
		final String taskId = dependencyCheckpoint.getTaskId();
		final Serializable magicObject;
		try {
			if (dependencyCheckpoint.isSetBinVal()) {
				magicObject = Deserialize.fromBase64(dependencyCheckpoint.getBinVal());
			} else if (dependencyCheckpoint.isSetStrVal()) {
				magicObject = Deserialize.fromString(dependencyCheckpoint.getStrVal());
			} else if (dependencyCheckpoint.isSetValue()) {
				magicObject = dependencyCheckpoint.getValue();
			} else {
				magicObject = null;
			}
		} catch (DeserializeException exception) {
			throw new TaskManagerException("Could not deserialize dependency", exception);
		}
		CheckPointEntry[] checkPointEntries =
			data.getCheckPoints(type, taskId, contextId, magicObject);
		
		return checkPointEntries.length > 0;
	}
	
	/**
	 * Tests if all CheckPoints on which this task depends was already
	 * reached.
	 * 
	 * @param taskData Task to test.
	 * @return True if all dependencies were already reached, false
	 * 	otherwise.
	 * @throws TaskManagerException 
	 */
	private boolean checkDependencies(TaskData taskData) throws TaskManagerException {
		final TaskDescriptor taskDescriptor = taskData.getTaskDescriptor();
		final String contextId = taskDescriptor.getContextId();
		
		if (taskDescriptor.isSetDependencies()) {
			Dependencies dependencies = taskDescriptor.getDependencies();
			if (dependencies.isSetDependencyCheckPoint()) {
				for (DependencyCheckPoint dcp : dependencies.getDependencyCheckPoint()) {
					if (!checkDependency(dcp, contextId)) {
						logTrace("[" + contextId
							+ ":" + taskDescriptor.getTaskId()
							+ "] Testing dependencies: failed.");
						return false;
					}
				}
			}
		}
		logTrace("[" + contextId
				+ ":" + taskDescriptor.getTaskId()
				+ "] Testing dependencies: succeded.");
		return true;
	}
	
	/**
	 * Adds new task to list of tasks and starts it.
	 * 
	 * @param modifiedTaskDescriptor A scratch task descriptor that can be modified.
	 * @param originalTaskDescriptor A read-only task descriptor to keep the original for reference.
	 * @throws RemoteException Almost never. Should manipulate local objects only!
	 * @throws IllegalArgumentException If taskDescriptor is not valid.
	 */
	private boolean taskNew(TaskDescriptor modifiedTaskDescriptor, TaskDescriptor originalTaskDescriptor)
	throws TaskManagerException, RemoteException {
		StringBuilder validateLog = TaskDescriptorHelper.validate(modifiedTaskDescriptor);			// Validate TaskDescriptor.
		if (0 < validateLog.length()) {
			throw new IllegalArgumentException(
				"Task descriptor is not valid:\n" + validateLog.toString()
			);
		}
		
		final String taskId = modifiedTaskDescriptor.getTaskId();
		final String contextId = modifiedTaskDescriptor.getContextId();
		
		if (contextId.equals(SYSTEM_CONTEXT_ID) && taskId.equals(TASKMANAGER_TASKNAME)) {
			throw new TaskManagerException(
				"Task ID " + TASKMANAGER_TASKNAME +
				" in the context " + SYSTEM_CONTEXT_ID + " is reserved"
			);
		}

		/* Test if task already exists. */
		final TaskEntry foundTaskEntry = data.getTaskById(taskId, contextId);
		synchronized (data) {
			if (foundTaskEntry != null) {
				if (
					(foundTaskEntry.getState() == TaskState.FINISHED) ||
					(foundTaskEntry.getState() == TaskState.ABORTED)
				) {
					data.delTask(taskId, contextId);												// Remove task when needed.
					// taskTree.clearInclusive(foundTaskEntry.getTreeAddress());					// Not here! delTask() does it.
				}																					// LogStorage: logs appended.
			}

			/* Prepare packageName. */
			preparePackageName(modifiedTaskDescriptor);

			/* Prepare TaskEntry. */
			TaskEntry taskEntry = new TaskEntry(
				taskTree.addressFromPath(modifiedTaskDescriptor.getTreeAddress()),
				modifiedTaskDescriptor,
				originalTaskDescriptor
			);

			// TODO: Beginning of an ugly hack.
			/* ABORTED/FINISHED doesn't seem to be set on manually terminated system tasks. */
			if (TaskManagerInterface.SYSTEM_CONTEXT_ID.equals(contextId)) {							// Is this a system task?
				try {
					taskTree.clearInclusive(taskEntry.getTreeAddress());							// Then it can be re-inserted.
				} catch (IllegalAddressException exception) {
					// IGNORE!!!
				} catch (RemoteException exception) {
					assert false : "RemoteException from a local call.";
				}
			}
			// TODO: End of an ugly hack.
			
			/* Try the task tree stuff. This can throw an exception on tree misuse. */
			taskTree.addLeaf(taskEntry.getTreeAddress(), taskEntry);
			
			/* Prepare TaskData. */
			TaskData taskData = new TaskData(modifiedTaskDescriptor);

			/* Add new task to TM's Data. */
			data.newTask(taskEntry, taskData);

			/* Add new task to log storage. */
			try {
				logStorage.addTask(contextId, taskId);
			} catch (LogStorageException e) {
				throw new TaskManagerException("Cannot register the task in the log storage", e);
			}

			return runTaskIfReady(taskEntry, taskData);
		}
	}


//	/**
//	 * Starts main loop of Task Manager.
//	 * 
//	 * @param taskDescriptors List of task descriptor files.
//	 */
//	private void run(String[] taskDescriptors) {
//		try {
//			runTask(taskDescriptors);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * Returns RMI interface of hostRuntime based on hostName.
	 * 
	 * @param hostName Name of host running hostRuntime.
	 * @return RMI interface of hostRuntime.
	 */
	private HostRuntimeInterface getHostRuntimeInterface(String hostName) {
		URI hostRuntimeUri;
		
		try {
			hostRuntimeUri = new URI("rmi", null, hostName,
					RMI.REGISTRY_PORT,
					HostRuntimeInterface.URL, null, null);
		} catch (URISyntaxException e) {
			logError("Could not construct URI of Host Runtime.");
			return null;
		}
		
		HostRuntimeInterface hostRuntimeInterface;
		
		try {
			hostRuntimeInterface = (HostRuntimeInterface)
				Naming.lookup(hostRuntimeUri.toString());
		} catch (Exception e) {
			logError("Could not connect to required Host "
				+ "Runtime (URI=\"" + hostRuntimeUri.toString() + "\").");
			return null;
		}
		
		return hostRuntimeInterface;
	}
	
	/**
	 * Returns the size limit of the Host Runtime's package cache.
	 * 
	 * @return size limit of the Host Runtime's package cache.
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call.
	 */
	public long getMaxPackageCacheSize() throws RemoteException {
		return maxPackageCacheSize;
	}
	
	/**
	 * Set MaxPackageCacheSize option for hostRuntimes.
	 * 
	 * @param maxPackageCacheSize New value for maxPackageCacheSize option.
	 */
	public void setMaxPackageCacheSize(long maxPackageCacheSize) throws RemoteException {
		this.maxPackageCacheSize = maxPackageCacheSize;
		storeConfiguration(configurationFile);
		
		/* Distribute to all known hostRuntimes. */
		HostRuntimeEntry[] hostRuntimes = data.getHostRuntimes();
		for (HostRuntimeEntry hostRuntime : hostRuntimes) {
			String hostName = hostRuntime.getHostName();
			HostRuntimeInterface hostRuntimeInterface
				= getHostRuntimeInterface(hostName);
			if (hostRuntimeInterface != null) {
				hostRuntimeInterface
					.setMaxPackageCacheSize(maxPackageCacheSize);
			}
		}
	}
	
	/**
	 * Returns the number of closed contexts, for which the Host Runtime should
	 * keep data on the disk
	 * 
	 * @return number of closed contexts, for which the Host Runtime should keep
	 *          data on the disk
	 * @throws RemoteException if something failed during the execution of
	 *          the remote method call.
	 */
	public int getKeptClosedContextCount() throws RemoteException {
		return keptClosedContextCount;
	}
	
	/**
	 * Set KeptClosedContextCount option for the task manager.
	 * 
	 * @param keptClosedContextCount New value for keptClosedContextCount option.
	 */
	public void setKeptClosedContextCount(int keptClosedContextCount) throws RemoteException {
		this.keptClosedContextCount = keptClosedContextCount;
		storeConfiguration(configurationFile);
	}

	/**
	 * @see cz.cuni.mff.been.taskmanager.HostRuntimesPortInterface#getTaskManager()
	 */
	public TaskManagerInterface getTaskManager() throws RemoteException {
		return this;
	}

	/**
	 * Searches the service registry and if it finds a service with the same 
	 * task ID and context ID, it unregisters it.
	 * 
	 * @param contextId
	 * @param taskId
	 */
	private void unregisterFinishedService(String contextId, String taskId) 
	throws RemoteException {
		ServiceEntry template = new ServiceEntry();
		template.setContextId(contextId);
		template.setTaskId(taskId);
		ServiceEntry[] registeredServices = serviceLook(template);
		if (registeredServices.length > 0) {
			for (ServiceEntry entry : registeredServices) {
				serviceRegistry.remove(entry);
			}
		}
	}
	
	/**
	 * Logs a log message.
	 * 
	 * @param level log level of the log message.
	 * @param message log message.
	 */
	private void log(LogLevel level, String message) {
		if (!level.isGreaterOrEqual(logLevel)) {
			return;
		}
		
		Date timestamp = new Date(System.currentTimeMillis());

		SimpleDateFormat format = 
			(SimpleDateFormat) DateFormat.getDateTimeInstance();
		format.applyPattern("dd.MM.yyyy HH:mm:ss.SSS");

		System.out.println(level + " " + message);

		try {
			logStorage.log(SYSTEM_CONTEXT_ID, 
					TASKMANAGER_TASKNAME, 
					timestamp,
					level,
					message);
		} catch (Exception e) {
			System.err.println("Unable to store log message: "
					+ e.getMessage());
		}
	}
	
	/**
	 * Logs a log message with the FATAL log level.
	 *  
	 * @param message log message.
	 * @throws TaskManagerException if something goes wrong.
	 */
	private void logFatal(String message) {
		log(LogLevel.FATAL, message);
	}

	/**
	 * Logs a log message with the ERROR log level.
	 *  
	 * @param message log message.
	 * @throws TaskManagerException if something goes wrong.
	 */
	private void logError(String message) {
		log(LogLevel.ERROR, message);
	}

	/**
	 * Logs a log message with the WARN log level.
	 *  
	 * @param message log message.
	 * @throws TaskManagerException if something goes wrong.
	 */
	private void logWarning(String message) {
		log(LogLevel.WARN, message);
	}

	/**
	 * Logs a log message with the INFO log level.
	 *  
	 * @param message log message.
	 * @throws TaskManagerException if something goes wrong.
	 */
	private void logInfo(String message) {
		log(LogLevel.INFO, message);
	}

	/**
	 * Logs a log message with the DEBUG log level.
	 *  
	 * @param message log message.
	 * @throws TaskManagerException if something goes wrong.
	 */
	private void logDebug(String message) {
		log(LogLevel.DEBUG, message);
	}

	/**
	 * Logs a log message with the TRACE log level.
	 *  
	 * @param message log message.
	 * @throws TaskManagerException if something goes wrong.
	 */
	private void logTrace(String message) {
		log(LogLevel.TRACE, message);
	}

	@Override
	public synchronized void registerEventListener(HostRuntimeRegistrationListener listener)
	throws RemoteException {
		if (listener == null) {
			throw new NullPointerException("Listener is null");
		}
		if (registrationListeners.contains(listener)) {
			throw new IllegalArgumentException("Listener already registered");
		}
		registrationListeners.add(listener);
	}

	@Override
	public synchronized void registerEventListener(TaskEventListener listener)
	throws RemoteException {
		if (listener == null) {
			throw new NullPointerException("Listener is null");
		}
		if (taskListeners.contains(listener)) {
			throw new IllegalArgumentException("Listener already registered");
		}
		taskListeners.add(listener);
	}

	@Override
	public synchronized void registerHostRuntime(String hostname) throws RemoteException {
		if (hostname == null) {
			throw new NullPointerException("Hostname is null");
		}
		
		String canonicalHostName; 
		try {
			canonicalHostName = InetAddress.getByName(hostname)
					.getCanonicalHostName();
		} catch (UnknownHostException e) {
			throw new RemoteException("Getting of canonical host name failed",
					e);
		}
		
		HostRuntimeEntry hostRuntime = new HostRuntimeEntry(canonicalHostName);
		data.addHostRuntime(hostRuntime);
		
		for (HostRuntimeRegistrationListener listener : registrationListeners) {
			listener.hostRuntimeRegistered(hostname);
		}
		
		logInfo("Host Runtime registered for host: " + hostname);
	}

	@Override
	public synchronized void unregisterEventListener(HostRuntimeRegistrationListener listener)
	throws RemoteException {
		if (listener == null) {
			throw new NullPointerException("Listener is null");
		}
		if (!registrationListeners.remove(listener)) {
			throw new IllegalArgumentException("Listener not registered");
		}
	}

	@Override
	public synchronized void unregisterEventListener(TaskEventListener listener)
	throws RemoteException {
		if (listener == null) {
			throw new NullPointerException("Listener is null");
		}
		if (!taskListeners.remove(listener)) {
			throw new IllegalArgumentException("Listener not registered");
		}
	}

	@Override
	public synchronized void unregisterHostRuntime(String hostName) throws RemoteException {
		if (hostName == null) {
			throw new NullPointerException("Hostname is null");
		}
		
		String canonicalHostName; 
		try {
			canonicalHostName = InetAddress.getByName(hostName)
					.getCanonicalHostName();
		} catch (UnknownHostException e) {
			throw new RemoteException("Getting of canonical host name failed",
					e);
		}
		
		/* Abort all non-finished tasks. */
		TaskEntry[] tasks = data.getTasksOnHost(hostName);
		for (TaskEntry task : tasks) {
			TaskState taskState = task.getState();
			if ((taskState != TaskState.FINISHED)
					&& (taskState != TaskState.ABORTED)) {
				taskReachedEnd(task.getTaskId(), task.getContextId(),
						TaskState.ABORTED);
			}
		}
		
		data.removeHostRuntime(canonicalHostName);
		
		for (HostRuntimeRegistrationListener listener : registrationListeners) {
			listener.hostRuntimeUnregistered(hostName);
		}
		
		logInfo("Host Runtime unregistered for host: " + hostName);
	}

	@Override
	public String[] getRegisteredHostRuntimes() throws RemoteException {
		HostRuntimeEntry[] hostRuntimes = data.getHostRuntimes();
		String[] hostnames = new String[hostRuntimes.length];
		for (int i = 0; i < hostRuntimes.length; i++) {
			hostnames[i] = hostRuntimes[i].getHostName();
		}
		return hostnames;
	}

	@Override
	public long getLogCountForTask(String context, String taskID)
	throws LogStorageException, IllegalArgumentException, NullPointerException {
		return logStorage.getLogCountForTask(context, taskID);
	}

	@Override
	public LogRecord[] getLogsForTask(String context, String taskID, long first, long last)
	throws LogStorageException, IllegalArgumentException, NullPointerException {
		return logStorage.getLogsForTask(context, taskID, first, last);
	}

	@Override
	public TaskTreeInput getTaskTreeInput() {
		return taskTree;
	}

	@Override
	public TaskTreeQuery getTaskTreeQuery() {
		return taskTreeReader;
	}

	@Override
	public String getUniqueTaskID() throws RemoteException {
		lastTaskNumber++;
		Calendar now = Calendar.getInstance();
		StringBuilder result = new StringBuilder();
		result.append(lastTaskNumber);
		result.append(now.get(Calendar.YEAR));
		result.append(now.get(Calendar.MONTH));
		result.append(now.get(Calendar.DAY_OF_MONTH));
		result.append(now.get(Calendar.HOUR_OF_DAY));
		result.append(now.get(Calendar.MINUTE));
		result.append(now.get(Calendar.SECOND));
		return result.toString();
	}

	/*
	 * Andrej's Task Manager Extensions. The following methods ared meant to be a temporary
	 * workaround to avoid multiple RMI calls when using TaskTreeAddress to access tasks.
	 * The final goal is to remove contexts completely and use the task tree only. The task
	 * tree is more flexible than contexts. It can do all that contexts can do plus much more.
	 */
	
	/**
	 * {@inheritDoc} TODO: This is an ugly temporary implementation. It will certainly go away
	 * once the Task Manager and the rest of BEEN gets integrated with the new Task Tree
	 * in a more reasonable way. The Log Storage must be reimplemented to include a directory
	 * structure based on the task tree instead of contexts.
	 */
	@Override
	public OutputHandle getErrorOutput( TaskTreeAddress address )
	throws RemoteException, LogStorageException, IllegalAddressException {
		TaskEntry entry;
		
		entry = taskTreeReader.getTaskAt( address );
		return logStorage.getErrorOutput( entry.getContextId(), entry.getTaskId() );
	}

	/**
	 * {@inheritDoc} TODO: This is an ugly temporary implementation. It will certainly go away
	 * once the Task Manager and the rest of BEEN gets integrated with the new Task Tree
	 * in a more reasonable way. The Log Storage must be reimplemented to include a directory
	 * structure based on the task tree instead of contexts.
	 */
	@Override
	public long getLogCountForTask( TaskTreeAddress address )
	throws RemoteException, LogStorageException, IllegalAddressException {
		TaskEntry entry;
		
		entry = taskTreeReader.getTaskAt( address );
		return logStorage.getLogCountForTask( entry.getContextId(), entry.getTaskId() );
	}

	/**
	 * {@inheritDoc} TODO: This is an ugly temporary implementation. It will certainly go away
	 * once the Task Manager and the rest of BEEN gets integrated with the new Task Tree
	 * in a more reasonable way. The Log Storage must be reimplemented to include a directory
	 * structure based on the task tree instead of contexts.
	 */
	@Override
	public LogRecord[] getLogsForTask( TaskTreeAddress address )
	throws RemoteException, LogStorageException, IllegalAddressException {
		TaskEntry entry;
		
		entry = taskTreeReader.getTaskAt( address );
		return logStorage.getLogsForTask( entry.getContextId(), entry.getTaskId() );
	}

	/**
	 * {@inheritDoc} TODO: This is an ugly temporary implementation. It will certainly go away
	 * once the Task Manager and the rest of BEEN gets integrated with the new Task Tree
	 * in a more reasonable way. The Log Storage must be reimplemented to include a directory
	 * structure based on the task tree instead of contexts.
	 */
	@Override
	public LogRecord[] getLogsForTask( TaskTreeAddress address, long first, long last )
	throws RemoteException, LogStorageException, IllegalAddressException {
		TaskEntry entry;
		
		entry = taskTreeReader.getTaskAt( address );
		return logStorage.getLogsForTask( entry.getContextId(), entry.getTaskId(), first, last );
	}

	/**
	 * {@inheritDoc} TODO: This is an ugly temporary implementation. It will certainly go away
	 * once the Task Manager and the rest of BEEN gets integrated with the new Task Tree
	 * in a more reasonable way. The Log Storage must be reimplemented to include a directory
	 * structure based on the task tree instead of contexts.
	 */
	@Override
	public OutputHandle getStandardOutput( TaskTreeAddress address )
	throws RemoteException, LogStorageException, IllegalAddressException {
		TaskEntry entry;
		
		entry = taskTreeReader.getTaskAt( address );
		return logStorage.getStandardOutput( entry.getContextId(), entry.getTaskId() );
	}

	/**
	 * {@inheritDoc} TODO: This is an ugly temporary implementation. It will certainly go away
	 * once the Task Manager and the rest of BEEN gets integrated with the new Task Tree
	 * in a more reasonable way. The Log Storage must be reimplemented to include a directory
	 * structure based on the task tree instead of contexts.
	 */
	@Override
	public boolean isTaskRegistered( TaskTreeAddress address )
	throws RemoteException, LogStorageException, IllegalAddressException {
		TaskEntry entry;
		
		entry = taskTreeReader.getTaskAt( address );
		return logStorage.isTaskRegistered( entry.getContextId(), entry.getTaskId() );
	}

	/**
	 * {@inheritDoc} TODO: This is an ugly temporary implementation. It will certainly go away
	 * once the Task Manager and the rest of BEEN gets integrated with the new Task Tree
	 * in a more reasonable way.
	 */
	@Override
	public void killNodeByAddress( TaskTreeAddress address )
	throws RemoteException, IllegalAddressException {
		killRecursive( address );
	}
	
	/**
	 * {@inheritDoc} TODO: This is an ugly temporary implementation. It will certainly go away
	 * once the Task Manager and the rest of BEEN gets integrated with the new Task Tree
	 * in a more reasonable way.
	 */
	@Override
	public void deleteNodeByAddress( TaskTreeAddress address )
	throws RemoteException, IllegalAddressException {
		deleteRecursive( address );
	}

	/**
	 * {@inheritDoc} TODO: This is an ugly temporary implementation. It will certainly go away
	 * once the Task Manager and the rest of BEEN gets integrated with the new Task Tree
	 * in a more reasonable way. The Log Storage must be reimplemented to include a directory
	 * structure based on the task tree instead of contexts.
	 */
	@Override
	public void killTaskByAddress( TaskTreeAddress address )
	throws RemoteException, IllegalAddressException {
		TaskEntry entry;
		
		entry = taskTreeReader.getTaskAt( address );
		killTaskById( entry.getTaskId(), entry.getContextId() );
	}
	
	/**
	 * {@inheritDoc} TODO: This is an ugly temporary implementation. It will certainly go away
	 * once the Task Manager and the rest of BEEN gets integrated with the new Task Tree
	 * in a more reasonable way. The Log Storage must be reimplemented to include a directory
	 * structure based on the task tree instead of contexts.
	 */
	@Override
	public void deleteTaskByAddress( TaskTreeAddress address )
	throws RemoteException, IllegalAddressException {
		TaskEntry entry;
		
		entry = taskTreeReader.getTaskAt( address );
		killTaskById( entry.getTaskId(), entry.getContextId() );
		data.delTask( entry.getTaskId(), entry.getContextId() );
	}
	
	/**
	 * Kills all tasks from a tree node recursively. Added by Andrej as a temporary workaround.
	 * The final goal is to remove contexts and migrate everything to the task tree structure.
	 * 
	 * @param address The tree node or tree leaf to kill recursively.
	 * @throws IOException When it rains.
	 */
	private void killRecursive( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException {
		TaskTreeRecord record;
		TaskEntry entry;
		
		record = taskTreeReader.getRecordAt( address, true, true, false );							// If thrown here, it failed.
		switch ( record.getType() ) {
			case LEAF:
				entry = record.getTask();
				try {
					killTaskById( entry.getTaskId(), entry.getContextId() );
				} catch ( IllegalArgumentException exception ) {									// Report, but loop on!
					logWarning(
						"Task '" + entry.getTaskId() +
						"' from context '" + entry.getContextId() +
						"' removed on the fly."
					);
				}						
				break;
			case NODE:
				for ( TaskTreeAddress child : record.getChildren() ) {
					try {
						killRecursive( child );
					} catch ( IllegalAddressException exception ) {									// Report, but loop on!
						logWarning( "Address removed on the fly: " + record.getPathString() );
					}
				}
				break;
		}
	}
	
	/**
	 * Deletes a node identified by address recursively.
	 * 
	 * @param address Address of the node to delete.
	 * @throws IllegalAddressException When the address is unknown or refers to a leaf.
	 * @throws RemoteException When it rains.
	 */
	private void deleteRecursive( TaskTreeAddress address )
	throws IllegalAddressException, RemoteException {
		TaskTreeRecord record;
		TaskEntry entry;
		
		record = taskTreeReader.getRecordAt( address, true, true, false );
		switch ( record.getType() ) {
			case LEAF:
				entry = record.getTask();
				try {
					killTaskById( entry.getTaskId(), entry.getContextId() );
					data.delTask( entry.getTaskId(), entry.getContextId() );						// No clearInc, delTask does it.
				} catch ( IllegalArgumentException exception ) {
					logWarning(
						"Task '" + entry.getTaskId() +
						"' from context '" + entry.getContextId() +
						"' removed on the fly."
					);
				}
				break;
			case NODE:
				for ( TaskTreeAddress child : record.getChildren() ) {
					try {
						deleteRecursive( child );
					} catch ( IllegalAddressException exception ) {
						logWarning( "Address removed on the fly: " + record.getPathString() );		
					}
				}
				taskTree.clearInclusive( address );													// Cleaning an *empty* node.
				break;
		}
	}

	
}
