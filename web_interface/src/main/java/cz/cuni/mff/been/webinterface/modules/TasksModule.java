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
package cz.cuni.mff.been.webinterface.modules;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;

import cz.cuni.mff.been.services.Names;
import org.xml.sax.SAXException;

import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.common.rsl.EqualsCondition;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import cz.cuni.mff.been.jaxb.BindingParser;
import cz.cuni.mff.been.jaxb.ConvertorException;
import cz.cuni.mff.been.jaxb.XSD;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.logging.LogRecord;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.logging.LogUtils;
import cz.cuni.mff.been.logging.OutputHandle;
import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.softwarerepository.MatchException;
import cz.cuni.mff.been.softwarerepository.PackageMetadata;
import cz.cuni.mff.been.softwarerepository.PackageQueryCallbackInterface;
import cz.cuni.mff.been.softwarerepository.PackageType;
import cz.cuni.mff.been.softwarerepository.SoftwareRepositoryInterface;
import cz.cuni.mff.been.softwarerepository.SoftwareRepositoryService;
import cz.cuni.mff.been.task.TaskUtils;
import cz.cuni.mff.been.taskmanager.CheckPoint;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper;
import cz.cuni.mff.been.taskmanager.TaskManagerException;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;
import cz.cuni.mff.been.taskmanager.data.ContextEntry;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;
import cz.cuni.mff.been.taskmanager.tasktree.IllegalAddressException;
import cz.cuni.mff.been.taskmanager.tasktree.MalformedAddressException;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeAddress;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeQuery;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeRecord;
import cz.cuni.mff.been.webinterface.InvalidParamValueException;
import cz.cuni.mff.been.webinterface.MissingParamException;
import cz.cuni.mff.been.webinterface.Routines;
import cz.cuni.mff.been.webinterface.event.Event;
import cz.cuni.mff.been.webinterface.event.EventListener;
import cz.cuni.mff.been.webinterface.ref.ServiceReference;
import cz.cuni.mff.been.webinterface.ref.TaskManagerReference;

import static cz.cuni.mff.been.services.Names.*;

/**
 * Web interface module for the Execution Environment.
 * 
 * @author David Majda
 */
public class TasksModule extends Module implements EventListener {
	/** Class instance (singleton pattern). */
	private static TasksModule instance;

	private static final String TASK_RUN_TYPE_GUI = "gui";
	private static final String TASK_RUN_TYPE_XML = "xml";
	private static final String TASK_TREE_PREFIX = "/webui/";

	private final TaskManagerReference taskManager = new TaskManagerReference();
	private final ServiceReference<SoftwareRepositoryInterface> softwareRepository = new ServiceReference<SoftwareRepositoryInterface>(
			taskManager,
			SoftwareRepositoryService.SERVICE_NAME,
			Service.RMI_MAIN_IFACE,
			SoftwareRepositoryService.SERVICE_HUMAN_NAME);
	private final ServiceReference<HostManagerInterface> hostManager = new ServiceReference<HostManagerInterface>(
			taskManager,
			Names.HOST_MANAGER_SERVICE_NAME,
			Names.HOST_MANAGER_REMOTE_INTERFACE_MAIN,
			Names.HOST_MANAGER_SERVICE_HUMAN_NAME);

	private long idIndex = 0;

	private static class TasksQuery implements PackageQueryCallbackInterface,
			Serializable {

		private static final long serialVersionUID = -3720817515690864148L;

		@Override
		public boolean match(PackageMetadata metadata) throws MatchException {
			return metadata.getType() == PackageType.TASK;
		}
	}

	/**
	 * Allocates a new <code>TasksModule</code> object. Construcor is private so
	 * only instance in <code>instance</code> field can be constructed
	 * (singleton pattern).
	 */
	private TasksModule() {
		super();

		/* Initialize general module info... */
		id = "tasks";
		name = "Tasks";
		defaultAction = "context-list";

		menu = new MenuItem[] {
				new MenuItem("context-list", "Contexts & Tasks"),
				new MenuItem("task-run", "Run task"),
				new MenuItem("task-logs", "Task logs"),
				new MenuItem("task-manager-logs", "Task Manager logs"),
				new MenuItem("task-tree", "Task Tree View"), };

		eventManager.registerEventListener(this);
	}

	/**
	 * Returns the only class instance (singleton pattern).
	 * 
	 * @return class instance
	 */
	public static TasksModule getInstance() {
		if (instance == null) {
			instance = new TasksModule();
		}
		return instance;
	}

	/**
	 * Called by the event manager when status of some service changes
	 * programatically or when the configuation changes.
	 * 
	 * We invalidate remote reference because they could be meaningless now.
	 * 
	 * @param event
	 *            sent event
	 */
	@Override
	public void receiveEvent(Event event) {
		taskManager.drop();
		softwareRepository.drop();
		hostManager.drop();
	}

	/**
	 * Invokes method for given action, which is found by reflection.
	 * 
	 * The method is overriden in this class to allow catching and processing
	 * exceptions thrown in executed methods in one place (so no big ugly
	 * <code>try { ... } catch { ... }</code> is needed in each method).
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @param action
	 *            action to invoke
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ServletException
	 *             if some kind of methode invocation error occurs
	 * @throws InvocationTargetException
	 *             wraps an exception thrown by the invoked method
	 * 
	 * @see cz.cuni.mff.been.webinterface.modules.Module#invokeMethodForAction(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void invokeMethodForAction(
			HttpServletRequest request,
			HttpServletResponse response,
			String action) throws ServletException, IOException,
			InvocationTargetException {
		try {
			super.invokeMethodForAction(request, response, action);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof ConnectException) {
				throw new InvocationTargetException(
						new ConnectException(
								"<strong>Can't execute remote call to the Task Manager."
										+ "</strong><br /><br />"
										+ "Try to reload the page. If the error persists after multiple reloads, "
										+ "make sure the Task Manager is running.<br /><br/>"
										+ "Most probale causes of this error are network-related problems or "
										+ "crash of the Task Manager."),
						e.getMessage());
			} else {
				throw e;
			}
		}
	}

	/**
	 * Handles the "context-list" action.
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ServletException
	 *             if including the template file fails
	 * @throws InvalidParamValueException
	 *             if required parameter contains invalid value
	 * @throws ComponentInitializationException
	 *             if the Task Manager can't be initialized
	 */
	public void contextList(
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			ComponentInitializationException, InvalidParamValueException {
		if (params.exists("action")) {
			String action = request.getParameter("action");
			if (action.equals("context-killed")) {
				infoMessages
						.addTextMessage("All tasks in the context killed successfully.");
			} else if (action.equals("context-deleted")) {
				infoMessages.addTextMessage("Context deleted successfully.");
			} else if (action.equals("task-killed")) {
				infoMessages.addTextMessage("Task killed successfully.");
			} else {
				throw new InvalidParamValueException(
						"Parameter \"action\" has invalid value.");
			}
		}
		ContextEntry[] contexts = taskManager.get().getContexts();

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("contexts", contexts);

		page.setTitle("Contexts");
		page.writeHeader();
		page.writeTemplate("tasks-context-list", data);
		page.writeFooter();
	}

	/**
	 * Handles the "context-details" action.
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ServletException
	 *             if including the template file fails
	 * @throws MissingParamException
	 *             if some required parameter is missing
	 * @throws InvalidParamValueException
	 *             if required parameter contains invalid value
	 * @throws ComponentInitializationException
	 *             if the Task Manager can't be initialized
	 */
	public void contextDetails(
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			MissingParamException, InvalidParamValueException,
			ComponentInitializationException {
		if (params.exists("action")) {
			String action = request.getParameter("action");
			if (action.equals("task-runned")) {
				infoMessages.addTextMessage("Task runned successfully.");
			} else {
				throw new InvalidParamValueException(
						"Parameter \"action\" has invalid value.");
			}
		}
		params.ensureExists("cid");

		ContextEntry context = null;
		try {
			context = taskManager.get().getContextById(
					request.getParameter("cid"));
		} catch (IllegalArgumentException e) {
			throw new InvalidParamValueException(
					"Parameter \"cid\" has invalid value.");
		}

		TaskEntry[] tasks = taskManager.get().getTasksInContext(
				context.getContextId());
		Map<TaskEntry, CheckPoint[]> checkpoints = TaskUtils
				.getCheckPointsForTasks(taskManager.get(), tasks);

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("context", context);
		data.put("tasks", tasks);
		data.put("checkpoints", checkpoints);

		page.setTitle("Context details: " + context.getContextId());
		page.writeHeader();
		page.writeTemplate("tasks-context-details", data);
		page.writeFooter();
	}

	/**
	 * Handles the "context-kill" action.
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ServletException
	 *             if including the template file fails
	 * @throws MissingParamException
	 *             if some required parameter is missing
	 * @throws InvalidParamValueException
	 *             if required parameter contains invalid value
	 * @throws ComponentInitializationException
	 *             if the Task Manager can't be initialized
	 */
	public void contextKill(
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			MissingParamException, InvalidParamValueException,
			ComponentInitializationException {
		params.ensureExists("cid");

		try {
			taskManager.get().killContextById(request.getParameter("cid"));
		} catch (IllegalArgumentException e) {
			throw new InvalidParamValueException(
					"Parameter \"cid\" has invalid value.");
		}

		HashMap<String, String> actionParams = new HashMap<String, String>();
		actionParams.put("action", "context-killed");
		page.redirectToAction("context-list", actionParams);
	}

	/**
	 * Handles the "context-delete" action.
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ServletException
	 *             if including the template file fails
	 * @throws MissingParamException
	 *             if some required parameter is missing
	 * @throws InvalidParamValueException
	 *             if required parameter contains invalid value
	 * @throws ComponentInitializationException
	 *             if the Task Manager can't be initialized
	 */
	public void contextDelete(
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			MissingParamException, InvalidParamValueException,
			ComponentInitializationException {
		params.ensureExists("cid");
		params.ensureCondition(
				"cid",
				!request.getParameter("cid").equals(
						TaskManagerInterface.SYSTEM_CONTEXT_ID));

		try {
			String contextID = request.getParameter("cid");
			taskManager.get().killAndDeleteContext(contextID);
		} catch (IllegalArgumentException e) {
			throw new InvalidParamValueException("Invalid context identifier.");
		}

		HashMap<String, String> actionParams = new HashMap<String, String>();
		actionParams.put("action", "context-deleted");
		page.redirectToAction("context-list", actionParams);
	}

	/**
	 * Handles the "task-details" action.
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ServletException
	 *             if including the template file fails
	 * @throws MissingParamException
	 *             if some required parameter is missing
	 * @throws InvalidParamValueException
	 *             if required parameter contains invalid value
	 * @throws ComponentInitializationException
	 *             if the Task Manager can't be initialized
	 */
	public void taskDetails(
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			MissingParamException, InvalidParamValueException,
			ComponentInitializationException {
		params.ensureExist("tid", "cid");

		TaskEntry task = null;
		CheckPoint[] checkpoints = null;
		LogRecord[] logRecords = null;
		OutputHandle standardOutputHandle = null;
		OutputHandle errorOutputHandle = null;
		try {
			task = taskManager.get().getTaskById(
					request.getParameter("tid"),
					request.getParameter("cid"));
			try {
				checkpoints = taskManager.get().checkPointLook(
						new CheckPoint(
								task.getTaskId(),
								task.getContextId(),
								null,
								null),
						0);
			} catch (TaskManagerException e) {
				errorMessages.addTextMessage("Error retrieving checkpoints: "
						+ e.getMessage());
				checkpoints = new CheckPoint[0];
			}

			try {
				logRecords = taskManager.get().getLogsForTask(
						task.getContextId(),
						task.getTaskId());
				standardOutputHandle = taskManager.get().getStandardOutput(
						task.getContextId(),
						task.getTaskId());
				errorOutputHandle = taskManager.get().getErrorOutput(
						task.getContextId(),
						task.getTaskId());
			} catch (LogStorageException e) {
				errorMessages.addTextMessage("Error retrieving logs: "
						+ e.getMessage());
			}
		} catch (IllegalArgumentException e) {
			throw new InvalidParamValueException(
					"Parameter \"cid\" or \"tid\" has invalid value ("
							+ e.getMessage() + ")");
		}

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("task", task);
		data.put("checkpoints", checkpoints);
		data.put("logRecords", logRecords);
		data.put("logFields", EnumSet.of(
				LogRecord.Fields.TIMESTAMP,
				LogRecord.Fields.LEVEL,
				LogRecord.Fields.MESSAGE));
		data.put("standardOutputHandle", standardOutputHandle);
		data.put("errorOutputHandle", errorOutputHandle);

		page.setTitle("Task details: " + task.getTaskId());
		page.writeHeader();
		page.writeTemplate("tasks-task-details", data);
		page.writeFooter();
	}

	/**
	 * Handles the "task-kill" action.
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ServletException
	 *             if including the template file fails
	 * @throws MissingParamException
	 *             if some required parameter is missing
	 * @throws InvalidParamValueException
	 *             if required parameter contains invalid value
	 * @throws ComponentInitializationException
	 *             if the Task Manager can't be initialized
	 */
	public void taskKill(
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			MissingParamException, InvalidParamValueException,
			ComponentInitializationException {
		params.ensureExist("cid", "tid");

		try {
			taskManager.get().killTaskById(
					request.getParameter("tid"),
					request.getParameter("cid"));
		} catch (IllegalArgumentException e) {
			throw new InvalidParamValueException(
					"Parameter \"cid\" or \"tid\" has " + "invalid value.");
		}

		HashMap<String, String> actionParams = new HashMap<String, String>();
		actionParams.put("cid", request.getParameter("cid"));
		infoMessages.addTextMessage("Task killed successfully.");
		page.redirectToAction("context-details", actionParams);
	}

	private void checkProperties(HttpServletRequest request) {
		String propertiesString = request.getParameter("properties");
		String[] propertiesArray = propertiesString.split("\n");
		boolean wasError = false;
		for (String property : propertiesArray) {
			property = Routines.trim(property);
			if (property.equals("")) {
				continue;
			}
			String[] propertyParts = property.split("=");
			if (propertyParts.length != 2) {
				wasError = true;
			}
		}
		params.checkCondition(
				!wasError,
				"Properties must be formatted correctly.");
	}

	private Map<String, String> getProperties(HttpServletRequest request) {
		Map<String, String> result = new HashMap<String, String>();
		String propertiesString = request.getParameter("properties");
		String[] propertiesArray = propertiesString.split("\n");
		for (String property : propertiesArray) {
			property = Routines.trim(property);
			if (property.equals("")) {
				continue;
			}
			String[] propertyParts = property.split("=");
			result.put(
					Routines.trim(propertyParts[0]),
					Routines.trim(propertyParts[1]));
		}
		return result;
	}

	/**
	 * Handles the "task-run" action.
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ServletException
	 *             if including the template file fails
	 * @throws MissingParamException
	 *             if some required parameter is missing
	 * @throws InvalidParamValueException
	 *             if required parameter contains invalid value
	 * @throws ComponentInitializationException
	 *             if the Task Manager can't be initialized
	 */
	public void
			taskRun(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException,
					MissingParamException, InvalidParamValueException,
					ComponentInitializationException {
		HashMap<String, Object> data = new HashMap<String, Object>();

		if (params.exists("run")) {
			params.ensureExists("task-run-type");

			Set<String> taskRunTypes = new HashSet<String>();
			taskRunTypes.add(TASK_RUN_TYPE_GUI);
			taskRunTypes.add(TASK_RUN_TYPE_XML);
			params.ensureCondition(
					"task-run-type",
					params.isInSet("task-run-type", taskRunTypes));

			if (request.getParameter("task-run-type").equals(TASK_RUN_TYPE_GUI)) {
				params.ensureExist("properties");
			} else if (request.getParameter("task-run-type").equals(
					TASK_RUN_TYPE_XML)) {
				params.ensureExist("xml");
			} else {
				assert false : "Impossible things happen sometimes :-)";
			}

			TaskDescriptor taskDescriptor = null;

			if (request.getParameter("task-run-type").equals(TASK_RUN_TYPE_GUI)) {
				params.checkCondition(
						params.exists("task-name"),
						"Select task name.");
				params.checkCondition(params.exists("host"), "Select host.");
				params.checkCondition(
						params.exists("context-id"),
						"Select context ID.");
				checkProperties(request);
			} else if (request.getParameter("task-run-type").equals(
					TASK_RUN_TYPE_XML)) {
				params.checkCondition(
						params.notEmpty("xml"),
						"Enter non-empty task descriptor.");
				if (params.notEmpty("xml")) {
					BindingParser<TaskDescriptor> parser; // Must be a local var
															// here!!!
					try {
						/*
						 * this is fix for issue that webUI cannot find the .xsd
						 * files
						 */
						System.setProperty("been.directory.jaxb", page
								.getContext().getRealPath("/WEB-INF/work"));
						parser = XSD.TD.createParser(TaskDescriptor.class);
						try {
							taskDescriptor = parser
									.parse(new ByteArrayInputStream(request
											.getParameter("xml").getBytes()));
						} catch (UnmarshalException e) { // Thrown from parser
															// run.
							params.checkCondition(
									false,
									"XML descriptor validation error: "
											+ e.getMessage());
						} catch (JAXBException e) { // Thrown from parser run.
							params.checkCondition(
									false,
									"XML descriptor parser error: "
											+ e.getMessage());
						} catch (ConvertorException e) { // Thrown from parser
															// run.
							params.checkCondition(
									false,
									"XML descriptor data conversion error: "
											+ e.getMessage());
						}
					} catch (SAXException e) { // Thrown from parser init.
						params.checkCondition(
								false,
								"Fatal failure when initializing XML schema: "
										+ e.getMessage());
						parser = null;
					} catch (JAXBException e) { // Thrown from parser init.
						params.checkCondition(
								false,
								"Fatal failure when initializing XML binding: "
										+ e.getMessage());
						parser = null;
					}
				}
			} else {
				assert false : "Impossible things happen sometimes :-)";
			}

			if (errorMessages.isEmpty()) {
				if (request.getParameter("task-run-type").equals(
						TASK_RUN_TYPE_GUI)) {
					taskDescriptor = TaskDescriptorHelper.createTask(
							"task-" + Long.toString(idIndex++),
							request.getParameter("task-name"),
							request.getParameter("context-id"),
							new EqualsCondition<String>("name", request
									.getParameter("host")),
							TASK_TREE_PREFIX
									+ request.getParameter("context-id") + "/"
									+ request.getParameter("task-name"));
					Map<String, String> properties = getProperties(request);
					TaskDescriptorHelper.addTaskProperties(
							taskDescriptor,
							properties.entrySet().toArray(
									new Entry<?, ?>[properties.size()]));
				} else if (request.getParameter("task-run-type").equals(
						TASK_RUN_TYPE_XML)) { // OK, nothing to be done.
				} else {
					assert false : "Impossible things happen sometimes :-)";
				}

				try {
					taskManager.get().runTask(taskDescriptor);
				} catch (IllegalArgumentException e) {
					errorMessages.addTextMessage(e.getMessage());
				}

			}

			if (errorMessages.isEmpty() && taskDescriptor != null) { // '!=
																		// null'
																		// avoids
																		// a
																		// warning.
				HashMap<String, String> actionParams = new HashMap<String, String>();
				actionParams.put("action", "task-runned");
				actionParams.put("cid", taskDescriptor.getContextId());
				page.redirectToAction("context-details", actionParams);
				return;
			} else {
				if (request.getParameter("task-run-type").equals(
						TASK_RUN_TYPE_GUI)) {
					data.put(
							"taskRunType",
							request.getParameter("task-run-type"));
					data.put("taskName", request.getParameter("taskName"));
					data.put("host", request.getParameter("host"));
					data.put("contextId", request.getParameter("context-id"));
					data.put("properties", request.getParameter("properties"));
					data.put("xml", "");
				} else if (request.getParameter("task-run-type").equals(
						TASK_RUN_TYPE_XML)) {
					data.put(
							"taskRunType",
							request.getParameter("task-run-type"));
					data.put("taskName", "");
					data.put("host", "");
					data.put("contextId", "");
					data.put("properties", "");
					data.put("xml", request.getParameter("xml"));
				} else {
					assert false : "Impossible things happen sometimes :-)";
				}
			}

		} else {
			data.put("taskRunType", TASK_RUN_TYPE_GUI);
			data.put("taskName", "");
			data.put("host", "");
			data.put("contextId", "");
			data.put("properties", "");
			data.put("xml", "");
		}

		PackageMetadata[] packages = null;
		try {
			packages = softwareRepository.get().queryPackages(new TasksQuery());
		} catch (MatchException e) {
			assert false : "TasksQuery.match should never throw MatchException";
		}
		Arrays.sort(packages, new Comparator<PackageMetadata>() {
			@Override
			public int compare(PackageMetadata o1, PackageMetadata o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		String[] hosts = hostManager.get().getHostNames();
		Arrays.sort(hosts);
		ContextEntry[] contexts = taskManager.get().getContexts();

		data.put("packages", packages);
		data.put("hosts", hosts);
		data.put("contexts", contexts);

		page.setTitle("Run task");
		page.writeHeader();
		page.writeTemplate("tasks-task-run", data);
		page.writeFooter();
	}

	/**
	 * Handles the "task-logs" action.
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @throws MissingParamException
	 *             if some required parameter is missing
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ServletException
	 *             if including the template file fails
	 * @throws InvalidParamValueException
	 *             if required parameter contains invalid value
	 * @throws ComponentInitializationException
	 *             if the Task Manager can't be initialized
	 */
	public void taskLogs(
			HttpServletRequest request,
			HttpServletResponse response) throws MissingParamException,
			ServletException, IOException, InvalidParamValueException,
			ComponentInitializationException {

		LogRecord[] logRecords = null;
		try {
			logRecords = LogUtils.getLogRecordsForTasks(
					taskManager.get(),
					taskManager.get().getTasks());
		} catch (LogStorageException e) {
			errorMessages.addTextMessage("Error retrieving logs: "
					+ e.getMessage());
		}

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("logRecords", logRecords);
		data.put("logFields", EnumSet.of(
				LogRecord.Fields.CONTEXT,
				LogRecord.Fields.TASK_ID,
				LogRecord.Fields.HOSTNAME,
				LogRecord.Fields.TIMESTAMP,
				LogRecord.Fields.LEVEL,
				LogRecord.Fields.MESSAGE));

		page.setTitle("Task logs");
		page.writeHeader();
		page.writeTemplate("tasks-task-logs", data);
		page.writeFooter();
	}

	/**
	 * Handles the "task-manager-logs" action.
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @throws MissingParamException
	 *             if some required parameter is missing
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws ServletException
	 *             if including the template file fails
	 * @throws InvalidParamValueException
	 *             if required parameter contains invalid value
	 * @throws ComponentInitializationException
	 *             if the Task Manager can't be initialized
	 */
	public void taskManagerLogs(
			HttpServletRequest request,
			HttpServletResponse response) throws MissingParamException,
			ServletException, IOException, InvalidParamValueException,
			ComponentInitializationException {

		LogRecord[] logRecords = null;
		try {
			logRecords = taskManager.get().getLogsForTask(
					TaskManagerInterface.SYSTEM_CONTEXT_ID,
					TaskManagerInterface.TASKMANAGER_TASKNAME);
		} catch (LogStorageException e) {
			errorMessages.addTextMessage("Error retrieving logs: "
					+ e.getMessage());
		}

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("logRecords", logRecords);
		data.put("logFields", EnumSet.of(
				LogRecord.Fields.HOSTNAME,
				LogRecord.Fields.TIMESTAMP,
				LogRecord.Fields.LEVEL,
				LogRecord.Fields.MESSAGE));

		page.setTitle("Task Manager logs");
		page.writeHeader();
		page.writeTemplate("tasks-task-manager-logs", data);
		page.writeFooter();
	}

	/**
	 * Handles the task-tree action. Shows the task tree view starting at the
	 * root node.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 * @throws ComponentInitializationException
	 */
	public void taskTree(
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			ComponentInitializationException {
		page.setTitle("Task Tree View");

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("path", null);
		data.put("children", null);
		data.put("task", null);

		// get the root node record and resolve it
		try {
			TaskTreeQuery query = taskManager.get().getTaskTreeQuery();
			TaskTreeAddress address = query.addressFromPath(""); // get the root
																	// address
			TaskTreeRecord nodeRecord = query.getRecordAt(
					address,
					true,
					true,
					true);
			saveTaskTreeNodeData(nodeRecord, query, data);
		} catch (MalformedAddressException e) {
			// Should never happen
			assert false : "MalformedAddressException thrown for root address";
		} catch (IllegalAddressException e) {
			// Should never happen
			assert false : "IllegalAddressException thrown for root address";
		}

		page.writeHeader();
		page.writeTemplate("tasks-task-tree", data);
		page.writeFooter();
	}

	/**
	 * Outputs page fragment (no header or footer) that contains information
	 * about TaskTree node. Sets the following keys in data: <br>
	 * <b>path</b> - the requested node path</li>
	 * 
	 * @see #saveTaskTreeNodeData(TaskTreeRecord, TaskTreeQuery, HashMap)
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	public void AJAXtaskTreeNode(
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HashMap<String, Object> data = new HashMap<String, Object>();

		// choose which node to resolve
		String node = "";
		if (params.exists("node") && !request.getParameter("node").equals("/")) {
			node = request.getParameter("node");
		}
		data.put("path", node);
		data.put("children", null);
		data.put("task", null);

		// get the node record and resolve it
		TaskTreeRecord nodeRecord = null;
		try {
			TaskTreeQuery query = taskManager.get().getTaskTreeQuery();
			TaskTreeAddress address = query.addressFromPath(node);
			nodeRecord = query.getRecordAt(address, true, true, true);
			saveTaskTreeNodeData(nodeRecord, query, data);
		} catch (Exception e) {
			errorMessages.addTextMessage(e.getMessage());
			page.writeErrorMessages();
			return;
		}
		page.writeTemplate("tasks-task-tree-node", data);
	}

	/**
	 * Saves record information into data. Sets following keys:
	 * <ul>
	 * <li><b>name</b> - name of the node</li>
	 * <li><b>task</b> - if node is leaf, this contains taskEntry, {@code null}
	 * otherwise</li>
	 * <li><b>children</b> - if node is inner node, this contains list of
	 * children names</li>
	 * </ul>
	 * 
	 * @param record
	 * @param data
	 */
	private void saveTaskTreeNodeData(
			TaskTreeRecord record,
			TaskTreeQuery query,
			HashMap<String, Object> data) {
		/*
		 * TODO: resolve the flags for( Pair<TaskTreeFlag, TreeFlagValue> pair :
		 * record.getFlags() ){ if( pair.getKey().equals(BasicFlags.COMPLETED) )
		 * }
		 */
		// Node name
		String[] path = record.getPathSegments();
		if (path.length == 0) {
			data.put("name", "<ROOT>");
		} else {
			data.put("name", path[path.length - 1]);
		}

		switch (record.getType()) {
			case LEAF:
				data.put("task", record.getTask());
				break;
			case NODE:
				TaskTreeAddress[] children = record.getChildren();
				String[] childNames = new String[children.length];
				for (int i = 0; i < childNames.length; i++) {
					String[] segments;
					try {
						segments = query.getPathAt(children[i]);
						childNames[i] = segments[segments.length - 1];
					} catch (RemoteException e) {
						childNames[i] = "ERROR: " + e.getMessage();
					}
				}
				data.put("children", childNames);
				break;
		}
	}

}
