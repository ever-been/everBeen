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
import java.util.HashMap;
import java.util.prefs.BackingStoreException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.hostmanager.HostManagerException;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import cz.cuni.mff.been.hostmanager.HostManagerOptionsInterface;
import cz.cuni.mff.been.services.Names;
import cz.cuni.mff.been.webinterface.Config;
import cz.cuni.mff.been.webinterface.InvalidParamValueException;
import cz.cuni.mff.been.webinterface.MissingParamException;
import cz.cuni.mff.been.webinterface.event.Event;
import cz.cuni.mff.been.webinterface.event.EventListener;
import cz.cuni.mff.been.webinterface.ref.ServiceReference;
import cz.cuni.mff.been.webinterface.ref.TaskManagerReference;

/**
 * Web interface module for the configuration.
 * 
 * @author David Majda
 */
public class ConfigurationModule extends Module implements EventListener {
	private static final int MB = 1024 * 1024;
	private static final int MILISECONDS_IN_SECOND = 1000;
	
	/** Class instance (singleton pattern). */
	private static ConfigurationModule instance;

	private TaskManagerReference taskManager = new TaskManagerReference();
	private ServiceReference<HostManagerInterface> hostManager
		= new ServiceReference<HostManagerInterface>(
			taskManager,
			Names.HOST_MANAGER_SERVICE_NAME,
			Names.HOST_MANAGER_REMOTE_INTERFACE_MAIN,
			Names.HOST_MANAGER_SERVICE_HUMAN_NAME
		);

	private Config config;

	/**
	 * Allocates a new <code>ConfigurationModule</code> object. Construcor is private so only
	 * instance in <code>instance</code> field can be constructed (singleton
	 * pattern).
	 */
	private ConfigurationModule() {
		super();
		
		/* Initialize general module info... */
		id = "configuration";
		name = "Configuration";
		defaultAction = "configuration";
		
		menu = new MenuItem[] {
				new MenuItem("configuration", "Configuration"), 
		};

		//ServicesModule.getInstance().addServiceStatusObserver(this);

		/* ...and now do the specific stuff. */
		config = Config.getInstance();
	}
	
	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 */
	public static ConfigurationModule getInstance() {
		if (instance == null) {
			 instance = new ConfigurationModule();
		}
		return instance;
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
		hostManager.drop();
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

	private void checkNonNegativeIntegerParam(HttpServletRequest request,
			String name, String description) {
		params.checkCondition(params.isInteger(name),
			description + " must be a number.");
		if (params.isInteger(name)) {
			params.checkCondition(Integer.valueOf(request.getParameter(name)) >= 0,
				description + " must be a non-negative number.");
		}
	}

	private void checkPositiveLongParam(HttpServletRequest request, String name,
			String description) {
		params.checkCondition(params.isLong(name),
			description + " must be a number.");
		if (params.isLong(name)) {
			params.checkCondition(Long.valueOf(request.getParameter(name)) > 0,
				description + " must be a positive number.");
		}
	}
	
	/**
	 * Handles the "configuration" action. 
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
	public void configuration(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, ServletException, IOException,
			InvalidParamValueException, ComponentInitializationException {
		HashMap<String, Object> data = new HashMap<String, Object>();

		if (params.exists("action")) {
			String action = request.getParameter("action");
			if (action.equals("saved")) {
				infoMessages.addTextMessage("Configuration saved successfully.");
			} else {
				throw new InvalidParamValueException("Parameter \"action\" has invalid value.");
			}
		}

		if (params.exists("save")) {
			params.ensureExist("task-manager-hostname",
				"show-host-runtime-configuration", "show-host-manager-configuration");
			params.ensureCondition("show-host-runtime-configuration",
				params.isBoolean("show-host-runtime-configuration"));
			params.ensureCondition("show-host-manager-configuration",
					params.isBoolean("show-host-manager-configuration"));
			
			boolean showHostRuntimeConfiguration
				= Boolean.valueOf(request.getParameter("show-host-runtime-configuration"));		
			if (showHostRuntimeConfiguration) {
				params.ensureExist("max-package-cache-size", "kept-closed-context-count");
			}
			
			boolean showHostManagerConfiguration 
				= Boolean.valueOf(request.getParameter("show-host-manager-configuration"));		
			if (showHostManagerConfiguration) {
				params.ensureExist("host-detection-timeout", "pending-refresh-interval",
					"activity-monitor-interval", "dead-host-timeout",
					"brief-mode-interval", "default-detailed-mode-interval");
			}
			
			if (params.exists("show-debug-options")) {
				params.ensureCondition("show-debug-options",
					params.isCheckboxBool("show-debug-options"));
			}
			
			params.checkCondition(params.notEmpty("task-manager-hostname"),
				"Enter non-empty host.");
			try {
				InetAddress.getByName(request.getParameter("task-manager-hostname"));
			} catch (UnknownHostException e) {
				params.checkCondition(false, "Unknown host: " + request.getParameter("task-manager-hostname") + ".");
			}

			if (showHostRuntimeConfiguration) {
				checkPositiveLongParam(request, "max-package-cache-size",
					"Package cache size limit");
				checkNonNegativeIntegerParam(request, "kept-closed-context-count",
					"Number of closed contexts kept");
			}

			if (showHostManagerConfiguration) {
				checkPositiveLongParam(request, "host-detection-timeout",
					"Host detection timeout");
				checkPositiveLongParam(request, "pending-refresh-interval",
					"Host detection timeout check interval");
				checkPositiveLongParam(request, "activity-monitor-interval",
					"Activity Monitor refresh interval");
				checkPositiveLongParam(request, "dead-host-timeout",
					"Host crash timeout");
				checkPositiveLongParam(request, "brief-mode-interval",
					"Brief mode sampling interval");
				checkPositiveLongParam(request, "default-detailed-mode-interval",
					"Default detailed mode sampling interval");
				if (params.isLong("dead-host-timeout") && params.isLong("brief-mode-interval")) {
					params.checkCondition(
						Long.valueOf(request.getParameter("dead-host-timeout")) * MILISECONDS_IN_SECOND
							> Long.valueOf(request.getParameter("brief-mode-interval")),
						"Host crash timeout must be greater than brief mode sampling interval."
					);
				}
			}
			
			if (errorMessages.isEmpty()) {
				try {
					config.setTaskManagerHostname(
						request.getParameter("task-manager-hostname")
					);
					config.setShowDebugOptions(
						params.exists("show-debug-options")
					);

					/* Acquire the Task Manager reference, because the old one may be
					 * invalid after possible changing of the Task Manager's hostname, and
					 * send an event about the change.
					 */ 
					taskManager.acquire();
					eventManager.sendEvent(Event.TASK_MANAGER_STATUS_CHANGE);

					if (showHostRuntimeConfiguration) {

						taskManager.get().setMaxPackageCacheSize(
								Long.valueOf(request.getParameter("max-package-cache-size")) * MB
						);
						taskManager.get().setKeptClosedContextCount(
								Integer.valueOf(request.getParameter("kept-closed-context-count"))
						);
					}

					if (showHostManagerConfiguration) {
						HostManagerOptionsInterface options = hostManager.get().getConfiguration();
						options.setHostDetectionTimeout(
							Long.valueOf(request.getParameter("host-detection-timeout")) * MILISECONDS_IN_SECOND
						);
						options.setPendingRefreshInterval(
							Long.valueOf(request.getParameter("pending-refresh-interval")) * MILISECONDS_IN_SECOND
						);
						options.setActivityMonitorInterval(
							Long.valueOf(request.getParameter("activity-monitor-interval")) * MILISECONDS_IN_SECOND
						);
						options.setDeadHostTimeout(
							Long.valueOf(request.getParameter("dead-host-timeout")) * MILISECONDS_IN_SECOND
						);
						options.setBriefModeInterval(
							Long.valueOf(request.getParameter("brief-mode-interval"))
						);
						options.setDefaultDetailedModeInterval(
							Long.valueOf(request.getParameter("default-detailed-mode-interval"))
						);
					}
				} catch (BackingStoreException e) {
					errorMessages.addTextMessage("Error saving web interface configuration: " + e.getMessage());
				} catch (HostManagerException e) {
					errorMessages.addTextMessage("Error saving Host Manager configuration: " + e.getMessage());
				}
			}
			
			if (errorMessages.isEmpty()) {
				HashMap<String, String> actionParams = new HashMap<String, String>();
				actionParams.put("action", "saved");
				page.redirectToAction("configuration", actionParams);
				return;
			} else {
				data.put("taskManagerHostname", request.getParameter("task-manager-hostname"));
				data.put("maxPackageCacheSize", request.getParameter("max-package-cache-size"));
				data.put("showHostRuntimeConfiguration", showHostRuntimeConfiguration);
				data.put("showHostManagerConfiguration", showHostManagerConfiguration);
				if (showHostRuntimeConfiguration) {
					data.put("keptClosedContextCount", request.getParameter("kept-closed-context-count"));
					data.put("showDebugOptions", request.getParameter("show-debug-options") != null);
				}
				if (showHostManagerConfiguration) {
					data.put("hostDetectionTimeout", request.getParameter("host-detection-timeout"));
					data.put("pendingRefreshInterval", request.getParameter("pending-refresh-interval"));
					data.put("activityMonitorInterval", request.getParameter("activity-monitor-interval"));
					data.put("deadHostTimeout", request.getParameter("dead-host-timeout"));
					data.put("briefModeInterval", request.getParameter("brief-mode-interval"));
					data.put("defaultDetailedModeInterval", request.getParameter("default-detailed-mode-interval"));
				}
			}
		} else {
			data.put("taskManagerHostname", config.getTaskManagerHostname());
			data.put("showDebugOptions", config.getShowDebugOptions());
			
			boolean showHostRuntimeConfiguration = taskManager.hasReference();
			data.put("showHostRuntimeConfiguration", showHostRuntimeConfiguration);
			if (showHostRuntimeConfiguration) {
				data.put(
					"maxPackageCacheSize",
					Long.toString(taskManager.get().getMaxPackageCacheSize() / MB)
				);
				data.put(
					"keptClosedContextCount",
					Integer.toString(taskManager.get().getKeptClosedContextCount())
				);
			}

			boolean showHostManagerConfiguration = hostManager.hasReference();
			data.put("showHostManagerConfiguration", showHostManagerConfiguration);
			if (showHostManagerConfiguration) {
				HostManagerOptionsInterface options = hostManager.get().getConfiguration();
				data.put(
					"hostDetectionTimeout",
					Long.toString(options.getHostDetectionTimeout() / MILISECONDS_IN_SECOND)
				);
				data.put(
					"pendingRefreshInterval",
					Long.toString(options.getPendingRefreshInterval() / MILISECONDS_IN_SECOND)
				);
				data.put(
					"activityMonitorInterval",
					Long.toString(options.getActivityMonitorInterval() / MILISECONDS_IN_SECOND)
				);
				data.put(
					"deadHostTimeout",
					Long.toString(options.getDeadHostTimeout() / MILISECONDS_IN_SECOND)
				);
				data.put(
					"briefModeInterval",
					Long.toString(options.getBriefModeInterval())
				);
				data.put(
					"defaultDetailedModeInterval",
					Long.toString(options.getDefaultDetailedModeInterval())
				);
			}
		}

		page.setTitle("Configuration");
		page.setFocusedElement(0, "task-manager-hostname");
		page.writeHeader();
		page.writeTemplate("configuration-configuration", data);
		page.writeFooter();
	}
}
