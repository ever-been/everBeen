/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Jiri Tauber
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
package cz.cuni.mff.been.task.benchmarkmanagerng;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;

import cz.cuni.mff.been.benchmarkmanagerng.Analysis;
import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerCallbackInterface;
import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerException;
import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerService;
import cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorInterface;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Job;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.task.TaskInitializationException;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper;

/**
 * Task that only runs specified generator pluggable module and plans all the
 * tasks returned by it. When all work is done it reports success back to the
 * BenchmarkManagerng.
 * 
 * @author Jiri Tauber
 */
public class GeneratorRunner extends Job {

	PluggableModuleManager manager;

	/**
	 * @throws TaskInitializationException
	 */
	public GeneratorRunner() throws TaskInitializationException {
		try {
			manager = CurrentTaskSingleton.getTaskHandle()
					.getPluggableModuleManager();
		} catch (TaskException e) {
			throw new TaskInitializationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.task.Job#checkRequiredProperties()
	 */
	@Override
	protected void checkRequiredProperties() throws TaskException {
		StringBuilder errors = new StringBuilder();
		Serializable analysis = getTaskPropertyObject("analysis");
		if (analysis == null) {
			errors.append("analysis is null;");
		} else if (!(analysis instanceof Analysis)) {
			errors.append("analysis is not Analysis;");
		} else if (!((Analysis) analysis).isComplete()) {
			errors.append("analysis is missing some fields;");
		} else {
			for (String str : ((Analysis) analysis).getGenerator().validate(
					CurrentTaskSingleton.getTaskHandle()
							.getPluggableModuleManager())) {
				errors.append(str);
				errors.append(";");
			}
		}
		if (errors.length() > 0) {
			throw new TaskException(errors.toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.task.Job#run()
	 */
	@Override
	protected void run() throws TaskException {
		runMonitorTask();
		Analysis analysis = (Analysis) getTaskPropertyObject("analysis");
		logInfo("Generating tasks for analysis " + analysis.getName());
		try {
			GeneratorInterface generator = (GeneratorInterface) analysis
					.getGenerator().getPluggableModule(manager);
			generator.configure(analysis);
			Collection<TaskDescriptor> tasks = generator.generate();

			if (tasks != null && tasks.size() > 0) {
				logInfo("Sending " + tasks.size()
						+ " tasks to the Task Manager");
				getTasksPort().runTasks(
						tasks.toArray(new TaskDescriptor[tasks.size()]));

			} else {
				logWarning("Generator didn't produce any tasks to run");
				getBMCallbackInterface().reportAnalysisFinish(
						getTaskDescriptor().getContextId());
			}
		} catch (BenchmarkManagerException e) {
			logError("Can't run generator because error occured: "
					+ e.getMessage());
			exitError();
		} catch (RemoteException e) {
			logError("Can't run generated tasks because error occured: "
					+ e.getMessage());
			exitError();
		} catch (PluggableModuleException e) {
			logError("Can't run generator because error occured: "
					+ e.getMessage());
			exitError();
		}

		// signal the BMNG success
		logInfo("Contacting BenchmarkManager to report success");
		try {
			BenchmarkManagerCallbackInterface bmng;
			bmng = getBMCallbackInterface();
			bmng.reportGeneratorSuccess(analysis.getName(), getTaskDescriptor()
					.getContextId(), getTaskDescriptor().getTaskId());
		} catch (RemoteException e) {
			logError("Can't increase the succesful run count because error occured: "
					+ e.getMessage());
		} catch (BenchmarkManagerException e) {
			logError("Can't increase the succesful run count because error occured: "
					+ e.getMessage());
		}
		exitSuccess();
	}

	/**
	 * @throws TaskException
	 * 
	 */
	private void runMonitorTask() throws TaskException {
		try {
			getTasksPort().runTask(getMonitorTask());
		} catch (RemoteException e) {
			throw new TaskException("Can't run context monitor", e);
		}
	}

	/**
	 * @return bechmark manager callback interface
	 * @throws RemoteException
	 */
	private BenchmarkManagerCallbackInterface getBMCallbackInterface()
			throws RemoteException {
		return (BenchmarkManagerCallbackInterface) getTasksPort().serviceFind(
				BenchmarkManagerService.SERVICE_NAME,
				BenchmarkManagerService.CALLBACK_INTERFACE);
	}

	/**
	 * Method is completely independent on task properties
	 * 
	 * @return task descriptor for monitor task
	 */
	private TaskDescriptor getMonitorTask() {
		String context = getTaskDescriptor().getContextId();
		String treeAdress = getTaskDescriptor().getTreeAddress();
		treeAdress = treeAdress.substring(0, treeAdress.lastIndexOf('/'))
				+ "/monitor";
		TaskDescriptor descriptor = TaskDescriptorHelper.createTask("monitor-"
				+ context, "context-monitor", context, null, treeAdress);
		descriptor.getHostRuntimes().setAsTask(getTaskDescriptor().getTaskId()); // TODO
																					// -
																					// should
																					// be
																					// restricted
																					// to
																					// host
																					// where
																					// Taskmanager
																					// is
																					// runnig
		// Andrej: No, it shouldn't. What if that host doesn't have any Host
		// Runtime? Such a situation is perfectly legal.
		return descriptor;
	}

}
