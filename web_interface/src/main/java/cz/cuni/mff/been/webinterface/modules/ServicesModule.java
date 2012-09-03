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
package cz.cuni.mff.been.webinterface.modules;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.logging.LogRecord;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.logging.OutputHandle;
import cz.cuni.mff.been.services.Names;
import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.services.ServiceControlInterface;
import cz.cuni.mff.been.services.ServiceInfo;
import cz.cuni.mff.been.services.ServiceInfo.Status;
import cz.cuni.mff.been.softwarerepository.SoftwareRepositoryService;
import cz.cuni.mff.been.task.InvalidServiceStateException;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper;
import cz.cuni.mff.been.taskmanager.TaskManagerException;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper.BootTask;
import cz.cuni.mff.been.webinterface.Config;
import cz.cuni.mff.been.webinterface.InvalidParamValueException;
import cz.cuni.mff.been.webinterface.MissingParamException;
import cz.cuni.mff.been.webinterface.event.Event;
import cz.cuni.mff.been.webinterface.event.EventListener;
import cz.cuni.mff.been.webinterface.ref.TaskManagerReference;

import static cz.cuni.mff.been.services.Names.DEBUG_ASSISTANT_SERVICE_NAME;
import static cz.cuni.mff.been.services.Names.DEBUG_ASSISTANT_SERVICE_HUMAN_NAME;

import static cz.cuni.mff.been.services.Names.*;


/**
 * Web interface module for the services management.
 * 
 * @author David Majda
 */
public class ServicesModule extends Module implements EventListener {
	/** How long we wait for service operation to complete, in milliseconds. */
	private static final long SERVICE_OPERATION_TIMEOUT = 30000;
	/**
	 * How long we sleep in active waiting for service status change, in
	 * milliseconds. 
	 */
	private static final long STATUS_WAIT_SLEEP_TIME = 200;
	/**
	 * How long we sleep in active waiting when waiting for service task finish. 
	 */
	private static final long SERVICE_TASK_FINISH_WAIT_SLEEP_TIME = 200;

	/** Number of milliseconds in one second. */
	private static final int MILISECONDS_IN_SECOND = 1000;

	/** Class instance (singleton pattern). */
	private static ServicesModule instance;

	private TaskManagerReference taskManager = new TaskManagerReference();
	
	private static ServiceInfo[] services;
	
	static {
		services = new ServiceInfo[] {
			new ServiceInfo(
				SoftwareRepositoryService.SERVICE_NAME,
				SoftwareRepositoryService.SERVICE_HUMAN_NAME
			),
			new ServiceInfo(
				Names.HOST_MANAGER_SERVICE_NAME,
				Names.HOST_MANAGER_SERVICE_HUMAN_NAME
			),
			new ServiceInfo(
				DEBUG_ASSISTANT_SERVICE_NAME,
				DEBUG_ASSISTANT_SERVICE_HUMAN_NAME
			),
			
			new ServiceInfo(
				cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerService.SERVICE_NAME,
				cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerService.SERVICE_HUMAN_NAME
			),
			new ServiceInfo(
					cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryService.SERVICE_NAME,
					cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryService.SERVICE_HUMAN_NAME
			),
			new ServiceInfo(
				Names.COMMAND_LINE_SERVICE_NAME,
				Names.COMMAND_LINE_SERVICE_HUMAN_NAME
			)
		};
	}
	
	/**
	 * Allocates a new <code>ServicesModule</code> object. Constructor is private so only
	 * instance in <code>instance</code> field can be constructed (singleton
	 * pattern).
	 */
	public ServicesModule() {
		super();
		
		/* Initialize general module info... */
		id = "services";
		name = "Services";
		defaultAction = "list";
		
		menu = new MenuItem[] {
				new MenuItem("list", "Services"), 
		};

		eventManager.registerEventListener(this);
	}

	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 */
	public static ServicesModule getInstance() {
		if (instance == null) {
			 instance = new ServicesModule();
		}
		return instance;
	}
	
	private ServiceInfo ensureAndGetService(HttpServletRequest request)
		throws InvalidParamValueException {
		String result = request.getParameter("service");
		for (ServiceInfo service: services) {
			if (service.getName().equals(result)) {
				return service;
			}
		}
		params.ensureCondition("service", false);
		return null; // to shut up the compiler
	}
	
	/**
	 * @see cz.cuni.mff.been.webinterface.modules.Module#invokeMethodForAction(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void invokeMethodForAction(HttpServletRequest request,
			HttpServletResponse response, String action) throws ServletException,
			IOException, InvocationTargetException {
		super.invokeMethodForAction(request, response, action);
	}

	private ServiceControlInterface getServiceControlInterface(String serviceName)
			throws ComponentInitializationException {
		ServiceControlInterface result;
		try {
			result = (ServiceControlInterface) taskManager.get().serviceFind(
				serviceName,
				Service.RMI_CONTROL_IFACE
			);
		} catch (RemoteException e) {
			throw new ComponentInitializationException(
				"Can't connect to the RMI registry. Make sure the RMI registry is running and correctly configured.",
				e);
		}
		if (result != null) {
			return result;
		} else {
			throw new ComponentInitializationException("Can't find service \""
				+ serviceName + "\".");
		}
	}

	private boolean waitForStatus(ServiceInfo service, Status status)
			throws RemoteException, ComponentInitializationException {
		long t = new Date().getTime();
		boolean statusMatches;
		boolean timeoutReached;
		
		do {
			service.fillHostAndStatus(taskManager.get());
			statusMatches = (status == null && service.getStatus() == null)
			  || (status != null && status.equals(service.getStatus()));
			timeoutReached = new Date().getTime() > t + SERVICE_OPERATION_TIMEOUT;
			
			/* If we won't exit the loop, sleep for a while. */
			if (!timeoutReached && !statusMatches) {
				try {
					Thread.sleep(STATUS_WAIT_SLEEP_TIME);
				} catch (InterruptedException e) {
					/* If we're interrupted, we don't care. */
				}
			}
		} while (!timeoutReached && !statusMatches);
		
		return !timeoutReached;
	}

	/**
	 * Called by the event manager when status of some service changes
	 * programatically or when the configuation changes.
	 * 
	 * We invalidate remote reference because they could be meaningless now.
	 * 
	 * @param event sent event 
	 */
	public void receiveEvent(Event event) {
		taskManager.drop();
	}

	@SuppressWarnings("deprecation")
    private boolean startService(ServiceInfo service, String host, boolean debug)
			throws TaskManagerException, RemoteException,
			ComponentInitializationException {
		TaskDescriptor taskDescriptor = TaskDescriptorHelper.createBootTask(BootTask.forName(service.getName()), host);
		
		
		TaskDescriptorHelper.addJavaOptions(taskDescriptor, "-ea");
		
		//TODO: remove this old debugging option code
		if (debug) {
			TaskDescriptorHelper.addJavaOptions(
				taskDescriptor,
				"-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,suspend=y,server=y"
			);
		}
		
		/* After stopping, the service's task hangs out for a while before it
		 * terminates (see Service class, especially exitDelayed method). If the 
		 * user presses the "Start" button in this time, Task Manager will complain
		 * (= throw IllegalArgumentException). We catch this exception and try to
		 * resubmit service's task.
		 * 
		 * I know this is not ideal solution (IllegalArgumentException thrown for
		 * ather reasons may be hidden, active waiting is bad...), but there is no
		 * time for something more clever.
		 */
		boolean needsRetry;
		do {
			needsRetry = false;
			try {
				taskManager.get().runTask(taskDescriptor);
			} catch (IllegalArgumentException e) {
				needsRetry = true;
			} catch (RemoteException e) {
				/* Deal with Tonda's stupid exceptions... */
				Throwable cause = e;
				while (true) {
					if (cause.getCause() != null) {
						cause = cause.getCause();
					} else {
						break;
					}
				}
				if (cause != null && cause instanceof TaskManagerException) {
					throw (TaskManagerException) cause;
				} else {
					throw e;
				}
			}
			if (needsRetry) {
				try {
					Thread.sleep(SERVICE_TASK_FINISH_WAIT_SLEEP_TIME);
				} catch (InterruptedException e) {
					/* We don't care. */
				}
			}
		} while (needsRetry);
		
		return waitForStatus(service, Status.RUNNING);
	}
	
	private boolean handleServiceStart(HttpServletRequest request, boolean debug)
			throws MissingParamException, InvalidParamValueException,
			ComponentInitializationException, IOException {
		boolean result = false;
		
		params.ensureExist("service", "host");
		
		ServiceInfo service = ensureAndGetService(request);
		
		params.checkCondition(params.notEmpty("host"), "Enter non-empty host.");
		try {
			InetAddress.getByName(request.getParameter("host"));
		} catch (UnknownHostException e) {
			params.checkCondition(false, "Unknown host: " + request.getParameter("host") + ".");
		}
		
		if (errorMessages.isEmpty()) {
			try {
				HashMap<String, String> actionParams = new HashMap<String, String>();
				if (startService(service, request.getParameter("host"), debug)) {
					eventManager.sendEvent(Event.SERVICE_STATUS_CHANGE);
				
					actionParams.put("action", "started");
					page.redirectToAction("list", actionParams);
				} else {
					actionParams.put("action", "not-started");
					page.redirectToAction("list", actionParams);
				}
				result = true;
			} catch (TaskManagerException e) {
				errorMessages.addTextMessage("Can't start  the service: "
					+ e.getMessage());
			}
		}
		
		return result;
	}

	private boolean handleServiceStop(HttpServletRequest request)
			throws MissingParamException, InvalidParamValueException,
			IOException {
		boolean result = false;

		params.ensureExists("service");
		
		ServiceInfo service = ensureAndGetService(request);
		
		try {
			ServiceControlInterface controlInterface = getServiceControlInterface(
					service.getName()
			);
			try {
				controlInterface.stopService();

				HashMap<String, String> actionParams = new HashMap<String, String>();
				if (waitForStatus(service, null)) {
					eventManager.sendEvent(Event.SERVICE_STATUS_CHANGE);

					actionParams.put("action", "stopped");
					page.redirectToAction("list", actionParams);
				} else {
					actionParams.put("action", "not-stopped");
					page.redirectToAction("list", actionParams);
				}
				result = true;
			} catch (RemoteException e) {
				errorMessages.addTextMessage(e.getMessage());
			} catch (InvalidServiceStateException e) {
				errorMessages.addTextMessage(e.getMessage());
			} catch (TaskException e) {
				errorMessages.addTextMessage(e.getMessage());
			}
		} catch (ComponentInitializationException e) {
			errorMessages.addTextMessage(e.getMessage());
		}
	
		return result;
	}
			
	private boolean handleServiceRestart(HttpServletRequest request)
			throws MissingParamException, InvalidParamValueException,
			IOException {
		boolean result = false;

		params.ensureExists("service");
		
		ServiceInfo service = ensureAndGetService(request);
		String host = service.getHost();
		
		try {
			ServiceControlInterface controlInterface = getServiceControlInterface(
				service.getName()
			);
			try {
				controlInterface.stopService();

				HashMap<String, String> actionParams = new HashMap<String, String>();
				if (waitForStatus(service, null)) {
					eventManager.sendEvent(Event.SERVICE_STATUS_CHANGE);
					if (startService(service, host, false)) {
						eventManager.sendEvent(Event.SERVICE_STATUS_CHANGE);
					
						actionParams.put("action", "restarted");
						page.redirectToAction("list", actionParams);
					} else {
						actionParams.put("action", "not-restarted");
						page.redirectToAction("list", actionParams);
					}
					result = true;
				} else {
					actionParams.put("action", "not-restarted");
					page.redirectToAction("list", actionParams);
				}
			} catch (TaskManagerException e) {
				errorMessages.addTextMessage("Can't start  the service: "
					+ e.getMessage());
			} catch (RemoteException e) {
				errorMessages.addTextMessage(e.getMessage());
			} catch (InvalidServiceStateException e) {
				errorMessages.addTextMessage(e.getMessage());
			} catch (TaskException e) {
				errorMessages.addTextMessage(e.getMessage());
			}
		} catch (ComponentInitializationException e) {
			errorMessages.addTextMessage(e.getMessage());
		}

		return result;
	}
	
	private boolean handleStartAllOnLocalhost()
			throws ComponentInitializationException, IOException {
		boolean result = true;
		
		boolean allServicesStarted = true;
		for (ServiceInfo service: services) {
			service.fillHostAndStatus(taskManager.get());
			if (service.getStatus() == null) {
				try {
					if (startService(service, "localhost", false)) {
						eventManager.sendEvent(Event.SERVICE_STATUS_CHANGE);
					} else {
						allServicesStarted = false;
					}
				} catch (TaskManagerException e) {
					errorMessages.addTextMessage("Can't start  the service: "
						+ e.getMessage());
					result = false;
				}
			}
		}

		HashMap<String, String> actionParams = new HashMap<String, String>();
		if (allServicesStarted) {
			actionParams.put("action", "all-started");
			page.redirectToAction("list", actionParams);
		} else {
			actionParams.put("action", "some-not-started");
			page.redirectToAction("list", actionParams);
		}
		
		return result;
	}

	private boolean handleStopAll()
			throws ComponentInitializationException, IOException {
		boolean result = true;

		boolean allServicesStopped = true;
		for (ServiceInfo service: services) {
			service.fillHostAndStatus(taskManager.get());
			if (service.getStatus() == ServiceInfo.Status.RUNNING) {
				try {
					ServiceControlInterface controlInterface = getServiceControlInterface(
							service.getName()
					);
					try {
						controlInterface.stopService();

						if (waitForStatus(service, null)) {
							eventManager.sendEvent(Event.SERVICE_STATUS_CHANGE);
						} else {
							allServicesStopped = false;
						}
					} catch (InvalidServiceStateException e) {
						errorMessages.addTextMessage(e.getMessage());
						result = false;
					} catch (TaskException e) {
						errorMessages.addTextMessage(e.getMessage());
						result = false;
					}
				} catch (ComponentInitializationException e) {
					errorMessages.addTextMessage(e.getMessage());
					result = false;
				}
			}
		}

		HashMap<String, String> actionParams = new HashMap<String, String>();
		if (allServicesStopped) {
			actionParams.put("action", "all-stopped");
			page.redirectToAction("list", actionParams);
		} else {
			actionParams.put("action", "some-not-stopped");
			page.redirectToAction("list", actionParams);
		}

		return result;
	}

	/**
	 * Handles the "list" action. 
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws MissingParamException if some required parameter is missing
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws ComponentInitializationException if the Task Manager can't
	 *                                           be initialized
	 */
	public void list(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, ServletException, IOException,
			InvalidParamValueException, ComponentInitializationException {
		
		if (params.exists("action")) {
			String action = request.getParameter("action");
			if (action.equals("started")) {
				infoMessages.addTextMessage("Service started successfully.");
			} else if (action.equals("all-started")) {
				infoMessages.addTextMessage("All services started successfully.");
			} else if (action.equals("stopped")) {
				infoMessages.addTextMessage("Service stopped successfully.");
			} else if (action.equals("all-stopped")) {
				infoMessages.addTextMessage("All services stopped successfully.");
			} else if (action.equals("restarted")) {
				infoMessages.addTextMessage("Service restarted successfully.");
			} else if (action.equals("not-started")) {
				errorMessages.addTextMessage("Service was not started successfully in allowed time ("
					+ (SERVICE_OPERATION_TIMEOUT / MILISECONDS_IN_SECOND) + " seconds).");
			} else if (action.equals("some-not-started")) {
				errorMessages.addTextMessage("Some services were not started successfully in allowed time ("
					+ (SERVICE_OPERATION_TIMEOUT / MILISECONDS_IN_SECOND) + " seconds).");
			} else if (action.equals("not-stopped")) {
				errorMessages.addTextMessage("Service was not stopped successfully in allowed time ("
						+ (SERVICE_OPERATION_TIMEOUT / MILISECONDS_IN_SECOND) + " seconds).");
			} else if (action.equals("some-not-stopped")) {
				errorMessages.addTextMessage("Some services were not stopped successfully in allowed time ("
					+ (SERVICE_OPERATION_TIMEOUT / MILISECONDS_IN_SECOND) + " seconds).");
			} else if (action.equals("not-restarted")) {
				errorMessages.addTextMessage("Service was not restarted successfully in allowed time ("
						+ (SERVICE_OPERATION_TIMEOUT / MILISECONDS_IN_SECOND) + " seconds).");
			} else {
				throw new InvalidParamValueException("Parameter \"action\" has invalid value.");
			}
		}
		
		if (params.exists("start")) {
			if (handleServiceStart(request, false)) {
				return;
			}
		}

		if (params.exists("start-debug")) {
			if (handleServiceStart(request, true)) {
				return;
			}
		}

		if (params.exists("stop")) {
			if (handleServiceStop(request)) {
				return;
			}
		}
		
		if (params.exists("restart")) {
			if (handleServiceRestart(request)) {
				return;
			}
		}

		if (params.exists("start-all-on-localhost")) {
			if (handleStartAllOnLocalhost()) {
				return;
			}
		}

		if (params.exists("stop-all")) {
			if (handleStopAll()) {
				return;
			}
		}

		for (ServiceInfo serviceInfo: services) {
			serviceInfo.fillHostAndStatus(taskManager.get());
		}

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("services", services);
		Config config = Config.getInstance();
		data.put("showDebugOptions", config.getShowDebugOptions());
		if (config.getShowDebugOptions()) {
			data.put("taskManagerHostname", config.getTaskManagerHostname());
		}
		
		page.setTitle("Services");
		page.writeHeader();
		page.writeTemplate("services-list", data);
		page.writeFooter();
	}
	
	/**
	 * Handles the "logs" action. 
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws MissingParamException if some required parameter is missing
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws ComponentInitializationException if the Task Manager can't
	 *                                           be initialized
	 */
	public void logs(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, ServletException, IOException,
			InvalidParamValueException, ComponentInitializationException {
		params.ensureExists("service");
		ServiceInfo service = ensureAndGetService(request);
		
		LogRecord[] logRecords = new LogRecord[] {};
		OutputHandle standardOutputHandle = null;
		OutputHandle errorOutputHandle = null;
		try {
			if (taskManager.get().isContextRegistered(TaskManagerInterface.SYSTEM_CONTEXT_ID)
					&& taskManager.get().isTaskRegistered(TaskManagerInterface.SYSTEM_CONTEXT_ID, service.getTid())) {
				logRecords = taskManager.get()
					.getLogsForTask(TaskManagerInterface.SYSTEM_CONTEXT_ID, service.getTid());
				standardOutputHandle = taskManager.get()
					.getStandardOutput(TaskManagerInterface.SYSTEM_CONTEXT_ID, service.getTid());
				errorOutputHandle = taskManager.get()
					.getErrorOutput(TaskManagerInterface.SYSTEM_CONTEXT_ID, service.getTid());
			}
		} catch (LogStorageException e) {
			errorMessages.addTextMessage("Error retrieving logs: " + e.getMessage());
		}

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("logRecords", logRecords);
		data.put("logFields", EnumSet.of(
			LogRecord.Fields.HOSTNAME,
			LogRecord.Fields.TIMESTAMP,
			LogRecord.Fields.LEVEL,
			LogRecord.Fields.MESSAGE
		));
		data.put("standardOutputHandle", standardOutputHandle);
		data.put("errorOutputHandle", errorOutputHandle);

		page.setTitle("Service logs: " + service.getHumanName());
		page.writeHeader();
		page.writeTemplate("services-logs", data);
		page.writeFooter();
	}
}
