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
package cz.cuni.mff.been.clinterface.modules;

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;

import org.xml.sax.SAXException;

import cz.cuni.mff.been.clinterface.CommandLineAction;
import cz.cuni.mff.been.clinterface.CommandLineException;
import cz.cuni.mff.been.clinterface.CommandLineModule;
import cz.cuni.mff.been.clinterface.CommandLineRequest;
import cz.cuni.mff.been.clinterface.CommandLineResponse;
import cz.cuni.mff.been.clinterface.ModuleOutputException;
import cz.cuni.mff.been.clinterface.ModuleSpecificException;
import cz.cuni.mff.been.clinterface.ref.TaskManagerReference;
import cz.cuni.mff.been.clinterface.writers.ContextEntryWriter;
import cz.cuni.mff.been.clinterface.writers.LogRecordWriter;
import cz.cuni.mff.been.clinterface.writers.TaskEntryWriter;
import cz.cuni.mff.been.clinterface.writers.TaskTreeItemWriter;
import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.common.Message;
import cz.cuni.mff.been.jaxb.BindingComposer;
import cz.cuni.mff.been.jaxb.BindingParser;
import cz.cuni.mff.been.jaxb.ConvertorException;
import cz.cuni.mff.been.jaxb.XSD;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.logging.LogRecord;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.logging.OutputHandle;
import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;
import cz.cuni.mff.been.taskmanager.data.ContextEntry;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;
import cz.cuni.mff.been.taskmanager.tasktree.IllegalAddressException;
import cz.cuni.mff.been.taskmanager.tasktree.MalformedAddressException;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeAddress;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeQuery;

/**
 * A command line interface component that corresponds to the tasks listing
 * screen.
 * 
 * @author Andrej Podzimek
 */
public final class TasksModule extends CommandLineModule {

	/**
	 * Error messages reported by this module.
	 * 
	 * @author Andrej Podzimek
	 */
	public enum Errors implements Message {

		/** Unknown action name. */
		UNKN_ACTION("Unknown action."),

		/** Action not implemented yet. */
		IMPL_ACTION("Action not implemented yet. Volunteers?"),

		/**
		 * When the Task Manager is not responding (RemoteException,
		 * IOException).
		 */
		CONN_TM("Could not contact the Task Manager."),

		/** A failure occured when trying to retrieve logs for task. */
		CONN_LOG_LOG("Failed to retrieve logs."),

		/** When reading stdin or stderr fails. */
		CONN_LOG_OUT("Failed to retrieve task output."),

		/** Invalid parameters or required parameters missing. */
		INVD_PARAMS(""),

		/** Invalid regular expression used. */
		INVD_REGEXP("Illegal regular expression."),

		/** Invalid address used. There's no node on this address. */
		INVD_ADDRESS("Invalid address. No such element."),

		/** Context id invalid. */
		INVD_CTX("Unknown or invalid context id."),

		/**
		 * Context id or task id invalid. Hard to say due to *bad* error
		 * reporting in TM.
		 */
		INVD_ID_CTX("Invalid task id or context id or outdated tree reference."),

		/** The supplied address is malformed. */
		MALF_ADDRESS("Malformed address."),

		/** Task name and task id in conflict (when both are specified). */
		CONF_NAME_ID("Conflicting context name or ID."),

		/** Attributes 'id' and 'desc' used both at once. */
		EXCL_ID_DESC("'id' and 'desc' are mutually exclusive."),

		/** Attributes 'id' and 'path' used both at once. */
		EXCL_ID_CTX_PATH("('id', 'context') and 'path' are mutually exclusive."),

		/** Attributes 'id' and 'delete' used both at once. */
		EXCL_DEL_ID("'id' and 'delete' ara mutually excelusive."),

		/** Attributes 'id' and 'path' not used. */
		REQD_ID_PATH("Neither ('id', 'context') nor 'path' specified."),

		/** Attribute 'id' used alone or no attributes used. */
		REQD_ID_CTX_PATH(
				"Neither ('id', 'context'), nor 'context', nor 'path' specified."),

		/** The 'recursive' flag is required to kill the whole context. */
		REQD_RECURSIVE("To delete contexts or nodes, 'recursive' must be set."),

		/**
		 * A 'real' or 'artificial' IOException occured. TM has *bad* error
		 * reporting.
		 */
		FAIL_IO_REMOTE("A network or I/O error in TaskManager occured."),

		/**
		 * Could not parse the XML TaskDescriptor for some reason. Parser
		 * errors? Who knows?
		 */
		FAIL_TD_PARSER("Failed to parse the XML task descriptor."),

		/**
		 * IllegalArgumentException from the TM. Nobody knows what exactly
		 * happened.
		 */
		MALF_TD_RUNNING(
				"Task Manager refused the Task Descriptor. Details follow."),

		/** When object marshaling fails. Might be a validator problem. */
		FAIL_MARSHAL("Marshaling data to XML failed."),

		/** When object unmarhaling fails for unknown reasons. */
		FAIL_UMSL("Unmarshaling data from XML failed."),

		/**
		 * When object unmarshaling fails due to validation and data format
		 * issues.
		 */
		FAIL_UMSL_VAL("Unmarshaling data from XML failed. Validation problem."),

		/** When a parseMethod throws an exception. */
		FAIL_UMSL_CONV("Conversion of special data types from XML failed."),

		/** The RMIIO stream has failed for some unexpected reason. */
		RMIIO_FAILED("XML data transfer to the Task Manager failed."),

		/** The RMIIO stream was closed with a false status. */
		RMIIO_DIRTY("XML data transfer reported an unknown problem."),

		/**
		 * When the TM throws an IllegalArgumentException, which could mean just
		 * about whatever.
		 */
		STAT_RUNNING(
				"Task refused by Task Manager. It may be running. See logs for details."),

		/** An attempt to retrieve logs for task that has not run yet. */
		STAT_NOTYET("The service has not run yet."),

		/** When the host RSL string is malformed. */
		MALF_TD_HOST("Malformed host RSL expression."),

		/** When the package RSL string is malformed. */
		MALF_TD_PKG("Malformed package RSL expression."),

		/** When 'from' can't be read as Long. */
		MALF_FROM("The 'from' parameter is malformed."),

		/** When 'to' can't be read as Long. */
		MALF_TO("The 'to' parameter is malformed."),

		/** Class deserialization has failed. */
		FAIL_DESER("Object deserialization failed."),

		/** Class serialization has failed. */
		FAIL_SER("Object serialization failed."),

		/** Trying to deserialize an unknown object. */
		UNKN_CLASS("Could not find or instantiate the class."),

		/** Trying to deserialize or serialize something non-Serializable. */
		INVD_SER("Attempting to deserialize a non-Serializable object."),

		/** A bug - constructor referenced incorrectly. */
		INTG_REF(
				"Integrity error. Could not call a (String) constructor via reflection."),

		/** The security manager did not allow instantiation. */
		FAIL_SECURITY(
				"The (String) constructor is banned by the Security Manager."),

		/** Trying to instantiate an abstract class. */
		INVD_CLASS("Attempting to instantiate an abstract class."),

		/** The constructor exists, but is not accessible. */
		INVD_CTOR("The (String) constructor is not accessible."),

		/** The constructor threw an exception. */
		FAIL_CTOR("The (String) constructor threw an exception."),

		/** Unknown constructor referenced. */
		UNKN_CTOR("Could not find a String constructor for the class.");

		/** The message the enum item will convey. */
		private final String message;

		/**
		 * Initializes the enum member with a human-readable error message.
		 * 
		 * @param message
		 *            The error message this enum member will contain.
		 */
		private Errors(String message) {
			this.message = message;
		}

		@Override
		public final String getMessage() {
			return message;
		}
	}

	/** Name of this module. */
	public static final String MODULE_NAME = "tasks";

	/** A map of actions provided by this module. */
	private static final TreeMap<String, CommandLineAction<TasksModule>> actions;

	/** A list of actions this module provides. */
	private static final String ACTIONS_LIST;

	/** Number of log lines to read at once. */
	private static final int LINES_AT_ONCE = 128;

	static {
		actions = new TreeMap<String, CommandLineAction<TasksModule>>();

		actions.put("context-list", new CommandLineAction<TasksModule>() {
			private final Set<String> parameters = stringSet("id", "pattern");
			private final Set<String> flags = stringSet("desc");
			private final String help = constructHelp(parameters, flags, false);

			@Override
			public void handle(
					TasksModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
				StringBuilder errors;

				errors = verifyArguments(request, parameters, flags, false);
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				String pattern;
				String id;
				Pattern cpattern;

				pattern = request.getParameter("pattern");
				id = request.getParameter("id");

				try {
					if (null == pattern) {
						if (null == id) {
							ContextEntry[] contexts;
							ContextEntryWriter writer;

							contexts = module.taskManagerReference.get()
									.getContexts();
							writer = new ContextEntryWriter(response);
							if (request.getFlag("desc")) {
								for (ContextEntry context : contexts) {
									writer.sendLineDescr(context);
								}
							} else {
								for (ContextEntry context : contexts) {
									writer.sendLinePlain(context);
								}
							}
						} else {
							TaskEntry[] tasks;
							TaskEntryWriter writer;

							tasks = module.taskManagerReference.get()
									.getTasksInContext(id);
							writer = new TaskEntryWriter(response);
							if (request.getFlag("desc")) {
								for (TaskEntry task : tasks) {
									writer.sendLineDescr(task);
								}
							} else {
								for (TaskEntry task : tasks) {
									writer.sendLinePlain(task);
								}
							}
						}
					} else {
						cpattern = Pattern.compile(pattern);
						if (null == id) {
							ContextEntry[] contexts;
							ContextEntryWriter writer;

							contexts = module.taskManagerReference.get()
									.getContexts();
							writer = new ContextEntryWriter(response);
							if (request.getFlag("desc")) {
								for (ContextEntry context : contexts) {
									if (cpattern.matcher(
											context.getContextName()).matches()) {
										writer.sendLineDescr(context);
									}
								}
							} else {
								for (ContextEntry context : contexts) {
									if (cpattern.matcher(
											context.getContextName()).matches()) {
										writer.sendLinePlain(context);
									}
								}
							}
						} else {
							TaskEntry[] tasks;
							TaskEntryWriter writer;

							tasks = module.taskManagerReference.get()
									.getTasksInContext(id);
							writer = new TaskEntryWriter(response);
							if (request.getFlag("desc")) {
								for (TaskEntry task : tasks) {
									if (cpattern.matcher(task.getTaskName())
											.matches()) {
										writer.sendLineDescr(task);
									}
								}
							} else {
								for (TaskEntry task : tasks) {
									if (cpattern.matcher(task.getTaskName())
											.matches()) {
										writer.sendLinePlain(task);
									}
								}
							}
						}
					}
				} catch (PatternSyntaxException exception) {
					throw new ModuleSpecificException(Errors.INVD_REGEXP, " ("
							+ pattern + ')', exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				} catch (IOException exception) {
					throw new ModuleOutputException(exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("context-new", new CommandLineAction<TasksModule>() {
			private final Set<String> allParameters = stringSet(
					"id",
					"name",
					"desc");
			private final Set<String> requiredParameters = stringSet("name");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					allParameters,
					flags,
					requiredParameters,
					false);

			@Override
			public void handle(
					TasksModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException {
				StringBuilder errors;

				errors = verifyArguments(
						request,
						allParameters,
						flags,
						requiredParameters,
						false);
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				String id;
				String name;

				id = request.getParameter("id");
				name = request.getParameter("name");

				try {
					if (null == id) {
						module.taskManagerReference.get().newContext(
								name,
								request.getParameter("desc"), // Might be null.
								null);
					} else {
						module.taskManagerReference.get().newContext(
								id,
								name,
								request.getParameter("desc"), // Might be null.
								null);
					}
				} catch (IllegalStateException exception) {
					throw new ModuleSpecificException(Errors.CONF_NAME_ID, " ("
							+ name + ')', exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("context-delete", new CommandLineAction<TasksModule>() {
			private final Set<String> parameters = stringSet("id");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					TasksModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException {
				StringBuilder errors;

				errors = verifyArguments(
						request,
						parameters,
						flags,
						parameters,
						false);
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				String id;

				id = request.getParameter("id");

				try {
					module.taskManagerReference.get().killAndDeleteContext(id);
				} catch (IllegalArgumentException exception) {
					throw new ModuleSpecificException(Errors.INVD_CTX, " ("
							+ id + ')', exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("tree-list", new CommandLineAction<TasksModule>() {
			private final Set<String> parameters = stringSet("path");
			private final Set<String> flags = stringSet("recursive", "flags");
			private final String help = constructHelp(parameters, flags, false);

			@Override
			public void handle(
					TasksModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
				StringBuilder errors;

				errors = verifyArguments(request, parameters, flags, false);
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				TaskTreeQuery query;
				String path;
				TaskTreeAddress address;
				TaskTreeItemWriter writer;

				path = request.getParameter("path");
				if (null == path) {
					path = "";
				}

				try {
					query = module.taskManagerReference.get()
							.getTaskTreeQuery();
					address = query.addressFromPath(path);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				} catch (MalformedAddressException exception) {
					throw new ModuleSpecificException(Errors.MALF_ADDRESS, " ("
							+ path + ')', exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				}

				writer = new TaskTreeItemWriter(response);
				try {
					if (request.getFlag("recursive")) {
						if (request.getFlag("flags")) {
							writer.sendLineFlagsRecursive(query, address, 0);
						} else {
							writer.sendLinePlainRecursive(query, address, 0);
						}
					} else {
						if (request.getFlag("flags")) {
							writer.sendLineFlagsSimple(query, address);
						} else {
							writer.sendLinePlainSimple(query, address);
						}
					}
				} catch (IOException exception) {
					throw new ModuleOutputException(exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("info", new CommandLineAction<TasksModule>() {
			private final Set<String> parameters = stringSet(
					"path",
					"id",
					"context");
			private final Set<String> flags = stringSet("desc");
			private final String help = constructHelp(parameters, flags, false);

			@Override
			public void handle(
					TasksModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
				StringBuilder errors;

				errors = verifyArguments(request, parameters, flags, false);
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				String path;
				String id;
				String context;
				TaskEntry task;
				TaskEntryWriter writer;

				path = request.getParameter("path");
				id = request.getParameter("id");
				context = request.getParameter("context");

				if (null == path) {
					if (null == id || null == context) {
						throw new ModuleSpecificException(Errors.REQD_ID_PATH);
					} else {
						try {
							task = module.taskManagerReference.get()
									.getTaskById(id, context);
						} catch (IllegalArgumentException exception) {
							throw new ModuleSpecificException(
									Errors.INVD_ID_CTX,
									" (" + id + ", " + context + ')',
									exception);
						} catch (ComponentInitializationException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_TM,
									exception);
						} catch (RemoteException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_TM,
									exception);
						}
					}
				} else {
					if (null == id && null == context) {
						TaskTreeQuery query;
						TaskTreeAddress address;

						try {
							try {
								query = module.taskManagerReference.get()
										.getTaskTreeQuery();
							} catch (ComponentInitializationException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_TM,
										exception);
							}
							try {
								address = query.addressFromPath(path);
							} catch (MalformedAddressException exception) {
								throw new ModuleSpecificException(
										Errors.MALF_ADDRESS,
										" (" + path + ')',
										exception);
							}
							try {
								task = query.getTaskAt(address);
							} catch (IllegalAddressException exception) {
								throw new ModuleSpecificException(
										Errors.INVD_ADDRESS,
										" (" + path + ')',
										exception);
							}
						} catch (RemoteException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_TM,
									exception);
						}
					} else {
						throw new ModuleSpecificException(
								Errors.EXCL_ID_CTX_PATH);
					}
				}
				writer = new TaskEntryWriter(response);
				try {
					if (request.getFlag("desc")) {
						writer.sendLineDescr(task);
					} else {
						writer.sendLinePlain(task);
					}
				} catch (IOException exception) {
					throw new ModuleOutputException(exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("logs", new CommandLineAction<TasksModule>() {
			private final Set<String> parameters = stringSet(
					"path",
					"id",
					"context",
					"from",
					"to");
			private final Set<String> flags = stringSet("numbers");
			private final String help = constructHelp(parameters, flags, false);

			@Override
			public void handle(
					TasksModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
				StringBuilder errors;

				errors = verifyArguments(request, parameters, flags, false);
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				String path;
				String id, context;
				String from, to;
				long lfrom, lto;
				LogRecord[] records;
				LogRecordWriter writer;

				from = request.getParameter("from");
				try {
					lfrom = null == from ? 0 : Long.valueOf(from);
				} catch (NumberFormatException exception) {
					throw new ModuleSpecificException(Errors.MALF_FROM, " ("
							+ from + ')', exception);
				}

				to = request.getParameter("to");
				try {
					lto = null == to ? Long.MAX_VALUE : Long.valueOf(to);
				} catch (NumberFormatException exception) {
					throw new ModuleSpecificException(Errors.MALF_TO, " (" + to
							+ ')', exception);
				}

				path = request.getParameter("path");
				id = request.getParameter("id");
				context = request.getParameter("context");

				try {
					if (null == path) {
						if (null == id || null == context) {
							throw new ModuleSpecificException(
									Errors.REQD_ID_PATH);
						} else {
							try {
								records = module.taskManagerReference
										.get()
										.getLogsForTask(context, id, lfrom, lto);
							} catch (LogStorageException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_LOG_LOG,
										exception);
							} catch (IllegalArgumentException exception) {
								throw new ModuleSpecificException(
										Errors.STAT_NOTYET,
										" (" + context + ", " + id + ')',
										exception);
							} catch (ComponentInitializationException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_TM,
										exception);
							}
						}
					} else {
						if (null == id && null == context) {
							TaskTreeQuery query;
							TaskTreeAddress address;
							TaskManagerInterface taskManager;

							try {
								taskManager = module.taskManagerReference.get();
							} catch (ComponentInitializationException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_TM,
										exception);
							}
							query = taskManager.getTaskTreeQuery();
							try {
								address = query.addressFromPath(path);
							} catch (MalformedAddressException exception) {
								throw new ModuleSpecificException(
										Errors.MALF_ADDRESS,
										" (" + path + ')',
										exception);
							}

							try {
								records = taskManager.getLogsForTask(
										address,
										lfrom,
										lto);
							} catch (IllegalAddressException exception) {
								throw new ModuleSpecificException(
										Errors.INVD_ADDRESS,
										" (" + path + ')',
										exception);
							} catch (LogStorageException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_LOG_LOG,
										exception);
							} catch (IllegalArgumentException exception) {
								throw new ModuleSpecificException(
										Errors.STAT_NOTYET,
										" (" + path + ')',
										exception);
							}
						} else {
							throw new ModuleSpecificException(
									Errors.EXCL_ID_CTX_PATH);
						}
					}
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				}
				writer = new LogRecordWriter(response);
				try {
					if (request.getFlag("numbers")) {
						long l = lfrom;
						for (LogRecord record : records) {
							writer.sendLine(l++, record);
						}
					} else {
						for (LogRecord record : records) {
							writer.sendLine(record);
						}
					}
				} catch (IOException exception) {
					throw new ModuleOutputException(exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("stdout", new CommandLineAction<TasksModule>() {
			private final Set<String> parameters = stringSet(
					"path",
					"id",
					"context",
					"from",
					"to");
			private final Set<String> flags = stringSet("numbers");
			private final String help = constructHelp(parameters, flags, false);

			@Override
			public void handle(
					TasksModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
				StringBuilder errors;

				errors = verifyArguments(request, parameters, flags, false);
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				String path;
				String id, context;
				String from, to;
				long lfrom, lto, lread;
				int iread;
				OutputHandle handle;
				String[] lines;

				from = request.getParameter("from");
				try {
					lfrom = null == from ? 0 : Long.valueOf(from);
				} catch (NumberFormatException exception) {
					throw new ModuleSpecificException(Errors.MALF_FROM, " ("
							+ from + ')', exception);
				}

				to = request.getParameter("to");
				try {
					lto = null == to ? Long.MAX_VALUE - 1 : Long.valueOf(to); // -
																				// 1
																				// avoids
																				// later
																				// overflow.
				} catch (NumberFormatException exception) {
					throw new ModuleSpecificException(Errors.MALF_TO, " (" + to
							+ ')', exception);
				}

				if (lfrom > lto) {
					lto = lfrom - 1;
				}

				path = request.getParameter("path");
				id = request.getParameter("id");
				context = request.getParameter("context");

				try {
					if (null == path) {
						if (null == id || null == context) {
							throw new ModuleSpecificException(
									Errors.REQD_ID_PATH);
						} else {
							try {
								handle = module.taskManagerReference.get()
										.getStandardOutput(context, id);
							} catch (LogStorageException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_LOG_OUT,
										exception);
							} catch (IllegalArgumentException exception) {
								throw new ModuleSpecificException(
										Errors.STAT_NOTYET,
										" (" + context + ", " + id + ')',
										exception);
							} catch (ComponentInitializationException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_TM,
										exception);
							}
						}
					} else {
						if (null == id && null == context) {
							TaskTreeQuery query;
							TaskTreeAddress address;
							TaskManagerInterface taskManager;

							try {
								taskManager = module.taskManagerReference.get();
							} catch (ComponentInitializationException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_TM,
										exception);
							}
							query = taskManager.getTaskTreeQuery();
							try {
								address = query.addressFromPath(path);
							} catch (MalformedAddressException exception) {
								throw new ModuleSpecificException(
										Errors.MALF_ADDRESS,
										" (" + path + ')',
										exception);
							}

							try {
								handle = taskManager.getStandardOutput(address);
							} catch (IllegalAddressException exception) {
								throw new ModuleSpecificException(
										Errors.INVD_ADDRESS,
										" (" + path + ')',
										exception);
							} catch (LogStorageException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_LOG_OUT,
										exception);
							} catch (IllegalArgumentException exception) {
								throw new ModuleSpecificException(
										Errors.STAT_NOTYET,
										" (" + path + ')',
										exception);
							}
						} else {
							throw new ModuleSpecificException(
									Errors.EXCL_ID_CTX_PATH);
						}
					}
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				}
				try {
					handle.skipLines(lfrom);
				} catch (IOException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_LOG_OUT,
							exception);
				}

				try {
					if (request.getFlag("numbers")) {
						for (;;) {
							lread = lto - lfrom + 1;
							iread = lread > LINES_AT_ONCE ? LINES_AT_ONCE
									: (int) lread;
							try {
								lines = handle.getNextLines(iread);
							} catch (IOException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_LOG_OUT,
										exception);
							}
							for (String line : lines) {
								response.sendOut(lfrom++ + " " + line + '\n');
							}
							if (lines.length < LINES_AT_ONCE) { // 'to' can be
																// huge.
								break;
							}
						}
					} else {
						for (;;) {
							lread = lto - lfrom + 1;
							iread = lread > LINES_AT_ONCE ? LINES_AT_ONCE
									: (int) lread;
							try {
								lines = handle.getNextLines(iread);
							} catch (IOException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_LOG_OUT,
										exception);
							}
							for (String line : lines) {
								response.sendOut(line + '\n');
							}
							if (lines.length < LINES_AT_ONCE) { // 'to' can be
																// huge.
								break;
							}
							lfrom += lines.length;
						}
					}
				} catch (IOException exception) {
					throw new ModuleOutputException(exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("stderr", new CommandLineAction<TasksModule>() {
			private final Set<String> parameters = stringSet(
					"path",
					"id",
					"context",
					"from",
					"to");
			private final Set<String> flags = stringSet("numbers");
			private final String help = constructHelp(parameters, flags, false);

			@Override
			public void handle(
					TasksModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
				StringBuilder errors;

				errors = verifyArguments(request, parameters, flags, false);
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				String path;
				String id, context;
				String from, to;
				long lfrom, lto, lread;
				int iread;
				OutputHandle handle;
				String[] lines;

				from = request.getParameter("from");
				try {
					lfrom = null == from ? 0 : Long.valueOf(from);
				} catch (NumberFormatException exception) {
					throw new ModuleSpecificException(Errors.MALF_FROM, " ("
							+ from + ')', exception);
				}

				to = request.getParameter("to");
				try {
					lto = null == to ? Long.MAX_VALUE - 1 : Long.valueOf(to); // -
																				// 1
																				// avoids
																				// later
																				// overflow.
				} catch (NumberFormatException exception) {
					throw new ModuleSpecificException(Errors.MALF_TO, " (" + to
							+ ')', exception);
				}

				if (lfrom > lto) {
					lto = lfrom - 1;
				}

				path = request.getParameter("path");
				id = request.getParameter("id");
				context = request.getParameter("context");

				try {
					if (null == path) {
						if (null == id || null == context) {
							throw new ModuleSpecificException(
									Errors.REQD_ID_PATH);
						} else {
							try {
								handle = module.taskManagerReference.get()
										.getErrorOutput(context, id);
							} catch (LogStorageException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_LOG_OUT,
										exception);
							} catch (IllegalArgumentException exception) {
								throw new ModuleSpecificException(
										Errors.STAT_NOTYET,
										" (" + context + ", " + id + ')',
										exception);
							} catch (ComponentInitializationException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_TM,
										exception);
							}
						}
					} else {
						if (null == id && null == context) {
							TaskTreeQuery query;
							TaskTreeAddress address;
							TaskManagerInterface taskManager;

							try {
								taskManager = module.taskManagerReference.get();
							} catch (ComponentInitializationException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_TM,
										exception);
							}
							query = taskManager.getTaskTreeQuery();
							try {
								address = query.addressFromPath(path);
							} catch (MalformedAddressException exception) {
								throw new ModuleSpecificException(
										Errors.MALF_ADDRESS,
										" (" + path + ')',
										exception);
							}

							try {
								handle = taskManager.getErrorOutput(address);
							} catch (IllegalAddressException exception) {
								throw new ModuleSpecificException(
										Errors.INVD_ADDRESS,
										" (" + path + ')',
										exception);
							} catch (LogStorageException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_LOG_OUT,
										exception);
							} catch (IllegalArgumentException exception) {
								throw new ModuleSpecificException(
										Errors.STAT_NOTYET,
										" (" + path + ')',
										exception);
							}
						} else {
							throw new ModuleSpecificException(
									Errors.EXCL_ID_CTX_PATH);
						}
					}
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				}
				try {
					handle.skipLines(lfrom);
				} catch (IOException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_LOG_OUT,
							exception);
				}
				try {
					if (request.getFlag("numbers")) {
						for (;;) {
							lread = lto - lfrom + 1;
							iread = lread > LINES_AT_ONCE ? LINES_AT_ONCE
									: (int) lread;
							try {
								lines = handle.getNextLines(iread);
							} catch (IOException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_LOG_OUT,
										exception);
							}
							for (String line : lines) {
								response.sendOut(lfrom++ + " " + line + '\n');
							}
							if (lines.length < LINES_AT_ONCE) { // 'to' can be
																// huge.
								break;
							}
						}
					} else {
						for (;;) {
							lread = lto - lfrom + 1;
							iread = lread > LINES_AT_ONCE ? LINES_AT_ONCE
									: (int) lread;
							try {
								lines = handle.getNextLines(iread);
							} catch (IOException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_LOG_OUT,
										exception);
							}
							for (String line : lines) {
								response.sendOut(line + '\n');
							}
							if (lines.length < LINES_AT_ONCE) { // 'to' can be
																// huge.
								break;
							}
							lfrom += lines.length;
						}
					}
				} catch (IOException exception) {
					throw new ModuleOutputException(exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("dump", new CommandLineAction<TasksModule>() {
			private final Set<String> parameters = stringSet(
					"path",
					"id",
					"context");
			private final Set<String> flags = stringSet("resolved");
			private final String help = constructHelp(parameters, flags, false);

			@Override
			public void handle(
					TasksModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
				StringBuilder errors;

				errors = verifyArguments(request, parameters, flags, false);
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				String path;
				String id;
				String context;
				TaskEntry task;
				OutputStream stream;

				path = request.getParameter("path");
				id = request.getParameter("id");
				context = request.getParameter("context");

				try {
					if (null == path) {
						if (null == id || null == context) {
							throw new ModuleSpecificException(
									Errors.REQD_ID_PATH);
						} else {
							try {
								task = module.taskManagerReference.get()
										.getTaskById(id, context);
							} catch (IllegalArgumentException exception) {
								throw new ModuleSpecificException(
										Errors.INVD_ID_CTX,
										" (" + id + ", " + context + ')',
										exception);
							} catch (ComponentInitializationException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_TM,
										exception);
							}
						}
					} else {
						if (null == id && null == context) {
							TaskTreeQuery query;
							TaskTreeAddress address;

							try {
								query = module.taskManagerReference.get()
										.getTaskTreeQuery();
							} catch (ComponentInitializationException exception) {
								throw new ModuleSpecificException(
										Errors.CONN_TM,
										exception);
							}
							try {
								address = query.addressFromPath(path);
							} catch (MalformedAddressException exception) {
								throw new ModuleSpecificException(
										Errors.MALF_ADDRESS,
										" (" + path + ')',
										exception);
							}
							try {
								task = query.getTaskAt(address);
							} catch (IllegalAddressException exception) {
								throw new ModuleSpecificException(
										Errors.INVD_ADDRESS,
										" (" + path + ')',
										exception);
							}
						} else {
							throw new ModuleSpecificException(
									Errors.EXCL_ID_CTX_PATH);
						}
					}
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				}

				stream = response.getStandardOutputStream();
				try {
					module.taskDescriptorComposer.compose(
							request.getFlag("resolved") ? task
									.getModifiedTaskDescriptor() : // Must be
																	// VALID.
																	// Cannot...
									task.getOriginalTaskDescriptor(), // ...have
																		// both
																		// RSL
																		// and
																		// asTask.
							stream);
				} catch (JAXBException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_MARSHAL,
							exception);
				} finally {
					try {
						stream.close(); // NECESSARY! (Double buffering.)
					} catch (IOException exception) {
						throw new ModuleOutputException(exception);
					}
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("run", new CommandLineAction<TasksModule>() {
			private final Set<String> parameters = stringSet();
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(parameters, flags, true);

			@Override
			public void handle(
					TasksModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException {
				StringBuilder errors;

				errors = verifyArguments(request, parameters, flags, true); // WOW!
																			// We
																			// want
																			// a
																			// blob!
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				TaskDescriptor td;

				try {
					td = module.taskDescriptorParser.parse(request
							.getBlobStream());
					module.taskManagerReference.get().runTask(td);
				} catch (UnmarshalException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_UMSL_VAL,
							exception);
				} catch (JAXBException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_UMSL,
							exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				} catch (IllegalArgumentException exception) {
					throw new ModuleSpecificException(
							Errors.MALF_TD_RUNNING,
							exception);
				} catch (ConvertorException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_UMSL_CONV,
							exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("kill", new CommandLineAction<TasksModule>() {
			private final Set<String> parameters = stringSet(
					"path",
					"id",
					"context");
			private final Set<String> flags = stringSet("recursive", "delete");
			private final String help = constructHelp(parameters, flags, false);

			@Override
			public void handle(
					TasksModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException {
				StringBuilder errors;

				errors = verifyArguments(request, parameters, flags, false);
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				TaskManagerInterface taskManager;
				String path;
				String id;
				String context;

				path = request.getParameter("path");
				id = request.getParameter("id");
				context = request.getParameter("context");

				try {
					if (null == path) { // No path => Expect a context.
						if (null == context) { // No context => Invalid request.
							throw new ModuleSpecificException(
									Errors.REQD_ID_CTX_PATH);
						} else { // OK, context specified.
							if (null == id) { // No id, kill the whole context.
								if (request.getFlag("recursive")) { // We
																	// require
																	// this
																	// safety
																	// word...
									if (request.getFlag("delete")) { // Delete
																		// context
																		// when
																		// killing?
										module.taskManagerReference.get()
												.killAndDeleteContext(context); // TODO:
																				// NOT
																				// REFLECTED
																				// in
																				// the
																				// tree.
									} else { // Don't delete, just kill.
										module.taskManagerReference.get()
												.killContextById(context);
									}
								} else { // Not recursive? Don't kill.
									throw new ModuleSpecificException(
											Errors.REQD_RECURSIVE);
								}
							} else { // ID specified, kill just one task.
								if (request.getFlag("delete")) { // Delete the
																	// task
																	// after
																	// killing?
									taskManager = module.taskManagerReference
											.get();
									try {
										taskManager
												.deleteTaskByAddress(taskManager
														.getTaskById(
																id,
																context)
														.getTreeAddress());
									} catch (IllegalAddressException exception) {
										throw new ModuleSpecificException(
												Errors.INVD_ID_CTX,
												" (" + id + ", " + context
														+ ')',
												exception);
									}
								} else { // Don't delete, just kill.
									module.taskManagerReference.get()
											.killTaskById(id, context);
								}
							}
						}
					} else { // Path specified.
						if (null == id && null == context) {
							TaskTreeQuery query;
							TaskTreeAddress address;

							taskManager = module.taskManagerReference.get();
							query = taskManager.getTaskTreeQuery();
							try {
								address = query.addressFromPath(path);
							} catch (MalformedAddressException exception) {
								throw new ModuleSpecificException(
										Errors.MALF_ADDRESS,
										" (" + path + ')',
										exception);
							}

							try {
								if (request.getFlag("delete")) { // Delete tasks
																	// and
																	// nodes?
									if (request.getFlag("recursive")) { // Kill
																		// recursively.
										taskManager
												.deleteNodeByAddress(address);
									} else { // Expect a leaf, kill one task.
										taskManager
												.deleteTaskByAddress(address);
									}
								} else { // Don't delete, just kill?
									if (request.getFlag("recursive")) { // Kill
																		// recursively.
										taskManager.killNodeByAddress(address);
									} else { // Expect a leaf, kill one task.
										taskManager.killTaskByAddress(address);
									}
								}
							} catch (IllegalAddressException exception) {
								throw new ModuleSpecificException(
										Errors.INVD_ADDRESS,
										" (" + path + ')',
										exception);
							}
						} else { // Context and ID can't be here.
							throw new ModuleSpecificException(
									Errors.EXCL_ID_CTX_PATH);
						}
					}
				} catch (IllegalArgumentException exception) {
					throw new ModuleSpecificException(Errors.INVD_ID_CTX, " ("
							+ id + ", " + context + ')', exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		StringBuilder builder = new StringBuilder();
		for (String action : actions.keySet()) {
			builder.append(action).append('\n');
		}
		ACTIONS_LIST = builder.toString();

		actions.put("help", new CommandLineAction<TasksModule>() {

			@Override
			public void handle(
					TasksModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
				try {
					response.sendOut(ACTIONS_LIST);
				} catch (IOException exception) {
					throw new ModuleOutputException(exception);
				}
			}

			@Override
			public String getHelpString() {
				return "";
			}
		});
	}

	/** A reference to the Task Manager. */
	private final TaskManagerReference taskManagerReference;

	/** A marshaller that outputs TD objects as XML. */
	private final BindingComposer<TaskDescriptor> taskDescriptorComposer;

	/** An unmarshaller that creates TD objects from XML. */
	private final BindingParser<TaskDescriptor> taskDescriptorParser;

	/**
	 * Initializes a new module to the default state ready for use.
	 * 
	 * @throws CommandLineException
	 *             When JAXB marshallers or unmarshallers cannot be created.
	 */
	public TasksModule() throws CommandLineException {
		this.taskManagerReference = new TaskManagerReference();

		try {
			this.taskDescriptorComposer = XSD.TD
					.createComposer(TaskDescriptor.class);
			this.taskDescriptorParser = XSD.TD
					.createParser(TaskDescriptor.class);
		} catch (SAXException exception) {
			Task task = CurrentTaskSingleton.getTaskHandle();
			if (null != task) {
				task.logError("JAXB parser could not find, read or parse schema files.");
			}
			throw new CommandLineException(exception); // Trace will be written.
		} catch (JAXBException exception) {
			Task task = CurrentTaskSingleton.getTaskHandle();
			if (null != task) {
				task.logError("JAXB parser refused or could not load the binding class.");
			}
			throw new CommandLineException(exception); // Trace will be written.
		}
	}

	@Override
	protected String getName() {
		return MODULE_NAME;
	}

	@Override
	protected void handleAction(
			String action,
			CommandLineRequest request,
			CommandLineResponse response) throws ModuleSpecificException,
			ModuleOutputException {
		CommandLineAction<TasksModule> act;

		act = actions.get(action);
		if (null == act) {
			throw new ModuleSpecificException(Errors.UNKN_ACTION, " (" + action
					+ ')');
		}
		if (request.getFlag("help")) {
			try {
				response.sendOut(act.getHelpString());
			} catch (IOException exception) {
				throw new ModuleOutputException(exception);
			}
		} else {
			act.handle(this, request, response);
		}
	}

	@Override
	protected void restoreState() {
		taskManagerReference.drop();
	}

	@Override
	protected String getActionsList() {
		return ACTIONS_LIST;
	}
}
