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
import java.net.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.hostmanager.HostDatabaseException;
import cz.cuni.mff.been.hostmanager.HostManagerException;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import cz.cuni.mff.been.hostmanager.HostManagerService;
import cz.cuni.mff.been.hostmanager.HostOperationStatus;
import cz.cuni.mff.been.hostmanager.InputParseException;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.OperationHandle;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;
import cz.cuni.mff.been.hostmanager.ValueTypeIncorrectException;
import cz.cuni.mff.been.hostmanager.database.HostGroup;
import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;
import cz.cuni.mff.been.hostmanager.database.NameValuePair;
import cz.cuni.mff.been.hostmanager.database.PropertyTreeFactory;
import cz.cuni.mff.been.hostmanager.database.RSLRestriction;
import cz.cuni.mff.been.hostmanager.database.RestrictionInterface;
import cz.cuni.mff.been.hostmanager.database.SoftwareAliasDefinition;
import cz.cuni.mff.been.hostmanager.load.HostDataStatisticianInterface;
import cz.cuni.mff.been.hostmanager.load.LoadMonitorException;
import cz.cuni.mff.been.hostmanager.value.ValueBoolean;
import cz.cuni.mff.been.hostmanager.value.ValueCommonInterface;
import cz.cuni.mff.been.hostmanager.value.ValueDouble;
import cz.cuni.mff.been.hostmanager.value.ValueInteger;
import cz.cuni.mff.been.hostmanager.value.ValueRegexp;
import cz.cuni.mff.been.hostmanager.value.ValueString;
import cz.cuni.mff.been.hostmanager.value.ValueVersion;
import cz.cuni.mff.been.logging.LogRecord;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.logging.LogUtils;
import cz.cuni.mff.been.task.TaskUtils;
import cz.cuni.mff.been.taskmanager.CheckPoint;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;
import cz.cuni.mff.been.webinterface.InvalidParamValueException;
import cz.cuni.mff.been.webinterface.MissingParamException;
import cz.cuni.mff.been.webinterface.RSLValidator;
import cz.cuni.mff.been.webinterface.Routines;
import cz.cuni.mff.been.webinterface.Page.LayoutType;
import cz.cuni.mff.been.webinterface.event.Event;
import cz.cuni.mff.been.webinterface.event.EventListener;
import cz.cuni.mff.been.webinterface.hosts.UserPropertiesHandler;
import cz.cuni.mff.been.webinterface.ref.LoadServerReference;
import cz.cuni.mff.been.webinterface.ref.ServiceReference;
import cz.cuni.mff.been.webinterface.ref.TaskManagerReference;

/**
 * Web interface module for the Host Manager.
 * 
 * @author David Majda
 */
public class HostsModule extends Module implements EventListener {
	/** Class instance (singleton pattern). */
	private static HostsModule instance;
	
	private static final String RSL_HELP_TYPE_OS = "os";
	private static final String RSL_HELP_TYPE_APP = "app";
	
	private static final String VALUE_TYPE_BOOLEAN = "boolean";
	private static final String VALUE_TYPE_INTEGER = "integer";
	private static final String VALUE_TYPE_DOUBLE = "double";
	private static final String VALUE_TYPE_STRING = "string";
	private static final String VALUE_TYPE_REGEXP = "regexp";
	private static final String VALUE_TYPE_VERSION = "version";

	private TaskManagerReference taskManager = new TaskManagerReference();
	private ServiceReference<HostManagerInterface> hostManager
		= new ServiceReference<HostManagerInterface>(
			taskManager,
			HostManagerService.SERVICE_NAME,
			HostManagerService.REMOTE_INTERFACE_MAIN,
			HostManagerService.SERVICE_HUMAN_NAME
		);
	private LoadServerReference loadServer
		= new LoadServerReference(taskManager);
	
	/**
	 * Little utility class, which compares information about two hosts (objects
	 * implementing <code>HostInfoInterface</code>) in the host list, so it can be
	 * sorted alphabetically by their hostnames.
	 * 
	 * @author David Majda
	 */
	private static class HostListComparator implements Comparator<HostInfoInterface> {
		/**
		 * Compares information about two hosts (objects implementing
		 * <code>HostInfoInterface</code>). Comparison key is the hostname
		 * (comparison is alphabetical and case-insensitive).
		 *   
		 * @param o1 information about first host
		 * @param o2 information about second host
		 * @return a negative integer, zero, or a positive integer as the first
		 *          argument is less than, equal to, or greater than the second
		 */
		public int compare(HostInfoInterface o1, HostInfoInterface o2) {
			return o1.getHostName().compareToIgnoreCase(o2.getHostName());
		}
	}
	
	/**
	 * Little utility class, which compares information about two hosts (objects
	 * implementing <code>HostInfoInterface</code>) in the host list in group
	 * edit form, so it can be sorted by their incidence to the group and than
	 * alphabetically by their hostnames.
	 * 
	 * @author David Majda
	 */
	private static class HostListInGroupComparator implements Comparator<HostInfoInterface> {
		/** Hosts in edited group. */
		private List<String> groupHosts;
		
		/**
		 * Allocates a new <code>HostListInGroupComparator</code> object.
		 * 
		 * @param groupHosts hosts in edited group
		 */
		public HostListInGroupComparator(List<String> groupHosts) {
			this.groupHosts = groupHosts;
		}

		/**
		 * Compares information about two hosts (objects implementing
		 * <code>HostInfoInterface</code>). Primary comparison key is incidence to
		 * given group (hosts belonging to the group go first), secondary key is the
		 * hostname (comparison is alphabetical and case-insensitive).
		 *   
		 * @param o1 information about first host
		 * @param o2 information about second host
		 * @return a negative integer, zero, or a positive integer as the first
		 *          argument is less than, equal to, or greater than the second
		 */
		public int compare(HostInfoInterface o1, HostInfoInterface o2) {
			String hostname1 = o1.getHostName();
			String hostname2 = o2.getHostName();
			
			/* One of the host belongs to the group, the other does not. */
			boolean groupContainsHost1 = groupHosts.contains(hostname1);
			boolean groupContainsHost2 = groupHosts.contains(hostname2);
			if (groupContainsHost1 && !groupContainsHost2) {
				return -1;
			}
			if (!groupContainsHost1 && groupContainsHost2) {
				return 1;
			}
				
			/* Both hosts belong to the group or both do not. */
			return o1.getHostName().compareToIgnoreCase(o2.getHostName());
		}
	}

	/**
	 * Little utility class, which compares information about two groups
	 * (<code>HostGroup</code> objects) in the group list, so it can be
	 * sorted alphabetically by their names (with the exception that "Universe"
	 * group is allways first).
	 * 
	 * @author David Majda
	 */
	private static class GroupListComparator implements Comparator<HostGroup> {
		/**
		 * Compares information about two groups (<code>HostGroup</code> objects).
		 * Comparison key is the group name (comparison is alphabetical and
		 * case-insensitive). There is one exception: "Universe" group is allways first.
		 *   
		 * @param o1 information about first group
		 * @param o2 information about second group
		 * @return a negative integer, zero, or a positive integer as the first
		 *          argument is less than, equal to, or greater than the second
		 */
		public int compare(HostGroup o1, HostGroup o2) {
			/* "Universe" group. */
			if (o1.isDefaultGroup()) {
				return -1;
			}
			if (o2.isDefaultGroup()) {
				return 1;
			}
			
			/* Other groups. */
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	}
	
	/**
	 * Little utility class, which compares information about two alias
	 * definitions (<code>SoftwareAliasDefinition</code> objects) in the alias
	 * list, so it can be sorted alphabetically by their names.
	 * 
	 * @author David Majda
	 */
	private static class AliasListComparator implements Comparator<SoftwareAliasDefinition> {
		/**
		 * Compares information about two alias definitions
		 * (<code>SoftwareAliasDefinition</code> objects). Comparison key is the
		 * alias definition name and (comparison is alphabetical and
		 * case-insensitive).
		 *   
		 * @param o1 information about first alias definition
		 * @param o2 information about second alias definition
		 * @return a negative integer, zero, or a positive integer as the first
		 *          argument is less than, equal to, or greater than the second
		 */
		public int compare(SoftwareAliasDefinition o1, SoftwareAliasDefinition o2) {
			return o1.getAliasName().compareToIgnoreCase(o2.getAliasName());
		}
	}

	/**
	 * Allocates a new <code>HostsModule</code> object. Construcor is private so only
	 * instance in <code>instance</code> field can be constructed (singleton
	 * pattern).
	 */
	private HostsModule() {
		super();
		
		/* Initialize general module info... */
		id = "hosts";
		name = "Hosts";
		defaultAction = "host-list";
		
		menu = new MenuItem[] {
				new MenuItem("host-list", "Hosts"), 
				//new MenuItem("host-add", "Add host"),
				new MenuItem("group-list", "Groups"),
				new MenuItem("group-add", "Add group"),
				new MenuItem("alias-list", "Aliases"),
				new MenuItem("alias-add", "Add alias"),
		};
	
		eventManager.registerEventListener(this);
	}

	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 */
	public static HostsModule getInstance() {
		if (instance == null) {
			 instance = new HostsModule();
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
		taskManager.drop();
		hostManager.drop();
		loadServer.drop();
	}
	
	/**
	 * Invokes method for given action, which is found by reflection.
	 * 
	 * The method is overriden in this class to allow catching and processing
	 * exceptions thrown in executed methods in one place (so no big ugly
	 * <code>try { ... } catch { ... }</code> is needed in each method).
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @param action action to invoke
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if some kind of methode invocation error occurs 
	 * @throws InvocationTargetException wraps an exception thrown by the invoked
	 *                                    method
	 *                                      
	 * @see cz.cuni.mff.been.webinterface.modules.Module#invokeMethodForAction(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void invokeMethodForAction(HttpServletRequest request,
			HttpServletResponse response, String action) throws ServletException,
			IOException, InvocationTargetException {
		try {
			super.invokeMethodForAction(request, response, action);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof ConnectException) {
				throw new InvocationTargetException(
					new ConnectException(
						"<strong>Can't execute remote call to the Host Manager."
						+ "</strong><br /><br />"
						+ "Try to reload the page. If the error persists after multiple reloads, "
						+ "go to the <a href=\"../../services/>Services</a> tab and make "
						+ "sure the Host Manager is running.<br /><br/>"
						+ "Most probale causes of this error are network-related problems or "
						+ "crash of the service."
					),
					e.getMessage()
				);
			} else {
				throw e;
			}
		}
	}

	private void closeOperationHandle(HttpServletRequest request, String handleParamName)
			throws MissingParamException, InvalidParamValueException,
			RemoteException, ComponentInitializationException {
		params.ensureExists(handleParamName);
		params.ensureCondition(handleParamName, params.isLong(handleParamName));
		try {
			hostManager.get().removeOperationStatus(
					OperationHandle.valueOf(request.getParameter(handleParamName))
			);
		} catch (IllegalArgumentException e) {
			throw new InvalidParamValueException("Parameter \"" + handleParamName
				+ "\" has invalid value.");
		}
	}

	/**
	 * Handles the "operation-status" action.
	 *  
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void operationStatus(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, InvalidParamValueException,
			ServletException, IOException, ComponentInitializationException {
		params.ensureExists("handle");
		
		params.ensureCondition("handle", params.isLong("handle"));
		
		HostOperationStatus status;
		try {
			status = hostManager.get().getOperationStatus(
				OperationHandle.valueOf(request.getParameter("handle"))
			);
		} catch (IllegalArgumentException e) {
			status = null;
		}

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("status", status);
		
		response.setContentType("text/javascript");
		page.writeTemplate("hosts-operation-status", data);
	}

	/**
	 * Handles the "host-list" action. 
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void hostList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, MissingParamException,
			InvalidParamValueException, ComponentInitializationException {
		if (params.exists("action")) {
			String action = request.getParameter("action");
			if (action.equals("added")) {
				closeOperationHandle(request, "handle");
				infoMessages.addTextMessage("Host added successfully.");
			} else if (action.equals("deleted")) {
				infoMessages.addTextMessage("Host deleted successfully.");
			} else {
				throw new InvalidParamValueException("Parameter \"action\" has invalid value.");
			}
		}
		
		List<HostInfoInterface> hosts = new LinkedList<HostInfoInterface>();
		for (String hostName: hostManager.get().getHostNames()) {
			try {
				hosts.add(hostManager.get().getHostInfo(hostName));
			} catch (ValueNotFoundException e) {
				/*
				 * We ignore this exception, which could happen only if someone changes
				 * the host database under our hands. The host simply won't be included
				 * in the list.
				 */
			}
		}
		
		Collections.sort(hosts, new HostListComparator());
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("hosts", hosts);
		data.put("hostStatusMap", loadServer.get().getHostStatusMap());
				
		page.setTitle("Hosts");
		page.writeHeader();
		page.writeTemplate("hosts-host-list", data);
		page.writeFooter();
	}

	/**
	 * Handles the "host-javascript-list" action. 
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void hostJavascriptList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, InvalidParamValueException,
			ComponentInitializationException {

		List<HostInfoInterface> hosts = new LinkedList<HostInfoInterface>();
		try {
			for (String hostName: hostManager.get().getHostNames()) {
				try {
					hosts.add(hostManager.get().getHostInfo(hostName));
				} catch (ValueNotFoundException e) {
					/*
					 * We ignore this exception, which could happen only if someone changes
					 * the host database under our hands. The host simply won't be included
					 * in the list.
					 */
				}
			}
		} catch (ComponentInitializationException e) {
			/* This exception means that we can't connect to the Host manager.
			 * We don't want to report anything, just send the client empty list of
			 * hosts.
			 */ 
		}
					
		response.setContentType("Content-Type: text/javascript; charset=utf-8");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("hosts", hosts);
		page.writeTemplate("hosts-host-javascript-list", data);
	}

	/**
	 * Handles the "host-javascript-list-rsl" action. 
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void hostJavascriptListRsl(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, MissingParamException,
			InvalidParamValueException, ComponentInitializationException {
		params.ensureExists("rsl");
		
		HostInfoInterface[] hosts = {};
		String validationResult = RSLValidator.validate(request.getParameter("rsl"));
		if (validationResult == null) { /* RSL is lexically and syntactically correct. */
			try {
				hosts = hostManager.get().queryHosts(new RestrictionInterface[] {
						new RSLRestriction(request.getParameter("rsl"))
				});
			} catch (ValueNotFoundException e) {
				validationResult = Routines.htmlspecialchars(e.getMessage());
			} catch (ValueTypeIncorrectException e) {
				validationResult = Routines.htmlspecialchars(e.getMessage());
			} catch (HostManagerException e) {
				validationResult = "Error matching hosts: "
					+ Routines.htmlspecialchars(e.getMessage());
			}
		}
							
		response.setContentType("Content-Type: text/javascript; charset=utf-8");
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("validationResult", validationResult);
		data.put("hosts", hosts);
		page.writeTemplate("hosts-host-javascript-list-rsl", data);
	}

	/**
	 * Handles the "host-add" action.
	 *  
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws MissingParamException if some required parameter is missing
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void hostAdd(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, ServletException, IOException,
			ComponentInitializationException {
		HashMap<String, Object> data = new HashMap<String, Object>();

		if (params.exists("add")) {
			params.ensureExists("hostname");
		
			params.checkCondition(params.notEmpty("hostname"),
				"Enter non-empty hostname.");
			try {
				params.checkCondition(!hostManager.get().isHostInDatabase(request.getParameter("hostname")),
					"Entered hostname is already in the database.");
			} catch (UnknownHostException e) {
				params.checkCondition(false, "Unknown hostname: " + request.getParameter("hostname") + ".");
			}
		
			if (errorMessages.isEmpty()) {
				OperationHandle handle = hostManager.get().addHost(
					request.getParameter("hostname")
				);
				
				HashMap<String, String> actionParams = new HashMap<String, String>();
				actionParams.put("hostname", request.getParameter("hostname"));
				actionParams.put("handle", handle.toString());
				page.redirectToAction("host-adding", actionParams);
				return;
			} else {
				data.put("hostname", request.getParameter("hostname"));
			}
		} else if (params.exists("cancel")) {
			page.redirectToAction("host-list");
			return;
		} else {
			data.put("hostname", "");
		}
		
		page.setTitle("Add host");
		page.setFocusedElement(0, "hostname");
		page.writeHeader();
		page.writeTemplate("hosts-host-add", data);
		page.writeFooter();
	}
	
	/**
	 * Handles the "host-adding" action.
	 *  
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void hostAdding(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, InvalidParamValueException,
			ServletException, IOException, ComponentInitializationException {
		params.ensureExist("hostname", "handle");
		
		params.ensureCondition("handle", params.isLong("handle"));
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("hostname", request.getParameter("hostname"));
		data.put("handle", OperationHandle.valueOf(request.getParameter("handle")));

		page.setTitle("Adding host");
		page.writeHeader();
		page.writeTemplate("hosts-host-adding", data);
		page.writeFooter();
	}

	private Map<String, String> buildUserProperties(NameValuePair[] userProperties) {
		Map<String, String> result = new HashMap<String, String>();
		for (NameValuePair property: userProperties) {
			result.put(property.getName(), property.getValue().toString());
		}
		return result;
		
	}
	
	/**
	 * Handles the "host-details" action.
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void hostDetails(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, InvalidParamValueException, 
			ServletException, IOException, ComponentInitializationException {
		String activeSheet = "general-sheet";

		if (params.exists("action")) {
			String action = request.getParameter("action");
			if (action.equals("refreshed")) {
				closeOperationHandle(request, "handle");
				infoMessages.addTextMessage("Host configuration refreshed successfully.");
			} else if (action.equals("user-property-added")) {
				infoMessages.addTextMessage("User property added successfully.");
				activeSheet = "user-properties-sheet";
			} else if (action.equals("user-properties-edited")) {
				infoMessages.addTextMessage("User properties edited successfully.");
				activeSheet = "user-properties-sheet";
			} else if (action.equals("user-property-deleted")) {
				infoMessages.addTextMessage("User property deleted successfully.");
				activeSheet = "user-properties-sheet";
			} else {
				throw new InvalidParamValueException("Parameter \"action\" has invalid value.");
			}
		}

		params.ensureExists("hostname");

		Date[] dates = null;
		try {
			dates = hostManager.get().getHostHistoryDates(request.getParameter("hostname"));
		} catch (ValueNotFoundException e) {
			throw new InvalidParamValueException("Parameter \"hostname\" has invalid value.");
		}

		Date date = null;
		if (params.exists("date")) {
			params.ensureCondition("date", params.isLong("date"));
			date = new Date(Long.valueOf(request.getParameter("date")));
		} else {
			if (dates.length > 0) {
				date = dates[0];
			}
		}
		
		HostInfoInterface hostInfo = null;
		try {
			hostInfo = hostManager.get().getHostHistoryEntry(
				request.getParameter("hostname"),
				date
			);
		} catch (ValueNotFoundException e) {
			throw new InvalidParamValueException("Parameter \"hostname\" has invalid value.");
		} catch (HostDatabaseException e) {
			errorMessages.addTextMessage("Error retrieving information about host: "
				+ e.getMessage() + ".");
			return;																					// To avoid warnings below.
		}																							// Nothing can be done anyway.
		
		Map<String, String> userProperties = null;
		String newUserPropertyName = null;
		String newUserPropertyType = null;
		String newUserPropertyValue = null;
		
		if (params.exists("add")) {
			params.ensureExist("new-name", "new-type", "new-value-boolean", "new-value-text");
			Set<String> types = new HashSet<String>();
			types.add(VALUE_TYPE_BOOLEAN);
			types.add(VALUE_TYPE_INTEGER);
			types.add(VALUE_TYPE_DOUBLE);
			types.add(VALUE_TYPE_STRING);
			types.add(VALUE_TYPE_REGEXP);
			types.add(VALUE_TYPE_VERSION);
			params.ensureCondition("new-type", params.isInSet("new-type", types));
			
			params.checkCondition(params.notEmpty("new-name"),
			  "Enter non-empty property name.");
			if (params.notEmpty("new-name")) {
				boolean isValidTypeName = PropertyTreeFactory.isValidTypeName(request.getParameter("new-name"));
				params.checkCondition( isValidTypeName,
					"Enter valid property name.");
				if( isValidTypeName ){
					params.checkCondition(!hostInfo.hasUserProperty(request.getParameter("new-name")),
							"Property \"" + request.getParameter("new-name") + "\" already exists.");
				}
			}
			
			Map<String, String> typesToNames = new HashMap<String, String>();
			typesToNames.put(VALUE_TYPE_BOOLEAN, "boolean");
			typesToNames.put(VALUE_TYPE_INTEGER, "text");
			typesToNames.put(VALUE_TYPE_DOUBLE, "text");
			typesToNames.put(VALUE_TYPE_STRING, "text");
			typesToNames.put(VALUE_TYPE_REGEXP, "text");
			typesToNames.put(VALUE_TYPE_VERSION, "text");
			
			Map<String, ValueCommonInterface> typesToValues
				= new HashMap<String, ValueCommonInterface>();
			typesToValues.put(VALUE_TYPE_BOOLEAN, new ValueBoolean(false));
			typesToValues.put(VALUE_TYPE_INTEGER, new ValueInteger(0));
			typesToValues.put(VALUE_TYPE_DOUBLE, new ValueDouble(0.0));
			typesToValues.put(VALUE_TYPE_STRING, new ValueString(""));
			typesToValues.put(VALUE_TYPE_REGEXP, new ValueRegexp(""));
			typesToValues.put(VALUE_TYPE_VERSION, new ValueVersion(""));

			NameValuePair[] properties = new NameValuePair[] { new NameValuePair(
				typesToNames.get(request.getParameter("new-type")),
				typesToValues.get(request.getParameter("new-type"))
			) };

			UserPropertiesHandler handler = new UserPropertiesHandler(request, "new-value", false);
			handler.ensure(properties);
			handler.check(properties);
			
			if (errorMessages.isEmpty()) {
				NameValuePair[] newProperties = handler.getValues(properties);
				for (NameValuePair property: newProperties) {
					hostInfo.addUserProperty(request.getParameter("new-name"), property.getValue());
				}
				try {
					hostManager.get().updateUserProperties(hostInfo);
				} catch (InvalidArgumentException e) {
					throw new InvalidParamValueException("Some parameter has invalid value.");
				} catch (HostDatabaseException e) {
					errorMessages.addTextMessage("Error adding host properties: "
						+ e.getMessage() + ".");
				}

				HashMap<String, String> actionParams = new HashMap<String, String>();
				actionParams.put("hostname", request.getParameter("hostname"));
				actionParams.put("action", "user-property-added");
				page.redirectToAction("host-details", actionParams);
				return;
			} else {
				userProperties = buildUserProperties(hostInfo.getUserProperties());
				newUserPropertyName = request.getParameter("new-name");
				newUserPropertyType = request.getParameter("new-type");
				newUserPropertyValue = request.getParameter(
					request.getParameter("new-type").equals(VALUE_TYPE_BOOLEAN)
						? "new-value-boolean"
						: "new-value-text"
				);
				activeSheet = "user-properties-sheet";
			}
		} else if (params.exists("edit-all")) {
			NameValuePair[] properties = hostInfo.getUserProperties();
			UserPropertiesHandler handler = new UserPropertiesHandler(request, "value", true);
			handler.ensure(properties);
			handler.check(properties);
			
			if (errorMessages.isEmpty()) {
				NameValuePair[] newProperties = handler.getValues(properties);
				for (NameValuePair property: newProperties) {
					hostInfo.putUserProperty(property);
				}
				try {
					hostManager.get().updateUserProperties(hostInfo);
				} catch (InvalidArgumentException e) {
					throw new InvalidParamValueException("Some parameter has invalid value.");
				} catch (HostDatabaseException e) {
					errorMessages.addTextMessage("Error editing host properties: "
						+ e.getMessage() + ".");
				}
				
				HashMap<String, String> actionParams = new HashMap<String, String>();
				actionParams.put("hostname", request.getParameter("hostname"));
				actionParams.put("action", "user-properties-edited");
				page.redirectToAction("host-details", actionParams);
				return;
			} else {
				userProperties = new HashMap<String, String>();
				for (NameValuePair property: properties) {
					String paramValue = request.getParameter("value-" + property.getName());
					userProperties.put(
						property.getName(),
						paramValue != null ? paramValue : property.getValue().toString()
					); 
				}
				newUserPropertyName = "";
				newUserPropertyType = "boolean";
				newUserPropertyValue = "";
				activeSheet = "user-properties-sheet";
			}
		} else  if (params.existsIndexed("delete")) {
			String index = params.getIndex("delete");
			try {
				hostInfo.removeUserProperty(index);
				hostManager.get().updateUserProperties(hostInfo);

				HashMap<String, String> actionParams = new HashMap<String, String>();
				actionParams.put("hostname", request.getParameter("hostname"));
				actionParams.put("action", "user-property-deleted");
				page.redirectToAction("host-details", actionParams);
				return;
			} catch (ValueNotFoundException e) {
				throw new InvalidParamValueException("Parameter \"delete\" has invalid value.");
			} catch (InvalidArgumentException e) {
				throw new InvalidParamValueException("Parameter \"delete\" has invalid value.");
			} catch (HostDatabaseException e) {
				errorMessages.addTextMessage("Error deleting host properties: "
					+ e.getMessage() + ".");
			}
		} else {
			userProperties = buildUserProperties(hostInfo.getUserProperties());
			newUserPropertyName = "";
			newUserPropertyType = "boolean";
			newUserPropertyValue = "";
		}
		
		HostDataStatisticianInterface loadData = null;
		try {
			loadData = loadServer.get().getStatsProvider(request.getParameter("hostname"));
			try {
				loadData.refresh();
			} catch (InputParseException e) {
				/* I don't care at all if the files are corrupted. That's not my
				 * problem.
				 */
			}
		} catch (LoadMonitorException e) {
			errorMessages.addTextMessage("Error retrieving load data: " + e.getMessage());
		} catch (ValueNotFoundException e) {
			throw new InvalidParamValueException("Parameter \"hostname\" has invalid value.");
		}
		
		TaskEntry[] tasks = taskManager.get().getTasksOnHost(request.getParameter("hostname")); 
		Map<TaskEntry, CheckPoint[]> checkpoints = TaskUtils.getCheckPointsForTasks(
			taskManager.get(),
			tasks
		);

		LogRecord[] logRecords = null;
		try {
			logRecords = LogUtils.getLogRecordsForTasks(
				taskManager.get(), 
				taskManager.get().getTasksOnHost(request.getParameter("hostname"))
			);
		} catch (LogStorageException e) {
			errorMessages.addTextMessage("Error retrieving logs: " + e.getMessage());
		}
									
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("date", date);
		data.put("hostInfo", hostInfo);
		data.put("dates", dates);
		data.put("userProperties", userProperties);
		data.put("newUserPropertyName", newUserPropertyName);
		data.put("newUserPropertyType", newUserPropertyType);
		data.put("newUserPropertyValue", newUserPropertyValue);
		data.put("loadData", loadData);
		data.put("tasks", tasks);
		data.put("checkpoints", checkpoints);
		data.put("logRecords", logRecords);
		data.put("logFields", EnumSet.of(
			LogRecord.Fields.CONTEXT,
			LogRecord.Fields.TASK_ID,
			LogRecord.Fields.HOSTNAME,
			LogRecord.Fields.TIMESTAMP,
			LogRecord.Fields.LEVEL,
			LogRecord.Fields.MESSAGE
		));
		data.put("activeSheet", activeSheet);
			
		page.setTitle("Host details: " + hostInfo.getHostName());
		page.writeHeader();
		page.writeTemplate("hosts-host-details", data);
		page.writeFooter();
	}
	
	/**
	 * Handles the "host-refresh" action.
	 *  
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void hostRefresh(HttpServletRequest request, HttpServletResponse response)
			throws IOException, MissingParamException, InvalidParamValueException,
			ServletException, ComponentInitializationException {
		params.ensureExists("hostname");
		
		OperationHandle handle = null;
		try {
			handle = hostManager.get().refreshHost(request.getParameter("hostname"));
		} catch (ValueNotFoundException e) {
			throw new InvalidParamValueException("Parameter \"hostname\" has invalid value.");
		} catch (UnknownHostException e) {
			throw new InvalidParamValueException("Unknown hostname: " + request.getParameter("hostname") + ".");
		}
			
		HashMap<String, String> actionParams = new HashMap<String, String>();
		actionParams.put("hostname", request.getParameter("hostname"));
		actionParams.put("handle", handle.toString());
		page.redirectToAction("host-refreshing", actionParams);
		return;
	}

	/**
	 * Handles the "host-refreshing" action.
	 *  
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void hostRefreshing(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, InvalidParamValueException,
			ServletException, IOException, ComponentInitializationException {
		params.ensureExist("hostname", "handle");
		
		params.ensureCondition("handle", params.isLong("handle"));
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("hostname", request.getParameter("hostname"));
		data.put("handle", OperationHandle.valueOf(request.getParameter("handle")));

		page.setTitle("Refreshing host configuration");
		page.writeHeader();
		page.writeTemplate("hosts-host-refreshing", data);
		page.writeFooter();
	}

	/**
 	 * Handles the "host-delete" action.
 	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws IOException if some I/O error occurs
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void hostDelete(HttpServletRequest request, HttpServletResponse response)
			throws IOException, MissingParamException, InvalidParamValueException,
			ComponentInitializationException {
		params.ensureExists("hostname");
		
		try {
			hostManager.get().removeHost(request.getParameter("hostname"));
		} catch (ValueNotFoundException e) {
			throw new InvalidParamValueException("Parameter \"hostname\" has invalid value.");
		} catch (HostDatabaseException e) {
			errorMessages.addTextMessage("Error deleting host: "
					+ e.getMessage() + ".");
		}
		
		if (errorMessages.isEmpty()) {
			HashMap<String, String> actionParams = new HashMap<String, String>();
			actionParams.put("action", "deleted");
			page.redirectToAction("host-list", actionParams);
		}
	}

	/**
	 * Handles the "group-list" action.
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void groupList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, InvalidParamValueException,
			ComponentInitializationException {
		if (params.exists("action")) {
			String action = request.getParameter("action");
			if (action.equals("added")) {
				infoMessages.addTextMessage("Group added successfully.");
			} else if (action.equals("edited")) {
				infoMessages.addTextMessage("Group edited successfully.");
			} else if (action.equals("deleted")) {
				infoMessages.addTextMessage("Group deleted successfully.");
			} else {
				throw new InvalidParamValueException("Parameter \"action\" has invalid value.");
			}
		}

		List<HostGroup> groups = new LinkedList<HostGroup>();
		for (String groupName: hostManager.get().getGroupNames()) {
			try {
				groups.add(hostManager.get().getGroup(groupName));
			} catch (ValueNotFoundException e) {
				/**
				 * We ignore this exception, which could happen only if someone changes
				 * the host database under our hands. The group simply won't be
				 * included in the list.
				 */
			}
		}

		Collections.sort(groups, new GroupListComparator());
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("groups", groups);
			
		page.setTitle("Groups");
		page.writeHeader();
		page.writeTemplate("hosts-group-list", data);
		page.writeFooter();
	}
	
	public void groupAddOrEdit(HttpServletRequest request, boolean editing) throws
			MissingParamException, InvalidParamValueException, ServletException,
			IOException, ComponentInitializationException {
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		if ((editing && params.exists("edit")) || (!editing && params.exists("add"))) {
			HostGroup group = null;
			if (editing) {
				params.ensureExists("group");
				try {
					group = hostManager.get().getGroup(request.getParameter("group"));
				} catch (ValueNotFoundException e) {
					throw new InvalidParamValueException("Parameter \"group\" has invalid value.");
				}
				if (!group.isDefaultGroup()) {
					params.ensureExists("name");
				}
			} else {
				params.ensureExists("name");
				/* If the group name is empty or equal to "Universe", create temporary
				 * group with random name, because HostGroup constructor doesn't allow
				 * empty group name and will mess up when creating group with name "All
				 * hosts".
				 * 
				 * This is to simplify the code bellow, where we can assume that the
				 * group object is allways created.
				 * 
				 * Incorrect group name doesn't harm anything, because if the "name"
				 * parameter is empty or equal to "Universe", group won't be added to
				 * the Host Manager.
				 */
				if (params.isEmpty("name")
						|| request.getParameter("name").equals(HostGroup.DEFAULT_GROUP_NAME)) {
					try {
						group = new HostGroup(Double.toString(Math.random()));
					} catch (InvalidArgumentException e) {
						assert false: "Should not happen, group name should be allways valid.";
						return;																		// Formal, to avoid warnings.
					}
				} else {
					try {
						group = new HostGroup(request.getParameter("name"));
					} catch (InvalidArgumentException e) {
						assert false: "Should not happen, non-empty group name should be "
							+ "allways valid.";
						return;																		// Formal, to avoid warnings.
					}
				}
			}						
			params.ensureExists("description");
			if (!group.isDefaultGroup()) {
				params.ensureExists("rsl");
			}
		
			int hostCount = hostManager.get().getHostCount();
			String[] hostNames = hostManager.get().getHostNames();
			
			if (!group.isDefaultGroup()) {
				for (int i = 0; i < hostCount; i++) {
				  String paramName = params.makeIndexed("group-hosts", hostNames[i]);
					if (params.exists(paramName)) {
						params.ensureCondition(paramName, params.isCheckboxBool(paramName));
					}
				}
			}
			
			if (!group.isDefaultGroup()) {
				params.checkCondition(params.notEmpty("name"),
					"Enter non-empty name.");
				if (errorMessages.isEmpty()) {
					if (editing) {
						if (!request.getParameter("name").equals(group.getName())) {
							params.checkCondition(!hostManager.get().isGroup(request.getParameter("name")),
								"Entered name is already in the database.");
						}
					} else {
						params.checkCondition(!hostManager.get().isGroup(request.getParameter("name")),
							"Entered name is already in the database.");
					}
				}
			}

			if (errorMessages.isEmpty()) {
				try {
					if (editing
							&& !group.isDefaultGroup()
							&& !request.getParameter("group").equals(request.getParameter("name"))) {
						hostManager.get().renameGroup(
							request.getParameter("group"),
							request.getParameter("name")
						);
						group = hostManager.get().getGroup(request.getParameter("name"));
					}
					group.setDescription(request.getParameter("description"));
					if (!group.isDefaultGroup()) {
						if (editing) {
							group.removeAllHosts();
						}
						for (int i = 0; i < hostCount; i++) {
						  String paramName = params.makeIndexed("group-hosts", hostNames[i]);
							if (params.exists(paramName)) {
								group.addHost(hostNames[i]);
							}
						}
					}
					if (editing) {
						hostManager.get().updateGroup(group);
					} else {
						hostManager.get().addGroup(group);
					}
				} catch (Exception e) {
					errorMessages.addTextMessage("Error "
						+ (editing ? "editing" : "adding")
						+ " group \"" + request.getParameter("name") + "\": "
						+ e.getMessage());
				}
			}
			
			if (errorMessages.isEmpty()) {
				HashMap<String, String> actionParams = new HashMap<String, String>(); 
				actionParams.put("action", editing ? "edited" : "added");
				page.redirectToAction("group-list", actionParams);
				return;
			} else {
				if (editing) {
					data.put("group", request.getParameter("group"));
				}
				data.put("name", group.isDefaultGroup() ? group.getName() : request.getParameter("name"));
				data.put("description", request.getParameter("description"));
				data.put("isDefault", new Boolean(group.isDefaultGroup()));
				
				List<HostInfoInterface> hosts = new LinkedList<HostInfoInterface>();
				for (String hostName: hostNames) {
					try {
						hosts.add(hostManager.get().getHostInfo(hostName));
					} catch (ValueNotFoundException e) {
						/*
						 * We ignore this exception, which could happen only if someone changes
						 * the host database under our hands. The host simply won't be included
						 * in the list.
						 */
					}
				}
				
				List<String> groupHosts = new LinkedList<String>();
				for (int i = 0; i < hostCount; i++) {
				  String paramName = params.makeIndexed("group-hosts", hostNames[i]);
					if (params.exists(paramName)) {
						groupHosts.add(hostNames[i]);
					}
				}

				Collections.sort(hosts, new HostListInGroupComparator(groupHosts));

				data.put("hosts", hosts);
				data.put("groupHosts", groupHosts);
				if (group.isDefaultGroup()) {
					data.put("rsl", request.getParameter("rsl"));
				}
				data.put("editing", new Boolean(editing));
			}
		} else if (params.exists("cancel")) {
			page.redirectToAction("group-list");
			return;
		} else {
			List<String> groupHosts = new ArrayList<String>();
			if (editing) {
				params.ensureExists("group");
				HostGroup group = null;
				try {
					group = hostManager.get().getGroup(request.getParameter("group"));
				} catch (ValueNotFoundException e) {
					throw new InvalidParamValueException("Parameter \"group\" has invalid value.");
				}
				data.put("group", request.getParameter("group"));
				data.put("name", request.getParameter("group"));
				data.put("description", group.getDescription());
				data.put("isDefault", new Boolean(group.isDefaultGroup()));
				for (String host: group) {
					groupHosts.add(host);
				}
				data.put("groupHosts", groupHosts);
				if (group.isDefaultGroup()) {
					data.put("rsl", "");
				}
			} else {
				data.put("name", "");
				data.put("description", "");
				data.put("isDefault", new Boolean(false));
				data.put("groupHosts", groupHosts);
				data.put("rsl", "");
			}
			data.put("editing", new Boolean(editing));

			List<HostInfoInterface> hosts = new LinkedList<HostInfoInterface>();
			for (String hostName: hostManager.get().getHostNames()) {
				try {
					hosts.add(hostManager.get().getHostInfo(hostName));
				} catch (ValueNotFoundException e) {
					/*
					 * We ignore this exception, which could happen only if someone changes
					 * the host database under our hands. The host simply won't be included
					 * in the list.
					 */
				}
			}

			Collections.sort(hosts, new HostListInGroupComparator(groupHosts));
			
			data.put("hosts", hosts);
		}
		
		page.setTitle(editing ? "Edit group: " + data.get("name") : "Add group");
		if (!((Boolean) data.get("isDefault")).booleanValue()) {
			page.setFocusedElement(0, "name");
		}
		page.writeHeader();
		page.writeTemplate("hosts-group-add-edit", data);
		page.writeFooter();
	}
	
	/**
	 * Handles the "group-add" action.
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void groupAdd(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, InvalidParamValueException,
			ServletException, IOException, ComponentInitializationException {
		groupAddOrEdit(request, false);
	}
	
	
	/**
	 * Handles the "group-edit" action.
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void groupEdit(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, InvalidParamValueException,
			ServletException, IOException, ComponentInitializationException {
		groupAddOrEdit(request, true);
	}
	
	/**
	 * Handles the "group-delete" action.
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws IOException if some I/O error occurs
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws RemoteException when something in RMI goes bad
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void groupDelete(HttpServletRequest request, HttpServletResponse response)
			throws IOException, MissingParamException, InvalidParamValueException,
			ComponentInitializationException {
		params.ensureExists("group");
		
		try {
			hostManager.get().removeGroup(request.getParameter("group"));
		} catch (ValueNotFoundException e) {
			throw new InvalidParamValueException("Parameter \"group\" has invalid value.");
		} catch (HostDatabaseException e) {
			errorMessages.addTextMessage("Error deleting group: "
					+ e.getMessage() + ".");
		}
		
		if (errorMessages.isEmpty()) {
			HashMap<String, String> actionParams = new HashMap<String, String>();
			actionParams.put("action", "deleted");
			page.redirectToAction("group-list", actionParams);
		}
	}

	/**
	 * Handles the "alias-list" action. 
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void aliasList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, MissingParamException,
			InvalidParamValueException, ComponentInitializationException {
		if (params.exists("action")) {
			String action = request.getParameter("action");
			if (action.equals("added")) {
				infoMessages.addTextMessage("Alias added successfully.");
			} else if (action.equals("edited")) {
				infoMessages.addTextMessage("Alias edited successfully.");
			} else if (action.equals("deleted")) {
				infoMessages.addTextMessage("Alias deleted successfully.");
			} else {
				throw new InvalidParamValueException("Parameter \"action\" has invalid value.");
			}
		}
		
		List<SoftwareAliasDefinition> aliases = new LinkedList<SoftwareAliasDefinition>();
		for (int i = 0; i < hostManager.get().getAliasDefinitionCount(); i++) {
			try {
				aliases.add(hostManager.get().getAliasDefinition(i));
			} catch (ValueNotFoundException e) {
				/*
				 * We ignore this exception, which could happen only if someone changes
				 * the software alias definition database under our hands. The software
				 * alias definition simply won't be included in the list.
				 */
			}
		}
		
		Collections.sort(aliases, new AliasListComparator());
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("aliases", aliases);
				
		page.setTitle("Software aliases");
		page.writeHeader();
		page.writeTemplate("hosts-alias-list", data);
		page.writeFooter();
	}

	private SoftwareAliasDefinition findSoftwareAliasDefinitionByName( String aliasName )
	throws RemoteException, ComponentInitializationException {
		SoftwareAliasDefinition alias = null;
		try {
			alias = hostManager.get().getAliasDefinitionByName( aliasName );
		} catch (ValueNotFoundException e) {
			/*
			 * We ignore this exception, which could happen only if someone changes
			 * the software alias definition database under our hands. The software
			 * alias definition simply won't be included in the list.
			 */
		}
		return alias;
	}
	
	private void deleteSoftwareAliasDefinitionByName( String aliasName )
	throws RemoteException, ComponentInitializationException, HostDatabaseException {
		try {
			hostManager.get().removeAliasDefinitionByName( aliasName );
		} catch (ValueNotFoundException e) {
			/*
			 * This function has a different behaviour than the find... version.
			 * It used to be a terrible non-atomic read-modify-write stuff that distinguished
			 * between two situations (alias not found initially and alias removed concurrently).
			 * However, this operation should be atomic and both of the two situations must result
			 * in the same behaviour. Strictly speaking, there is (from the theoretical point
			 * of view) absolutely no way to tell one of them from the other.
			 */
			throw new IllegalArgumentException("Alias \"" + aliasName + "\" not found.");
		}
	}

	private void checkRSLParam(HttpServletRequest request, String paramName,
			String paramDescription) {
		String rsl = request.getParameter(paramName);
		String validationResult = RSLValidator.validate(rsl);
		if (validationResult != null) {
			errorMessages.addHTMLMessage(paramDescription + ": " + validationResult);
		}
	}

	public void aliasAddOrEdit(HttpServletRequest request, boolean editing) throws
			MissingParamException, InvalidParamValueException, ServletException,
			IOException, ComponentInitializationException {
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		if ((editing && params.exists("edit")) || (!editing && params.exists("add"))) {
			if (editing) {
				params.ensureExists("alias");
			}					
			params.ensureExist("alias-name", "result-name", "result-vendor",
				"result-version", "os-restriction", "app-restriction");
			
			params.checkCondition(params.notEmpty("alias-name"),
				"Enter non-empty alias name.");
			if (editing) {
				params.checkCondition(
					request.getParameter("alias-name").equals(request.getParameter("alias"))
						|| findSoftwareAliasDefinitionByName(request.getParameter("alias-name")) == null,
					"Entered alias name is already in the database.");
			} else {
				params.checkCondition(
					findSoftwareAliasDefinitionByName(request.getParameter("alias-name")) == null,
					"Entered alias name is already in the database."
				);
			}
			params.checkCondition(params.notEmpty("result-name"),
				"Enter non-empty result name.");
			if (params.notEmpty("os-restriction")) {
				checkRSLParam(request, "os-restriction", "Restriction for the operating system");
			}
			params.checkCondition(params.notEmpty("app-restriction"),
				"Enter non-empty restriction for the application.");
			if (params.notEmpty("app-restriction")) {
				checkRSLParam(request, "app-restriction", "Restriction for the application");
			}
			
			RSLRestriction osRestriction = null;
			RSLRestriction appRestriction = null;
			try {
				osRestriction = !request.getParameter("os-restriction").equals("")
					? new RSLRestriction(request.getParameter("os-restriction"))
					: null; 
				appRestriction = new RSLRestriction(request.getParameter("app-restriction"));
			} catch (IllegalArgumentException e) {
				errorMessages.addTextMessage(e.getMessage());
			}
				
			SoftwareAliasDefinition definition = null;
			if (errorMessages.isEmpty()) {
				try {
					definition = new SoftwareAliasDefinition(
						request.getParameter("alias-name"),
						request.getParameter("result-name"),
						request.getParameter("result-vendor"),
						request.getParameter("result-version"),
						osRestriction,
						appRestriction
					);
				} catch (InvalidArgumentException e) {
					errorMessages.addTextMessage(e.getMessage());
				}
			}
			
			if (errorMessages.isEmpty()) {
				try {
					if (editing) {
						try {
							deleteSoftwareAliasDefinitionByName(request.getParameter("alias"));
						} catch (IllegalArgumentException e) {
							throw new InvalidParamValueException("Parameter \"alias\" has invalid value.");
						}
					}
					hostManager.get().addAliasDefinition(definition);
					hostManager.get().rebuildAliasTableForAllHosts();
				} catch (HostDatabaseException e) {
					errorMessages.addTextMessage("Error "
						+ (editing ? "editing" : "adding")
						+ " software alias definition \""
						+ request.getParameter("alias") + "\": " + e.getMessage());
				}
			}

			if (errorMessages.isEmpty()) {
				HashMap<String, String> actionParams = new HashMap<String, String>(); 
				actionParams.put("action", editing ? "edited" : "added");
				page.redirectToAction("alias-list", actionParams);
			} else {
				data.put("alias", request.getParameter("alias"));
				data.put("aliasName", request.getParameter("alias-name"));
				data.put("resultName", request.getParameter("result-name"));
				data.put("resultVendor", request.getParameter("result-vendor"));
				data.put("resultVersion", request.getParameter("result-version"));
				data.put("osRestriction", request.getParameter("os-restriction"));
				data.put("appRestriction", request.getParameter("app-restriction"));
			}
		} else if (params.exists("cancel")) {
			page.redirectToAction("alias-list");
			return;
		} else {
			if (editing) {
				params.ensureExists("alias");
				SoftwareAliasDefinition alias
					= findSoftwareAliasDefinitionByName(request.getParameter("alias"));
				params.ensureCondition("alias", alias != null);
				
				if (null != alias) {
					data.put("alias", alias.getAliasName());
					data.put("aliasName", alias.getAliasName());
					data.put("resultName", alias.getResultName());
					data.put("resultVendor", alias.getResultVendor());
					data.put("resultVersion", alias.getResultVersion());
					RSLRestriction osRestriction = (RSLRestriction) alias.getOsRestriction(); 
					data.put("osRestriction", osRestriction != null
						? osRestriction.getRSLString()
						: "");
					RSLRestriction appRestriction = (RSLRestriction) alias.getAppRestriction(); 
					data.put("appRestriction", appRestriction.getRSLString());
				}
			} else {
				data.put("aliasName", "");
				data.put("resultName", "");
				data.put("resultVendor", "");
				data.put("resultVersion", "");
				data.put("osRestriction", "");
				data.put("appRestriction", "");
			}
			data.put("editing", new Boolean(editing));
		}
		
		page.setTitle(editing
			? "Edit software alias definition: " + data.get("aliasName")
			: "Add software alias definition");
		page.setFocusedElement(0, "alias-name");
		page.writeHeader();
		page.writeTemplate("hosts-alias-add-edit", data);
		page.writeFooter();
	}

	/**
	 * Handles the "alias-add" action.
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void aliasAdd(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, InvalidParamValueException,
			ServletException, IOException, ComponentInitializationException {
		aliasAddOrEdit(request, false);
	}
	
	
	/**
	 * Handles the "alias-edit" action.
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws IOException if some I/O error occurs
	 * @throws ServletException if including the template file fails
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void aliasEdit(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, InvalidParamValueException,
			ServletException, IOException, ComponentInitializationException {
		aliasAddOrEdit(request, true);
	}

	/**
	 * Handles the "alias-delete" action.
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws IOException if some I/O error occurs
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws ComponentInitializationException if the Host Manager can't
	 *                                           be initialized
	 */
	public void aliasDelete(HttpServletRequest request, HttpServletResponse response)
			throws IOException, MissingParamException, InvalidParamValueException,
			ComponentInitializationException {
		params.ensureExists("alias");
		
		try {
			deleteSoftwareAliasDefinitionByName(request.getParameter("alias"));
		} catch (IllegalArgumentException e) {
			throw new InvalidParamValueException("Parameter \"alias\" has invalid value.");
		} catch (HostDatabaseException e) {
			errorMessages.addTextMessage("Error deleting alias: " + e.getMessage() + ".");
		}
		
		if (errorMessages.isEmpty()) {
			HashMap<String, String> actionParams = new HashMap<String, String>();
			actionParams.put("action", "deleted");
			page.redirectToAction("alias-list", actionParams);
		}
	}

	/**
	 * Handles the "rsl-help" action. 
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws IOException if some I/O error occurs
	 * @throws MissingParamException if some required parameter is missing
	 * @throws InvalidParamValueException if required parameter contains invalid
	 *          value
	 * @throws ServletException if including the template file fails
	 */
	public void rslHelp(HttpServletRequest request, HttpServletResponse response)
			throws MissingParamException, InvalidParamValueException,
			ServletException, IOException {
		params.ensureExists("type");

		Set<String> types = new HashSet<String>();
		types.add(RSL_HELP_TYPE_OS);
		types.add(RSL_HELP_TYPE_APP);
		params.ensureCondition("type", params.isInSet("type", types));
		
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("type", request.getParameter("type"));

		page.setTitle("RSL help ("
			+ (request.getParameter("type").equals(RSL_HELP_TYPE_OS)
				? "operating system"
				: "application")
			+ " specific)");
		page.setShowTitle(false);
		page.setLayoutType(LayoutType.SIMPLE);
		page.writeHeader();
		page.writeTemplate("rsl-help", data);
		page.writeFooter();
	}
}
