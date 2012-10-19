/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Jan Tattermusch
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
package cz.cuni.mff.been.resultsrepositoryng.implementation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;

import cz.cuni.mff.been.common.Pair;
import cz.cuni.mff.been.hostruntime.TasksPortInterface;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.pluggablemodule.hibernate.HibernatePluggableModule;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor;
import cz.cuni.mff.been.resultsrepositoryng.DatasetDescriptor.DatasetType;
import cz.cuni.mff.been.resultsrepositoryng.RRDataInterface;
import cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface;
import cz.cuni.mff.been.resultsrepositoryng.RRTrigger;
import cz.cuni.mff.been.resultsrepositoryng.ResultsRepositoryException;
import cz.cuni.mff.been.resultsrepositoryng.condition.Condition;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandle;
import cz.cuni.mff.been.resultsrepositoryng.data.DataHandleTuple;
import cz.cuni.mff.been.resultsrepositoryng.filestore.FileStoreClient;
import cz.cuni.mff.been.resultsrepositoryng.filestore.implementation.FileStoreClientImpl;
import cz.cuni.mff.been.resultsrepositoryng.filestore.implementation.FileStoreImpl;
import cz.cuni.mff.been.resultsrepositoryng.transaction.RRTransaction;
import cz.cuni.mff.been.task.CurrentTaskSingleton;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.task.benchmarkmanagerng.evaluator.CommonEvaluatorProperties;
import cz.cuni.mff.been.taskmanager.TaskDescriptorHelper;
import cz.cuni.mff.been.taskmanager.TaskManagerInterface;

/**
 * Implementation of Results Repository
 * 
 * @author Jan Tattermusch
 * 
 */
public class ResultsRepositoryImplementation extends UnicastRemoteObject
		implements RRManagerInterface, RRDataInterface {

	/**
	 * Prefix of dataset table names
	 */
	public final static String DATASET_TABLE_PREFIX = "dataset_";

	/**
	 * Task handle
	 */
	private final Task task = CurrentTaskSingleton.getTaskHandle();

	/**
	 * Existing datasets list (each dataset contains also list of associated
	 * triggers)
	 */
	private List<RRDataset> datasets = new ArrayList<RRDataset>();

	/**
	 * Existing triggers
	 */
	private List<RRTrigger> triggers = new ArrayList<RRTrigger>();

	/**
	 * Triggers that were fired and associated notifyDataProcessed wasn't called
	 * yet.
	 */
	private final Set<UUID> firedTriggers = new HashSet<UUID>();

	/**
	 * Number of queued events for given trigger.
	 */
	private final Map<UUID, Integer> discardedTriggerEvents = new HashMap<UUID, Integer>();

	/** Dynamic entity classes for existing datasets */
	private final Map<DatasetName, Class<? extends DynamicEntity>> entityClasses = new HashMap<DatasetName, Class<? extends DynamicEntity>>();

	private final HibernatePluggableModule hibernatePluggableModule;

	private final String databaseName;

	/**
	 * 
	 */
	private SerialNumberCounter serialNumberCounter;

	/**
	 * File store service associated with this instance of RR.
	 */
	private final FileStoreImpl fileStore;

	/**
	 * Creates new results repository instance.
	 * 
	 * Derby has to be running before this constructor is called.
	 * 
	 * @throws RemoteException
	 */
	public ResultsRepositoryImplementation(HibernatePluggableModule hibernate,
			String databaseName, String fileStoreHomeDir)
			throws ResultsRepositoryException, RemoteException {
		super();
		this.hibernatePluggableModule = hibernate;
		this.databaseName = databaseName;
		this.fileStore = new FileStoreImpl(fileStoreHomeDir);

		initialize();
	}

	/**
	 * Hibernate session factory for RR dataset metadata (dataset and trigger
	 * catalog)
	 */
	private SessionFactory mainSessionFactory;

	/**
	 * Hibernate session factories associated with datasets
	 */
	private final Map<DatasetName, SessionFactory> datasetSessionFactories = new HashMap<DatasetName, SessionFactory>();

	private EvaluatorScheduler evaluatorScheduler;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Initializes RR's internal data structures (trigger and dataset list)
	 */
	private void initialize() throws ResultsRepositoryException {
		Session session = null;
		try {
			/* initialize default evaluator scheduler */
			evaluatorScheduler = new EvaluatorScheduler() {

				@Override
				public void scheduleEvaluator(
						TaskDescriptor task,
						UUID triggerId,
						long lastProcessedSerial)
						throws ResultsRepositoryException {

					String tid = task.getTaskId();

					task.setTaskId(replaceEvaluatorTidTreeAddressWildchars(tid));

					String treeAddress = task.getTreeAddress();
					task.setTreeAddress(replaceEvaluatorTidTreeAddressWildchars(treeAddress));

					TaskDescriptorHelper.addTaskProperties(task, Pair.pair(
							CommonEvaluatorProperties.TRIGGER_ID,
							triggerId.toString()), Pair.pair(
							CommonEvaluatorProperties.LAST_SERIAL_PROCESSED,
							String.valueOf(lastProcessedSerial)));

					try {
						TasksPortInterface tasksPort = ResultsRepositoryImplementation.this.task
								.getTasksPort();
						TaskManagerInterface taskManager = tasksPort
								.getTaskManager();

						/* check whether task's context exists, create it if not */
						String context = task.getContextId();
						if (!taskManager.isContextRegistered(context)) {
							String description = "Context for evaluator tasks";
							taskManager.newContext(
									context,
									context,
									description,
									context,
									true);
						}

						tasksPort.runTask(task);
					} catch (RemoteException e) {
						throw new ResultsRepositoryException(
								"Error running evaluator task",
								e);
					} catch (LogStorageException e) {
						throw new ResultsRepositoryException(
								"Error running evaluator task",
								e);
					} catch (IllegalArgumentException e) {
						throw new ResultsRepositoryException(
								"Error running evaluator task",
								e);
					} catch (NullPointerException e) {
						throw new ResultsRepositoryException(
								"Error running evaluator task",
								e);
					}
				}

			};

			EntityClassFactory.resetEntityClassLoader();
			Thread.currentThread().setContextClassLoader(
					EntityClassFactory.getEntityClassLoader());

			Class<?>[] annotatedClasses = { RRDataset.class, RRTrigger.class };

			String derbyUrl = getDerbyUrl();
			mainSessionFactory = hibernatePluggableModule.createSessionFactory(
					derbyUrl,
					annotatedClasses);

			session = mainSessionFactory.openSession();
			reloadMetadata(session);

			for (RRDataset dataset : datasets) {
				DatasetName qname = new DatasetName(
						dataset.getAnalysis(),
						dataset.getName());
				SessionFactory sessionFactory = createDatasetSessionFactory(dataset);
				datasetSessionFactories.put(qname, sessionFactory);
			}

			serialNumberCounter = new SerialNumberCounter();
			for (RRDataset dataset : datasets) {
				long serial = getDatasetMaxSerial(dataset);
				serialNumberCounter.raise(serial);
			}

			for (RRTrigger trigger : triggers) {
				long serial = trigger.getLastProcessedSerial();
				serialNumberCounter.raise(serial);
			}
		} catch (Exception e) {
			throw new ResultsRepositoryException(
					"Error initializing results repository.",
					e);
		} finally {
			if (session != null)
				session.close();
		}
	}

	/**
	 * Replaces wildchars in evaluator's TID Supported wildchars:
	 * <ul>
	 * <li>%u gets replaced by random UUID</li>
	 * <li>%s gets replaced by current serial number</li>
	 * </ul>
	 * 
	 * @param tid
	 *            task id with wildchars
	 * @return tid with wilchards replaced
	 */
	private String replaceEvaluatorTidTreeAddressWildchars(String tid) {
		String uuidString = UUID.randomUUID().toString();

		String serialString = Long.valueOf(
				serialNumberCounter.getCurrentSerial()).toString();

		tid = tid.replaceAll("%u", uuidString);

		tid = tid.replaceAll("%s", serialString);

		return tid;
	}

	/**
	 * Reads datasets' max row serial number
	 * 
	 * @return dataset's biggest row serial
	 */
	private long getDatasetMaxSerial(RRDataset dataset) {
		long max = 1;
		Session session = null;

		try {
			Class<?> entityClass = entityClasses.get(new DatasetName(dataset
					.getAnalysis(), dataset.getName()));
			SessionFactory sf = datasetSessionFactories.get(new DatasetName(
					dataset.getAnalysis(),
					dataset.getName()));

			session = sf.openSession();

			Query q = session.createQuery("SELECT MAX("
					+ DynamicEntity.SERIAL_FIELD_NAME + ") FROM "
					+ entityClass.getName());
			List<?> result = q.list();
			assert result.size() == 1;

			Long datasetMax = (Long) result.get(0);

			if (datasetMax != null) {
				if (max < datasetMax) {
					max = datasetMax;
				}
			}

		} finally {
			if (session != null)
				session.close();
		}
		return max;
	}

	/**
	 * @return URL used to connect to embedded instance of derby
	 */
	private String getDerbyUrl() {
		return "jdbc:derby:" + databaseName + ";create=true";
	}

	/**
	 * Creates hibernate session factory that will be used for accessing data in
	 * given dataset
	 * 
	 * @param dataset
	 *            dataset for which to create hibernate session factory
	 * @return hibernate session factory
	 */
	@SuppressWarnings("unchecked")
	private SessionFactory createDatasetSessionFactory(RRDataset dataset) {
		Class<?> entityClass = EntityClassFactory.generateEntityClass(dataset);
		DatasetName qname = new DatasetName(
				dataset.getAnalysis(),
				dataset.getName());

		entityClasses.put(qname, (Class<? extends DynamicEntity>) entityClass);
		SessionFactory sessionFactory = hibernatePluggableModule
				.createSessionFactory(
						getDerbyUrl(),
						new Class<?>[] { entityClass });
		return sessionFactory;
	}

	/**
	 * Creates dynamic entity instance
	 * 
	 * @param qname
	 *            qualified name of dynamic entity
	 * @return dynamic entity instance
	 */
	private DynamicEntity createDynamicEntity(DatasetName qname) {
		try {
			Class<? extends DynamicEntity> clazz = entityClasses.get(qname);
			return clazz.newInstance();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Loads all data from a dataset satisfying given condition and having its
	 * serial number in given interval (when inserting a new record to results
	 * repository, each records gets a serial number from growing sequence).
	 * 
	 * 
	 * @param analysisName
	 *            name of analysis to which dataset belongs to
	 * @param datasetName
	 *            name of dataset to load from
	 * @param condition
	 *            condition that returned entries must meet (if null this
	 *            constraint is not applied)
	 * @param fromSerial
	 *            record's serial number lower bound (if null this constraint is
	 *            not applied)
	 * @return all data from dataset which meet given condition.
	 * @throws RemoteException
	 * @throws ResultsRepositoryException
	 */
	@Override
	public synchronized List<DataHandleTuple> loadData(
			String analysisName,
			String datasetName,
			Condition condition,
			Long fromSerial,
			Long toSerial) throws RemoteException, ResultsRepositoryException {
		return loadData(
				analysisName,
				datasetName,
				condition,
				fromSerial,
				toSerial,
				null);
	}

	/**
	 * Loads all data from a dataset satisfying given condition and having its
	 * serial number in given interval (when inserting a new record to results
	 * repository, each records gets a serial number from growing sequence).
	 * 
	 * Allows to supply hibernate session to use when loading.
	 * 
	 * @param analysisName
	 *            name of analysis to which dataset belongs to
	 * @param datasetName
	 *            name of dataset to load from
	 * @param condition
	 *            condition that returned entries must meet (if null this
	 *            constraint is not applied)
	 * @param fromSerial
	 *            record's serial number lower bound (if null this constraint is
	 *            not applied)
	 * @param externalSession
	 *            session to use, or null if session should be retrieved
	 *            automatically
	 * @return all data from dataset which meet given condition.
	 * @throws RemoteException
	 * @throws ResultsRepositoryException
	 */
	synchronized List<DataHandleTuple> loadData(
			String analysisName,
			String datasetName,
			Condition condition,
			Long fromSerial,
			Long toSerial,
			Session externalSession) throws RemoteException,
			ResultsRepositoryException {

		setContextClassLoader();

		DatasetName qname = new DatasetName(analysisName, datasetName);

		SessionFactory sessionFactory = datasetSessionFactories.get(qname);
		Session session = null;

		Class<?> entityClass = entityClasses.get(qname);
		if (entityClass == null) {
			throw new ResultsRepositoryException("Dataset \"" + qname
					+ "\" does not exist.");
		}

		try {
			RRDataset dataset = new RRDataset(
					analysisName,
					datasetName,
					getDatasetDescriptor(analysisName, datasetName));

			/* use external session if supplied */
			session = (externalSession != null ? externalSession
					: sessionFactory.openSession());

			Criteria query = session.createCriteria(entityClass);

			/* apply constraints */
			if (condition != null) {
				query.add(condition.toHibernateCriterion());
			}
			if (fromSerial != null) {
				query.add(Restrictions.ge(
						DynamicEntity.SERIAL_FIELD_NAME,
						fromSerial));
			}
			if (toSerial != null) {
				query.add(Restrictions.le(
						DynamicEntity.SERIAL_FIELD_NAME,
						toSerial));
			}

			List<DataHandleTuple> result = new ArrayList<DataHandleTuple>();

			/* convert results to data handle tuples */
			for (Object row : query.list()) {
				DynamicEntity dynamicEntity = (DynamicEntity) row;
				DataHandleTuple tuple = dynamicEntity.toDataHandleTuple(dataset
						.getDatasetDescriptor());
				result.add(tuple);
			}

			return result;
		} catch (Exception e) {
			throw new ResultsRepositoryException(
					"Error occured while loading dataset's data.",
					e);
		} finally {
			if (externalSession == null) {
				if (session != null)
					session.close();
			}
		}
	}

	/**
	 * Saves a new data tuple to dataset of given name
	 * 
	 * @param analysisName
	 *            name of analysis to which dataset belongs to
	 * @param datasetName
	 *            name of dataset to save to
	 * @param data
	 *            data to save
	 * 
	 * @return serial number assigned to new record
	 * 
	 * @throws RemoteException
	 * @throws ResultsRepositoryException
	 */
	@Override
	public synchronized long saveData(
			String analysisName,
			String datasetName,
			DataHandleTuple data) throws RemoteException,
			ResultsRepositoryException {
		return saveData(analysisName, datasetName, data, null);
	}

	/**
	 * Saves a new data tuple to dataset of given name
	 * 
	 * @param analysisName
	 *            name of analysis to which dataset belongs to
	 * @param datasetName
	 *            name of dataset to save to
	 * @param data
	 *            data to save
	 * @param externalSession
	 *            session to use for saving
	 * 
	 * @return serial number assigned to new record
	 * 
	 * @throws RemoteException
	 * @throws ResultsRepositoryException
	 */
	synchronized long saveData(
			String analysisName,
			String datasetName,
			DataHandleTuple data,
			Session externalSession) throws RemoteException,
			ResultsRepositoryException {

		setContextClassLoader();

		long resultSerial = 0;

		DatasetName qname = new DatasetName(analysisName, datasetName);

		SessionFactory sessionFactory = datasetSessionFactories.get(qname);
		Session session = null;
		Transaction t = null;
		try {
			if (externalSession == null) {
				session = sessionFactory.openSession();
				t = session.beginTransaction();
			} else {
				session = externalSession;
				// t = session.getTransaction();
			}

			RRDataset dataset = new RRDataset(
					analysisName,
					datasetName,
					getDatasetDescriptor(analysisName, datasetName));

			for (String dataTagName : dataset.getDatasetDescriptor().dataTags()) {
				DataHandle dataHandle = data.get(dataTagName);
				if (dataHandle == null)
					throw new ResultsRepositoryException("Data tag \""
							+ dataTagName + "\" is not specified.");
			}

			if (dataset.getDatasetDescriptor().idTags().size() > 0) {
				/*
				 * if dataset has key, delete record with the same key before
				 * saving
				 */
				Criteria criteria = session.createCriteria(entityClasses
						.get(qname));

				for (String idTagName : dataset.getDatasetDescriptor().idTags()) {
					DataHandle dataHandle = data.get(idTagName);
					if (dataHandle == null)
						throw new ResultsRepositoryException("Id tag \""
								+ idTagName + "\" is not specified.");
					Object value = dataHandle.getValue(dataHandle.getType()
							.getJavaType());
					criteria.add(Restrictions.eq(idTagName, value));
				}

				for (Object o : criteria.list()) {
					session.delete(o);
				}
			}

			DynamicEntity entity = createDynamicEntity(qname);
			entity.loadDataHandleTuple(data);
			resultSerial = serialNumberCounter.incrementSerial();
			entity.setSerial(resultSerial);
			session.save(entity);

			if (t != null)
				t.commit();

		} catch (Exception e) {
			if (t != null)
				t.rollback();
			throw new ResultsRepositoryException(
					"Error occured while saving data.",
					e);
		} finally {
			if (externalSession == null) {
				if (session != null)
					session.close();
			}
		}

		// /* check triggers associated with particular data set */
		/* transaction enabled sessions will have no triggers */
		for (RRTrigger trigger : triggers) {
			if (trigger.getDataset().equals(datasetName)) {
				if (trigger.getCondition().evaluate(data)) {
					/* firingTrigger also disables it */
					fireTrigger(trigger);
				}
			}
		}

		return resultSerial;
	}

	/**
	 * If trigger with the same UUID is not running, fires the given trigger
	 * (submits evaluator task to taskmanager).
	 * 
	 * If trigger was called before and corresponding notifyDataProcessed
	 * haven't been called yet, just saves information that trigger's event have
	 * been discarded (trigger will be fired after notifyDataProcessed will be
	 * called).
	 * 
	 * 
	 * @param trigger
	 */
	private synchronized void fireTrigger(RRTrigger trigger) {
		UUID triggerId = trigger.getId();

		if (!firedTriggers.contains(triggerId)) {
			if (trigger.getLastProcessedSerial() < serialNumberCounter
					.getCurrentSerial()) {
				try {
					/* schedule trigger's evaluator */
					scheduleEvaluator(trigger);

					/* add to list of fired triggers */
					firedTriggers.add(triggerId);
					/* reset number of discarded events */
					discardedTriggerEvents.put(triggerId, 0);
					logInfo("Fired trigger " + trigger.getAnalysis() + '/'
							+ trigger.getDataset() + '/'
							+ trigger.getEvaluator() + '/'
							+ trigger.getTriggeredTask().getName());

				} catch (ResultsRepositoryException e) {
					logError("Couldn't fire trigger " + trigger.getAnalysis()
							+ '/' + trigger.getDataset() + '/'
							+ trigger.getEvaluator() + '/'
							+ trigger.getTriggeredTask().getName()
							+ " because error occured: " + e.getMessage());
				}
			}
		} else {
			/* increment number of discarded events for given trigger */
			int discardedEvents = discardedTriggerEvents.get(triggerId)
					.intValue();
			discardedEvents++;

			discardedTriggerEvents.put(triggerId, discardedEvents);
		}
	}

	/**
	 * @param message
	 *            message to log
	 */
	private void logError(String message) {
		if (task != null) {
			task.logError(message);
		}
	}

	/**
	 * @param message
	 *            message to log
	 */
	private void logInfo(String message) {
		if (task != null) {
			task.logInfo(message);
		}
	}

	/**
	 * Schedules evaluator task associated with trigger (and sets appropriate
	 * evaluator's properties).
	 * 
	 * @param trigger
	 *            trigger
	 * @throws RemoteException
	 */
	private void scheduleEvaluator(RRTrigger trigger)
			throws ResultsRepositoryException {
		TaskDescriptor taskDescriptor = trigger.getTriggeredTask();

		evaluatorScheduler.scheduleEvaluator(
				taskDescriptor,
				trigger.getId(),
				trigger.getLastProcessedSerial());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface#createDataset
	 * (java.lang.String,
	 * cz.cuni.mff.been.resultsrepositoryng.data.DatasetDescriptor)
	 */
	@Override
	public synchronized void createDataset(
			String analysis,
			String name,
			DatasetDescriptor descriptor) throws ResultsRepositoryException,
			RemoteException {

		setContextClassLoader();

		DatasetName qname = new DatasetName(analysis, name);

		for (RRDataset dataset : datasets) {
			if (qname.equals(new DatasetName(dataset.getAnalysis(), dataset
					.getName()))) {
				throw new ResultsRepositoryException("Dataset \"" + qname
						+ "\" already exists.");
			}
		}

		Session session = null;
		Transaction t = null;
		try {
			RRDataset newDataset = new RRDataset(analysis, name, descriptor);

			checkRRDataset(newDataset);

			session = mainSessionFactory.openSession();
			t = session.beginTransaction();
			session.save(newDataset);
			reloadMetadata(session);
			t.commit();

			datasetSessionFactories.put(
					qname,
					createDatasetSessionFactory(newDataset));
		} catch (org.hibernate.HibernateException e) {
			CurrentTaskSingleton.getTaskHandle().logError("org.hibernate.HibernateException");
			for (String msg: e.getMessages()) {
				CurrentTaskSingleton.getTaskHandle().logError(msg);
			}
			throw new ResultsRepositoryException("Cannot create dataset! probably class loader problem");
		} catch (Exception e) {
			if (t != null)
				t.rollback();
			throw new ResultsRepositoryException("Error adding dataset \""
					+ name + "\".", e);
		} finally {

			try {
				if (session != null)
					session.close();
			} catch (Exception e) {
				// quell the exception
			}
		}

	}

	private void reloadMetadata(Session session) {
		datasets = getDatasets(session);
		triggers = getTriggers(session, null, null);
	}

	/**
	 * Checks dataset's integrity
	 * 
	 * @param newDataset
	 *            dataset to check
	 */
	private void checkRRDataset(RRDataset newDataset)
			throws ResultsRepositoryException {
		if (!isJavaIdentifier(newDataset.getAnalysis())) {
			throw new ResultsRepositoryException("Analysis name cannot be \""
					+ newDataset.getAnalysis() + "\"");
		}
		if (!isJavaIdentifier(newDataset.getName())) {
			throw new ResultsRepositoryException("Dataset's name cannot be \""
					+ newDataset.getAnalysis() + "\"");
		}
	}

	/**
	 * Decides whether string is valid java identifier
	 * 
	 * @param s
	 *            string
	 * @return true if s is valid java identifier
	 */
	private boolean isJavaIdentifier(String s) {
		return Pattern.matches("^[A-Za-z_][A-Za-z0-9_]*$", s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface#createTrigger
	 * (cz.cuni.mff.been.resultsrepositoryng.RRTrigger)
	 */
	@Override
	public synchronized void createTrigger(RRTrigger trigger)
			throws ResultsRepositoryException, RemoteException {

		setContextClassLoader();

		Session session = null;
		Transaction t = null;
		try {
			checkTrigger(trigger);

			/* check whether dataset is of allowed type */
			DatasetDescriptor datasetDescriptor = getDatasetDescriptor(
					trigger.getAnalysis(),
					trigger.getDataset());
			if (!DatasetType.TRIGGER_ENABLED.equals(datasetDescriptor
					.getDatasetType())) {
				throw new ResultsRepositoryException(
						"Cannot create trigger because dataset is not of type TRIGGER_ENABLED.");
			}

			for (RRTrigger existingTrigger : triggers) {
				if (existingTrigger.getId().equals(trigger.getId()))
					throw new ResultsRepositoryException(
							"Trigger with the same ID already exists.");
			}

			/* trigger will be only fired for new data */
			trigger.setLastProcessedSerial(serialNumberCounter
					.getCurrentSerial());

			session = mainSessionFactory.openSession();
			t = session.beginTransaction();

			session.save(trigger);

			reloadMetadata(session);
			t.commit();

		} catch (Exception e) {
			if (t != null)
				t.rollback();
			throw new ResultsRepositoryException(
					"Error adding trigger to dataset \"" + trigger.getDataset()
							+ "\".",
					e);
		} finally {
			if (session != null)
				session.close();
		}
	}

	/**
	 * Releases all used resources. Object shouldn't be used anymore after
	 * destroy() is called. Create a new instance.
	 */
	public synchronized void destroy() {

		setContextClassLoader();

		for (RRDataset dataset : datasets) {
			DatasetName name = new DatasetName(
					dataset.getAnalysis(),
					dataset.getName());
			SessionFactory sf = datasetSessionFactories.get(name);
			if (!sf.isClosed())
				sf.close();
		}
		if (!mainSessionFactory.isClosed())
			mainSessionFactory.close();
	}

	/**
	 * Checks trigger's integrity
	 * 
	 * @param trigger
	 */
	private void checkTrigger(RRTrigger trigger) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface#deleteDataset
	 * (java.lang.String)
	 */
	@Override
	public synchronized void deleteDataset(
			String analysisName,
			String datasetName) throws ResultsRepositoryException,
			RemoteException {

		setContextClassLoader();

		DatasetName qname = new DatasetName(analysisName, datasetName);

		Session session = null;
		Transaction t = null;
		try {
			session = mainSessionFactory.openSession();
			t = session.beginTransaction();

			/* delete dataset */
			for (RRDataset dataset : datasets)
				if (qname.equals(new DatasetName(dataset.getAnalysis(), dataset
						.getName()))) {
					session.delete(dataset);
				}

			/* delete associated triggers */
			List<RRTrigger> triggers = this.getTriggers(
					session,
					analysisName,
					datasetName);
			for (RRTrigger trigger : triggers) {
				firedTriggers.remove(trigger.getId());
				discardedTriggerEvents.remove(trigger.getId());
				session.delete(trigger);
			}

			reloadMetadata(session);
			t.commit();

			SessionFactory sf = datasetSessionFactories.remove(qname);
			Session datasetSession = null;
			Transaction datasetTransaction = null;
			try {
				datasetSession = sf.openSession();
				datasetTransaction = datasetSession.beginTransaction();

				Query q = datasetSession.createQuery("delete from "
						+ entityClasses.get(qname).getName());
				q.executeUpdate();

				datasetTransaction.commit();
			} catch (Exception e) {
				if (datasetTransaction != null)
					datasetTransaction.rollback();
				throw new ResultsRepositoryException(
						"Error deleting dataset's \"" + qname + "\" data.",
						e);
			} finally {
				if (datasetSession != null)
					datasetSession.close();
			}

			sf.close();
			entityClasses.remove(qname);
		} catch (Exception e) {
			if (t != null)
				t.rollback();
			throw new ResultsRepositoryException("Error deleting dataset \""
					+ qname + "\".", e);
		} finally {
			if (session != null)
				session.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface#deleteTriggers
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void deleteTriggers(
			String analysis,
			String dataset,
			String evaluator) throws ResultsRepositoryException,
			RemoteException {

		setContextClassLoader();

		Session session = null;
		Transaction t = null;
		try {
			session = mainSessionFactory.openSession();
			t = session.beginTransaction();

			List<RRDataset> selectedDatasets;
			if (dataset == null) {
				selectedDatasets = datasets;
			} else {
				selectedDatasets = new ArrayList<RRDataset>();
				for (RRDataset ds : datasets)
					if (ds.getName().equals(dataset)) {
						if ((analysis == null)
								|| analysis.equals(ds.getAnalysis())) {
							selectedDatasets.add(ds);
						}
					}
			}

			for (RRDataset ds : selectedDatasets) {
				List<RRTrigger> triggers = getTriggers(
						session,
						ds.getAnalysis(),
						ds.getName());

				for (RRTrigger trigger : triggers) {
					if ((evaluator == null)
							|| trigger.getEvaluator().equals(evaluator)) {
						session.delete(trigger);
						firedTriggers.remove(trigger.getId());
						discardedTriggerEvents.remove(trigger.getId());
					}
				}
			}
			reloadMetadata(session);
			t.commit();

		} catch (Exception e) {
			if (t != null)
				t.rollback();
			throw new ResultsRepositoryException("Error deleting triggers.", e);
		} finally {
			if (session != null)
				session.close();
		}
	}

	@Override
	public void deleteTrigger(UUID triggerId)
			throws ResultsRepositoryException, RemoteException,
			NoSuchElementException {
		Session session;
		Transaction t;
		RRTrigger trigger;

		setContextClassLoader();

		session = null;
		t = null;
		try {
			session = mainSessionFactory.openSession();
			t = session.beginTransaction();
			trigger = getTrigger(session, triggerId);
			if (null == trigger) { // TODO: Don't throw unchecked!
				throw new NoSuchElementException("Trigger with UUID "
						+ triggerId.toString() + " does not exist.");
			}
			session.delete(trigger);
			firedTriggers.remove(trigger.getId());
			discardedTriggerEvents.remove(trigger.getId());
			reloadMetadata(session);
			t.commit();
		} catch (Exception exception) {
			if (null != t) {
				t.rollback();
			}
			throw new ResultsRepositoryException(
					"Error deleting trigger.",
					exception);
		} finally {
			if (null != session) {
				session.close();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface#getDatasetDescriptor
	 * (java.lang.String)
	 */
	@Override
	public synchronized DatasetDescriptor getDatasetDescriptor(
			String analysisName,
			String datasetName) throws ResultsRepositoryException,
			RemoteException {

		setContextClassLoader();

		DatasetName qname = new DatasetName(analysisName, datasetName);

		DatasetDescriptor result = null;
		for (RRDataset dataset : datasets) {
			if (qname.equals(new DatasetName(dataset.getAnalysis(), dataset
					.getName()))) {
				result = dataset.getDatasetDescriptor();
			}
		}
		if (result == null) {
			throw new ResultsRepositoryException("Dataset with name \"" + qname
					+ "\" does not exist.");
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface#getDatasets()
	 */
	@Override
	public synchronized List<String> getDatasets(String analysisName)
			throws RemoteException, ResultsRepositoryException {

		setContextClassLoader();

		List<String> result = new ArrayList<String>();
		for (RRDataset dataset : datasets) {
			if (analysisName.equals(dataset.getAnalysis())) {
				result.add(dataset.getName());
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface#getAnalyses()
	 */
	@Override
	public synchronized List<String> getAnalyses() throws RemoteException,
			ResultsRepositoryException {

		setContextClassLoader();

		Set<String> analyses = new HashSet<String>();

		for (RRDataset dataset : datasets) {
			analyses.add(dataset.getAnalysis());
		}
		return new ArrayList<String>(analyses);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.cuni.mff.been.resultsrepositoryng.RRManagerInterface#getTriggers(java
	 * .lang.String)
	 */
	@Override
	public synchronized List<RRTrigger> getTriggers(
			String analysis,
			String dataset) throws RemoteException, ResultsRepositoryException {

		setContextClassLoader();

		Session session = null;
		try {
			session = mainSessionFactory.openSession();
			List<RRTrigger> result = getTriggers(session, analysis, dataset);
			return result;
		} finally {
			if (session != null)
				session.close();
		}
	}

	@Override
	public synchronized RRTrigger getTrigger(UUID triggerId)
			throws ResultsRepositoryException, RemoteException,
			NoSuchElementException {
		Session session;
		RRTrigger result;

		setContextClassLoader();

		session = null;
		try {
			session = mainSessionFactory.openSession();
			result = getTrigger(session, triggerId);
			if (null == result) { // TODO: Don't throw unchecked!!!
				throw new NoSuchElementException("Trigger with UUID "
						+ triggerId.toString() + " does not exist.");
			}
			return result;
		} catch (Exception exception) {
			throw new ResultsRepositoryException(
					"Error retrieving trigger.",
					exception);
		} finally {
			if (null != session) {
				session.close();
			}
		}
	}

	/**
	 * Loads a trigger from persistent storage.
	 * 
	 * @param session
	 *            Hibernate session to use.
	 * @param triggerId
	 *            Trigger to look up.
	 * @return An instance of the trigger (or null if none found).
	 */
	private RRTrigger getTrigger(Session session, UUID triggerId) {
		return (RRTrigger) session.get(RRTrigger.class, triggerId);
	}

	/**
	 * Loads datasets from persistent storage
	 * 
	 * @param session
	 *            hibernate session to use
	 * @return list of datasets
	 */
	private synchronized List<RRDataset> getDatasets(Session session) {
		Criteria query = session.createCriteria(RRDataset.class);
		ArrayList<RRDataset> result = new ArrayList<RRDataset>();
		for (Object o : query.list()) {
			result.add((RRDataset) o);
		}
		return result;
	}

	/**
	 * Loads triggers from persistent storage
	 * 
	 * @param session
	 *            hibernate session to use
	 * @param analysisName
	 *            analysis name filter or null (if null, condition will not be
	 *            applied)
	 * @param datasetName
	 *            dataset name filter or null (if null, condition will not be
	 *            applied)
	 * @return list of triggers
	 */
	private List<RRTrigger> getTriggers(
			Session session,
			String analysisName,
			String datasetName) {
		Criteria query;
		query = session.createCriteria(RRTrigger.class);
		if (analysisName != null) {
			query.add(Restrictions.eq("analysis", analysisName));
		}
		if (datasetName != null) {
			query.add(Restrictions.eq("dataset", datasetName));
		}
		ArrayList<RRTrigger> result = new ArrayList<RRTrigger>();
		for (Object o : query.list()) {
			result.add((RRTrigger) o);
		}
		return result;
	}

	/**
	 * Notifies that data with serial number less or equal to argument
	 * lastProcessedSerial have been successfully processed by evaluator.
	 * 
	 * Trigger's lastProcessedSerial is set to max(originalValue,
	 * lastProcessedSerial).
	 * 
	 * @param triggerId
	 *            trigger's identifiers
	 * @param lastProcessedSerial
	 *            last serial number that has been processed
	 * @throws ResultsRepositoryException
	 * @throws RemoteException
	 */
	@Override
	public synchronized void notifyDataProcessed(
			UUID triggerId,
			long lastProcessedSerial) throws ResultsRepositoryException,
			RemoteException {

		setContextClassLoader();

		Session session = mainSessionFactory.openSession();
		Transaction t = null;
		try {
			t = session.beginTransaction();
			RRTrigger trigger = (RRTrigger) session.get(
					RRTrigger.class,
					triggerId);

			if (lastProcessedSerial >= trigger.getLastProcessedSerial()) {
				trigger.setLastProcessedSerial(lastProcessedSerial);
				session.merge(trigger);
			}
			reloadMetadata(session);
			t.commit();

			firedTriggers.remove(triggerId);
			Integer discardedEvents = discardedTriggerEvents.remove(triggerId);

			if ((discardedEvents != null) && discardedEvents.intValue() != 0) {
				/* fire triggers that have some discarded events */
				fireTrigger(trigger);
			}

		} catch (Exception e) {
			if (t != null)
				t.rollback();
			throw new ResultsRepositoryException(
					"Error running triggers associated with notification event.",
					e);
		} finally {
			session.close();
		}
	}

	/**
	 * Ensures context classloader is set to entity class classloader (otherwise
	 * hibernate would fail to locate datasets' entity classes he needs).
	 */
	private void setContextClassLoader() {
		Thread.currentThread().setContextClassLoader(
				EntityClassFactory.getEntityClassLoader());
	}

	/**
	 * Sets scheduler for evaluator. If scheduler not set, a default internal
	 * scheduler is used.
	 */
	public void setEvaluatorScheduler(EvaluatorScheduler evaluatorScheduler) {
		this.evaluatorScheduler = evaluatorScheduler;
	}

	/**
	 * Can be used for testing purposes.
	 * 
	 * @return last serial number used by results repository
	 */
	public long getLastSerialNumber() {
		return this.serialNumberCounter.getCurrentSerial();
	}

	/**
	 * Produces growing series of serials.
	 * 
	 * @author Jan Tattermusch
	 * 
	 */
	private class SerialNumberCounter {
		private long currentSerial = 1;

		/**
		 * Raises current serial to given value, if current value is lower
		 * before.
		 * 
		 * @param maxSerial
		 *            new current serial
		 */
		public synchronized void raise(long maxSerial) {
			if (maxSerial > currentSerial) {
				currentSerial = maxSerial;
			}
		}

		/**
		 * 
		 * @return current serial
		 */
		public synchronized long getCurrentSerial() {
			return currentSerial;
		}

		/**
		 * Increments current serial and returns its new value
		 * 
		 * @return new serial number
		 */
		public synchronized long incrementSerial() {
			return ++currentSerial;
		}
	}

	/**
	 * Class for representing qualified dataset's name. Qualified name consists
	 * of analysis name and dataset name.
	 * 
	 * @author Jan Tattermusch
	 * 
	 */
	private class DatasetName {
		private final String analysis;
		private final String dataset;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((analysis == null) ? 0 : analysis.hashCode());
			result = prime * result
					+ ((dataset == null) ? 0 : dataset.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DatasetName other = (DatasetName) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (analysis == null) {
				if (other.analysis != null)
					return false;
			} else if (!analysis.equals(other.analysis))
				return false;
			if (dataset == null) {
				if (other.dataset != null)
					return false;
			} else if (!dataset.equals(other.dataset))
				return false;
			return true;
		}

		public DatasetName(String analysis, String dataset) {
			this.analysis = analysis;
			this.dataset = dataset;
		}

		@Override
		public String toString() {
			return "[" + analysis + "]" + dataset;

		}

		private ResultsRepositoryImplementation getOuterType() {
			return ResultsRepositoryImplementation.this;
		}
	}

	@Override
	public FileStoreClient getFileStoreClient() throws RemoteException {
		return new FileStoreClientImpl(fileStore);
	}

	/**
	 * Creates new transaction on given dataset. Dataset's type has to allow
	 * performing transactions (TRANSACTION_ENABLED)
	 * 
	 * @param analysisName
	 *            name of analysis
	 * @param datasetName
	 *            name of dataset
	 * @return new RR transaction associated with given dataset
	 * @throws RemoteException
	 * @throws ResultsRepositoryException
	 */
	@Override
	public synchronized RRTransaction getTransaction(
			String analysisName,
			String datasetName) throws RemoteException,
			ResultsRepositoryException {

		setContextClassLoader();

		DatasetName qname = new DatasetName(analysisName, datasetName);

		SessionFactory sessionFactory = datasetSessionFactories.get(qname);

		DatasetDescriptor datasetDescriptor = getDatasetDescriptor(
				analysisName,
				datasetName);

		if (datasetDescriptor == null) {
			throw new ResultsRepositoryException("Dataset \"" + qname
					+ "\" does not exist.");
		}

		if (!DatasetType.TRANSACTION_ENABLED.equals(datasetDescriptor
				.getDatasetType())) {
			throw new ResultsRepositoryException(
					"Dataset \""
							+ qname
							+ "\" does not have transactions allowed. Check dataset type.");
		}

		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();

		// session.setFlushMode(FlushMode.COMMIT);

		RRTransaction transaction = new RRTransactionImplementation(
				this,
				analysisName,
				datasetName,
				session,
				t);

		return transaction;
	}
}
