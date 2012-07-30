/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Jaroslav Urban
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
package cz.cuni.mff.been.task;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URI;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.hostruntime.TasksPortInterface;
import cz.cuni.mff.been.taskmanager.ServiceEntry;
import cz.cuni.mff.been.webinterface.services.ServiceInfo.Status;

/**
 * A task that runs like a service, that means it runs for a long time and
 * provides some remote interfaces with some functionality.
 * This task binds one or several remote interfaces in the RMI registry and
 * in the BEEN's naming service. The service has
 * a remote control interface which can be used to stop or restart it. 
 * 
 * @author Jaroslav Urban
 * @author David Majda
 */
public abstract class Service extends Task {
	/**
	 * RMI references to BEENs services are in the form
	 * rmi://hostname:[RMI.REGISTRY_PORT]/RMI_BEEN_PREFIX/service-name/interface-name
	 */  
	public static final String RMI_BEEN_PREFIX = "been";
	/**
	 * Name of the ServiceControlInterface interface reference in the RMI URL.
	 */
	public static final String RMI_CONTROL_IFACE = "control";
	/**
	 * Name of the service's main interface reference in the RMI URL.
	 */
	public static final String RMI_MAIN_IFACE = "main";
	/**
	 * Name of the checkpoint that signals the state of the service.
	 */
	public static final String STATUS_CHECKPOINT = "service status";
	/**
	 * Value of the {@value #STATUS_CHECKPOINT} checkpoint which
	 * signals that the service is starting.
	 */
	public static final String STATUS_STARTING = "starting";
	/**
	 * Value of the {@value #STATUS_CHECKPOINT} checkpoint which
	 * signals that the service is running.
	 */
	public static final String STATUS_RUNNING = "running";
	/**
	 * Value of the {@value #STATUS_CHECKPOINT} checkpoint which
	 * signals that the service is stopping.
	 */
	public static final String STATUS_STOPPING = "stopping";
	/**
	 * Value of the {@value #STATUS_CHECKPOINT} checkpoint which
	 * signals that the service is restarting.
	 */
	public static final String STATUS_RESTARTING = "restarting";
	
	/**
	 * The JVM needs to be exited after the service stops. But we can't call the 
	 * System.exit() method in a method that is invoked remotely, because
	 * that would throw an exception on the client's side. Thus, in the stopService 
	 * method we start a Thread that calls System.exit() after this time (in milliseconds).
	 * The method returns before the thread can exit the JVM. 
	 */
	public static final int EXIT_DELAY_TIME = 500;
	
	/** Buffer size used in the <code>copyFile</code> method. */
	private static final int COPY_FILE_BUFFER_SIZE = 4096;
	
	/** Run status of the service **/
	private Status status;
	/**
	 *  Remote interfaces of the service
	 *  
	 *  String - rmi reference
	 *  Remote - remote interface bound in rmi
	 */
	private HashMap<String, Remote> rmiInterfaces = new HashMap<String, Remote>();
	/** Interface names
	 * 
	 *  String - rmi reference
	 *  String - interface name
	 */
	private HashMap<String, String> interfaceNames = new HashMap<String, String>();
	/**
	 * Lock object that is used to lock stopping/restarting sections
	 */
	private Object lock = new Object();

	private class ServiceControlImplementation extends UnicastRemoteObject implements ServiceControlInterface {

		private static final long	serialVersionUID	= -3514568190828472913L;

		/**
		 * Allocates a new StatusHandle object
		 * 
		 * @throws RemoteException
		 */
		public ServiceControlImplementation() throws RemoteException {
			// empty, constructor had to be created because extending the UnicastRemoteObject
		}

		/**
		 * Determines the current status of the service, e.g. if it's running, or if it's
		 * just starting up etc.
		 * 
		 * @return status of the service
		 * @throws RemoteException 
		 */
		public Status getStatus() throws RemoteException {
			return status;
		}

		/**
	 	 * Stops the service, then unbinds it's RMI interfaces and exits the JVM
		 * 
		 * NOTE Urban: the JVM is exited only while we use the 1 JVM per 1 task model
		 * @throws RemoteException 
		 * @throws InvalidServiceStateException if the service is in such state
		 * 	that doesn't allow restarting. For example if it's just starting up. 
		 * @throws TaskException if stopping of the service fails
		 */
		public void stopService() 
		throws RemoteException, InvalidServiceStateException, TaskException {
			synchronized (lock) {
				if (status != Status.RUNNING) {
					throw new InvalidServiceStateException("Cannot stop now, service status is: " + status);
				}
				logInfo("Service is stopping");
				checkPointReached(STATUS_CHECKPOINT, STATUS_STOPPING);
				status = Status.STOPPING;
				try {
					stop();
				} catch (TaskException e) {
					logFatal("Cannot stop service: " + e.getMessage());
					stateChangeError(e);
					throw e;
				}
				try {
					unbindInterfaces();
				} catch (Exception e) {
					throw bindError(e);
				}
				logInfo("Service is stopped");
				exitSuccessDelayed();
			}
		}
		
		/**
		 * Does nothing, but can be used to test validity of a remote reference
		 * to the service.
		 */
		public void ping() {
			// do nothing, this method is used to test if a remote reference
			// is valid
			return;
		}
	}
	
	/**
	 * 
	 * Allocates a new <code>Service</code> object.
	 *
	 * @throws TaskInitializationException
	 */
	public Service() throws TaskInitializationException {
		try {
			addRemoteInterface(RMI_CONTROL_IFACE, 
				new ServiceControlImplementation());
		} catch (RemoteException e) {
			e.printStackTrace();
			logFatal("Couldn't create ServiceControlInterface: " + e.getMessage());
			throw new TaskInitializationException(e);
		}
	}
	
	/**
	 * Determines the service's name.
	 * 
	 * @return service's name
	 */
	public abstract String getName();
	
	/**
	 * Starts the service. That means it performs all the initialization work 
	 * needed for the remote interfaces of the service to function properly.
	 *  
	 * @throws TaskException if starting of the service fails
	 */
	protected abstract void start() throws TaskException;
	
	/**
	 * Stops the service. That means it does all the clean-up needed for a clean
	 * stop of the service.
	 *  
	 * @throws TaskException if stopping of the service fails
	 */
	protected abstract void stop() throws TaskException;
	
	/**
	 * Starts the service and then binds it's RMI interfaces. This method should be 
	 * called after creating the services instance in the main method of
	 * a descendant of the Service class.
	 * 
	 * This is not a part of the remote control interface! Services cannot be 
	 * started by the control interface, they are automatically started when their
	 * task starts.
	 *  
	 * @throws TaskException if starting of the service fails
	 */
	public void startService() throws TaskException {
		synchronized (lock) {
			logInfo("Service is starting");
			status = Status.STARTING;
			checkPointReached(STATUS_CHECKPOINT, STATUS_STARTING);
			try {
				start();
			} catch (TaskException e) { 
				logFatal("Cannot start service: " + e.getMessage());
				stateChangeError(e);
				throw e;
			}
			try {
				bindInterfaces();
			} catch (Exception e) {
				throw bindError(e);
			}
			status = Status.RUNNING;
			checkPointReached(STATUS_CHECKPOINT, STATUS_RUNNING);
			logInfo("Service is running");
		}
	}
	
	/**
	 * Get control interface of the service. Used when need to access
	 * control interface of the service for a local instance (not through RMI)
	 * 
	 * @return Control interface of the service.
	 */
	public ServiceControlInterface getControlInterface() {
		return (ServiceControlInterface) rmiInterfaces.get(RMI_CONTROL_IFACE);
	}
	
	/**
	 * Adds an interface to the list of this service's implemented
	 * remote interfaces. If an interface with this name was already 
	 * in the list, it will be replaced with the new interface.
	 * 
	 * @param name name of the interface
	 * @param iface the remote interface
	 */
	protected void addRemoteInterface(String name, Remote iface) {
		String fullRMIName = RMI_BEEN_PREFIX + "/" + getName() + "/" + name;
		rmiInterfaces.put(fullRMIName, iface);
		interfaceNames.put(fullRMIName, name);
		logInfo("Added service interface " + fullRMIName);
	}

	/**
	 * Removes an interface from the list of this service's implemented
	 * remote interfaces
	 * 
	 * @param name
	 */
	protected void removeRemoteInterface(String name) {
		String fullRMIName = RMI_BEEN_PREFIX + "/" + getName() + "/" + name;
		rmiInterfaces.remove(fullRMIName);
		interfaceNames.remove(fullRMIName);
	}
	
	/**
	 * Exits the service that finished successfully. It exits after a short
	 * delay, so a service's remote method can return correctly. 
	 *
	 */
	protected void exitSuccessDelayed() {
		exitDelayed(true, EXIT_DELAY_TIME);
	}
	
	/**
	 * Exits the service that finished with an error. It exits after a short
	 * delay, so a service's remote method can return correctly. 
	 *
	 */
	protected void exitErrorDelayed() {
		exitDelayed(true, EXIT_DELAY_TIME);
	}
	
	/**
	 * Exits the task's JVM
	 * 
	 * @param success if <code>true</code>, then exits with a success return value,
	 * otherwise exists with an error return value. 
	 * @param time time in milliseconds after which the service will exit
	 */
	protected void exitDelayed(boolean success, long time) {
		// a small trick, we need the parameters to be final to 
		// be able to use them in the anonymous Thread class,
		// but we don't want final parameters of this method
		final boolean successFlag = success;
		final long millis = time;
		
		Thread exiter = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
					//nothing bad happened
				}
				if (successFlag) {
					exitSuccess();
				} else {
					exitError();
				}
			}
		};
		exiter.start();
	}
	
	/**
	 * Binds the service's remote interface to the rmiregistry and the lookup
	 * service
	 */
	private void bindInterfaces() throws Exception {
		Task.getTaskHandle().logInfo("Binding service interfaces. Total: "+rmiInterfaces.size());
		for (String key : rmiInterfaces.keySet()) {
			try {
				String hostName = InetAddress.getLocalHost().getCanonicalHostName();
				String contextId = getTaskDescriptor().getContextId();
				String taskId = getTaskDescriptor().getTaskId();
				URI uri = new URI("rmi://" + hostName + ":" + RMI.REGISTRY_PORT + "/" + key);

				Task.getTaskHandle().logInfo(key);
				Naming.rebind(uri.toString(), rmiInterfaces.get(key));
				
				// register in the BEEN naming service
				ServiceEntry serviceEntry = new ServiceEntry(
						getName(),
						interfaceNames.get(key),
						uri,
						rmiInterfaces.get(key),
						contextId,
						taskId);
				
				TasksPortInterface tasksPort = getTasksPort();
				tasksPort.serviceRegister(serviceEntry);
			} catch (Exception e) {
				Task.getTaskHandle().logError("Cannot bind interface " + key);
				throw new Exception("Cannot bind interface " + key, e);
			}
		}

	}
	
	/**
	 * Unbinds the service's remote interfaces from the rmiregistry and the lookup
	 * service 
	 */
	private void unbindInterfaces() throws Exception {
		for (String key : rmiInterfaces.keySet()) {
			try {
				String hostName = InetAddress.getLocalHost().getCanonicalHostName();
				String contextId = getTaskDescriptor().getContextId();
				String taskId = getTaskDescriptor().getTaskId();
				URI uri = new URI("rmi://" + hostName + ":" + RMI.REGISTRY_PORT + "/" + key);

				Naming.unbind(uri.toString());
				
				// unregister in the BEEN naming service
				ServiceEntry serviceEntry = new ServiceEntry(
						getName(),
						interfaceNames.get(key),
						uri,
						rmiInterfaces.get(key),
						contextId,
						taskId);
				
				TasksPortInterface tasksPort = getTasksPort();
				tasksPort.serviceUnregister(serviceEntry);
			} catch (Exception e) {
				throw new Exception("Cannot unbind interface " + key, e);
			}
		}
	}

	/**
	 * Reacts to an error that occurred during service registration or 
	 * unregistration from the naming service
	 * 
	 * @param e exception that was thrown by the error, the exception should
	 * contain a user readable error message because the message will be logged
	 * @return a new TaskException that should be thrown to the user
	 */
	private TaskException bindError(Exception e) {
		e.printStackTrace();
		logFatal(e.getMessage());
		status = Status.STOPPING;
		exitErrorDelayed();
		return new TaskException(e);
	}
	
	/**
	 * Reacts to an error that occurred during change of state of the service, 
	 * e.g. during starting
	 *  
	 * @param e exception that was thrown by the error
	 */
	private void stateChangeError(TaskException e) {
		e.printStackTrace();
		status = Status.STOPPING;
		exitErrorDelayed();
	}
	
	/**
	 * Helper method to copy a file.
	 * 
	 * @param src source file
	 * @param dest destination file
	 * @throws IOException if some I/O operation fails
	 */
	protected void copyFile(File src, File dest) throws IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(src));
		OutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
		byte[] buffer = new byte[COPY_FILE_BUFFER_SIZE];
		int bytesRead;
		
		try {
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		} finally {
			in.close();
			out.close();
		}
	}
	
	/**
	 * Copy directory contents to another directory. Function is recursive and
	 * will not create the destination directory.
	 * 
	 * @param srcDir source directory
	 * @param destDir destination directory
	 * @throws IOException if some I/O operation fails
	 */
	private void copyDirectoryContentsRecursive(File srcDir, File destDir)
			throws IOException {
		for (File f: srcDir.listFiles()) {
			File newFileOrDir = new File(destDir, f.getName());
			if (!newFileOrDir.exists()) {
				if (!f.isDirectory()) {
					copyFile(f, newFileOrDir);
				} else {
					newFileOrDir.mkdir();
					copyDirectoryContentsRecursive(f, newFileOrDir);
				}
			}
		}
	}
	
	/**
	 * Copies example data (if it exists) to the working directory.
	 * 
	 * The example data files are copied from two places:
	 * 
	 * <ol>
	 *   <li><tt>data</tt> sub-directory of the task directory</li> 
	 *   <li><tt>$BEEN_HOME/data/<em>serviceName</em></tt> directory</li>
	 * </ol> 
	 * 
	 * @throws IOException if some I/O operation fails
	 */
	protected void tryCopyExampleData() throws IOException {
		File workingDir = new File(getWorkingDirectory());
		
		File packageDataDir = new File(getTaskDirectory() + File.separator + "data");
		if (packageDataDir.isDirectory()) {
			copyDirectoryContentsRecursive(packageDataDir, workingDir);
		}
		
		String beenHome = System.getenv("BEEN_HOME");
		File globalDataDir = new File(
			beenHome + File.separator + "data" + File.separator + getName()
		);
		if (globalDataDir.isDirectory()) {
			copyDirectoryContentsRecursive(globalDataDir, workingDir);
		}
	}

	/**
	 * Copy example data (if present) from the task's directory to the specified sub-directory
	 * of the working directory of the task. Source is assumed to be in the <tt>data</tt>
	 * sub-directory of the task's directory.
	 * 
	 * @param targetDir Name of the target directory. This name is relative to the task's working
	 *        directory.
	 *        
	 * @throws IOException If I/O error occurred during file transfer. 
	 */
	protected void tryCopyExampleData(String targetDir) throws IOException {

		File dataDir = new File(getTaskDirectory() + File.separator + "data");
		
		if (dataDir.isDirectory()) {
		
			String targetPath = getWorkingDirectory() + File.separator + targetDir;
			copyDirectoryContentsRecursive(dataDir,	new File(targetPath));
		}
	}
}
