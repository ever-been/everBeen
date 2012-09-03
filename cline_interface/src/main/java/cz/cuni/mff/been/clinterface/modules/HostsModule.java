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
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Map;
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
import cz.cuni.mff.been.clinterface.adapters.HostDataConvertor;
import cz.cuni.mff.been.clinterface.ref.LoadServerReference;
import cz.cuni.mff.been.clinterface.ref.ServiceReference;
import cz.cuni.mff.been.clinterface.ref.TaskManagerReference;
import cz.cuni.mff.been.clinterface.writers.AliasDefinitionWriter;
import cz.cuni.mff.been.clinterface.writers.HostGroupWriter;
import cz.cuni.mff.been.clinterface.writers.HostInfoWriter;
import cz.cuni.mff.been.clinterface.writers.HostStatusWriter;
import cz.cuni.mff.been.clinterface.writers.LogRecordWriter;
import cz.cuni.mff.been.clinterface.writers.PropertyTreeWriter;
import cz.cuni.mff.been.clinterface.writers.TaskEntryWriter;
import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.common.Message;
import cz.cuni.mff.been.hostmanager.HostDatabaseException;
import cz.cuni.mff.been.hostmanager.HostManagerException;
import cz.cuni.mff.been.hostmanager.HostManagerInterface;
import cz.cuni.mff.been.hostmanager.InvalidArgumentException;
import cz.cuni.mff.been.hostmanager.OperationHandle;
import cz.cuni.mff.been.hostmanager.ValueNotFoundException;
import cz.cuni.mff.been.hostmanager.database.HostGroup;
import cz.cuni.mff.been.hostmanager.database.HostInfoInterface;
import cz.cuni.mff.been.hostmanager.database.NameValuePair;
import cz.cuni.mff.been.hostmanager.database.PropertyDescriptionTable;
import cz.cuni.mff.been.hostmanager.database.PropertyTreeReadInterface;
import cz.cuni.mff.been.hostmanager.database.SoftwareAliasDefinition;
import cz.cuni.mff.been.hostmanager.load.LoadServerInterface;
import cz.cuni.mff.been.jaxb.BindingComposer;
import cz.cuni.mff.been.jaxb.BindingParser;
import cz.cuni.mff.been.jaxb.ConvertorException;
import cz.cuni.mff.been.jaxb.XSD;
import cz.cuni.mff.been.jaxb.group.Group;
import cz.cuni.mff.been.jaxb.properties.Alias;
import cz.cuni.mff.been.jaxb.properties.Properties;
import cz.cuni.mff.been.logging.LogRecord;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.logging.LogUtils;
import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;
import cz.cuni.mff.been.taskmanager.data.TaskEntry;

import static cz.cuni.mff.been.services.Names.HOST_MANAGER_REMOTE_INTERFACE_MAIN;
import static cz.cuni.mff.been.services.Names.HOST_MANAGER_SERVICE_NAME;
import static cz.cuni.mff.been.services.Names.HOST_MANAGER_SERVICE_HUMAN_NAME;

/**
 * A command line interface component that corresponds to the hosts listing
 * screen.
 * 
 * @author Andrej Podzimek
 */
public final class HostsModule extends CommandLineModule {

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
		CONN_TM("Could not contact the Task Manager"),

		/**
		 * When the Host Manager is not responding (RemoteException,
		 * IOException).
		 */
		CONN_HM("Could not contact the Host Manager."),

		/**
		 * When the Load Server is not responding (RemoteException,
		 * IOException).
		 */
		CONN_LS("Could not contact the Load Server."),

		/** Invalid parameters or required parameters missing. */
		INVD_PARAMS(""),

		/** Invalid regular expression used. */
		INVD_REGEXP("Illegal regular expression."),

		/** Invalid or unknown host name specified. */
		UNKN_HOST("Unknown host name."),

		/** Invalid date specified. */
		INVD_DATE("Invalid date format."),

		/** When an unknown property is requested. */
		UNKN_PROP("No such property value in current context."),

		/** When a property of a non-matching type is provided for comparison. */
		INVD_TYPE("Property type does not match."),

		/** When an unknown property subtree is requested. */
		UNKN_OBJ("No such property object in current context."),

		/**
		 * Either the host name is invalid, or no record exists for the date.
		 * (!!!)
		 */
		UNKN_DATE_HOST("No record for the host and date."),

		/**
		 * Either the host name is invalid, or no logs have been generated so
		 * far. (!!!)
		 */
		UNKN_HOST_LOGS("No logs generated or host unknown"),

		/** An unknown software alias name requested. */
		UNKN_ALIAS("No such software alias."),

		/** An unknown host group name requested. */
		UNKN_GROUP("No such host group."),

		/** HostManagerException caught. Nobody knows what happened. */
		FAIL_HM("Unknown host manager failure occured."),

		/** A query to the database of hosts failed. */
		FAIL_HOST_QUERY("Host database query failed."),

		/** A weird and unknown failure inside the Log Storage occured. */
		FAIL_LOG_QUERY("Log Storage query failed."),

		/** The host manager could not save its database of aliases. */
		FAIL_ALIAS_QUERY("Failed to update the aliases database."),

		/** The host manager could not save its database of host groups. */
		FAIL_GROUP_QUERY("Failed to update the groups database."),

		/** When both 'user' and 'desc' are specified together. */
		EXCL_USER_DESC("'user' and 'desc' are mutually exclusive."),

		/** When both 'date' and 'history' are specified together. */
		EXCL_DATE_HIST("'date' and 'history' are mutually exclusive."),

		/**
		 * When an unknown/unexpected NameValuePair is found in a software
		 * alias.
		 */
		INTG_NVP(
				"Illegal item in restriction or properties data (name-value pair)."),

		/**
		 * When an unknown/unexpected NameValuePair is found in a software
		 * alias.
		 */
		INTG_PROPS("Illegal item in restriction or properties XML data."),

		/**
		 * When the restriction cannot be cast to ObjectRestriction or
		 * RSLRestriction.
		 */
		INTG_ALIAS("Unknown restriction type in Alias definition."),

		/** When the Host Manager refuses an alias definition. */
		MALF_ALIAS("Alias definiton refused. Details follow."),

		/** When the Host Manager refuses a group definition. */
		MALF_GROUP("Group definition refused. Details follow."),

		/** When the host maanger refuses a property name. */
		MALF_PROP("Illegal or malformed name of property."),

		/** When the host manager refuses an object ('type') name. */
		MALF_OBJ("Illegal or malformed name of object."),

		/** When the RSLRestriction refuses a RSL query. */
		MALF_RSL_APP(
				"The RSL expression for appliaction selection is malformed."),

		/** When the RSLRestriction refuses a RSL query. */
		MALF_RSL_OS(
				"The RSL expression for operating system selection is malformed."),

		/** When the RSLRestriction refuses a RSL query. */
		MALF_RSL_GROUP("The RSL expression for host selection is malformed."),

		/** When modification of the properties object fails. */
		FAIL_MOD_PROP(""),

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

		/** When the refresh action is called without 'host' and without 'all'. */
		REQD_ALL_HOST("Either 'host' must be specified, or 'all' must be set."),

		/** When 'host' and 'all' are both used at once. */
		EXCL_ALL_HOST("'host' and 'all' are mutually exclusive");

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
	public static final String MODULE_NAME = "hosts";

	/** A map of actions provided by this module. */
	private static final TreeMap<String, CommandLineAction<HostsModule>> actions;

	/** A list of actions this module provides. */
	private static final String ACTIONS_LIST;

	static {
		actions = new TreeMap<String, CommandLineAction<HostsModule>>();

		actions.put("list", new CommandLineAction<HostsModule>() {
			private final Set<String> parameters = stringSet("pattern");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(parameters, flags, false);

			@Override
			public void handle(
					HostsModule module,
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

				HostInfoWriter writer;
				HostInfoInterface[] hostInfos;
				String pattern;
				Pattern cpattern;

				pattern = request.getParameter("pattern");
				writer = new HostInfoWriter(response);

				try {
					hostInfos = module.hostManagerReference.get()
							.getHostInfos();
					if (null == pattern) {
						for (HostInfoInterface hostInfo : hostInfos) {
							writer.sendLinePlain(hostInfo);
						}
					} else {
						cpattern = Pattern.compile(pattern);
						for (HostInfoInterface hostInfo : hostInfos) {
							if (cpattern.matcher(hostInfo.getHostName())
									.matches()) {
								writer.sendLinePlain(hostInfo);
							}
						}
					}
				} catch (PatternSyntaxException exception) {
					throw new ModuleSpecificException(Errors.INVD_REGEXP, " /"
							+ pattern + '/', exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (IOException exception) {
					throw new ModuleOutputException(exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("sample", new CommandLineAction<HostsModule>() {
			private final Set<String> parameters = stringSet("host");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					HostsModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
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

				LoadServerInterface loadServer;
				HostStatusWriter writer;
				String hostName;

				hostName = request.getParameter("host");
				writer = new HostStatusWriter(response);

				try {
					loadServer = module.loadServerReference.get();
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_LS, exception);
				}

				try {
					writer.sendLine(
							hostName,
							loadServer.getHostStatus(hostName), // Atomicity not
																// guaranteed!
							loadServer.getLastSample(hostName) // <<< HERE! A
																// different
																// host?
					);
				} catch (ValueNotFoundException exception) {
					throw new ModuleSpecificException(Errors.UNKN_HOST, " ("
							+ hostName + ')', exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_LS, exception);
				} catch (IOException exception) {
					throw new ModuleOutputException(exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("config", new CommandLineAction<HostsModule>() {
			private final Set<String> allParameters = stringSet("host", "date");
			private final Set<String> requiredParameters = stringSet("host");
			private final Set<String> flags = stringSet(
					"system",
					"cpu",
					"storage",
					"network",
					"history" // Keep in sync with Writer!
			);
			private final String help = constructHelp(
					allParameters,
					flags,
					requiredParameters,
					false);

			@Override
			public void handle(
					HostsModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
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

				HostManagerInterface hostManager;
				HostInfoInterface hostInfo;
				DateFormat dateFormat;
				HostInfoWriter writer;
				String date;
				String hostName;
				boolean makeGap;

				hostName = request.getParameter("host");
				date = request.getParameter("date");
				dateFormat = DateFormat.getDateTimeInstance(
						DateFormat.SHORT,
						DateFormat.SHORT);
				writer = new HostInfoWriter(response);

				try {
					hostManager = module.hostManagerReference.get();
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				}

				if (null == date) {
					try {
						hostInfo = hostManager.getHostInfo(hostName);
					} catch (ValueNotFoundException exception) {
						throw new ModuleSpecificException(
								Errors.UNKN_HOST,
								" (" + hostName + ')',
								exception);
					} catch (RemoteException exception) {
						throw new ModuleSpecificException(
								Errors.CONN_HM,
								exception);
					}
				} else {
					if (request.getFlag("history")) {
						throw new ModuleSpecificException(Errors.EXCL_DATE_HIST);
					}
					try {
						hostInfo = hostManager.getHostHistoryEntry(
								hostName,
								dateFormat.parse(date));
					} catch (ParseException exception) {
						throw new ModuleSpecificException(
								Errors.INVD_DATE,
								" (" + date + ')',
								exception);
					} catch (HostDatabaseException exception) {
						throw new ModuleSpecificException(
								Errors.FAIL_HOST_QUERY,
								exception);
					} catch (ValueNotFoundException exception) {
						throw new ModuleSpecificException(
								Errors.UNKN_DATE_HOST,
								" (" + hostName + ", " + date + ')',
								exception);
					} catch (RemoteException exception) {
						throw new ModuleSpecificException(
								Errors.CONN_HM,
								exception);
					}
				}

				try {
					if (request.getFlag("history")) {
						writer.sendLinesHistory(
								hostManager.getHostHistoryDates(hostName),
								false);
						makeGap = true;
					} else {
						makeGap = false;
					}

					if (request.getFlag("system")) {
						writer.sendLineSystem(
								hostInfo.getOperatingSystem(),
								makeGap);
						makeGap = true;
					} else {
						makeGap = false;
					}

					if (request.getFlag("cpu")) {
						writer.sendLinesCPU(hostInfo, makeGap);
						makeGap = true;
					} else {
						makeGap = false;
					}

					if (request.getFlag("storage")) {
						writer.sendLinesStorage(hostInfo, makeGap);
						makeGap = true;
					} else {
						makeGap = false;
					}

					if (request.getFlag("network")) {
						writer.sendLinesNetwork(hostInfo, makeGap);
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

		actions.put("refresh", new CommandLineAction<HostsModule>() {
			private final Set<String> parameters = stringSet("host");
			private final Set<String> flags = stringSet("all");
			private final String help = constructHelp(parameters, flags, false);

			@Override
			public void handle(
					HostsModule module,
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

				String host;
				HostManagerInterface hostManager;

				try {
					host = request.getParameter("host");
					if (null == host) {
						if (request.getFlag("all")) {
							Map<String, OperationHandle> handles;

							hostManager = module.hostManagerReference.get();
							handles = hostManager.refreshAll();
							for (OperationHandle handle : handles.values()) { // No
																				// output.
																				// No
																				// active
																				// waiting!
								hostManager.removeOperationStatus(handle);
							}
						} else {
							throw new ModuleSpecificException(
									Errors.REQD_ALL_HOST);
						}
					} else {
						if (request.getFlag("all")) {
							throw new ModuleSpecificException(
									Errors.EXCL_ALL_HOST);
						} else {
							OperationHandle handle;

							hostManager = module.hostManagerReference.get();
							handle = hostManager.refreshHost(host);
							hostManager.removeOperationStatus(handle); // No
																		// output.
																		// No
																		// active
																		// waiting!
						}
					}
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (UnknownHostException exception) {
					throw new ModuleSpecificException(
							Errors.UNKN_HOST,
							exception);
				} catch (ValueNotFoundException exception) {
					throw new ModuleSpecificException(
							Errors.UNKN_HOST,
							exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("tasks", new CommandLineAction<HostsModule>() {
			private final Set<String> allParameters = stringSet(
					"host",
					"pattern");
			private final Set<String> requiredParameters = stringSet("host");
			private final Set<String> flags = stringSet("desc");
			private final String help = constructHelp(
					allParameters,
					flags,
					requiredParameters,
					false);

			@Override
			public void handle(
					HostsModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
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

				TaskEntry[] tasks;
				TaskEntryWriter writer;
				String pattern;
				Pattern cpattern;

				pattern = request.getParameter("pattern");
				writer = new TaskEntryWriter(response);

				try {
					tasks = module.taskManagerReference.get().getTasksOnHost(
							request.getParameter("host"));
					if (null == pattern) {
						if (request.getFlag("desc")) {
							for (TaskEntry task : tasks) {
								writer.sendLineDescr(task);
							}
						} else {
							for (TaskEntry task : tasks) {
								writer.sendLinePlain(task);
							}
						}
					} else {
						cpattern = Pattern.compile(pattern);
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
				} catch (PatternSyntaxException exception) {
					throw new ModuleSpecificException(Errors.INVD_REGEXP, " /"
							+ pattern + '/', exception);
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

		actions.put("logs", new CommandLineAction<HostsModule>() {
			private final Set<String> parameters = stringSet("host");
			private final Set<String> flags = stringSet("numbers");
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					HostsModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
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

				TaskManagerInterface taskManager;
				LogRecord[] records;
				LogRecordWriter writer;
				String hostName;

				hostName = request.getParameter("host");
				try {
					taskManager = module.taskManagerReference.get();
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				}

				try {
					records = LogUtils.getLogRecordsForTasks(
							taskManager,
							taskManager.getTasksOnHost(hostName));
				} catch (LogStorageException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_LOG_QUERY,
							exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_TM, exception);
				}

				if (records.length == 0) {
					throw new ModuleSpecificException(
							Errors.UNKN_HOST_LOGS,
							hostName);
				}

				writer = new LogRecordWriter(response);
				try {
					if (request.getFlag("numbers")) {
						for (int i = 0; i < records.length; ++i) {
							writer.sendLine(i, records[i]);
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

		actions.put("alias-list", new CommandLineAction<HostsModule>() {
			private final Set<String> parameters = stringSet("pattern");
			private final Set<String> flags = stringSet("restrict");
			private final String help = constructHelp(parameters, flags, false);

			@Override
			public void handle(
					HostsModule module,
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

				AliasDefinitionWriter writer;
				SoftwareAliasDefinition[] definitions;
				String pattern;
				Pattern cpattern;

				pattern = request.getParameter("pattern");
				writer = new AliasDefinitionWriter(response);

				try {
					definitions = module.hostManagerReference.get()
							.getAliasDefinitions();
					if (null == pattern) {
						if (request.getFlag("restrict")) {
							for (SoftwareAliasDefinition definition : definitions) {
								writer.sendLineXtend(definition);
							}
						} else {
							for (SoftwareAliasDefinition definition : definitions) {
								writer.sendLinePlain(definition);
							}
						}
					} else {
						cpattern = Pattern.compile(pattern);
						if (request.getFlag("restrict")) {
							for (SoftwareAliasDefinition definition : definitions) {
								if (cpattern.matcher(definition.getAliasName())
										.matches()) {
									writer.sendLineXtend(definition);
								}
							}
						} else {
							for (SoftwareAliasDefinition definition : definitions) {
								if (cpattern.matcher(definition.getAliasName())
										.matches()) {
									writer.sendLinePlain(definition);
								}
							}

						}
					}
				} catch (PatternSyntaxException exception) {
					throw new ModuleSpecificException(Errors.INVD_REGEXP, " /"
							+ pattern + '/', exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (IOException exception) {
					throw new ModuleOutputException(exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("alias-dump", new CommandLineAction<HostsModule>() {
			private final Set<String> parameters = stringSet("name");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					HostsModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
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

				Alias alias;
				String name;
				OutputStream stream;

				name = request.getParameter("name");

				try {
					alias = HostDataConvertor
							.aliasToAlias(module.hostManagerReference.get()
									.getAliasDefinitionByName(name));
				} catch (ValueNotFoundException exception) {
					throw new ModuleSpecificException(Errors.UNKN_ALIAS, " ("
							+ name + ')', exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				}

				stream = response.getStandardOutputStream();
				try {
					module.aliasComposer.compose(alias, stream);
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

		actions.put("alias-put", new CommandLineAction<HostsModule>() {
			private final Set<String> parameters = stringSet("edit");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(parameters, flags, true);

			@Override
			public void handle(
					HostsModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException {
				StringBuilder errors;

				errors = verifyArguments(request, parameters, flags, true);
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				String edit;
				Alias alias;

				try {
					alias = module.aliasParser.parse(request.getBlobStream());
				} catch (UnmarshalException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_UMSL_VAL,
							exception);
				} catch (JAXBException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_UMSL,
							exception);
				} catch (ConvertorException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_UMSL_CONV,
							exception);
				}

				edit = request.getParameter("edit");
				try {
					HostManagerInterface hostManager;

					hostManager = module.hostManagerReference.get();
					if (null == edit) {
						hostManager.addAliasDefinition( // !!! Duplicates
														// tolerated !!!
								HostDataConvertor.aliasToAlias(alias));
					} else {
						SoftwareAliasDefinition original;
						try {
							original = hostManager
									.removeAliasDefinitionByName(edit);
						} catch (ValueNotFoundException exception) {
							throw new ModuleSpecificException(
									Errors.UNKN_ALIAS,
									" (" + edit + ')',
									exception);
						}
						HostDataConvertor.modifyAlias(alias, original);
						hostManager.addAliasDefinition(original); // !!!
																	// Duplicates
																	// tolerated
																	// !!!
					}
					hostManager.rebuildAliasTableForAllHosts();
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (HostDatabaseException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_ALIAS_QUERY,
							exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("alias-del", new CommandLineAction<HostsModule>() {
			private final Set<String> parameters = stringSet("name");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					HostsModule module,
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

				String name;

				name = request.getParameter("name");

				try {
					module.hostManagerReference.get()
							.removeAliasDefinitionByName(name);
				} catch (HostDatabaseException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_ALIAS_QUERY,
							exception);
				} catch (ValueNotFoundException exception) {
					throw new ModuleSpecificException(Errors.UNKN_ALIAS, " ("
							+ name + ')', exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("group-list", new CommandLineAction<HostsModule>() {
			private final Set<String> parameters = stringSet("pattern", "host");
			private final Set<String> flags = stringSet("desc");
			private final String help = constructHelp(parameters, flags, false);

			@Override
			public void handle(
					HostsModule module,
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

				HostGroupWriter writer;
				HostGroup[] groups;
				String host;
				String pattern;
				Pattern cpattern;

				pattern = request.getParameter("pattern");
				host = request.getParameter("host");
				writer = new HostGroupWriter(response);

				try {
					groups = module.hostManagerReference.get().getGroups();
					if (null == pattern) {
						if (null == host) {
							if (request.getFlag("desc")) {
								for (HostGroup group : groups) {
									writer.sendLineXtend(group);
								}
							} else {
								for (HostGroup group : groups) {
									writer.sendLinePlain(group);
								}
							}
						} else {
							if (request.getFlag("desc")) {
								for (HostGroup group : groups) {
									if (group.getAllHosts().contains(host)) {
										writer.sendLineXtend(group);
									}
								}
							} else {
								for (HostGroup group : groups) {
									if (group.getAllHosts().contains(host)) {
										writer.sendLinePlain(group);
									}
								}
							}
						}
					} else {
						cpattern = Pattern.compile(pattern);
						if (null == host) {
							if (request.getFlag("desc")) {
								for (HostGroup group : groups) {
									if (cpattern.matcher(group.getName())
											.matches()) {
										writer.sendLineXtend(group);
									}
								}
							} else {
								for (HostGroup group : groups) {
									if (cpattern.matcher(group.getName())
											.matches()) {
										writer.sendLinePlain(group);
									}
								}
							}
						} else {
							if (request.getFlag("desc")) {
								for (HostGroup group : groups) {
									if (cpattern.matcher(group.getName())
											.matches()
											&& group.getAllHosts().contains(
													host)) {
										writer.sendLineXtend(group);
									}
								}
							} else {
								for (HostGroup group : groups) {
									if (cpattern.matcher(group.getName())
											.matches()
											&& group.getAllHosts().contains(
													host)) {
										writer.sendLinePlain(group);
									}
								}
							}
						}
					}
				} catch (PatternSyntaxException exception) {
					throw new ModuleSpecificException(Errors.INVD_REGEXP, " /"
							+ pattern + '/', exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (IOException exception) {
					throw new ModuleOutputException(exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("group-dump", new CommandLineAction<HostsModule>() {
			private final Set<String> parameters = stringSet("name");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					HostsModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
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

				String name;
				OutputStream stream;
				Group group;

				name = request.getParameter("name");
				try {
					group = HostDataConvertor
							.groupToGroup(module.hostManagerReference.get()
									.getGroup(name));
				} catch (ValueNotFoundException exception) {
					throw new ModuleSpecificException(Errors.UNKN_GROUP, " ("
							+ name + ')', exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				}

				stream = response.getStandardOutputStream();
				try {
					module.groupComposer.compose(group, stream);
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

		actions.put("group-put", new CommandLineAction<HostsModule>() {
			private final Set<String> parameters = stringSet("edit");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(parameters, flags, true);

			@Override
			public void handle(
					HostsModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException {
				StringBuilder errors;

				errors = verifyArguments(request, parameters, flags, true);
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				HostManagerInterface hostManager;
				String edit;
				Group group;

				try {
					group = module.groupParser.parse(request.getBlobStream());
				} catch (UnmarshalException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_UMSL_VAL,
							exception);
				} catch (JAXBException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_UMSL,
							exception);
				} catch (ConvertorException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_UMSL_CONV,
							exception);
				}

				edit = request.getParameter("edit");
				try {
					hostManager = module.hostManagerReference.get();
					if (null == edit) {
						hostManager.addGroup(HostDataConvertor.groupToGroup(
								group,
								hostManager));
					} else {
						HostGroup original;

						try {
							original = hostManager.removeGroup(edit);
						} catch (ValueNotFoundException exception) {
							throw new ModuleSpecificException(
									Errors.UNKN_GROUP,
									" (" + edit + ')',
									exception);
						}
						HostDataConvertor.modifyGroup(
								group,
								original,
								hostManager);
						hostManager.addGroup(original);
					}
				} catch (InvalidArgumentException exception) {
					throw new ModuleSpecificException(
							Errors.MALF_GROUP,
							exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (HostDatabaseException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_GROUP_QUERY,
							exception);
				} catch (HostManagerException exception) {
					throw new ModuleSpecificException(Errors.FAIL_HM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("group-del", new CommandLineAction<HostsModule>() {
			private final Set<String> parameters = stringSet("name");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					HostsModule module,
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

				String name;

				name = request.getParameter("name");
				try {
					module.hostManagerReference.get().removeGroup(name);
				} catch (HostDatabaseException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_GROUP_QUERY,
							exception);
				} catch (ValueNotFoundException exception) {
					throw new ModuleSpecificException(Errors.UNKN_GROUP, " ("
							+ name + ')', exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("property-list", new CommandLineAction<HostsModule>() {
			private final Set<String> allParameters = stringSet(
					"pattern",
					"host");
			private final Set<String> requiredParameters = stringSet("host");
			private final Set<String> flags = stringSet("desc", "user");
			private final String help = constructHelp(
					allParameters,
					flags,
					requiredParameters,
					false);

			@Override
			public void handle(
					HostsModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
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

				HostManagerInterface hostManager;
				HostInfoInterface hi;
				PropertyDescriptionTable table;
				PropertyTreeWriter writer;
				String pattern;
				Pattern cpattern;
				String host;

				pattern = request.getParameter("pattern");
				host = request.getParameter("host");
				writer = new PropertyTreeWriter(response);

				try {
					hostManager = module.hostManagerReference.get();
					hi = hostManager.getHostInfo(host);
					if (null == pattern) {
						if (request.getFlag("desc")) {
							table = hostManager.getPropertyDescriptionTable();
							if (request.getFlag("user")) {
								throw new ModuleSpecificException(
										Errors.EXCL_USER_DESC);
							} else {
								for (NameValuePair property : hi
										.getProperties()) {
									writer.sendPropDescr(property, 0, table, "");
								}
								for (PropertyTreeReadInterface obj : hi
										.getObjects()) {
									writer.sendObjDescr(obj, 0, table);
								}
							}
						} else {
							if (request.getFlag("user")) {
								for (NameValuePair property : hi
										.getUserProperties()) {
									writer.sendPropPlain(property, 0);
								}
							} else {
								for (NameValuePair property : hi
										.getProperties()) {
									writer.sendPropPlain(property, 0);
								}
								for (PropertyTreeReadInterface obj : hi
										.getObjects()) {
									writer.sendObjPlain(obj, 0);
								}
							}
						}
					} else {
						cpattern = Pattern.compile(pattern);
						if (request.getFlag("desc")) {
							table = hostManager.getPropertyDescriptionTable();
							if (request.getFlag("user")) {
								throw new ModuleSpecificException(
										Errors.EXCL_USER_DESC);
							} else {
								for (NameValuePair property : hi
										.getProperties()) {
									if (cpattern.matcher(property.getName())
											.matches()) {
										writer.sendPropDescr(
												property,
												0,
												table,
												"");
									}
								}
								for (PropertyTreeReadInterface obj : hi
										.getObjects()) {
									if (cpattern.matcher(obj.getName(true))
											.matches()) {
										writer.sendObjFilDs(
												obj,
												0,
												table,
												cpattern);
									}
								}
							}
						} else {
							if (request.getFlag("user")) {
								for (NameValuePair property : hi
										.getUserProperties()) {
									if (cpattern.matcher(property.getName())
											.matches()) {
										writer.sendPropPlain(property, 0);
									}
								}
							} else {
								for (NameValuePair property : hi
										.getProperties()) {
									if (cpattern.matcher(property.getName())
											.matches()) {
										writer.sendPropPlain(property, 0);
									}
								}
								for (PropertyTreeReadInterface obj : hi
										.getObjects()) {
									if (cpattern.matcher(obj.getName(true))
											.matches()) {
										writer.sendObjFiltr(obj, 0, cpattern);
									}
								}
							}
						}
					}
				} catch (PatternSyntaxException exception) {
					throw new ModuleSpecificException(Errors.INVD_REGEXP, " /"
							+ pattern + '/', exception);
				} catch (ValueNotFoundException exception) {
					throw new ModuleSpecificException(Errors.UNKN_HOST, " ("
							+ host + ')', exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (IOException exception) {
					throw new ModuleOutputException(exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("property-dump", new CommandLineAction<HostsModule>() {
			private final Set<String> parameters = stringSet("host");
			private final Set<String> flags = stringSet("user");
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					HostsModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
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

				Properties properties;
				String host;
				OutputStream stream;

				host = request.getParameter("host");

				try {
					properties = HostDataConvertor.objectToProperties(request
							.getFlag("user") ? module.hostManagerReference
							.get().getHostInfo(host).getUserPropertiesObject()
							: module.hostManagerReference.get().getHostInfo(
									host));
				} catch (ValueNotFoundException exception) {
					throw new ModuleSpecificException(Errors.UNKN_HOST, " ("
							+ host + ')', exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				}

				stream = response.getStandardOutputStream();
				try {
					module.propertiesComposer.compose(properties, stream);
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

		actions.put("property-put", new CommandLineAction<HostsModule>() {
			private final Set<String> parameters = stringSet("host");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					true);

			@Override
			public void handle(
					HostsModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException {
				StringBuilder errors;

				errors = verifyArguments(
						request,
						parameters,
						flags,
						parameters,
						true);
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				Properties properties;
				HostInfoInterface hi;
				HostManagerInterface hostManager;
				String host;

				host = request.getParameter("host");

				try {
					hostManager = module.hostManagerReference.get();
					hi = hostManager.getHostInfo(host);
					properties = module.propertiesParser.parse(request
							.getBlobStream());
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				} catch (ValueNotFoundException exception) {
					throw new ModuleSpecificException(
							Errors.UNKN_HOST,
							exception);
				} catch (UnmarshalException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_UMSL_VAL,
							exception);
				} catch (JAXBException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_UMSL,
							exception);
				} catch (ConvertorException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_UMSL_CONV,
							exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				}
				HostDataConvertor.modifyProperties(
						properties,
						hi.getUserPropertiesObject()); // OK, this ca throw. :-)

				try {
					hostManager.updateUserProperties(hi);
				} catch (HostDatabaseException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_HOST_QUERY,
							exception);
				} catch (InvalidArgumentException exception) { // Host removed
																// on the fly.
					throw new ModuleSpecificException( // As if it had never
														// existed.
							Errors.UNKN_HOST,
							" (" + host + ')',
							exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("property-del", new CommandLineAction<HostsModule>() {
			private final Set<String> parameters = stringSet("host", "name");
			private final Set<String> flags = stringSet("tree");
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					HostsModule module,
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

				HostInfoInterface hi;
				HostManagerInterface hostManager;
				String host;
				String name;

				host = request.getParameter("host");

				try {
					hostManager = module.hostManagerReference.get();

					try {
						hi = hostManager.getHostInfo(host);
					} catch (ValueNotFoundException exception) {
						throw new ModuleSpecificException(
								Errors.UNKN_HOST,
								" (" + host + ')',
								exception);
					} catch (RemoteException exception) {
						throw new ModuleSpecificException(
								Errors.CONN_HM,
								exception);
					}

					name = request.getParameter("name");
					if (request.getFlag("tree")) {
						try {
							hi.getUserPropertiesObject().removeObject(name);
						} catch (ValueNotFoundException exception) {
							throw new ModuleSpecificException(
									Errors.UNKN_OBJ,
									" (" + name + ')',
									exception);
						} catch (InvalidArgumentException exception) {
							throw new ModuleSpecificException(
									Errors.MALF_OBJ,
									" (" + name + ')',
									exception);
						}
					} else {
						try {
							hi.removeUserProperty(name);
						} catch (ValueNotFoundException exception) {
							throw new ModuleSpecificException(
									Errors.UNKN_PROP,
									" (" + name + ')',
									exception);
						} catch (InvalidArgumentException exception) {
							throw new ModuleSpecificException(
									Errors.MALF_PROP,
									" (" + name + ')',
									exception);
						}
					}
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_HM, exception);
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

		actions.put("help", new CommandLineAction<HostsModule>() {

			@Override
			public void handle(
					HostsModule module,
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

	/** A reference to the Host Manager. */
	private final ServiceReference<HostManagerInterface> hostManagerReference;

	/** A reference to the Load Server. */
	private final LoadServerReference loadServerReference;

	/** A mashaller that outputs Alias objects as XML. */
	private final BindingComposer<Alias> aliasComposer;

	/** A mashaller that outputs Group objects as XML. */
	private final BindingComposer<Group> groupComposer;

	/** A marshaller that outputs Properties objects as XML. */
	private final BindingComposer<Properties> propertiesComposer;

	/** An unmarshaller that creates Alias objects from XML. */
	private final BindingParser<Alias> aliasParser;

	/** An unmarshaller that creates Group objects from XML. */
	private final BindingParser<Group> groupParser;

	/** An unmarshaller that creates Properties objects from XML. */
	private final BindingParser<Properties> propertiesParser;

	/**
	 * Initializes a new module to the default state ready for use.
	 * 
	 * @throws CommandLineException
	 *             When JAXB marshallers or unmarshallers cannot be created.
	 */
	public HostsModule() throws CommandLineException {
		this.taskManagerReference = new TaskManagerReference();
		this.hostManagerReference = new ServiceReference<HostManagerInterface>(
				taskManagerReference,
				HOST_MANAGER_SERVICE_NAME,
				HOST_MANAGER_REMOTE_INTERFACE_MAIN, //TODO: CHECK THIS!
				HOST_MANAGER_SERVICE_HUMAN_NAME);
		this.loadServerReference = new LoadServerReference(hostManagerReference);

		try {
			this.aliasComposer = XSD.PROPERTIES.createComposer(Alias.class);
			this.aliasParser = XSD.PROPERTIES.createParser(Alias.class);

			this.groupComposer = XSD.GROUP.createComposer(Group.class);
			this.groupParser = XSD.GROUP.createParser(Group.class);

			this.propertiesComposer = XSD.PROPERTIES
					.createComposer(Properties.class);
			this.propertiesParser = XSD.PROPERTIES
					.createParser(Properties.class);
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
		CommandLineAction<HostsModule> act;

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
		hostManagerReference.drop();
		loadServerReference.drop();
	}

	@Override
	protected String getActionsList() {
		return ACTIONS_LIST;
	}
}
