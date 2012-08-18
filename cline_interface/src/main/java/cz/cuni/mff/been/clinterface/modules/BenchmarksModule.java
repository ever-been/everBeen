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

import cz.cuni.mff.been.benchmarkmanagerng.Analysis;
import cz.cuni.mff.been.benchmarkmanagerng.AnalysisException;
import cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerException;
import cz.cuni.mff.been.clinterface.CommandLineAction;
import cz.cuni.mff.been.clinterface.CommandLineException;
import cz.cuni.mff.been.clinterface.CommandLineModule;
import cz.cuni.mff.been.clinterface.CommandLineRequest;
import cz.cuni.mff.been.clinterface.CommandLineResponse;
import cz.cuni.mff.been.clinterface.ModuleOutputException;
import cz.cuni.mff.been.clinterface.ModuleSpecificException;
import cz.cuni.mff.been.clinterface.adapters.BenchmarkManagerAbstractAdapter.ModuleInfo;
import cz.cuni.mff.been.clinterface.adapters.BenchmarkManagerJAXBAdapter;
import cz.cuni.mff.been.clinterface.ref.TaskManagerReference;
import cz.cuni.mff.been.clinterface.writers.AnalysisWriter;
import cz.cuni.mff.been.clinterface.writers.ModuleInfoWriter;
import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.common.Message;
import cz.cuni.mff.been.jaxb.BindingComposer;
import cz.cuni.mff.been.jaxb.BindingParser;
import cz.cuni.mff.been.jaxb.ConvertorException;
import cz.cuni.mff.been.jaxb.XSD;
import cz.cuni.mff.been.jaxb.benchmark.Benchmark;
import cz.cuni.mff.been.jaxb.config.Config;
import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Task;

/**
 * A command line interface component that corresponds to the benchmarks listing
 * screen.
 * 
 * @author Andrej Podzimek
 */
public final class BenchmarksModule extends CommandLineModule {

	public enum Errors implements Message {

		/** Unknown action name. */
		UNKN_ACTION("Unknown action."),

		/** Action not implemented yet. */
		IMPL_ACTION("Action not implemented yet. Volunteers?"),

		/** Invalid parameters or required parameters missing. */
		INVD_PARAMS(""),

		/** Invalid regular expression used. */
		INVD_REGEXP("Illegal regular expression."),

		/** Invalid generator name specified. */
		INVD_GEN("No such generator."),

		/** Generator name exists, but no such version. */
		INVD_GEN_VER("No such version of generator."),

		/** Invalid evaluator name specified. */
		INVD_EVAL("No such evaluator."),

		/** Evaluator name exists, but no such version. */
		INVD_EVAL_VER("No such version of evaluator."),

		/** Invalid analysis name specified. */
		INVD_ANAL("No such analysis."), // :-D :-D :-D

		/** When the XML parser succeeds, but BM refuses the analysis. */
		MALF_ANAL("Invalid or illegal analysis description."), // :-D :-D :-D

		/** Malformed run period (not an integer) specified. */
		MALF_PERIOD("Run period malformed or out of range."),

		/** Invalid run period specified. */
		INVD_PERIOD("Invalid run period, zero or negative."),

		/** Semantic errors in configuration found, print out error messages. */
		INVD_CONFIG(
				"Errors in configuration of the generator and/or evaluator(s)."),

		/** When Benchmark Manager's remote reference reports a failure. */
		CONN_BM("Could not contact the Benchmark Manager."),

		/** When the Benchmark Manager reports a failure. */
		FAIL_BM("Benchmark manager query failed. Details follow."),

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
		FAIL_UMSL_CONV("Conversion of special data types from XML failed.");

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
	public static final String MODULE_NAME = "benchmarks";

	/** A map of actions provided by this module. */
	private static final TreeMap<String, CommandLineAction<BenchmarksModule>> actions;

	/** A list of actions this module provides. */
	private static final String ACTIONS_LIST;

	static {
		actions = new TreeMap<String, CommandLineAction<BenchmarksModule>>();

		actions.put(
				"generator-list",
				new CommandLineAction<BenchmarksModule>() {
					private final Set<String> parameters = stringSet("pattern");
					private final Set<String> flags = stringSet();
					private final String help = constructHelp(
							parameters,
							flags,
							false);

					@Override
					public void handle(
							BenchmarksModule module,
							CommandLineRequest request,
							CommandLineResponse response)
							throws ModuleSpecificException,
							ModuleOutputException {
						StringBuilder errors;

						errors = verifyArguments(
								request,
								parameters,
								flags,
								false);
						if (null != errors) {
							throw new ModuleSpecificException(
									Errors.INVD_PARAMS,
									errors);
						}

						ModuleInfoWriter writer;
						String pattern;
						Iterable<ModuleInfo> generators;

						pattern = request.getParameter("pattern");
						writer = new ModuleInfoWriter(response);

						try {
							if (null == pattern) {
								generators = module.benchmarkManagerAdapter
										.getGenerators();
							} else {
								generators = module.benchmarkManagerAdapter
										.getGenerators(Pattern.compile(pattern));
							}
						} catch (PatternSyntaxException exception) {
							throw new ModuleSpecificException(
									Errors.INVD_REGEXP,
									" /" + pattern + '/',
									exception);
						} catch (BenchmarkManagerException exception) {
							throw new ModuleSpecificException(
									Errors.FAIL_BM,
									exception);
						} catch (ComponentInitializationException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_BM,
									exception);
						} catch (RemoteException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_BM,
									exception);
						}

						try {
							for (ModuleInfo info : generators) {
								writer.sendLine(info);
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

		actions.put(
				"generator-dump",
				new CommandLineAction<BenchmarksModule>() {
					private final Set<String> parameters = stringSet(
							"name",
							"version");
					private final Set<String> flags = stringSet();
					private final String help = constructHelp(
							parameters,
							flags,
							parameters,
							false);

					@Override
					public void handle(
							BenchmarksModule module,
							CommandLineRequest request,
							CommandLineResponse response)
							throws ModuleSpecificException,
							ModuleOutputException {
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
						Config config;

						try {
							config = module.benchmarkManagerAdapter
									.getGeneratorConfig(
											request.getParameter("name"),
											request.getParameter("version"));
						} catch (BenchmarkManagerException exception) {
							throw new ModuleSpecificException(
									Errors.FAIL_BM,
									exception);
						} catch (ComponentInitializationException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_BM,
									exception);
						} catch (RemoteException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_BM,
									exception);
						}

						stream = response.getStandardOutputStream();
						try {
							module.configComposer.compose(config, stream);
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

					@Override
					public String getHelpString() {
						return help;
					}
				});

		actions.put(
				"evaluator-list",
				new CommandLineAction<BenchmarksModule>() {
					private final Set<String> parameters = stringSet("pattern");
					private final Set<String> flags = stringSet();
					private final String help = constructHelp(
							parameters,
							flags,
							false);

					@Override
					public void handle(
							BenchmarksModule module,
							CommandLineRequest request,
							CommandLineResponse response)
							throws ModuleSpecificException,
							ModuleOutputException {
						StringBuilder errors;

						errors = verifyArguments(
								request,
								parameters,
								flags,
								false);
						if (null != errors) {
							throw new ModuleSpecificException(
									Errors.INVD_PARAMS,
									errors);
						}

						ModuleInfoWriter writer;
						String pattern;
						Iterable<ModuleInfo> evaluators;

						pattern = request.getParameter("pattern");
						writer = new ModuleInfoWriter(response);

						try {
							if (null == pattern) {
								evaluators = module.benchmarkManagerAdapter
										.getEvaluators();
							} else {
								evaluators = module.benchmarkManagerAdapter
										.getEvaluators(Pattern.compile(pattern));
							}
						} catch (PatternSyntaxException exception) {
							throw new ModuleSpecificException(
									Errors.INVD_REGEXP,
									" /" + pattern + '/',
									exception);
						} catch (BenchmarkManagerException exception) {
							throw new ModuleSpecificException(
									Errors.FAIL_BM,
									exception);
						} catch (ComponentInitializationException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_BM,
									exception);
						} catch (RemoteException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_BM,
									exception);
						}

						try {
							for (ModuleInfo info : evaluators) {
								writer.sendLine(info);
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

		actions.put(
				"evaluator-dump",
				new CommandLineAction<BenchmarksModule>() {
					private final Set<String> parameters = stringSet(
							"name",
							"version");
					private final Set<String> flags = stringSet();
					private final String help = constructHelp(
							parameters,
							flags,
							parameters,
							false);

					@Override
					public void handle(
							BenchmarksModule module,
							CommandLineRequest request,
							CommandLineResponse response)
							throws ModuleSpecificException,
							ModuleOutputException {
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
						Config config;

						try {
							config = module.benchmarkManagerAdapter
									.getEvaluatorConfig(
											request.getParameter("name"),
											request.getParameter("version"));
						} catch (BenchmarkManagerException exception) {
							throw new ModuleSpecificException(
									Errors.FAIL_BM,
									exception);
						} catch (ComponentInitializationException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_BM,
									exception);
						} catch (RemoteException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_BM,
									exception);
						}

						stream = response.getStandardOutputStream();
						try {
							module.configComposer.compose(config, stream);
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

					@Override
					public String getHelpString() {
						return help;
					}
				});

		actions.put("analysis-list", new CommandLineAction<BenchmarksModule>() {
			private final Set<String> parameters = stringSet("pattern");
			private final Set<String> flags = stringSet("desc");
			private final String help = constructHelp(parameters, flags, false);

			@Override
			public void handle(
					BenchmarksModule module,
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

				AnalysisWriter writer;
				String pattern;
				Iterable<Analysis> analyses;

				pattern = request.getParameter("pattern");
				writer = new AnalysisWriter(response);

				try {
					if (null == pattern) {
						analyses = module.benchmarkManagerAdapter
								.getRawAnalyses();
					} else {
						analyses = module.benchmarkManagerAdapter
								.getAnalyses(Pattern.compile(pattern));
					}
				} catch (PatternSyntaxException exception) {
					throw new ModuleSpecificException(Errors.INVD_REGEXP, " /"
							+ pattern + '/', exception);
				} catch (BenchmarkManagerException exception) {
					throw new ModuleSpecificException(Errors.FAIL_BM, exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_BM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_BM, exception);
				}

				try {
					if (request.getFlag("desc")) {
						for (Analysis analysis : analyses) {
							writer.sendLineDescr(analysis);
						}
					} else {
						for (Analysis analysis : analyses) {
							writer.sendLinePlain(analysis);
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

		actions.put("analysis-dump", new CommandLineAction<BenchmarksModule>() {
			private final Set<String> parameters = stringSet("name");
			private final Set<String> flags = stringSet();
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					BenchmarksModule module,
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
				Benchmark benchmark;

				try {
					benchmark = module.benchmarkManagerAdapter
							.getAnalysis(request.getParameter("name"));
				} catch (BenchmarkManagerException exception) {
					throw new ModuleSpecificException(Errors.FAIL_BM, exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_BM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_BM, exception);
				}

				stream = response.getStandardOutputStream();
				try {
					module.benchmarkComposer.compose(benchmark, stream);
				} catch (JAXBException exception) {
					throw new ModuleSpecificException(
							Errors.FAIL_MARSHAL,
							exception);
				} finally {
					try {
						stream.close(); // NECESSARY! Double buffering.
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

		actions.put(
				"analysis-create",
				new CommandLineAction<BenchmarksModule>() {
					private final Set<String> parameters = stringSet();
					private final Set<String> flags = stringSet();
					private final String help = constructHelp(
							parameters,
							flags,
							true);

					@Override
					public void handle(
							BenchmarksModule module,
							CommandLineRequest request,
							CommandLineResponse response)
							throws ModuleSpecificException {
						StringBuilder errors;

						errors = verifyArguments(
								request,
								parameters,
								flags,
								true); // WOW! We want a blob!
						if (null != errors) {
							throw new ModuleSpecificException(
									Errors.INVD_PARAMS,
									errors);
						}

						Benchmark benchmark;

						try {
							benchmark = module.benchmarkParser.parse(request
									.getBlobStream());
							module.benchmarkManagerAdapter
									.createAnalysis(benchmark);
						} catch (UnmarshalException exception) {
							throw new ModuleSpecificException(
									Errors.FAIL_UMSL_VAL,
									exception);
						} catch (JAXBException exception) {
							throw new ModuleSpecificException(
									Errors.FAIL_UMSL,
									exception);
						} catch (AnalysisException exception) {
							throw new ModuleSpecificException(
									Errors.MALF_ANAL,
									exception);
						} catch (BenchmarkManagerException exception) {
							throw new ModuleSpecificException(
									Errors.FAIL_BM,
									exception);
						} catch (ComponentInitializationException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_BM,
									exception);
						} catch (ConvertorException exception) {
							throw new ModuleSpecificException(
									Errors.FAIL_UMSL_CONV,
									exception);
						} catch (RemoteException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_BM,
									exception);
						}
					}

					@Override
					public String getHelpString() {
						return help;
					}
				});

		actions.put(
				"analysis-update",
				new CommandLineAction<BenchmarksModule>() {
					private final Set<String> parameters = stringSet();
					private final Set<String> flags = stringSet();
					private final String help = constructHelp(
							parameters,
							flags,
							true);

					@Override
					public void handle(
							BenchmarksModule module,
							CommandLineRequest request,
							CommandLineResponse response)
							throws ModuleSpecificException {
						StringBuilder errors;

						errors = verifyArguments(
								request,
								parameters,
								flags,
								true); // WOW! We want a blob!
						if (null != errors) {
							throw new ModuleSpecificException(
									Errors.INVD_PARAMS,
									errors);
						}

						Benchmark benchmark;

						try {
							benchmark = module.benchmarkParser.parse(request
									.getBlobStream());
							module.benchmarkManagerAdapter
									.updateAnalysis(benchmark);
						} catch (UnmarshalException exception) {
							throw new ModuleSpecificException(
									Errors.FAIL_UMSL_VAL,
									exception);
						} catch (JAXBException exception) {
							throw new ModuleSpecificException(
									Errors.FAIL_UMSL,
									exception);
						} catch (AnalysisException exception) {
							throw new ModuleSpecificException(
									Errors.MALF_ANAL,
									exception);
						} catch (BenchmarkManagerException exception) {
							throw new ModuleSpecificException(
									Errors.FAIL_BM,
									exception);
						} catch (ComponentInitializationException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_BM,
									exception);
						} catch (ConvertorException exception) {
							throw new ModuleSpecificException(
									Errors.FAIL_UMSL_CONV,
									exception);
						} catch (RemoteException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_BM,
									exception);
						}
					}

					@Override
					public String getHelpString() {
						return help;
					}
				});

		actions.put("analysis-run", new CommandLineAction<BenchmarksModule>() {
			private final Set<String> parameters = stringSet("name");
			private final Set<String> flags = stringSet("force");
			private final String help = constructHelp(
					parameters,
					flags,
					parameters,
					false);

			@Override
			public void handle(
					BenchmarksModule module,
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
					module.benchmarkManagerAdapter.runAnalysis(
							name,
							request.getFlag("force"));
				} catch (AnalysisException exception) {
					throw new ModuleSpecificException(Errors.INVD_ANAL, " ("
							+ name + ')', exception);
				} catch (BenchmarkManagerException exception) {
					throw new ModuleSpecificException(Errors.FAIL_BM, exception);
				} catch (ComponentInitializationException exception) {
					throw new ModuleSpecificException(Errors.CONN_BM, exception);
				} catch (RemoteException exception) {
					throw new ModuleSpecificException(Errors.CONN_BM, exception);
				}
			}

			@Override
			public String getHelpString() {
				return help;
			}
		});

		actions.put(
				"analysis-delete",
				new CommandLineAction<BenchmarksModule>() {
					private final Set<String> parameters = stringSet("name");
					private final Set<String> flags = stringSet();
					private final String help = constructHelp(
							parameters,
							flags,
							parameters,
							false);

					@Override
					public void handle(
							BenchmarksModule module,
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
							module.benchmarkManagerAdapter.deleteAnalysis(name);
						} catch (AnalysisException exception) {
							throw new ModuleSpecificException(
									Errors.INVD_ANAL,
									" (" + name + ')',
									exception);
						} catch (BenchmarkManagerException exception) {
							throw new ModuleSpecificException(
									Errors.FAIL_BM,
									exception);
						} catch (ComponentInitializationException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_BM,
									exception);
						} catch (RemoteException exception) {
							throw new ModuleSpecificException(
									Errors.CONN_BM,
									exception);
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

		actions.put("help", new CommandLineAction<BenchmarksModule>() {

			@Override
			public void handle(
					BenchmarksModule module,
					CommandLineRequest request,
					CommandLineResponse response) throws ModuleOutputException {
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

	/** A special reference to the Benchmark Manager. */
	private final BenchmarkManagerJAXBAdapter benchmarkManagerAdapter;

	/** A marshaller that outputs Config objects as XML. */
	private final BindingComposer<Config> configComposer;

	/** A marshaller that outputs Benchmark objects as XML. */
	private final BindingComposer<Benchmark> benchmarkComposer;

	/** An unmarshaller that creates Benchmark objects from XML. */
	private final BindingParser<Benchmark> benchmarkParser;

	/**
	 * Initializes a new module to the default state ready for use.
	 * 
	 * @throws CommandLineException
	 *             When JAXB marshallers or unmarshallers cannot be created.
	 */
	public BenchmarksModule() throws CommandLineException {
		this.benchmarkManagerAdapter = new BenchmarkManagerJAXBAdapter(
				new TaskManagerReference());

		try {
			this.benchmarkComposer = XSD.BENCHMARK
					.createComposer(Benchmark.class);
			this.benchmarkParser = XSD.BENCHMARK.createParser(Benchmark.class);
			this.configComposer = XSD.CONFIG.createComposer(Config.class);
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
		CommandLineAction<BenchmarksModule> act;

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
		benchmarkManagerAdapter.drop();
	}

	@Override
	protected String getActionsList() {
		return ACTIONS_LIST;
	}
}
