/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
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
package cz.cuni.mff.been.hostruntime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.hostmanager.IllegalOperationException;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;
import cz.cuni.mff.been.hostmanager.load.LoadMonitorException;
import cz.cuni.mff.been.hostmanager.load.LoadMonitorImplementation;
import cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.softwarerepository.SoftwareRepositoryInterface;
import cz.cuni.mff.been.softwarerepository.SoftwareRepositoryService;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.taskmanager.HostRuntimesPortInterface;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;
import cz.cuni.mff.been.taskmanager.data.TaskState;
import cz.cuni.mff.been.utils.FileUtils;

/**
 * Implementation of Host Runtime.
 * 
 * @author Antonin Tomecek
 * @author David Majda
 */
public class HostRuntimeImplementation extends UnicastRemoteObject implements HostRuntimeInterface {

	private static final Logger logger = LoggerFactory.getLogger(HostRuntimeImplementation.class);

	private static final long serialVersionUID = 1765181516998265721L;

	/** Name of the cache directory. */
	private static final String CACHE_DIR = "cache";

	/** Name of the directory with boot packages. */
	private static final String BOOT_PACKAGES_DIR = "boot";

	/** Name of the directory that contains directories with task data. */
	private static final String TASKS_BASE_DIR = "tasks";

	/** Name of the task directory (task's JAR is extracted there). */
	private static final String TASK_DIR = "task";

	/**
	 * Name of the working directory (results of task's work are stored there,
	 * surviving task's death).
	 */
	private static final String WORKING_DIR = "working";

	/** Name of the temporary directory (deleted after the task's death). */
	private static final String TEMPORARY_DIR = "temporary";

	/** Name of the service directory (package contents are extracted there). */
	private static final String SERVICE_DIR = "service";

	/** Name of the directory with Load Monitor native libraries. */
	private static final String NATIVE_LIB_DIR = "native-lib";

	/** Name of the directory with load data. */
	private static final String LOAD_DIR = "load";

	/**
	 * Debug port that will be assigned to first task run by hostruntime (when in
	 * debug mode)
	 */
	private static final int FIRST_TASK_DEBUG_PORT = 8200;

	/** Counter used for assigning ports to task when running in debug mode */
	private int taskDebugPortCounter = FIRST_TASK_DEBUG_PORT;

	/** RMI interface to the Task Manager. */
	private TaskManagerInterface taskManager;

	/** RMI interface to the Task Manager's port to the Host Runtime. */
	private HostRuntimesPortInterface taskManagerRuntimePort;

	/** Package cache manager. */
	private PackageCacheManager packageCacheManager;

	/** List of tasks running in this Host Runtime. */
	private final List<TaskInterface> runningTasksSync = new LinkedList<TaskInterface>();

	/** Load monitor instance. */
	private LoadMonitorImplementation loadMonitor;

	/** Root directory of this Host Runtime. */
	private String rootDirectory;

	/** @return root directory of this Host Runtime */
	public String getRootDirectory() {
		return rootDirectory;
	}

	/** @return RMI interface to the Task Manager */
	public TaskManagerInterface getTaskManager() {
		return taskManager;
	}

	/** @return RMI interface to the Task Manager's port to the Host Runtime */
	public HostRuntimesPortInterface getHostRuntimesPort() {
		return taskManagerRuntimePort;
	}

	/** @return package cache manager */
	public PackageCacheManager getPackageCacheManager() {
		return packageCacheManager;
	}

	/** @return Load Monitor instance */
	public LoadMonitorInterface getLoadMonitor() {
		return loadMonitor;
	}

	private void checkDirectory(String directoryName) throws IOException {
		File directory = new File(directoryName);
		if (!directory.exists()) {
			throw new FileNotFoundException(String.format("Error: '%s' does not exist.", directory));
		}
		if (!directory.isDirectory()) {
			throw new FileNotFoundException(String.format("Error: '%s' is not directory.", directory));
		}
		if (!directory.canWrite()) {
			throw new IOException(String.format("Error: '%s' is not writable.", directory));
		}
	}

	/**
	 * Returns an unused port that can be used for new task's debugging and
	 * increments port counter
	 * 
	 * @return port number
	 */
	public int getNextTaskDebugPort() {
		return taskDebugPortCounter++;
	}

	private String getDirInRootDir(final String rootDirectory,
			final String dirInRoot) throws IOException {
		String dir = rootDirectory + File.separator + dirInRoot;
		checkDirectory(dir);
		return dir;
	}

	/**
	 * Allocates a new <code>HostRuntimeImplementation</code> object.
	 * 
	 * @param taskManagerHostname
	 *          host where the Task Manager is running
	 * @param rootDirectory
	 *          root directory of this Host Runtime
	 * @throws RemoteException
	 *           if failed to export object
	 * @throws IOException
	 *           if the root direcotry or one of its required subdirectories (
	 *           <tt>cache</tt>, <tt>boot</tt> and <tt>tasks</tt>) does not exist,
	 *           is not a directory, or is not writable
	 * @throws NotBoundException
	 *           if the Host Runtime can not connect to the Task Manager
	 * @throws LoadMonitorException
	 *           if the Load Monitor can not be created
	 */
	public HostRuntimeImplementation(String taskManagerHostname, String rootDirectory)
			throws IOException, LoadMonitorException, NotBoundException {
		this.rootDirectory = rootDirectory;
		checkDirectory(rootDirectory);
		String cacheDir = getDirInRootDir(rootDirectory, CACHE_DIR);
		String bootPackagesDir = getDirInRootDir(rootDirectory, BOOT_PACKAGES_DIR);
		String nativeLibDir = getDirInRootDir(rootDirectory, NATIVE_LIB_DIR);
		String loadDir = getDirInRootDir(rootDirectory, LOAD_DIR);

		packageCacheManager = new PackageCacheManager(cacheDir, bootPackagesDir);
		loadMonitor = new LoadMonitorImplementation(nativeLibDir, loadDir);

		Naming.rebind(RMI.URL_PREFIX + HostRuntimeInterface.URL, this);

		/* Initialize the Task Manager reference and register the Host Runtime. */
		try {
			taskManagerRuntimePort = (HostRuntimesPortInterface) Naming.lookup("//" + taskManagerHostname + ":" + RMI.REGISTRY_PORT + TaskManagerInterface.URL);
		} catch (MalformedURLException e) {
			logger.error(String.format("Fatal: unexpected exception '%s' thrown.", e.getClass().getSimpleName()), e);
			throw new AssertionError("MalformedURLException could not be thrown.");
		}

		taskManager = taskManagerRuntimePort.getTaskManager();
		if (taskManager == null) {
			throw new NotBoundException("Task Manager refference is null!");
		}
		String hostName = InetAddress.getLocalHost().getCanonicalHostName();
		if (hostName == null) {
			throw new NotBoundException("Canonical host name for localhost is null!");
		}
		taskManager.registerHostRuntime(hostName);

		/* If the Host Runtime shuts down, kill all running tasks. */
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// Kill all running tasks.
				synchronized (runningTasksSync) {
					for (TaskInterface task : runningTasksSync) {
						try {
							task.kill();
						} catch (RemoteException e) {
							String message = "Unexpected remote exception on local call while killing Task.";
							logger.error(message, e);
							throw new AssertionError(message);
						}
					}
				}

				try {
					loadMonitor.terminate();
				} catch (RemoteException e) {
					String message = "Unexpected remote exception on local call while terminating Load Monitor.";
					logger.error(message, e);
					throw new AssertionError(message);
				}

				try {
					taskManager.unregisterHostRuntime(InetAddress.getLocalHost().getCanonicalHostName());
				} catch (RemoteException e) {
					logger.error("Error executing remote call from the Host Runtime to the Task Manager.");
				} catch (UnknownHostException e) {
					logger.error("Unknown host.", e);
				} catch (IllegalArgumentException e) { // TODO: remove this when
					// possible!
					// it's sometimes thrown instead of UnknownHostException
					logger.error("Unknown host.", e);
				}
			}
		});

	}

	/**
	 * Returns directory that contains directories with task data for specified
	 * task (identified by the context and task ID).
	 * 
	 * @param contextID
	 *          context ID
	 * @param taskID
	 *          task ID
	 * @return directory that contains directories with task data
	 */
	public String getBaseDirectoryForTask(String contextID, String taskID) {
		return rootDirectory + File.separator + TASKS_BASE_DIR + File.separator + contextID + File.separator + taskID;
	}

	/**
	 * Returns task directory (task's JAR is extracted there) for specified task
	 * (identified by the context and task ID).
	 * 
	 * @param contextID
	 *          context ID
	 * @param taskID
	 *          task ID
	 * @return task directory (task's JAR is extracted there)
	 */
	public String getTaskDirectoryForTask(String contextID, String taskID) {
		return getBaseDirectoryForTask(contextID, taskID) + File.separator + TASK_DIR;
	}

	/**
	 * Returns wokring directory (results of task's work are stored there,
	 * surviving task's death) for specified task (identified by the context and
	 * task ID).
	 * 
	 * @param contextID
	 *          context ID
	 * @param taskID
	 *          task ID
	 * @return wokring directory (results of task's work are stored there,
	 *         surviving task's death)
	 */
	public String getWorkingDirectoryForTask(String contextID, String taskID) {
		return getBaseDirectoryForTask(contextID, taskID) + File.separator + WORKING_DIR;
	}

	/**
	 * Returns temporary directory (deleted after the task's death) for specified
	 * task (identified by the context and task ID).
	 * 
	 * @param contextID
	 *          context ID
	 * @param taskID
	 *          task ID
	 * @return temporary directory (deleted after the task's death)
	 */
	public String getTemporaryDirectoryForTask(String contextID, String taskID) {
		return getBaseDirectoryForTask(contextID, taskID) + File.separator + TEMPORARY_DIR;
	}

	/**
	 * Returns service directory (package contents are extracted there) for
	 * specified task (identified by the context and task ID).
	 * 
	 * @param contextID
	 *          context ID
	 * @param taskID
	 *          task ID
	 * @return service directory (package contents are extracted there)
	 */
	public String getServiceDirectoryForTask(String contextID, String taskID) {
		return getBaseDirectoryForTask(contextID, taskID) + File.separator + SERVICE_DIR;
	}

	/**
	 * Notifies the Host Runtime that the task has finished.
	 * 
	 * @param task
	 *          task that finished
	 */
	public void notifyTaskFinished(TaskInterface task, TaskState state) throws RemoteException {
		synchronized (runningTasksSync) {
			runningTasksSync.remove(task);
		}

		taskManagerRuntimePort.taskReachedEnd(task.getTaskID(), task.getContextID(), state);
	}

	/**
	 * @see cz.cuni.mff.been.hostruntime.HostRuntimeInterface#initialize(HostRuntimesPortInterface,
	 *      long)
	 */
	public void initialize(HostRuntimesPortInterface hostRuntimesPort,
			long maxPackageCacheSize) throws RemoteException {
		this.taskManagerRuntimePort = hostRuntimesPort;

		packageCacheManager.setMaxCacheSize(maxPackageCacheSize);

		taskManager = hostRuntimesPort.getTaskManager();
	}

	/**
	 * @see cz.cuni.mff.been.hostruntime.HostRuntimeInterface#terminate()
	 */
	public void terminate() throws RemoteException {
		System.exit(0);

		/*
		 * The dirty work of killing all running tasks will now be done by the
		 * shutdown hook, initialized in the constructor.
		 */
	}

	/**
	 * Tells whether any of the running tasks does detailed load monitoring.
	 * 
	 * Note that for this method to make sense, it should be called from a block
	 * that already synchronizes on the list of running tasks, otherwise a new
	 * task that does detailed load monitoring can appear just after this method
	 * returns.
	 */
	private boolean runningTaskDoesDetailedMonitoring() {
		synchronized (runningTasksSync) {
			for (TaskInterface task : runningTasksSync) {
				try {
					if (task.isDetailedLoad()) {
						return true;
					}
				} catch (RemoteException e) {
					String message = "Unexpected remote exception on local call.";
					logger.error(message, e);
					throw new AssertionError(message);
				}
			}
		}
		return false;
	}

	/**
	 * @see cz.cuni.mff.been.hostruntime.HostRuntimeInterface#createTask(TaskDescriptor)
	 */
	public TaskInterface createTask(TaskDescriptor taskDescriptor) throws HostRuntimeException, RemoteException {
		/* Check if we are initialized properly. */
		if (taskManager == null) {
			throw new IllegalStateException("Call \"initialize\" method before creating any task.");
		}

		/*
		 * Get the Software Repository service interface from the Task Manager.
		 * TODO Maybe we should be more clever here and not try to locate the
		 * reference every time...
		 */
		SoftwareRepositoryInterface softwareRepository = (SoftwareRepositoryInterface) taskManager.serviceFind(SoftwareRepositoryService.SERVICE_NAME, Service.RMI_MAIN_IFACE);
		packageCacheManager.setSoftwareRepository(softwareRepository);

		/* Create the task instance. */
		TaskImplementation task;
		try {
			// The synchronization here is for two reasons.
			// The obvious one is access to the list of running tasks.
			// The other one is making sure only one running task does detailed
			// load monitoring.
			synchronized (runningTasksSync) {
				boolean wantsDetailedLoad = taskDescriptor.isSetLoadMonitoring()
						? taskDescriptor.getLoadMonitoring().isDetailedLoad() : false;
				boolean measureDetailedLoad = wantsDetailedLoad && !runningTaskDoesDetailedMonitoring();
				task = new TaskImplementation(taskDescriptor, this, measureDetailedLoad);
				runningTasksSync.add(task);
			}
		} catch (TaskException e) {
			throw new HostRuntimeException(e);
		}

		return task;
	}

	/**
	 * @see cz.cuni.mff.been.hostruntime.HostRuntimeInterface#deleteContext(java.lang.String)
	 */
	public void deleteContext(String contextID) throws HostRuntimeException, RemoteException {
		// Check if there is a directory for given context.
		String contextDir = rootDirectory + File.separator + TASKS_BASE_DIR + File.separator + contextID;
		if (!new File(contextDir).exists()) {
			throw new IllegalArgumentException(String.format("No task from context \"%s\" was run on this Host Runtime.", contextID));
		}

		// Check if there is no task running from given context.
		synchronized (runningTasksSync) {
			for (TaskInterface task : runningTasksSync) {
				if (task.getContextID().equals(contextID)) {
					throw new IllegalArgumentException(String.format("Task \"%s\" from context \"%s\" is still running.", task.getTaskID(), task.getContextID()));
				}
			}
		}

		// Just delete the closed context straight away.
		try {
			FileUtils.deleteDirectory(new File(rootDirectory + File.separator + TASKS_BASE_DIR + File.separator + contextID));
		} catch (IOException e) {
			throw new HostRuntimeException(e);
		}

		try {
			loadMonitor.clearDetailedModeData(contextID);
		} catch (IllegalOperationException e) {
			throw new HostRuntimeException(e);
		} catch (RemoteException e) {
			String message = String.format("Fatal: '%s' should not happen in local call.", e.getClass().getSimpleName());
			logger.error(message, e);
			throw new AssertionError(message);
		} catch (ValueNotFoundException e) {
			throw new HostRuntimeException(e);
		}
	}

	/**
	 * @see cz.cuni.mff.been.hostruntime.HostRuntimeInterface#setMaxPackageCacheSize(long)
	 */
	public void setMaxPackageCacheSize(long maxPackageCacheSize) throws RemoteException {
		packageCacheManager.setMaxCacheSize(maxPackageCacheSize);
	}
}
