/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Jan Tattermusch
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
package cz.cuni.mff.been.debugassistant;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Task;

/**
 * Debug Assistant implementation
 * 
 * @author Jan Tattermusch
 * 
 */
public class DebugAssistantImplementation extends UnicastRemoteObject implements
		DebugAssistantInterface {

	/**
	 * registered tasks
	 */
	private final LinkedList<SuspendedTask> suspendedTasks = new LinkedList<SuspendedTask>();

	/**
	 * Creates new instance of Debug Assistant implementation
	 * 
	 * @throws RemoteException
	 */
	public DebugAssistantImplementation() throws RemoteException {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6134766029985259L;

	/**
	 * Returns list of suspended tasks.
	 * 
	 * @return list of suspended tasks.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public synchronized List<SuspendedTask> getSuspendedTasks()
			throws RemoteException {
		return (List<SuspendedTask>) suspendedTasks.clone();
	}

	/**
	 * Registers a new suspended task
	 * 
	 * @param task
	 *            task to register
	 */
	@Override
	public synchronized void registerSuspendedTask(SuspendedTask task)
			throws RemoteException {

		CurrentTaskSingleton.getTaskHandle().logInfo(
				"New suspended task registered: " + task);

		suspendedTasks.add(task);
	}

	/**
	 * Lets suspended task run.
	 * 
	 * @param id
	 *            suspended task UUID.
	 */
	@Override
	public synchronized void runSuspendedTask(UUID id) throws RemoteException,
			DebugAssistantException {

		SuspendedTask suspendedTask = null;
		for (SuspendedTask task : suspendedTasks) {

			if (task.getId().equals(id)) {
				suspendedTask = task;
				break;
			}
		}

		if (suspendedTask == null) {
			throw new DebugAssistantException(
					"Suspended task with given UUID not found.");
		}

		String host = suspendedTask.getHost();
		int port = suspendedTask.getPort();

		try {
			letVMRun(host, port);

			suspendedTasks.remove(suspendedTask);

			CurrentTaskSingleton.getTaskHandle().logInfo(
					"Suspended task " + suspendedTask
							+ " successfully enabled.");
		} catch (IOException e) {
			throw new DebugAssistantException("Error enabling task on host "
					+ host + ":" + port + " to run.", e);
		} catch (IllegalConnectorArgumentsException e) {
			throw new DebugAssistantException("Error enabling task on host "
					+ host + ":" + port + " to run.", e);
		}

	}

	/**
	 * Connects to Java debug port of a VM and lets the VM run.
	 * 
	 * @param hostname
	 *            hostname
	 * @param port
	 *            debug port
	 * @throws IOException
	 * @throws IllegalConnectorArgumentsException
	 */
	private void letVMRun(String hostname, int port) throws IOException,
			IllegalConnectorArgumentsException {
		for (AttachingConnector connector : Bootstrap.virtualMachineManager()
				.attachingConnectors()) {

			if (connector.name().equals("com.sun.jdi.SocketAttach")) {
				Map<String, Argument> arguments = connector.defaultArguments();

				arguments.get("hostname").setValue(hostname);
				arguments.get("port").setValue(new Integer(port).toString());

				VirtualMachine vm = connector.attach(arguments);
				vm.setDebugTraceMode(VirtualMachine.TRACE_NONE);
				vm.dispose();
			}
		}

	}

	/**
	 * Deletes task from the list.
	 * 
	 * @param id
	 *            task to unregister
	 */
	@Override
	public synchronized void unregisterTask(UUID id) throws RemoteException,
			DebugAssistantException {

		SuspendedTask suspendedTask = null;
		for (SuspendedTask task : suspendedTasks) {

			if (task.getId().equals(id)) {
				suspendedTask = task;
				break;
			}
		}

		if (suspendedTask == null) {
			throw new DebugAssistantException(
					"Suspended task with given UUID not found.");
		}

		suspendedTasks.remove(suspendedTask);

		CurrentTaskSingleton.getTaskHandle().logInfo(
				"Suspended task " + suspendedTask
						+ " unregistered from debug assistant.");

	}

}
