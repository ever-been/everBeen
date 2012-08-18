/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Andrej Podzimek
 * 
 * GNU Lesser General Public License Version 2.1
 * --------------------------------------------- Copyright (C) 2004-2006
 * Distributed Systems Research Group, Faculty of Mathematics and Physics,
 * Charles University in Prague
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 2.1, as published
 * by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package cz.cuni.mff.been.clinterface.adapters;

import java.net.MalformedURLException;
import java.net.URI;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.TreeMap;

import cz.cuni.mff.been.clinterface.CommandLineException;
import cz.cuni.mff.been.clinterface.ModuleSpecificException;
import cz.cuni.mff.been.clinterface.modules.ServicesModule.Errors;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.logging.LogRecord;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.logging.OutputHandle;
import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.services.ServiceControlInterface;
import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.InvalidServiceStateException;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper.BootTask;
import cz.cuni.mff.been.taskmanager.TaskManagerException;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;

/**
 * Representation of a BEEN core service in the Command Line Interface.
 * 
 * @author Andrej Podzimek
 */
public class ServiceHandle {

	/**
	 * A list of possible service statuses.
	 * 
	 * @author Andrej Podzimek
	 */
	public enum Status {

		/** Starting (not registered or reported anything yet). */
		STARTING("starting"),

		/** Up and communicating with the Task Manager. */
		RUNNING("running"),

		/** A stop request is pending, but not yet finished. */
		STOPPING("stopping"),

		/** A restart request is pending, but not yet finished. */
		RESTARTING("restarting");

		/** Mapping status names to enum members. */
		private static final TreeMap<String, Status> reverseMap;

		static {
			reverseMap = new TreeMap<String, Status>();

			for (Status status : Status.values()) {
				reverseMap.put(status.name, status);
			}
		}

		/** Human-readable name of the status. */
		private final String name;

		/**
		 * Creates a new status and initializes its name.
		 * 
		 * @param name
		 *            Human-readable name of the status.
		 */
		private Status(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		/**
		 * Status getter. Maps status names to the corresponding enum values.
		 * 
		 * @param s
		 *            Human-readable name of the status.
		 * @return The enum member representing the status.
		 */
		public static Status fromString(String s) {
			Status result;

			if (null == (result = reverseMap.get(s))) {
				throw new IllegalArgumentException("Invalid Status \"" + s
						+ "\".");
			} else {
				return result;
			}
		}
	}

	/** A sandard extra-long timeout. */
	private static final long TIMEOUT = 2000;

	/** A short timeout for active waiting. If only there was NO active waiting! */
	@Deprecated
	private static final long SHORT_TIMEOUT = 200;

	/** Suffix used to create task IDs from task names. */
	private static final String TID_SUFFIX = "-tid";

	/*
	 * The instance of the Services Module for callback references.
	 * Unfortunately, it's in a different JVM and there's no way to notify the
	 * interface.
	 */
	// private static final Module MODULE = ServicesModule.getInstance();

	/** Boot task entry of the service. */
	private final BootTask bootTask;

	/** Name of the service. */
	private final String name;

	/** Task ID of the service. */
	private final String tid;

	/** Name of the host the service runs on. */
	private String host;

	/** Status of the service. */
	private Status status;

	/** Last time status has been acquired. */
	private long acquireTime;

	public ServiceHandle(BootTask bootTask) {
		this.bootTask = bootTask;
		this.name = bootTask.getName();
		this.tid = name + TID_SUFFIX;
		this.status = null;
		// MODULE.registerEventListener( this ); // Would be nice...
	}

	/**
	 * Boot task getter.
	 * 
	 * @return Boot task entry associated with this member.
	 */
	public BootTask getBootTask() {
		return bootTask;
	}

	/**
	 * Name getter.
	 * 
	 * @return Task name associated with this member.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Task ID getter.
	 * 
	 * @return Task id associated with this member.
	 */
	public String getTid() {
		return tid;
	}

	/**
	 * Host name getter.
	 * 
	 * @return Name of the host the services is currently running on.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Status getter.
	 * 
	 * @return Status of the service.
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Re-read up-to-date information from the Task Manager.
	 * 
	 * @param taskManager
	 *            A reference to the Task Manager.
	 * @throws CommandLineException
	 *             When something goes wrong.
	 */
	public void acquireData(TaskManagerInterface taskManager)
			throws ModuleSpecificException {
		long newTime;

		newTime = new Date().getTime();
		if (newTime - acquireTime > TIMEOUT) {
			forceAcquireData(taskManager, newTime);
		}
	}

	/**
	 * Reset acquire time so that data is reacquired on all subsequent
	 * operations.
	 */
	public void invalidate() {
		acquireTime = 0;
	}

	/**
	 * Start the task.
	 * 
	 * @param taskManager
	 *            A reference to the Task Manager.
	 * @param host
	 *            The host on which the task should run.
	 * @param debug
	 *            Whether to start in debugging mode.
	 * @throws ModuleSpecificException
	 *             When input something goes wrong.
	 */
	@SuppressWarnings("deprecation")
	public synchronized void start(
			TaskManagerInterface taskManager,
			String host,
			boolean debug) throws ModuleSpecificException {
		int counter;
		acquireData(taskManager);
		if (null == status) {
			TaskDescriptor taskDescriptor;

			taskDescriptor = TaskDescriptorHelper
					.createBootTask(bootTask, host);
			TaskDescriptorHelper.addJavaOptions(taskDescriptor, "-ea");
			if (debug) {
				TaskDescriptorHelper
						.addJavaOptions(
								taskDescriptor,
								"-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,suspend=y,server=y");
			}
			try {
				for (counter = 0; counter < 50; ++counter) {
					try {
						taskManager.runTask(taskDescriptor);
						break;
					} catch (IllegalArgumentException exception) {
						CurrentTaskSingleton.getTaskHandle().logWarning(
								"BUG in TM! Delayed service unlisting.");
						try {
							Thread.sleep(SHORT_TIMEOUT);
						} catch (InterruptedException e) {
						}
						++counter;
					}
				}

				if (50 == counter) {
					CurrentTaskSingleton.getTaskHandle().logFatal(
							"TASK MANAGER SUX!!!");
					throw new ModuleSpecificException(
							Errors.CONN_TM_TIMEOUT,
							" (" + name + ')');
				}

				forceAcquireData(taskManager, new Date().getTime());
				while ( // TODO: Remove active waiting!
				status != Status.RUNNING
						|| null == taskManager.serviceFind(
								name,
								Service.RMI_CONTROL_IFACE)) {
					try {
						Thread.sleep(SHORT_TIMEOUT);
					} catch (InterruptedException e) {
					}
					forceAcquireData(taskManager, new Date().getTime());
				}
			} catch (RemoteException exception) {
				throw new ModuleSpecificException(Errors.CONN_TM, " (" + name
						+ ')', exception);
			}

			// MODULE.sendEvent( Event.SERVICE_STATUS_CHANGE ); // Calls
			// invalidate(), too.
		} else {
			throw new ModuleSpecificException(Errors.STAT_RUNNING, " (" + name
					+ ')');
		}
	}

	/**
	 * Stop a task.
	 * 
	 * @param taskManager
	 *            A reference to the Task Manager.
	 * @throws ModuleSpecificException
	 *             When something goes wrong.
	 */
	public synchronized void stop(TaskManagerInterface taskManager)
			throws ModuleSpecificException {
		acquireData(taskManager);
		if (status != Status.RUNNING) {
			throw new ModuleSpecificException(Errors.STAT_STOPPED, " (" + name
					+ ')');
		} else {
			ServiceControlInterface controlInterface;

			try {
				controlInterface = (ServiceControlInterface) taskManager
						.serviceFind(name, Service.RMI_CONTROL_IFACE);
			} catch (RemoteException exception) {
				throw new ModuleSpecificException(Errors.CONN_TM_LOOKUP, " ("
						+ name + ')', exception);
			}

			if (null == controlInterface) {
				throw new ModuleSpecificException(Errors.CONN_TM_REG, " ("
						+ name + ')');
			}

			try {
				controlInterface.stopService();
			} catch (InvalidServiceStateException exception) {
				throw new ModuleSpecificException(Errors.CONN_STOP_FAIL, " ("
						+ name + ')', exception);
			} catch (RemoteException exception) {
				throw new ModuleSpecificException(Errors.CONN_STOP_FAIL, " ("
						+ name + ')', exception);
			} catch (TaskException exception) {
				throw new ModuleSpecificException(Errors.CONN_STOP_FAIL, " ("
						+ name + ')', exception);
			}

			try {
				forceAcquireData(taskManager, new Date().getTime());
				while ( // TODO: Remove active waiting!
				status != null
						|| taskManager.serviceFind(
								name,
								Service.RMI_CONTROL_IFACE) != null) {
					try {
						Thread.sleep(SHORT_TIMEOUT);
					} catch (InterruptedException e) {
					}
					forceAcquireData(taskManager, new Date().getTime());
				}
			} catch (RemoteException exception) {
				throw new ModuleSpecificException(Errors.CONN_TM, " (" + name
						+ ')', exception);
			}

			// MODULE.sendEvent( Event.SERVICE_STATUS_CHANGE ); // Would be
			// nice.
		}
	}

	/**
	 * Restart the task (on the same host).
	 * 
	 * @param taskManager
	 *            A reference to the Task Manager.
	 * @param debug
	 *            Whether to restart the task in debugging mode.
	 * @throws ModuleSpecificException
	 */
	public synchronized void restart(
			TaskManagerInterface taskManager,
			boolean debug) throws ModuleSpecificException {
		acquireData(taskManager);
		if (status != Status.RUNNING) {
			throw new ModuleSpecificException(Errors.STAT_STOPPED, " (" + name
					+ ')');
		} else {
			String oldHost;

			oldHost = host;
			stop(taskManager);
			start(taskManager, oldHost, debug);
		}
	}

	/**
	 * Get log records of the task.
	 * 
	 * @param taskManager
	 *            A reference to the Task Manager.
	 * @param from
	 *            First log record to extract.
	 * @param to
	 *            Last log record to extract.
	 * @return An array of log records.
	 * @throws ModuleSpecificException
	 *             When something goes wrong.
	 */
	public LogRecord[] getLogs(
			TaskManagerInterface taskManager,
			long from,
			long to) throws ModuleSpecificException {
		try {
			return taskManager.getLogsForTask(
					TaskManagerInterface.SYSTEM_CONTEXT_ID,
					tid,
					from,
					to);
		} catch (RemoteException exception) {
			throw new ModuleSpecificException(Errors.CONN_LOG_LOG, " (" + name
					+ ')', exception);
		} catch (LogStorageException exception) {
			throw new ModuleSpecificException(Errors.CONN_LOG_LOG, " (" + name
					+ ')', exception);
		} catch (IllegalArgumentException exception) {
			throw new ModuleSpecificException(Errors.STAT_NOTYET, " (" + name
					+ ')', exception);
		}
	}

	/**
	 * Get standard output of the task.
	 * 
	 * @param taskManager
	 *            A reference to the Task Manager.
	 * @return A handle to the standard output.
	 * @throws ModuleSpecificException
	 *             When something goes wrong.
	 */
	public OutputHandle getStandardOutput(TaskManagerInterface taskManager)
			throws ModuleSpecificException {
		try {
			return taskManager.getStandardOutput(
					TaskManagerInterface.SYSTEM_CONTEXT_ID,
					tid);
		} catch (RemoteException exception) {
			throw new ModuleSpecificException(Errors.CONN_TM, exception);
		} catch (LogStorageException exception) {
			throw new ModuleSpecificException(Errors.CONN_LOG_OUT, exception);
		} catch (IllegalArgumentException exception) {
			throw new ModuleSpecificException(Errors.STAT_NOTYET, " (" + name
					+ ')', exception);
		}
	}

	/**
	 * Get error output of the task.
	 * 
	 * @param taskManager
	 *            A reference to the Task Manager.
	 * @return A handle to the standard output.
	 * @throws ModuleSpecificException
	 *             When something goes wrong.
	 */
	public OutputHandle getErrorOutput(TaskManagerInterface taskManager)
			throws ModuleSpecificException {
		try {
			return taskManager.getErrorOutput(
					TaskManagerInterface.SYSTEM_CONTEXT_ID,
					tid);
		} catch (RemoteException exception) {
			throw new ModuleSpecificException(Errors.CONN_TM, exception);
		} catch (LogStorageException exception) {
			throw new ModuleSpecificException(Errors.CONN_LOG_OUT, exception);
		} catch (IllegalArgumentException exception) {
			throw new ModuleSpecificException(Errors.STAT_NOTYET, " (" + name
					+ ')', exception);
		}
	}

	/*
	 * @Override public void receiveEvent( Event event ) { if ( event ==
	 * Event.SERVICE_STATUS_CHANGE ) { invalidate(); } }
	 */

	/**
	 * Acquires task status data, downloads it from the Task Manager.
	 * 
	 * @param taskManager
	 *            A remote reference pointing at the Task Manager.
	 * @throws CommandLineException
	 *             When something bad happens.
	 */
	private void
			forceAcquireData(TaskManagerInterface taskManager, Long newTime)
					throws ModuleSpecificException {
		URI uri;
		ServiceControlInterface service;

		try {
			uri = taskManager.serviceFindURI(name, Service.RMI_CONTROL_IFACE);
		} catch (RemoteException exception) {
			status = null;
			return;
		}

		if (null == uri) {
			status = null;
			return;
		} else {
			host = uri.getHost();
		}

		try {
			service = (ServiceControlInterface) Naming.lookup(uri.toString());
			service.ping();
			status = Status.fromString((String) taskManager.checkPointLook(
					Service.STATUS_CHECKPOINT,
					tid,
					TaskManagerInterface.SYSTEM_CONTEXT_ID,
					0));
			acquireTime = newTime;
		} catch (RemoteException exception) { // Not fatal, but...
			status = null;
			throw new ModuleSpecificException(Errors.CONN_RMI, exception);
		} catch (MalformedURLException exception) { // Should not happen.
			CurrentTaskSingleton.getTaskHandle().logFatal(
					"Invalid service URL!");
			status = null;
		} catch (NotBoundException exception) { // This is normal.
			status = null;
		} catch (TaskManagerException exception) { // TODO: This is not normal.
			status = null;
		}
	}
}
