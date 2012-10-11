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
package cz.cuni.mff.been.benchmarkmanagerng;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.xml.sax.SAXException;

import cz.cuni.mff.been.benchmarkmanagerng.module.BMEvaluator;
import cz.cuni.mff.been.benchmarkmanagerng.module.BMGenerator;
import cz.cuni.mff.been.benchmarkmanagerng.module.BMModule;
import cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorInterface;
import cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorPluggableModule;
import cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorInterface;
import cz.cuni.mff.been.benchmarkmanagerng.module.ModuleInterface;
import cz.cuni.mff.been.common.ComponentInitializationException;
import cz.cuni.mff.been.common.Debug;
import cz.cuni.mff.been.common.Pair;
import static cz.cuni.mff.been.services.Names.HOST_MANAGER_SERVICE_NAME;
import cz.cuni.mff.been.jaxb.BindingParser;
import cz.cuni.mff.been.jaxb.ConvertorException;
import cz.cuni.mff.been.jaxb.XSD;
import cz.cuni.mff.been.jaxb.config.Config;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.pluggablemodule.PluggableModule;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleDescriptor;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleException;
import cz.cuni.mff.been.pluggablemodule.PluggableModuleManager;
import cz.cuni.mff.been.pluggablemodule.derby.DerbyPluggableModule;
import cz.cuni.mff.been.pluggablemodule.hibernate.HibernatePluggableModule;
import cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryService;
import cz.cuni.mff.been.services.Service;
import cz.cuni.mff.been.softwarerepository.MatchException;
import cz.cuni.mff.been.softwarerepository.PackageMetadata;
import cz.cuni.mff.been.softwarerepository.ProvidedInterfaceQueryCallback;
import cz.cuni.mff.been.softwarerepository.SoftwareRepositoryInterface;
import cz.cuni.mff.been.softwarerepository.SoftwareRepositoryService;
import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;

/**
 * Implementation of the Benchmark Manager service.
 * 
 * @author Jiri Tauber
 */
public final class BenchmarkManagerImplementation extends UnicastRemoteObject
		implements BenchmarkManagerInterface, BenchmarkManagerCallbackInterface {

	/** UnicastRemoteObject mandatory field */
	private static final long serialVersionUID = 2128762920453341039L;

	/** Network port on which will derby be accessible when in debug mode */
	private static final int DERBY_DEBUG_NETWORK_PORT = 1531;

	/** Derby database name */
	public static final String ANALYSES_DATABASE = "benchmarkmanager_db";

	/** GeneratorRunner task settings */
	public static final String GENERATORTASK_NAME = "generator-runner";
	public static final String ANALYSES_TREEPATH_PREFIX = "/analysis";

	/** The JAXB-based XML parser. */
	private final BindingParser<Config> parser;

	/** Task container shortcut */
	private final Task task;

	/** reference to pluggable module manager */
	private final PluggableModuleManager manager;

	/** reference to derby pluggable module */
	private DerbyPluggableModule derby;

	/** reference to hibernate session, which is used as data storage */
	private SessionFactory hibernateSessionFactory;

	private final Scheduler scheduler;

	private final AnalysesTracker analysesTracker;

	// ----- Public Methods --------------------------------------------------//

	/**
	 * Default constructor.
	 * 
	 * @throws RemoteException
	 *             as a part of RMI definition
	 * @throws ComponentInitializationException
	 *             when something goes wrong
	 */
	public BenchmarkManagerImplementation(PluggableModuleManager manager)
			throws RemoteException, ComponentInitializationException {
		this.manager = manager;
		this.task = CurrentTaskSingleton.getTaskHandle(); // Must occur
															// BEFORE...

		Throwable t = null;
		try {
			this.parser = XSD.CONFIG.createParser(Config.class); // ...BEFORE
																	// this...
		} catch (SAXException exception) {
			t = exception;
			logError("JAXB parser could not find, read or parse schema files"); // ...so
																				// that
																				// logging
																				// works
																				// here...
			throw new ComponentInitializationException(
					"Schema initialization failed",
					exception);
		} catch (JAXBException exception) {
			t = exception;
			logError("JAXB parser refused or could not load the binding class"); // ...and
																					// here.
			throw new ComponentInitializationException(
					"JAXB initialization failed",
					exception);
		} finally {
			for (; null != t; t = t.getCause()) {
				System.err.println();
				System.err.println(t.getMessage());
				t.printStackTrace(System.err);
			}
		}
		initDerby();
		initHibernate();

		this.analysesTracker = new AnalysesTracker();
		this.scheduler = new Scheduler(this);
		logInfo("Running sheduler");
		scheduler.start();
	}

	/**
	 * Releases all used resources. Object shouldn't be used anymore after
	 * destroy() is called. Create a new instance instead.
	 */
	public void destroy() {
		hibernateSessionFactory.close();
		try {
			derby.stopEngine();
		} catch (PluggableModuleException e) {
			logError("Couldn't stop DB engine because error occured: "
					+ e.getMessage());
		}
	}

	// ----- Main Interface --------------------------------------------------//

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface#createAnalysis
	 * (cz.cuni.mff.been.benchmarkmanagerng.Analysis)
	 */
	@Override
	public void createAnalysis(Analysis analysis) throws RemoteException,
			BenchmarkManagerException, AnalysisException {

		// validate analysis
		Collection<String> errors = analysis.validate(manager);
		if (!errors.isEmpty()) {
			throw new AnalysisException("Analysis is not valid: "
					+ errors.iterator().next());
		}

		saveAnalysis(analysis);

		// Instruct Generator to create its datasets
		try {
			GeneratorInterface generator = (GeneratorInterface) analysis
					.getGenerator().getPluggableModule(manager);
			logInfo(analysis.getGenerator().getName()
					+ " is creating datasets for analysis "
					+ analysis.getName());
			generator.configure(analysis);
			generator.createDatasets();
		} catch (PluggableModuleException e) {
			deleteAnalysis(analysis);
			throw new BenchmarkManagerException(
					"Can't load generator pluggable module",
					e);
		} catch (ResultsRepositoryException e) {
			deleteAnalysis(analysis);
			throw new BenchmarkManagerException(
					"Generator can't create datasets",
					e);
		}

		// instruct each Evaluator to create his triggers and datasets
		EvaluatorInterface evaluator = null;
		for (BMEvaluator bme : analysis.getEvaluators()) {
			logInfo(bme.getName() + " is creating datasets and triggers");
			try {
				evaluator = (EvaluatorInterface) bme
						.getPluggableModule(manager);
				evaluator.attachToAnalysis(analysis, bme.getConfiguration());
			} catch (PluggableModuleException e) {
				deleteAnalysis(analysis);
				throw new BenchmarkManagerException(
						"Can't load evaluator pluggable module",
						e);
			} catch (RemoteException e) {
				deleteAnalysis(analysis);
				throw new RemoteException("Evaluator can't create datasets", e);
			} catch (ResultsRepositoryException e) {
				deleteAnalysis(analysis);
				throw new BenchmarkManagerException(
						"Evaluator can't create datasets",
						e);
			}
		}
		logInfo("Analysis '" + analysis.getName() + "' created sucessfully");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface#updateAnalysis
	 * (cz.cuni.mff.been.benchmarkmanagerng.Analysis)
	 */
	@Override
	public synchronized void updateAnalysis(Analysis aNew)
			throws RemoteException, BenchmarkManagerException,
			AnalysisException {
		// check given analysis ID
		if (aNew.getID() == null) {
			throw new AnalysisException("Unknown analysis to update");
		}
		Analysis aOld = null;
		aOld = loadAnalysis(aNew.getID()); // might throw an exception

		// check the new analysis validity
		Collection<String> errors = aNew.validate(manager);
		if (errors.size() > 0) {
			throw new AnalysisException("Error in analysis: "
					+ errors.iterator().next());
		}
		// check name change:
		// TODO: Analysis name change should be allowed in the future
		if (!aOld.getName().equals(aNew.getName())) {
			throw new AnalysisException("Error in analysis: "
					+ "Can't change analysis name");
		}
		// check generator change:
		if (!aOld.getGenerator().isSimilarTo(aNew.getGenerator())) {
			throw new AnalysisException("Error in analysis: "
					+ "Can't change generator");
		}

		// update the RR
		try {
			EvaluatorPluggableModule pm = null;
			// detach removed or changed evaluators
			for (BMEvaluator ev : aOld.getEvaluators()) {
				if (!aNew.getEvaluators().contains(ev)) {
					pm = (EvaluatorPluggableModule) ev
							.getPluggableModule(manager);
					pm.detachFromAnalysis(aOld, ev.getConfiguration());
				}
			}
			// TODO: change analysis name in the RR

			// attach new or changed evaluators:
			for (BMEvaluator ev : aNew.getEvaluators()) {
				if (!aOld.getEvaluators().contains(ev)) {
					pm = (EvaluatorPluggableModule) ev
							.getPluggableModule(manager);
					pm.attachToAnalysis(aNew, ev.getConfiguration());
				}
			}

		} catch (PluggableModuleException e) {
			throw new BenchmarkManagerException(
					"Error loading pluggable module",
					e);
		} catch (ResultsRepositoryException e) {
			throw new BenchmarkManagerException(
					"Error accessing the ResultsRepository",
					e);
		}

		// coppy automatic content from the old analysis:
		aNew.setLastTime(aOld.getLastTime());
		while (aNew.getRunCount() < aOld.getRunCount())
			aNew.increaseRunCount();
		saveAnalysis(aNew);
		logInfo("Successfully updated analysis " + aNew.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface#deleteAnalysis
	 * ()
	 */
	@Override
	public void deleteAnalysis(String name) throws RemoteException,
			BenchmarkManagerException {
		Analysis analysis = loadAnalysis(name);
		deleteAnalysis(analysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface#getAnalyses
	 * ()
	 */
	@Override
	public Collection<Analysis> getAnalyses() throws RemoteException,
			BenchmarkManagerException {
		Collection<Analysis> result = new LinkedList<Analysis>();
		Session session = null;
		try {
			session = hibernateSessionFactory.openSession();
			Criteria query = session.createCriteria(Analysis.class).addOrder(
					Order.asc("name"));
			Analysis an;
			for (Object o : query.list()) {
				an = new Analysis(o);
				an.setState(analysesTracker.getAnalysisState(an.getName()));
				result.add(an);
			}
		} catch (HibernateException e) {
			logError("Can't load analysis because error occured: "
					+ e.getMessage());
			throw new BenchmarkManagerException("Can't load analyses", e);
		} finally {
			if (session != null)
				session.close();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface#getAnalysis
	 * (java.lang.String)
	 */
	@Override
	public Analysis getAnalysis(String name) throws RemoteException,
			BenchmarkManagerException {
		Analysis result;
		try {
			result = loadAnalysis(name);
			result.setState(analysesTracker.getAnalysisState(result.getName()));
		} catch (AnalysisException e) {
			result = null;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface#
	 * getActiveContexts(java.lang.String)
	 */
	@Override
	public Map<String, AnalysisState> getActiveContexts(String analysisName) {
		Map<String, AnalysisState> result;
		try {
			Analysis analysis = loadAnalysis(analysisName);
			result = analysesTracker.getActiveContexts(analysis.getName());
		} catch (AnalysisException e) {
			logError("Can't load active contexts for analysis " + analysisName
					+ " because it was not found");
			result = new HashMap<String, AnalysisState>();
		} catch (BenchmarkManagerException e) {
			logError("Can't load active contexts for analysis " + analysisName
					+ " because error occured: " + e.getMessage());
			result = new HashMap<String, AnalysisState>();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface#getEvaluators
	 * ()
	 */
	@Override
	public Collection<BMEvaluator> getEvaluators() throws RemoteException,
			BenchmarkManagerException {
		PackageMetadata[] packages = null;

		SoftwareRepositoryInterface swRep = getSWRepositoryInterface();
		try {
			// Retrieve the packages that have evaluator interface
			packages = swRep
					.queryPackages(new ProvidedInterfaceQueryCallback(
							"cz.cuni.mff.been.benchmarkmanagerng.module.EvaluatorInterface"));
		} catch (MatchException e) {
			logError("Couldn't find evaluators because error occured: "
					+ e.getMessage());
			throw new BenchmarkManagerException("Couldn't find evaluators", e);
		}

		// convert the package metadate into BMEvaluators
		LinkedList<BMEvaluator> evaluators = new LinkedList<BMEvaluator>();
		for (PackageMetadata pcg : packages) {
			evaluators.add(new BMEvaluator(pcg.getName(), pcg.getVersion()
					.toString()));
		}
		return evaluators;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface#getGenerators
	 * ()
	 */
	@Override
	public Collection<BMGenerator> getGenerators() throws RemoteException,
			BenchmarkManagerException {
		PackageMetadata[] packages = null;

		SoftwareRepositoryInterface swRep = getSWRepositoryInterface();
		try {
			// Retrieve the packages that have evaluator interface
			packages = swRep
					.queryPackages(new ProvidedInterfaceQueryCallback(
							"cz.cuni.mff.been.benchmarkmanagerng.module.GeneratorInterface"));
		} catch (MatchException e) {
			logError("Couldn't find generators because error occured: "
					+ e.getMessage());
			throw new BenchmarkManagerException("Couldn't find generators", e);
		}

		// convert the package metadata into BMGenerators
		LinkedList<BMGenerator> generators = new LinkedList<BMGenerator>();
		for (PackageMetadata pcg : packages) {
			generators.add(new BMGenerator(pcg.getName(), pcg.getVersion()
					.toString()));
		}
		return generators;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface#
	 * getConfigurationDescription
	 * (cz.cuni.mff.been.benchmarkmanagerng.module.BMModule)
	 */
	@Override
	public Config getConfigurationDescription(BMModule module)
			throws RemoteException, BenchmarkManagerException {
		String dir;
		Throwable throwable = null;

		try {
			module.getPluggableModule(manager);
		} catch (PluggableModuleException e) {
			// / DEBUG
			for (Throwable t = e; null != t; t = t.getCause()) {
				System.err.println();
				System.err.println(t.getMessage());
				t.printStackTrace(System.err);
			}
			// /
			throw new BenchmarkManagerException(e);
		}

		dir = CurrentTaskSingleton.getTaskHandle().getTaskDirectory()
				+ File.separator + "pluggablemodules" + File.separator
				+ module.getPackageName();
		Config config = null;
		InputStream stream = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(dir
					+ "/module-config.xml"));
			config = parser.parse(stream);
		} catch (FileNotFoundException e) {
			throwable = e;
			logError("Can't open the configuration file");
			throw new BenchmarkManagerException(
					"Can't open the configuration file",
					e);
		} catch (JAXBException e) {
			throwable = e;
			logError("JAXB parser could not parse the module configuration");
			throw new BenchmarkManagerException(
					"JAXB parser could not parse the module configuration",
					e);
		} catch (ConvertorException e) { // NEVER happens with config.xsd.
			throwable = e;
			logError("Unknown XML data conversion failure");
			throw new BenchmarkManagerException(
					"Unknown XML data conversion failure",
					e);
		} finally {
			for (Throwable t = throwable; null != t; t = t.getCause()) {
				System.err.println();
				System.err.println(t.getMessage());
				t.printStackTrace(System.err);
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// should never happen
					e.printStackTrace(System.err);
				}
			}
		}

		return config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface#
	 * validateModuleConfiguration
	 * (cz.cuni.mff.been.benchmarkmanagerng.module.BMModule)
	 */
	@Override
	public Collection<String> validateModuleConfiguration(BMModule module) {
		Collection<String> result = new LinkedList<String>();
		if (module == null) {
			result.add("Module is null");
			return result;
		}
		if (module.getConfiguration() == null) {
			result.add(module.getName() + " configuration is null");
			return result;
		}
		try {
			ModuleInterface inst = (ModuleInterface) module
					.getPluggableModule(manager);
			result = inst.validateConfiguration(module.getConfiguration());
		} catch (PluggableModuleException e) {
			result.add(module.getName()
					+ " module couldn't validate the configuration!");
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface#runAnalysis
	 * (java.lang.String)
	 */
	@Override
	public synchronized void runAnalysis(String name, boolean force)
			throws RemoteException, BenchmarkManagerException,
			AnalysisException {
		Analysis analysis = loadAnalysis(name);
		AnalysisState state = analysesTracker.getAnalysisState(analysis
				.getName());
		if (state.isRunning() && !force) {
			throw new AnalysisException("Analysis is already runing");
		}
		if (force) {
			if (state == AnalysisState.GENERATING) {
				logError("Can't force-run analysis that is still generating");
				throw new BenchmarkManagerException(
						"Can't force-run analysis that is still generating");
			} else {
				logInfo("Forced start of analysis " + name);
			}
		}
		launchAnalysis(analysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface#stopScheduler
	 * ()
	 */
	@Override
	public void stopScheduler() throws RemoteException {
		scheduler.interrupt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerInterface#
	 * isSchedulerRunning()
	 */
	@Override
	public boolean isSchedulerRunning() throws RemoteException {
		return scheduler != null ? scheduler.isAlive() : false;
	}

	// ----- Callback Interface ----------------------------------------------//

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerCallbackInterface
	 * #reportGeneratorSuccess(java.lang.String)
	 */
	@Override
	public void reportGeneratorSuccess(
			String analysisName,
			String contextId,
			String generatorTid) throws RemoteException,
			BenchmarkManagerException {
		logDebug("Recieved success report from " + generatorTid);
		analysesTracker.generatorFinished(contextId, generatorTid); // might
																	// throw
																	// exception
		Analysis analysis = loadAnalysis(analysisName);
		analysis.setLastTime(new Date());
		analysis.increaseRunCount();
		saveAnalysis(analysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.benchmarkmanagerng.BenchmarkManagerCallbackInterface
	 * #reportAnalysisFinish(java.lang.String)
	 */
	@Override
	public void reportAnalysisFinish(String contextID) throws RemoteException,
			BenchmarkManagerException {
		logDebug("Recieved success report from context " + contextID);
		analysesTracker.analysisFinished(contextID);
	}

	// ----- Private Methods -------------------------------------------------//

	/**
	 * Logs an information message. If the Benchmark Manager is run as a task
	 * (i.e. <code>task</code> field is not <code>null</code>), it uses task
	 * logging facility to write the message, otherwise it prints it to the
	 * standard output.
	 * 
	 * @param message
	 *            message text
	 */
	private void logInfo(String message) {
		if (task != null) {
			task.logInfo(message);
		} else {
			System.out.println(message);
		}
	}

	/**
	 * Logs an error message. If the Benchmark Manager is run as a task (i.e.
	 * <code>task</code> field is not <code>null</code>), it uses task logging
	 * facility to write the message, otherwise it prints it to the error
	 * output.
	 * 
	 * @param message
	 *            message text
	 */
	private void logError(String message) {
		if (task != null) {
			task.logError(message);
		} else {
			System.err.println(message);
		}
	}

	/**
	 * Logs a debug message. If the Benchmark Manager is run as a task (i.e.
	 * <code>task</code> field is not <code>null</code>), it uses task logging
	 * facility to write the message, otherwise it prints it to the standard
	 * output.
	 * 
	 * @param message
	 *            message text
	 */
	private void logDebug(String message) {
		if (task != null) {
			task.logDebug(message);
		} else {
			System.out.println(message);
		}
	}

	/**
	 * Saves analysis information to the database using bm hibernate session.
	 * Creates new entry when Analysis doesn't have ID. Updates existing entry
	 * when the analysis does have ID
	 * 
	 * @param analysis
	 *            The analysis to save
	 * 
	 * @throws BenchmarkManagerException
	 *             when hibernate has problem with the saved object
	 * @throws AnalysisException
	 *             when the analysis is new (no ID) and doesn't have unique name
	 */
	private synchronized void saveAnalysis(Analysis analysis)
			throws BenchmarkManagerException, AnalysisException {
		Session session = null;
		try {
			session = hibernateSessionFactory.openSession();
			session.beginTransaction();
			session.saveOrUpdate(analysis);
			session.getTransaction().commit();

		} catch (ConstraintViolationException e) {
			logError("Couldn't save analysis '" + analysis.getName()
					+ "'. It probably doesn't have unique name");
			if (session != null)
				session.getTransaction().rollback();
			throw new AnalysisException("Couldn't save analysis '"
					+ analysis.getName()
					+ "'. It probably doesn't have unique name", e);
		} catch (HibernateException e) {
			logError("Couldn't save analysis because error occured: "
					+ e.getMessage());
			if (session != null)
				session.getTransaction().rollback();
			throw new BenchmarkManagerException("Couldn't save analysis", e);
		} finally {
			if (session != null)
				session.close();
		}

	}

	/**
	 * loads the specified analysis data.
	 * 
	 * @param name
	 *            name of the analysis
	 * 
	 * @return The found analysis object
	 * @throws BenchmarkManagerException
	 *             when Hibernate gives us any trouble
	 * @throws AnalysisException
	 *             when Analysis wasn't found
	 */
	private synchronized Analysis loadAnalysis(String name)
			throws BenchmarkManagerException, AnalysisException {
		Session session = null;
		try {
			session = hibernateSessionFactory.openSession();
			Criteria query = session.createCriteria(Analysis.class);
			query.add(Restrictions.eq("name", name));
			if (query.list().size() != 1) {
				throw new AnalysisException(
						"Couldn't find analysis with specified name");
			} else {
				return new Analysis(query.list().get(0));
			}
		} catch (HibernateException e) {
			logError("Can't load analysis because error occured: "
					+ e.getMessage());
			throw new BenchmarkManagerException("Can't load analysis", e);
		} finally {
			if (session != null)
				session.close();
		}
	}

	/**
	 * loads the specified analysis data.
	 * 
	 * @param id
	 *            the unique identifier
	 * 
	 * @return The analysis object
	 * @throws BenchmarkManagerException
	 *             when Hibernate gives any trouble
	 * @throws AnalysisException
	 *             when no analysis was found
	 */
	private synchronized Analysis loadAnalysis(int id)
			throws BenchmarkManagerException, AnalysisException {
		Session session = null;
		try {
			session = hibernateSessionFactory.openSession();
			Criteria query = session.createCriteria(Analysis.class);
			query.add(Restrictions.eq("id", id));
			if (query.list().size() != 1) {
				throw new AnalysisException(
						"Couldn't find analysis with specified id");
			} else {
				return new Analysis(query.list().get(0));
			}
		} catch (HibernateException e) {
			logError("Can't load analysis because error occured: "
					+ e.getMessage());
			throw new BenchmarkManagerException("Can't load analysis", e);
		} finally {
			if (session != null)
				session.close();
		}
	}

	/**
	 * Deletes given analysis
	 * 
	 * @param analysis
	 * @throws RemoteException
	 * @throws BenchmarkManagerException
	 */
	private synchronized void deleteAnalysis(Analysis analysis)
			throws RemoteException, BenchmarkManagerException {
		RRManagerInterface repository = (RRManagerInterface) CurrentTaskSingleton
				.getTaskHandle()
				.getTasksPort()
				.serviceFind(
						ResultsRepositoryService.SERVICE_NAME,
						Service.RMI_MAIN_IFACE);
		if (repository == null) {
			throw new BenchmarkManagerException(
					"Results Repository reference cannot be obtained, maybe it is not running.");
		}

		// Delete analysis from the persistent storage
		Session session = null;
		try {
			session = hibernateSessionFactory.openSession();
			session.beginTransaction();
			session.delete(analysis);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			logError("Couldn't delete analysis because error occured: "
					+ e.getMessage());
			if (session != null)
				session.getTransaction().rollback();
			throw new BenchmarkManagerException("Couldn't delete analysis", e);
		} finally {
			if (session != null)
				session.close();
		}

		// Delete analysis from the results repository
		try {
			List<String> dataset = repository.getDatasets(analysis.getName());
			for (String datasetName : dataset) {
				repository.deleteDataset(analysis.getName(), datasetName);
			}
		} catch (ResultsRepositoryException e) {
			throw new BenchmarkManagerException(
					"Couldn't delete analysis datasets from the Results Repository",
					e);
		}

	}

	/**
	 * Retrieves the RMI reference to the SW repository.
	 * 
	 * @return Software Repository Interface
	 */
	private SoftwareRepositoryInterface getSWRepositoryInterface()
			throws RemoteException, BenchmarkManagerException {
		SoftwareRepositoryInterface swRep = (SoftwareRepositoryInterface) task
				.getTasksPort().serviceFind(
						SoftwareRepositoryService.SERVICE_NAME,
						Service.RMI_MAIN_IFACE);

		if (swRep == null) {
			throw new BenchmarkManagerException(
					"Couldn't find running instance of Software Repository");
		}
		return swRep;
	}

	/**
	 * Initialize hibernate session. Loads necessary pluggable modules using
	 * <code>manager</code>, initializes derby database if neccessary and stores
	 * the hibernate session for future use in the <code>hibernateSession</code>
	 * Hibernate will create or update the database schema if neccessary.
	 * 
	 * @throws ComponentInitializationException
	 */
	private void initHibernate() throws ComponentInitializationException {
		if (hibernateSessionFactory != null) {
			return;
		}

		String moduleName = "hibernate";
		String moduleVersion = "2.1.0";
		PluggableModule m;
		HibernatePluggableModule hibernateModule = null;

		PluggableModuleDescriptor hibernate = new PluggableModuleDescriptor(
				moduleName,
				moduleVersion);

		logInfo("Initialising hibernate component...");
		try {
			m = manager.getModule(hibernate);
		} catch (PluggableModuleException e) {
			logError("Error loading pluggable module: " + e.getMessage());
			throw new ComponentInitializationException(
					"Error loading pluggable module.",
					e);
		}

		if (!(m instanceof HibernatePluggableModule)) {
			throw new ComponentInitializationException("Pluggable module "
					+ moduleName + "-" + moduleVersion
					+ " is not HibernatePluggableModule!");
		}

		String[] entityList = new String[] {
				"cz.cuni.mff.been.benchmarkmanagerng.module.BMGenerator",
				"cz.cuni.mff.been.benchmarkmanagerng.module.BMEvaluator",
				"cz.cuni.mff.been.benchmarkmanagerng.Analysis" };

		try {
			hibernateModule = (HibernatePluggableModule) m;
			logInfo("Initialising hibernate session factory...");
			hibernateSessionFactory = hibernateModule.createSessionFactory(
					getDerbyUrl(),
					entityList);
		} catch (HibernateException e) {
			logError("Can't open hibernate session because error occured: "
					+ e.getMessage());
			throw new ComponentInitializationException(
					"Can't create hibernate session factory",
					e);
		}
	}

	/**
	 * Initializes connection to Derby database. Uses <code>manager</code> to
	 * retrieve derby pluggable module, stores the refference to the pluggable
	 * module in the variable <code>derby</code>. Then it attempts to start the
	 * derby engine with home in the task working directory.
	 * 
	 * @throws ComponentInitializationException
	 */
	private void initDerby() throws ComponentInitializationException {
		if (derby != null) {
			return;
		}

		String moduleName = "derby";
		String moduleVersion = "2.1.0";
		PluggableModule m;

		logInfo("Initialising database...");
		try {
			m = manager.getModule(new PluggableModuleDescriptor(
					moduleName,
					moduleVersion));
		} catch (PluggableModuleException e) {
			logError("Error loading pluggable module: " + e.getMessage());
			throw new ComponentInitializationException(
					"Error loading pluggable module.",
					e);
		}

		if (!(m instanceof DerbyPluggableModule)) {
			String err = "Pluggable module " + moduleName + "-" + moduleVersion
					+ " is not DerbyPluggableModule!";
			logError(err);
			throw new ComponentInitializationException(err);
		}

		// Set up database in cwd when running as in a test suite
		// task.getWorkingDirectory() might cause nullPointerException
		String dbDir = System.getProperty(Task.PROP_DIR_WORK);
		if (dbDir == null) {
			dbDir = "";
		}
		try {
			derby = (DerbyPluggableModule) m;
			// start derby as network accessible if debug mode is on.
			derby.startEngine(
					dbDir,
					Debug.isDebugModeOn(),
					DERBY_DEBUG_NETWORK_PORT);
			logInfo("Derby engine started.");
		} catch (Exception e) {
			logError("Can't start derby engine: " + e.getMessage());
			throw new ComponentInitializationException(
					"Can't start derby engine.",
					e);
		}
	}

	/**
	 * @return URL used to connect to embedded instance of derby
	 */
	private String getDerbyUrl() {
		return "jdbc:derby:" + ANALYSES_DATABASE + ";create=true";
	}

	/**
	 * Launches GeneratorRunner with specified analysis as parameter. Not part
	 * of any interface to avoid changing analysis configuration with each run.
	 * 
	 * @param analysis
	 * 
	 * @throws RemoteException
	 *             when error occurs in any subsequent call
	 * @throws BenchmarkManagerException
	 *             when there is error on the BenchmarkManager side
	 */
	private synchronized void launchAnalysis(Analysis analysis)
			throws RemoteException, BenchmarkManagerException {
		// This check is pobably useless with the new task manager in place
		Remote hostManager = null;
		try {
			hostManager = task.getTasksPort().serviceFind(
					HOST_MANAGER_SERVICE_NAME,
					Service.RMI_MAIN_IFACE);
		} catch (NullPointerException e) {
			// will be dealt with in the next if
		}
		if (hostManager == null) {
			throw new BenchmarkManagerException("Couldn't find Host Manager");
		}

		logInfo("Launching analysis " + analysis.getName());

		TaskManagerInterface taskManager = task.getTasksPort().getTaskManager();

		String context = null;
		try {
			// Create brand new run context:
			context = analysis.getName() + "-" + analysis.getRunCount();
			int retry = 0;
			while (taskManager.isContextRegistered(context + "-" + retry)) {
				retry++;
			}
			context = context + "-" + retry;
			String description;
			description = "Context generated for analysis "
					+ analysis.getName() + " during run #"
					+ analysis.getRunCount();
			taskManager.newContext(
					context,
					context,
					description,
					analysis.getAID());
		} catch (RemoteException e) {
			throw new BenchmarkManagerException(
					"Error reading context from the TaskManager",
					e);
		} catch (LogStorageException e) {
			throw new BenchmarkManagerException(
					"Error reading context from the TaskManager",
					e);
		} catch (IllegalArgumentException e) {
			throw new BenchmarkManagerException(
					"Error reading context from the TaskManager",
					e);
		} catch (NullPointerException e) {
			throw new BenchmarkManagerException(
					"Error reading context from the TaskManager",
					e);
		}

		String tid = "generator-" + context; // context is unique enough at this
												// point
		String treePath = ANALYSES_TREEPATH_PREFIX + "/" + analysis.getName()
				+ "/" + context + "/" + tid;
		TaskDescriptor generatorRunner = TaskDescriptorHelper.createTask(
				tid,
				GENERATORTASK_NAME,
				context,
				analysis.getGeneratorHostRSL(),
				treePath);
		try {
			TaskDescriptorHelper.addTaskPropertyObjects(
					generatorRunner,
					Pair.pair("analysis", analysis));
		} catch (IOException e) {
			throw new BenchmarkManagerException(
					"Error serializing analysis to Base64",
					e);
		}
		task.getTasksPort().runTask(generatorRunner);
		analysesTracker.analysisStarted(context, generatorRunner.getTaskId());
	}

}
