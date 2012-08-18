/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: David Majda
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
package cz.cuni.mff.been.hostruntime;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import cz.cuni.mff.been.common.VariableReplacer;
import cz.cuni.mff.been.common.VariableReplacer.ValueProvider;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.logging.LogLevel;
import cz.cuni.mff.been.logging.LogRecord;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.logging.OutputHandle;
import cz.cuni.mff.been.softwarerepository.PackageType;
import cz.cuni.mff.been.taskmanager.CheckPoint;
import cz.cuni.mff.been.taskmanager.HostRuntimeRegistrationListener;
import cz.cuni.mff.been.taskmanager.HostRuntimesPortInterface;
import cz.cuni.mff.been.taskmanager.ServiceEntry;
import cz.cuni.mff.been.taskmanager.TaskManagerException;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;

/**
 * Implementation of the Host Runtime's port RMI interface provided for the
 * tasks.
 * 
 * @author Antonin Tomecek
 * @author David Majda
 */
public class TasksPortImplementation extends UnicastRemoteObject implements
		TasksPortInterface {

	private static final long serialVersionUID = 6142533585015365733L;

	/** Task this port is associated with. */
	private final TaskImplementation task;

	/** RMI interface to the Task Manager's port to the Host Runtime. */
	private final HostRuntimesPortInterface hostRuntimesPort;

	/**
	 * Allocates a new <code>TaskImplementation</code> object.
	 * 
	 * @param task
	 *            this port is associated with
	 * @throws RemoteException
	 *             if something fails during the execution of the remote method
	 *             call
	 */
	public TasksPortImplementation(TaskImplementation task)
			throws RemoteException {
		this.task = task;
		this.hostRuntimesPort = task.getHostRuntime().getHostRuntimesPort();
	}

	@Override
	public String getTaskDirectory(String contextID, String taskID)
			throws RemoteException {
		return task.getHostRuntime().getTaskDirectoryForTask(contextID, taskID);
	}

	@Override
	public String getWorkingDirectory(String contextID, String taskID)
			throws RemoteException {
		return task.getHostRuntime().getWorkingDirectoryForTask(
				contextID,
				taskID);
	}

	@Override
	public String getTemporaryDirectory(String contextID, String taskID)
			throws RemoteException {
		return task.getHostRuntime().getTemporaryDirectoryForTask(
				contextID,
				taskID);
	}

	@Override
	public Properties getTaskProperties() throws RemoteException {
		VariableReplacer.ValueProvider valueProvider = new ValueProvider() {
			@Override
			public String getValue(String variableName) {
				String[] parts = variableName.split(":");
				if (parts.length != 2) {
					return null;
				}

				try {
					if (parts[1].equals("taskDirectory")) {
						return getTaskDirectory(task.getTaskDescriptor()
								.getContextId(), parts[0]);
					} else if (parts[1].equals("temporaryDirectory")) {
						return getTemporaryDirectory(task.getTaskDescriptor()
								.getContextId(), parts[0]);
					} else if (parts[1].equals("workingDirectory")) {
						return getWorkingDirectory(task.getTaskDescriptor()
								.getContextId(), parts[0]);
					} else {
						return null;
					}
				} catch (RemoteException e) {
					return null;
				}
			}
		};

		return VariableReplacer
				.replace(task.getTaskDescriptor(), valueProvider); // Returns
																	// empty
																	// when not
																	// set.
	}

	@Override
	public Map<String, Serializable> getTaskPropertyObjects()
			throws RemoteException {
		return task.getTaskPropertyObjects();
	}

	@Override
	public void runTask(TaskDescriptor taskDescriptor) throws RemoteException {
		task.getHostRuntime().getTaskManager().runTask(taskDescriptor);
	}

	@Override
	public void runTasks(TaskDescriptor[] taskDescriptor)
			throws RemoteException {
		task.getHostRuntime().getTaskManager().runTask(taskDescriptor);
	}

	@Override
	public void newContext(
			String id,
			String name,
			String description,
			Serializable data) throws RemoteException {
		task.getHostRuntime().getTaskManager()
				.newContext(id, name, description, data);
	}

	@Override
	public void endContext(String id) throws RemoteException {
		task.getHostRuntime().getTaskManager().closeContext(id);
	}

	@Override
	public String[] getRegisteredHostRuntimes() throws RemoteException {
		return task.getHostRuntime().getTaskManager()
				.getRegisteredHostRuntimes();
	}

	@Override
	public void registerEventListener(HostRuntimeRegistrationListener listener)
			throws RemoteException {
		task.getHostRuntime().getTaskManager().registerEventListener(listener);
	}

	@Override
	public void
			unregisterEventListener(HostRuntimeRegistrationListener listener)
					throws RemoteException {
		task.getHostRuntime().getTaskManager()
				.unregisterEventListener(listener);
	}

	@Override
	public void log(LogLevel level, Date timestamp, String message)
			throws RemoteException {
		task.log(level, timestamp, message);
	}

	@Override
	public LogRecord[] getLogsForTask(String context, String taskID)
			throws RemoteException, LogStorageException,
			IllegalArgumentException, NullPointerException {
		return task.getHostRuntime().getTaskManager()
				.getLogsForTask(context, taskID);
	}

	@Override
	public OutputHandle getStandardOutput(String context, String taskID)
			throws RemoteException, LogStorageException,
			IllegalArgumentException, NullPointerException {
		return task.getHostRuntime().getTaskManager()
				.getStandardOutput(context, taskID);
	}

	@Override
	public OutputHandle getErrorOutput(String context, String taskID)
			throws RemoteException, LogStorageException,
			IllegalArgumentException, NullPointerException {
		return task.getHostRuntime().getTaskManager()
				.getErrorOutput(context, taskID);
	}

	@Override
	public void checkPointReached(String name, Serializable value) {
		task.signalCheckPoint(name, value);
	}

	@Override
	public Serializable checkPointWait(
			String contextID,
			String taskID,
			String name,
			long timeout) throws RemoteException {
		/* If context is null, use the context of the calling task. */
		String realContextID = contextID != null ? contextID : task
				.getTaskDescriptor().getContextId();

		try {
			CheckPoint[] checkPoints = hostRuntimesPort.checkPointLook(
					new CheckPoint(taskID, realContextID, name, null),
					timeout);

			if (checkPoints.length > 0) {
				return checkPoints[0].getValue();
			} else {
				return null;
			}
		} catch (TaskManagerException e) {
			return null;
		}
	}

	@Override
	public void serviceRegister(ServiceEntry service) throws RemoteException {
		hostRuntimesPort.serviceRegister(service);
	}

	@Override
	public void serviceUnregister(ServiceEntry serviceTemplate)
			throws RemoteException {
		hostRuntimesPort.serviceUnregister(new ServiceEntry(
				serviceTemplate.getServiceName(),
				serviceTemplate.getInterfaceName(),
				serviceTemplate.getRmiAddress(),
				null,
				null,
				null));
	}

	@Override
	public Remote serviceFind(String serviceName, String interfaceName)
			throws RemoteException {
		return hostRuntimesPort.serviceFind(serviceName, interfaceName);
	}

	@Override
	public ServiceEntry[] serviceFindAll() throws RemoteException {
		return hostRuntimesPort.serviceLook(new ServiceEntry(
				null,
				"main",
				null,
				null,
				null,
				null));
	}

	@Override
	public TaskDescriptor getTaskDescriptor() throws RemoteException {
		return task.getTaskDescriptor();
	}

	@Override
	public TaskEntry getTaskById(String taskId, String contextId)
			throws RemoteException, IllegalArgumentException {
		return task.getHostRuntime().getTaskManager()
				.getTaskById(taskId, contextId);
	}

	@Override
	public void extractPackage(
			String packageName,
			String packageVersion,
			String path,
			PackageType packageType) throws RemoteException {
		try {
			task
					.getHostRuntime()
					.getPackageCacheManager()
					.extractPackage(
							packageName,
							packageVersion,
							path,
							packageType);
		} catch (Exception ex) {
			throw new RemoteException("Error extracting package", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.hostruntime.TasksPortInterface#getUniqueTaskID()
	 */
	@Override
	public TaskManagerInterface getTaskManager() throws RemoteException {
		return task.getHostRuntime().getTaskManager();
	}
}
