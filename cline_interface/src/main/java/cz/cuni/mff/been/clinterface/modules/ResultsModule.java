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
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
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
import cz.cuni.mff.been.clinterface.adapters.ResultsRepositoryJAXBAdapter;
import cz.cuni.mff.been.clinterface.ref.TaskManagerReference;
import cz.cuni.mff.been.clinterface.writers.AnalysisDatasetWriter;
import cz.cuni.mff.been.clinterface.writers.DataHandleTupleWriter;
import cz.cuni.mff.been.clinterface.writers.DatasetWriter;
import cz.cuni.mff.been.clinterface.writers.TriggerWriter;
import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.common.Message;
import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.jaxb.BindingComposer;
import cz.cuni.mff.been.jaxb.BindingParser;
import cz.cuni.mff.been.jaxb.ConvertorException;
import cz.cuni.mff.been.jaxb.XSD;
import cz.cuni.mff.been.jaxb.dataset.Dataset;
import cz.cuni.mff.been.jaxb.td.Condition;
import cz.cuni.mff.been.jaxb.td.Trigger;
import cz.cuni.mff.been.jaxb.tuplit.TupLit;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.RRTrigger;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Task;

/**
 * This module provides access to the Results Repository and its data sets.
 * 
 * @author Andrej Podzimek
 */
public final class ResultsModule extends CommandLineModule {

	public enum Errors implements Message {

		/** Unknown action name. */
		UNKN_ACTION("Unknown action."),

		/** Action not implemented yet. */
		IMPL_ACTION("Action not implemented yet. Volunteers?"),

		/** Invalid parameters or required parameters missing. */
		INVD_PARAMS(""),

		/** When uuid is not specified alone. */
		INVD_PARAMS_TD(
				"Specify either 'uuid' xor ('analysis', 'dataset', 'evaluator')."),

		/** Invalid regular expression used. */
		INVD_REGEXP("Illegal regular expression."),

		/** Invalid dataset name specified. */
		INVD_DATASET("Unknown column, dataset or analysis identifier."),

		/** When an invalid trigger UUID is supplied. */
		INVD_TRIGGER("Unknown trigger identifier."),

		/** Invalid dataset name specified. */
		INVD_STORE("Analysis, dataset or data item names are invalid."),

		/**
		 * Trying to send an instance of a class with a non-public String
		 * constructor.
		 */
		INVD_CTOR("The (String) constructor is not accessible."),

		/** A class name of an abstract class supplied. */
		INVD_CLASS("Attempting to instantiate an abstract class."),

		/** Attempting to instantiate a non-serializable class. */
		INVD_SER("Attempting to deserialize a non-Serializable object."),

		/** A file that doesn't exist has been requested. */
		INVD_FILE("There is no file with this UUID in the Results Repository."),

		/** Storing data can throw an IllegalArgumentException!!! **/
		MALF_TRIGGER(
				"Data stored, but an invalid trigger encountered. Details follow."),

		/** Class loading from String with no String constructor. */
		UNKN_CTOR("Could not find a String constructor for the class."),

		/** Class not found or class loading failed somehow. */
		UNKN_CLASS("Could not find or instantiate the class."),

		/** When the 'names' flag is specified without the 'plain' flag. */
		DEPD_NAMES_PLAIN(
				"The 'names' flag can only be used with the 'plain' flag."),

		/** When uuid is not specified alone. */
		EXCL_UUID(
				"Parameters 'uuid' and ('analysis', 'dataset', 'evaluator') are exclusive."),

		/** When Results Repository's remote reference reports a failure. */
		CONN_RRMI(
				"Could not contact the Results Repository's Manager Interface."),

		/** When Results Repository's remote reference reports a failure. */
		CONN_RRDI("Could not contact the Results Repository's Data Interface."),

		/** When the Results Repository reports a failure. */
		FAIL_RR("Results Repository operation failed. Details follow."),

		/** When an object cannot be serialized. */
		FAIL_SER("Object serialization failed."),

		/**
		 * When an object cannot be deserialized from its binary representation.
		 */
		FAIL_DESER("Object deserialization failed."),

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

		/** When the String constructor throws an exception. */
		FAIL_CTOR("The (String) constructor threw an exception."),

		/** When the SecurityManager prohibits access to the constructor. */
		FAIL_SECURITY(
				"The (String) constructor is banned by the Security Manager."),

		/** When the FileStoreClient produces an IOException. */
		FAIL_DNL("Failed to download the file."),

		/** When the FileStoreClient produces an IOException. */
		FAIL_UPL("Failed to upload the file."),

		/** When the FileStoreClient produces an IOException. */
		FAIL_DEL("Failed to delete the file."),

		/** When triggers can't be deleted for some reason. */
		FAIL_TR_DEL("Failed to delete the trigger(s)."),

		/** When 'from' can't be read as Long. */
		MALF_FROM("The 'from' parameter is malformed."),

		/** When 'to' can't be read as Long. */
		MALF_TO("The 'to' parameter is malformed."),

		/** When UUID.toString() screams. */
		MALF_UUID("Malformed UUID supplied."),

		/** When the host RSL string is malformed. */
		MALF_RSL_HOST("Malformed host RSL expression."),

		/** When the package RSL string is malformed. */
		MALF_RSL_PKG("Malformed package RSL expression."),

		/** When data type casting in a DataHandle fails. */
		INTG_TYPE(
				"Integrity error. Data recovery failed due to a type mismatch."),

		/** When an IllegalArgumentException is thrown by newInstance(). */
		INTG_REF("Integrity error. Could not call a (String) constructor."),

		/** When something bad happens to the JAXB parser. */
		INTG_COND(
				"Integrity error. JAXB classes and condition.xsd out of sync."),

		/** When something bad happens to the JAXB parser. */
		INTG_TRIGGER(
				"Integrity error. JAXB classes and trigger.xsd out of sync."),

		/** When something bad happens to the JAXB parser. */
		INTG_TUPLIT("Integrity error. JAXB classes and tuplit.xsd out of sync.");

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
	public static final String MODULE_NAME = "results";

	/** A map of actions provided by this module. */
	private static final TreeMap<String, CommandLineAction<ResultsModule>> actions;

	/** A list of actions this module provides. */
	private static final String ACTIONS_LIST;

	static {
		actions = new TreeMap<String, CommandLineAction<ResultsModule>>();

		actions.put("analysis-list", new CommandLineAction<ResultsModule>() {
			private final Set<String> parameters = stringSet("pattern");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(parameters, flags, false);

			@Override
			public void handle(
					ResultsModule module,
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

				AnalysisDatasetWriter writer;
				String pattern;
				Iterable<Pair<String, ? extends Iterable<String>>> analyses;

				pattern = request.getParameter("pattern");
				writer = new AnalysisDatasetWriter(response);

				try {
					if (null == pattern) {
						analyses = module.resultsRepositoryAdapter
								.getAnalyses();
					} else {
						analyses = module.resultsRepositoryAdapter
								.getAnalyses(Pattern.compile(pattern));
					}
				} catch (PatternSyntaxException exception) {
					throw new ModuleSpecificException(Errors.INVD_REGEXP, " /"
							+ pattern + '/', exception);
				} catch (ResultsRepositoryException exception) {
					throw new ModuleSpecificException(Errors.FAIL_RR, exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				}

				try {
					for (Pair<String, ? extends Iterable<String>> analysis : analyses) {
						writer.sendLine(analysis.getKey(), analysis.getValue());
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

		actions.put("dataset-list", new CommandLineAction<ResultsModule>() {
			private final Set<String> allParameters = stringSet(
					"analysis",
					"pattern");
			private final Set<String> requiredParameters = stringSet("analysis");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					allParameters,
					flags,
					requiredParameters,
					false);

			@Override
			public void handle(
					ResultsModule module,
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

				DatasetWriter writer;
				String analysis;
				String pattern;
				Iterable<Pair<String, DatasetDescriptor>> datasets;

				pattern = request.getParameter("pattern");
				analysis = request.getParameter("analysis");
				writer = new DatasetWriter(response);

				try {
					if (null == pattern) {
						datasets = module.resultsRepositoryAdapter
								.getDatasets(analysis);
					} else {
						datasets = module.resultsRepositoryAdapter.getDatasets(
								analysis,
								Pattern.compile(pattern));
					}
				} catch (PatternSyntaxException exception) {
					throw new ModuleSpecificException(Errors.INVD_REGEXP, " /"
							+ pattern + '/', exception);
				} catch (ResultsRepositoryException exception) {
					throw new ModuleSpecificException(Errors.FAIL_RR, exception); // No
																					// other
																					// reason
																					// to
																					// throw...
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				}

				try {
					for (Pair<String, DatasetDescriptor> dataset : datasets) {
						writer.sendLine(dataset.getKey(), dataset.getValue());
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

		actions.put("dataset-dump", new CommandLineAction<ResultsModule>() {
			private final Set<String> parameters = stringSet(
					"analysis",
					"dataset");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					ResultsModule module,
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

				String dataset;
				String analysis;
				OutputStream stream;
				Dataset xdataset;

				dataset = request.getParameter("dataset");
				analysis = request.getParameter("analysis");

				try {
					xdataset = module.resultsRepositoryAdapter.getDataset(
							analysis,
							dataset);
				} catch (ResultsRepositoryException exception) { // TODO:
																	// Distinguish!!!
					throw new ModuleSpecificException(Errors.INVD_DATASET, " ("
							+ analysis + ", " + dataset + ')', exception); // No
																			// other
																			// reason
																			// to
																			// throw...
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				}

				stream = response.getStandardOutputStream();
				try {
					module.datasetComposer.compose(xdataset, stream);
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

		actions.put("dataset-data", new CommandLineAction<ResultsModule>() {
			private final Set<String> allParameters = stringSet(
					"analysis",
					"dataset",
					"from",
					"to");
			private final Set<String> requiredParameters = stringSet(
					"analysis",
					"dataset");
			private final Set<String> flags = stringSet("plain", "names");
			private final String help = constructHelp(
					allParameters,
					flags,
					requiredParameters,
					null);

			@Override
			public void handle(
					ResultsModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
				StringBuilder errors;

				errors = verifyArguments(
						request,
						allParameters,
						flags,
						requiredParameters);
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				String from, to;
				String analysis, dataset;
				Long lfrom, lto;
				Condition condition;
				boolean plain;
				boolean names;

				plain = request.getFlag("plain");
				names = request.getFlag("names");
				if (!plain && names) {
					throw new ModuleSpecificException(Errors.DEPD_NAMES_PLAIN);
				}

				from = request.getParameter("from");
				try {
					lfrom = null == from ? null : Long.valueOf(from);
				} catch (NumberFormatException exception) {
					throw new ModuleSpecificException(Errors.MALF_FROM, " ("
							+ from + ')', exception);
				}

				to = request.getParameter("to");
				try {
					lto = null == to ? null : Long.valueOf(to);
				} catch (NumberFormatException exception) {
					throw new ModuleSpecificException(Errors.MALF_TO, " (" + to
							+ ')', exception);
				}

				if (request.hasBlob()) {
					try {
						condition = module.conditionParser.parse(request
								.getBlobStream());
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
				} else {
					condition = null;
				}

				analysis = request.getParameter("analysis");
				dataset = request.getParameter("dataset");

				try {
					if (plain) {
						Iterable<DataHandleTuple> tuples;
						DataHandleTupleWriter writer;

						writer = new DataHandleTupleWriter(response);
						tuples = module.resultsRepositoryAdapter
								.getDatasetRawData(
										analysis,
										dataset,
										condition,
										lfrom,
										lto);
						try {
							if (names) {
								for (DataHandleTuple tuple : tuples) {
									writer.sendLineNames(tuple);
								}
							} else {
								for (DataHandleTuple tuple : tuples) {
									writer.sendLinePlain(tuple);
								}
							}
						} catch (IOException exception) {
							throw new ModuleOutputException(exception);
						}
					} else {
						OutputStream stream;
						TupLit tupLit;

						tupLit = module.resultsRepositoryAdapter
								.getDatasetData( // Before stream instantiation!
										analysis,
										dataset,
										condition,
										lfrom,
										lto);

						stream = response.getStandardOutputStream();
						try {
							module.tupleComposer.compose(tupLit, stream);
						} catch (JAXBException exception) {
							throw new ModuleSpecificException(
									Errors.FAIL_MARSHAL,
									exception);
						} finally {
							try {
								stream.close(); // NECESSARY! (Double
												// buffering.)
							} catch (IOException exception) {
								throw new ModuleOutputException(exception);
							}
						}
					}
				} catch (ResultsRepositoryException exception) { // TODO:
																	// Distinguish!!!
					throw new ModuleSpecificException(Errors.INVD_DATASET, // Any
																			// other
																			// probable
																			// failures...?
							" (" + analysis + ", " + dataset + ')',
							exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRDI,
							exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRDI,
							exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("dataset-store", new CommandLineAction<ResultsModule>() {
			private final Set<String> parameters = stringSet(
					"analysis",
					"dataset");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					true);

			@Override
			public void handle(
					ResultsModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
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

				TupLit tupLit;
				String analysis;
				String dataset;
				Iterable<Long> serials;
				StringBuilder builder;

				try {
					tupLit = module.tupleParser.parse(request.getBlobStream());
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

				analysis = request.getParameter("analysis");
				dataset = request.getParameter("dataset");

				try {
					serials = module.resultsRepositoryAdapter.saveData(
							analysis,
							dataset,
							tupLit);
				} catch (ResultsRepositoryException exception) { // TODO:
																	// Distinguish!!!
					throw new ModuleSpecificException(Errors.INVD_STORE, " ("
							+ analysis + ", " + dataset + ')', exception);
				} catch (IllegalArgumentException exception) {
					throw new ModuleSpecificException(
							Errors.MALF_TRIGGER,
							exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRDI,
							exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRDI,
							exception);
				}

				builder = new StringBuilder();
				for (Long serial : serials) {
					builder.append(serial).append('\n');
				}
				try {
					response.sendOut(builder.toString());
				} catch (IOException exception) {
					throw new ModuleOutputException(exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("dataset-create", new CommandLineAction<ResultsModule>() {
			private final Set<String> parameters = stringSet();
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(parameters, flags, true);

			@Override
			public void handle(
					ResultsModule module,
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

				Dataset dataset;
				try {
					dataset = module.datasetParser.parse(request
							.getBlobStream());
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

				try {
					module.resultsRepositoryAdapter.createDataset(dataset);
				} catch (ResultsRepositoryException exception) { // TODO:
																	// Distinguish!!!
					throw new ModuleSpecificException(Errors.INVD_DATASET, // Any
																			// other
																			// probable
																			// failures...?
							" (" + dataset.getAnalysis() + ", "
									+ dataset.getName() + ')',
							exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("dataset-delete", new CommandLineAction<ResultsModule>() {
			private final Set<String> parameters = stringSet(
					"analysis",
					"dataset");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					ResultsModule module,
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

				String analysis;
				String dataset;

				analysis = request.getParameter("analysis");
				dataset = request.getParameter("dataset");

				try {
					module.resultsRepositoryAdapter.deleteDataset(
							analysis,
							dataset);
				} catch (ResultsRepositoryException exception) { // TODO:
																	// Distinguish!!!
					throw new ModuleSpecificException(Errors.INVD_DATASET, // Any
																			// other
																			// probable
																			// failures...?
							" (" + analysis + ", " + dataset + ')',
							exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("trigger-list", new CommandLineAction<ResultsModule>() {
			private final Set<String> allParameters = stringSet(
					"analysis",
					"dataset",
					"pattern");
			private final Set<String> requiredParameters = stringSet(
					"analysis",
					"dataset");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					allParameters,
					flags,
					requiredParameters,
					false);

			@Override
			public void handle(
					ResultsModule module,
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

				TriggerWriter writer;
				String pattern;
				String analysis;
				String dataset;
				Iterable<RRTrigger> triggers;

				pattern = request.getParameter("pattern");
				analysis = request.getParameter("analysis");
				dataset = request.getParameter("dataset");
				writer = new TriggerWriter(response);

				try {
					if (null == pattern) {
						triggers = module.resultsRepositoryAdapter.getTriggers(
								analysis,
								dataset);
					} else {
						triggers = module.resultsRepositoryAdapter.getTriggers(
								analysis,
								dataset,
								Pattern.compile(pattern));
					}
				} catch (PatternSyntaxException exception) {
					throw new ModuleSpecificException(Errors.INVD_REGEXP, " /"
							+ pattern + '/', exception);
				} catch (ResultsRepositoryException exception) { // TODO:
																	// Distinguish!!!
					throw new ModuleSpecificException(Errors.INVD_DATASET, // Any
																			// other
																			// probable
																			// failures...?
							" (" + analysis + ", " + dataset + ')',
							exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				}

				try {
					for (RRTrigger trigger : triggers) {
						writer.sendLine(trigger);
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

		actions.put("trigger-dump", new CommandLineAction<ResultsModule>() {
			private final Set<String> parameters = stringSet("uuid");
			private final Set<String> flags = stringSet("binary");
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					ResultsModule module,
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

				OutputStream stream;
				String uuid;
				Trigger trigger;

				uuid = request.getParameter("uuid");
				try {
					trigger = module.resultsRepositoryAdapter.getTrigger(
							UUID.fromString(uuid),
							request.getFlag("binary"));
				} catch (ResultsRepositoryException exception) {
					throw new ModuleSpecificException(Errors.FAIL_RR, exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				} catch (NoSuchElementException exception) {
					throw new ModuleSpecificException(Errors.INVD_TRIGGER, " ("
							+ uuid + ')', exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				}

				stream = response.getStandardOutputStream();
				try {
					module.triggerComposer.compose(trigger, stream);
				} catch (JAXBException exception) { // MarshalExcepton included.
					throw new ModuleSpecificException(
							Errors.FAIL_MARSHAL,
							exception);
				} finally {
					try {
						stream.close();
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

		actions.put("trigger-create", new CommandLineAction<ResultsModule>() {
			private final Set<String> parameters = stringSet();
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(parameters, flags, true);

			@Override
			public void handle(
					ResultsModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException, ModuleOutputException {
				StringBuilder errors;

				errors = verifyArguments(request, parameters, flags, true);
				if (null != errors) {
					throw new ModuleSpecificException(
							Errors.INVD_PARAMS,
							errors);
				}

				Trigger trigger;

				try {
					trigger = module.triggerParser.parse(request
							.getBlobStream());
					response.sendOut(module.resultsRepositoryAdapter
							.createTrigger(trigger).toString() + '\n');
				} catch (UnmarshalException exception) {
					exception.printStackTrace(System.err);
					throw new ModuleSpecificException(
							Errors.FAIL_UMSL_VAL,
							exception);
				} catch (JAXBException exception) {
					exception.printStackTrace(System.err);
					throw new ModuleSpecificException(
							Errors.FAIL_UMSL,
							exception);
				} catch (ResultsRepositoryException exception) { // This should
																	// be more
																	// detailed.
					throw new ModuleSpecificException(Errors.FAIL_RR, exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				} catch (ConvertorException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_UMSL_CONV,
							exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRMI,
							exception);
				} catch (IOException exception) {
					throw new ModuleOutputException(exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("trigger-delete", new CommandLineAction<ResultsModule>() {
			private final Set<String> uuidParameters = stringSet("uuid");
			private final Set<String> idParameters = stringSet(
					"analysis",
					"dataset",
					"evaluator");
			private final Set<String> allParameters = new TreeSet<String>(
					idParameters);
			{
				allParameters.addAll(uuidParameters);
			}
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					allParameters,
					flags,
					false);

			@Override
			public void handle(
					ResultsModule module,
					CommandLineRequest request,
					CommandLineResponse response)
					throws ModuleSpecificException {
				StringBuilder errors;
				String uuid;

				uuid = request.getParameter("uuid");

				if (null == uuid) {
					errors = verifyArguments(
							request,
							idParameters,
							flags,
							idParameters,
							false);
					if (null != errors) {
						throw new ModuleSpecificException(
								Errors.INVD_PARAMS_TD,
								errors);
					}

					String analysis;
					String dataset;
					String evaluator;

					analysis = request.getParameter("analysis");
					dataset = request.getParameter("dataset");
					evaluator = request.getParameter("evaluator");

					try {
						module.resultsRepositoryAdapter.deleteTriggers(
								analysis,
								dataset,
								evaluator);
					} catch (ResultsRepositoryException exception) { // TODO:
																		// Distinguish!!!
						throw new ModuleSpecificException(
								Errors.FAIL_TR_DEL,
								" (" + analysis + ", " + dataset + ", "
										+ evaluator + ')',
								exception);
					} catch (ComponentInitializationException exception) {
						throw new ModuleSpecificException(
								Errors.CONN_RRMI,
								exception);
					} catch (RemoteException exception) {
						throw new ModuleSpecificException(
								Errors.CONN_RRMI,
								exception);
					}
				} else {
					errors = verifyArguments(
							request,
							uuidParameters,
							flags,
							uuidParameters,
							false);
					if (null != errors) {
						throw new ModuleSpecificException(
								Errors.INVD_PARAMS_TD,
								errors);
					}

					try {
						module.resultsRepositoryAdapter.deleteTrigger(UUID
								.fromString(uuid));
					} catch (IllegalArgumentException exception) {
						throw new ModuleSpecificException(
								Errors.MALF_UUID,
								" (" + uuid + ')',
								exception);
					} catch (NoSuchElementException exception) {
						throw new ModuleSpecificException(
								Errors.INVD_TRIGGER,
								" (" + uuid + ')',
								exception);
					} catch (ResultsRepositoryException exception) {
						throw new ModuleSpecificException(
								Errors.FAIL_RR,
								exception);
					} catch (ComponentInitializationException exception) {
						throw new ModuleSpecificException(
								Errors.CONN_RRMI,
								exception);
					} catch (RemoteException exception) {
						throw new ModuleSpecificException(
								Errors.CONN_RRMI,
								exception);
					}
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("file-upload", new CommandLineAction<ResultsModule>() {
			private final Set<String> parameters = stringSet();
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					true);

			@Override
			public void handle(
					ResultsModule module,
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

				try {
					response.sendOut( // Write the UUID!
					module.resultsRepositoryAdapter.uploadFile(
							request.getBlobStream()).toString() + '\n');
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRDI,
							exception);
				} catch (IOException exception) { // A special meaning here.
					throw new ModuleSpecificException(
							Errors.FAIL_UPL,
							exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("file-download", new CommandLineAction<ResultsModule>() {
			private final Set<String> parameters = stringSet("uuid");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					ResultsModule module,
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

				String uuid;

				uuid = request.getParameter("uuid");

				try {
					module.resultsRepositoryAdapter.downloadFile(
							UUID.fromString(uuid),
							response.getStandardOutputStream());
				} catch (IllegalArgumentException exception) {
					throw new ModuleSpecificException(Errors.MALF_UUID, " ("
							+ uuid + ")", exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRDI,
							exception);
				} catch (IOException exception) { // A special meaning here.
					throw new ModuleSpecificException(Errors.FAIL_DNL, " ("
							+ uuid + ')', exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put("file-delete", new CommandLineAction<ResultsModule>() {
			private final Set<String> parameters = stringSet("uuid");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					ResultsModule module,
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

				String uuid;

				uuid = request.getParameter("uuid");

				try {
					module.resultsRepositoryAdapter.deleteFile(UUID
							.fromString(uuid));
				} catch (IllegalArgumentException exception) {
					throw new ModuleSpecificException(Errors.MALF_UUID, " ("
							+ uuid + ")", exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(
							Errors.CONN_RRDI,
							exception);
				} catch (IOException exception) { // A special meaning here.
					throw new ModuleSpecificException(Errors.FAIL_DEL, " ("
							+ uuid + ')', exception);
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

		actions.put("help", new CommandLineAction<ResultsModule>() {

			@Override
			public void handle(
					ResultsModule module,
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

	/** A special reference to the ResultsRepository. */
	private final ResultsRepositoryJAXBAdapter resultsRepositoryAdapter;

	/** A marshaller that outputs Dataset objects as XML. */
	private final BindingComposer<Dataset> datasetComposer;

	/** A marshaller that outputs Trigger objects as XML. */
	private final BindingComposer<Trigger> triggerComposer;

	/** A marshaller that outputs Tuple objects as XML. */
	private final BindingComposer<TupLit> tupleComposer;

	/** An unmarshaller that creates Condition objects from XML. */
	private final BindingParser<Condition> conditionParser;

	/** An unmarshaller that creates Dataset objects from XML. */
	private final BindingParser<Dataset> datasetParser;

	/** An unmarshaller that creates Trigger objects from XML. */
	private final BindingParser<Trigger> triggerParser;

	/** An unmarshaller that creates Tuple objects from XML. */
	private final BindingParser<TupLit> tupleParser;

	/**
	 * Initializes a new module to the default state ready for use.
	 * 
	 * @throws CommandLineException
	 *             When JAXB marshallers or unmarshallers cannot be created.
	 */
	public ResultsModule() throws CommandLineException {
		this.resultsRepositoryAdapter = new ResultsRepositoryJAXBAdapter(
				new TaskManagerReference());

		try {
			this.conditionParser = XSD.CONDITION.createParser(Condition.class);

			this.datasetComposer = XSD.DATASET.createComposer(Dataset.class);
			this.datasetParser = XSD.DATASET.createParser(Dataset.class);

			this.triggerComposer = XSD.TRIGGER.createComposer(Trigger.class);
			this.triggerParser = XSD.TRIGGER.createParser(Trigger.class);

			this.tupleComposer = XSD.TUPLIT.createComposer(TupLit.class);
			this.tupleParser = XSD.TUPLIT.createParser(TupLit.class);
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
		CommandLineAction<ResultsModule> act;

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
		resultsRepositoryAdapter.drop();
	}

	@Override
	protected String getActionsList() {
		return ACTIONS_LIST;
	}
}
