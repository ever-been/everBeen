/*
 * BEEN: Benchmarking Environment ==============================
 * 
 * File author: Antonin Tomecek
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
package cz.cuni.mff.been.taskmanager.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.jaxb.td.TaskExclusivity;
import cz.cuni.mff.been.taskmanager.tasktree.TaskTreeInput;

/**
 * Class storing informations (to filesystem) for reason of TM's crash.
 * 
 * @author Antonin Tomecek
 */
public class Rescue {

	/* Name of directory storing informations about contexts. */
	private static final String CONTEXTS_DIR_NAME = "contexts";

	/* Name of directory storing informations about tasks. */
	private static final String TASKS_DIR_NAME = "tasks";

	/* Name of directory storing informations about checkPoints. */
	private static final String CHECKPOINTS_DIR_NAME = "check-points";

	/* Name of directory storing informations about hostRuntimes. */
	private static final String HOSTRUNTIMES_DIR_NAME = "host-runtimes";

	/* Root directory for storing informations. */
	private final File rootDir;

	/* Directory storing informations about contexts. */
	private final File contextsDir;

	/* Directory storing informations about tasks. */
	private final File tasksDir;

	/* Directory storing informations about checkPoints. */
	private final File checkPointsDir;

	/* Directory storing informations about hostRuntimes. */
	private final File hostRuntimesDir;

	/**
	 * Create new instance of Rescue class and set its root directory for storing
	 * informations.
	 * 
	 * @param rootDir
	 *          Directory where to store informations (new directory will be
	 *          created).
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If directory already exists.
	 */
	protected Rescue(File rootDir) {
		/* Check input parameter. */
		if (rootDir == null) {
			throw new NullPointerException("rootDir is null");
		}

		/* Test root directory. */
		if (rootDir.exists()) {
			throw new IllegalArgumentException("File or directory with name" + rootDir.getPath() + "already exists");
		}

		/* Create root directory for rescue. */
		this.rootDir = rootDir;
		rootDir.mkdir();

		/* Create subdirectories for rescue. */
		this.contextsDir = new File(rootDir, CONTEXTS_DIR_NAME);
		this.contextsDir.mkdir();
		this.tasksDir = new File(rootDir, TASKS_DIR_NAME);
		this.tasksDir.mkdir();
		this.checkPointsDir = new File(rootDir, CHECKPOINTS_DIR_NAME);
		this.checkPointsDir.mkdir();
		this.hostRuntimesDir = new File(rootDir, HOSTRUNTIMES_DIR_NAME);
		this.hostRuntimesDir.mkdir();
	}

	/**
	 * Return root directory for storing informations.
	 * 
	 * @return Root directory for storing informations.
	 */
	protected File getRootDir() {
		return this.rootDir;
	}

	/**
	 * Store <code>Object</code> to the specified <code>File</code>.
	 * 
	 * @param serializableObject
	 *          Object to store.
	 * @param file
	 *          File for writing object to.
	 * @throws NullPointerException
	 *           If some input parameter is <code>null</code>.
	 * @throws RuntimeException
	 *           If some unrecoverable error occurred.
	 */
	private static void storeObjectToFile(Serializable serializableObject,
			File file) {
		/* Check input parameters. */
		if (serializableObject == null) {
			return; // nothing to do
		}
		if (file == null) {
			throw new NullPointerException("file is null");
		}

		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not create new FileOutputStream " + "for object (file \"" + file.getAbsolutePath() + "\")", e);
		}

		ObjectOutputStream objectOutputStream;
		try {
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
		} catch (IOException e) {
			throw new RuntimeException("Could not create new " + "ObjectOutputStream for object", e);
		}

		try {
			objectOutputStream.writeObject(serializableObject);
		} catch (IOException e) {
			throw new RuntimeException("Could not write object to file \"" + file.getAbsolutePath() + "\"", e);
		}

		try {
			objectOutputStream.close();
		} catch (IOException e) {
			throw new RuntimeException("Could not close ObjectOutputStream", e);
		}
	}

	/**
	 * Load <code>Serializable</code> from the specified <code>File</code>.
	 * 
	 * @param file
	 *          File for reading object from.
	 * @return Loaded object of <code>null</code> if <code>file</code> does not
	 *         exist.
	 * @throws NullPointerException
	 *           If input paramater is <code>null</code>.
	 * @throws RuntimeException
	 *           If some unrecoverable error occurred.
	 */
	private static Serializable loadObjectFromFile(File file) {
		/* Check input parameters. */
		if (file == null) {
			throw new NullPointerException("file is null");
		}

		/* Test if file exists. */
		if (!file.exists()) {
			return null;
		}

		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not create new FileInputStream " + "for object (file \"" + file.getAbsolutePath() + "\")", e);
		}

		ObjectInputStream objectInputStream;
		try {
			objectInputStream = new ObjectInputStream(fileInputStream);
		} catch (IOException e) {
			throw new RuntimeException("Could not create new ObjectInputStream " + "for object", e);
		}

		Serializable serializableObject;
		try {
			serializableObject = (Serializable) objectInputStream.readObject();
		} catch (IOException e) {
			throw new RuntimeException("Could not read object from file \"" + file.getAbsolutePath() + "\"", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unrecoverable error when reading " + "object from file \"" + file.getAbsolutePath() + "\"", e);
		}

		try {
			objectInputStream.close();
		} catch (IOException e) {
			throw new RuntimeException("Could not close ObjectInputStream", e);
		}

		return serializableObject;
	}

	/**
	 * Parse CDATA from element containing it.
	 * 
	 * @param elementWithCDATA
	 *          Element containing CDATA.
	 * @return <code>String</code> representation of CDATA or <code>null</code> if
	 *         specified element does not contain any.
	 */
	private static String parseCDATA(Element elementWithCDATA) {
		/* Check input parameters. */
		if (elementWithCDATA == null) {
			throw new NullPointerException("elementWithCDATA is null");
		}

		/* Find element containing CDATA (if founded then return it). */
		NodeList childNodes = elementWithCDATA.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);

			if (childNode.getNodeName().equals("#cdata-section")) {
				return childNode.getNodeValue();
			}
		}

		/* If not found then return null. */
		return null;
	}

	/**
	 * Construct <code>ContextEntry</code> from rescue directory.
	 * 
	 * @param contextDir
	 *          Rescue directory for context.
	 * @return Rescued <code>ContextEntry</code>.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If entry can not be rescued from specified directory.
	 * @throws RuntimeException
	 *           If some other unexpected error occurred.
	 */
	private static ContextEntry rescueContext(File contextDir) {
		/* Check input parameters. */
		if (contextDir == null) {
			throw new NullPointerException("contextDir is null");
		}

		/* Find contextEntryFile. */
		File contextEntryFile = null;
		File[] items = contextDir.listFiles();
		for (File item : items) {
			if (item.getName().equals("contextEntry.xml")) {
				contextEntryFile = item;
				break;
			}
		}
		if (contextEntryFile == null) {
			throw new IllegalArgumentException("File named \"contextEntry\" " + "not found in specified directory \"" + contextDir.getAbsolutePath() + "\"");
		}

		/* Build ContextEntry object from XML file... */

		/* Prepare DOM and obtain Document. */
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);

		Document document;
		try {
			document = factory.newDocumentBuilder().parse(contextEntryFile);
		} catch (SAXException e) {
			throw new RuntimeException("Parse error occured.", e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("DocumentBuilder can not be " + "created", e);
		}

		/* Process the root element. */
		Element rootElement = document.getDocumentElement();
		if (!rootElement.getTagName().equals("contextEntry")) {
			throw new IllegalArgumentException("Root element of XML file \"" + contextEntryFile.getAbsolutePath() + "\" sould be " + "\"contextEntry\" (not \"" + rootElement.getTagName() + "\")");
		}

		/* Process attribute open. */
		String openAttribute = rootElement.getAttribute("open");
		boolean open = (openAttribute.equals("true")) ? true : false;

		/* Process element contextId. */
		Element contextIdElement = (Element) rootElement.getElementsByTagName("contextId").item(0);
		String contextId = parseCDATA(contextIdElement);

		/* Process element contextName. */
		Element contextNameElement = (Element) rootElement.getElementsByTagName("contextName").item(0);
		String contextName = parseCDATA(contextNameElement);

		/* Process element contextDescription. */
		Element contextDescriptionElement = (Element) rootElement.getElementsByTagName("contextDescription").item(0);
		String contextDescription = parseCDATA(contextDescriptionElement);

		/* Load MagicObject from file. */
		Serializable magicObject = loadObjectFromFile(new File(contextDir, "magicObject"));

		/* Create (initialize) and return new entry. */
		ContextEntryImplementation contextEntry = new ContextEntryImplementation(contextId, contextName, contextDescription, magicObject);
		if (!open) {
			contextEntry.close();
		}
		return contextEntry;
	}

	/**
	 * Construct <code>TaskEntry</code> from rescue directory.
	 * 
	 * @param taskDir
	 *          Rescue directory for task.
	 * @return Rescued <code>TaskEntry</code>.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If entry can not be rescued from specified directory.
	 * @throws RuntimeException
	 *           If some other unexpected error occurred.
	 */
	private static TaskEntryImplementation rescueTask(File taskDir) {
		/* Check input parameters. */
		if (taskDir == null) {
			throw new NullPointerException("taskDir is null");
		}

		/* Find taskEntryFile. */
		File taskEntryFile = null;
		File[] items = taskDir.listFiles();
		for (File item : items) {
			if (item.getName().equals("taskEntry.xml")) {
				taskEntryFile = item;
				break;
			}
		}
		if (taskEntryFile == null) {
			throw new IllegalArgumentException("File named \"taskEntry\" not " + "found in specified directory \"" + taskDir.getAbsolutePath() + "\"");
		}

		/* Build TaskEntry object from XML file... */

		/* Prepare DOM and obtain Document. */
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);

		Document document;
		try {
			document = factory.newDocumentBuilder().parse(taskEntryFile);
		} catch (SAXException e) {
			throw new RuntimeException("Parse error occured.", e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("DocumentBuilder can not be " + "created", e);
		}

		/* Process the root element. */
		Element rootElement = document.getDocumentElement();
		if (!rootElement.getTagName().equals("taskEntry")) {
			throw new IllegalArgumentException("Root element of XML file \"" + taskEntryFile.getAbsolutePath() + "\" sould be " + "\"taskEntry\" (not \"" + rootElement.getTagName() + "\")");
		}

		/* Process attribute exclusivity. */
		String exclusivityAttribute = rootElement.getAttribute("exclusivity");
		TaskExclusivity exclusivity = TaskExclusivity.fromValue(exclusivityAttribute);

		/* Process attribute serviceFlag. */
		String serviceFlagAttribute = rootElement.getAttribute("serviceFlag");
		boolean serviceFlag = (serviceFlagAttribute.equals("true")) ? true : false;

		/* Process attribute state. */
		String stateAttribute = rootElement.getAttribute("state");
		TaskState state = TaskState.fromString(stateAttribute);

		/* Process element taskId. */
		Element taskIdElement = (Element) rootElement.getElementsByTagName("taskId").item(0);
		String taskId = parseCDATA(taskIdElement);

		/* Process element contextId. */
		Element contextIdElement = (Element) rootElement.getElementsByTagName("contextId").item(0);
		String contextId = parseCDATA(contextIdElement);

		/* Process element packageName. */
		Element packageNameElement = (Element) rootElement.getElementsByTagName("packageName").item(0);
		String packageName = parseCDATA(packageNameElement);

		/* Process element taskName. */
		Element taskNameElement = (Element) rootElement.getElementsByTagName("taskName").item(0);
		String taskName = parseCDATA(taskNameElement);

		/* Process element taskDescription. */
		Element taskDescriptionElement = (Element) rootElement.getElementsByTagName("taskDescription").item(0);
		String taskDescription = parseCDATA(taskDescriptionElement);

		/* Process element hostName. */
		Element hostNameElement = (Element) rootElement.getElementsByTagName("hostName").item(0);
		String hostName = parseCDATA(hostNameElement);

		/* Process element directoryPathTask. */
		Element directoryPathTaskElement = (Element) rootElement.getElementsByTagName("directoryPathTask").item(0);
		String directoryPathTask = parseCDATA(directoryPathTaskElement);

		/* Process element directoryPathWorking. */
		Element directoryPathWorkingElement = (Element) rootElement.getElementsByTagName("directoryPathWorking").item(0);
		String directoryPathWorking = parseCDATA(directoryPathWorkingElement);

		/* Process element directoryPathTemporary. */
		Element directoryPathTemporaryElement = (Element) rootElement.getElementsByTagName("directoryPathTemporary").item(0);
		String directoryPathTemporary = parseCDATA(directoryPathTemporaryElement);

		/* Process element taskProperties. */
		Element taskPropertiesElement = (Element) rootElement.getElementsByTagName("taskProperties").item(0);
		/*
		 * Process elements taskProperty (sub-elements of taskProperties
		 * element).
		 */
		NodeList taskPropertyElementList = taskPropertiesElement.getElementsByTagName("taskProperty");
		Properties taskProperties = new Properties();
		for (int i = 0; i < taskPropertyElementList.getLength(); i++) {
			Element taskPropertyElement = (Element) taskPropertyElementList.item(i);
			String key = taskPropertyElement.getAttribute("key");
			String value = taskPropertyElement.getAttribute("value");
			taskProperties.setProperty(key, value);
		}

		/* Process element timeSubmitted. */
		Element timeSubmittedElement = (Element) rootElement.getElementsByTagName("timeSubmitted").item(0);
		long timeSubmitted = Long.parseLong(parseCDATA(timeSubmittedElement));

		/* Process element timeScheduled. */
		Element timeScheduledElement = (Element) rootElement.getElementsByTagName("timeScheduled").item(0);
		long timeScheduled = Long.parseLong(parseCDATA(timeScheduledElement));

		/* Process element timeStarted. */
		Element timeStartedElement = (Element) rootElement.getElementsByTagName("timeStarted").item(0);
		long timeStarted = Long.parseLong(parseCDATA(timeStartedElement));

		/* Process element timeFinished. */
		Element timeFinishedElement = (Element) rootElement.getElementsByTagName("timeFinished").item(0);
		long timeFinished = Long.parseLong(parseCDATA(timeFinishedElement));

		/* Process element restartCount. */
		Element restartCountElement = (Element) rootElement.getElementsByTagName("restartCount").item(0);
		int restartCount = Integer.parseInt(parseCDATA(restartCountElement));

		/* Process element restartMax. */
		Element restartMaxElement = (Element) rootElement.getElementsByTagName("restartMax").item(0);
		int restartMax = Integer.parseInt(parseCDATA(restartMaxElement));

		/* Process element timeoutRun. */
		Element timeoutRunElement = (Element) rootElement.getElementsByTagName("timeoutRun").item(0);
		long timeoutRun = Long.parseLong(parseCDATA(timeoutRunElement));

		/* Create (initialize) and return new entry. */
		TaskEntryImplementation taskEntry = new TaskEntryImplementation(taskId, contextId, packageName, taskName, taskDescription, hostName, directoryPathTask, directoryPathWorking, directoryPathTemporary, taskProperties, exclusivity, serviceFlag, restartMax, timeoutRun);
		taskEntry.setState(state);
		taskEntry.setTimeSubmitted(timeSubmitted);
		taskEntry.setTimeScheduled(timeScheduled);
		taskEntry.setTimeStarted(timeStarted);
		taskEntry.setTimeFinished(timeFinished);
		taskEntry.setRestartCount(restartCount);
		return taskEntry;
	}

	/**
	 * Construct <code>CheckPointEntry</code> from rescue directory.
	 * 
	 * @param checkPointDir
	 *          Rescue directory for checkPoint.
	 * @return Rescued <code>CheckPointEntry</code>.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If entry can not be rescued from specified directory.
	 * @throws RuntimeException
	 *           If some other unexpected error occurred.
	 */
	private static CheckPointEntry rescueCheckPoint(File checkPointDir) {
		/* Check input parameters. */
		if (checkPointDir == null) {
			throw new NullPointerException("checkPointDir is null");
		}

		/* Find checkPointEntryFile. */
		File checkPointEntryFile = null;
		File[] items = checkPointDir.listFiles();
		for (File item : items) {
			if (item.getName().equals("checkPointEntry.xml")) {
				checkPointEntryFile = item;
				break;
			}
		}
		if (checkPointEntryFile == null) {
			throw new IllegalArgumentException("File named \"checkPointEntry\" " + "not found in specified directory \"" + checkPointDir.getAbsolutePath() + "\"");
		}

		/* Build CheckPointEntry object from XML file... */

		/* Prepare DOM and obtain Document. */
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);

		Document document;
		try {
			document = factory.newDocumentBuilder().parse(checkPointEntryFile);
		} catch (SAXException e) {
			throw new RuntimeException("Parse error occured.", e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("DocumentBuilder can not be " + "created", e);
		}

		/* Process the root element. */
		Element rootElement = document.getDocumentElement();
		if (!rootElement.getTagName().equals("checkPointEntry")) {
			throw new IllegalArgumentException("Root element of XML file \"" + checkPointEntryFile.getAbsolutePath() + "\" sould be " + "\"checkPointEntry\" (not \"" + rootElement.getTagName() + "\")");
		}

		/* Process element name. */
		Element nameElement = (Element) rootElement.getElementsByTagName("name").item(0);
		String name = parseCDATA(nameElement);

		/* Process element taskId. */
		Element taskIdElement = (Element) rootElement.getElementsByTagName("taskId").item(0);
		String taskId = parseCDATA(taskIdElement);

		/* Process element contextId. */
		Element contextIdElement = (Element) rootElement.getElementsByTagName("contextId").item(0);
		String contextId = parseCDATA(contextIdElement);

		/* Process element hostName. */
		Element hostNameElement = (Element) rootElement.getElementsByTagName("hostName").item(0);
		String hostName = parseCDATA(hostNameElement);

		/* Load MagicObject from file. */
		Serializable magicObject = loadObjectFromFile(new File(checkPointDir, "magicObject"));

		/* Create (initialize) and return new entry. */
		CheckPointEntry checkPointEntry = new CheckPointEntry(name, taskId, contextId, hostName, magicObject);
		return checkPointEntry;
	}

	/**
	 * Construct <code>HostRuntimeEntry</code> from rescue directory.
	 * 
	 * @param hostRuntimeDir
	 *          Rescue directory for hostRuntime.
	 * @return Rescued <code>HostRuntimeEntry</code>.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If entry can not be rescued from specified directory.
	 * @throws RuntimeException
	 *           If some other unexpected error occurred.
	 */
	private static HostRuntimeEntry rescueHostRuntime(File hostRuntimeDir) {
		/* Check input parameters. */
		if (hostRuntimeDir == null) {
			throw new NullPointerException("hostRuntimeDir is null");
		}

		/* Find hostRuntimeEntryFile. */
		File hostRuntimeEntryFile = null;
		File[] items = hostRuntimeDir.listFiles();
		for (File item : items) {
			if (item.getName().equals("hostRuntimeEntry.xml")) {
				hostRuntimeEntryFile = item;
				break;
			}
		}
		if (hostRuntimeEntryFile == null) {
			throw new IllegalArgumentException("File named " + "\"hostRuntimeEntry\" not found in specified directory \"" + hostRuntimeDir.getAbsolutePath() + "\"");
		}

		/* Build HostRuntimeEntry object from XML file... */

		/* Prepare DOM and obtain Document. */
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);

		Document document;
		try {
			document = factory.newDocumentBuilder().parse(hostRuntimeEntryFile);
		} catch (SAXException e) {
			throw new RuntimeException("Parse error occured.", e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("DocumentBuilder can not be " + "created", e);
		}

		/* Process the root element. */
		Element rootElement = document.getDocumentElement();
		if (!rootElement.getTagName().equals("hostRuntimeEntry")) {
			throw new IllegalArgumentException("Root element of XML file \"" + hostRuntimeEntryFile.getAbsolutePath() + "\" sould be " + "\"hostRuntimeEntry\" (not \"" + rootElement.getTagName() + "\")");
		}

		/* Process element hostName. */
		Element hostNameElement = (Element) rootElement.getElementsByTagName("hostName").item(0);
		String hostName = parseCDATA(hostNameElement);

		/* Process element reservation. */
		Element reservationElement = (Element) rootElement.getElementsByTagName("reservation").item(0);
		String reservation = parseCDATA(reservationElement);

		/* Create (initialize) and return new entry. */
		HostRuntimeEntry hostRuntimeEntry = new HostRuntimeEntryImplementation(hostName);
		hostRuntimeEntry.setReservation(reservation);
		return hostRuntimeEntry;
	}

	/**
	 * Build and return <code>Data</code> object containing data of TaskManager
	 * based on rescue informations.
	 * 
	 * @param newRootDir
	 *          Root directory for storing rescue informations for newly created
	 *          <code>Data</code> object.
	 * @param oldRootDir
	 *          Root directory containing rescue informations.
	 * @param taskTree
	 *          Task Manager's tree of tasks.
	 * @return Rescued <code>Data</code> object for TaskManager.
	 * @throws NullPointerException
	 *           If some input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           It <code>oldRoodDir</code> direstory does not exist or is not a
	 *           directory.
	 */
	public static Data rescueData(File newRootDir, File oldRootDir,
			TaskTreeInput taskTree) {
		/* Check input parameters. */
		if (newRootDir == null) {
			throw new NullPointerException("newRootDir is null");
		}
		if (oldRootDir == null) {
			throw new NullPointerException("oldRootDir is null");
		}

		/* Test if oldRootDir directory exists. */
		if (!oldRootDir.exists() || !oldRootDir.isDirectory()) {
			throw new IllegalArgumentException("oldRootDir \"" + oldRootDir.getAbsolutePath() + "does not exist or it is " + "not directory");
		}

		/* Create new data object. */
		Data data = new Data(newRootDir, taskTree);

		/* Prepare directories. */
		File contextsDir = new File(oldRootDir, CONTEXTS_DIR_NAME);
		File tasksDir = new File(oldRootDir, TASKS_DIR_NAME);
		File checkPointsDir = new File(oldRootDir, CHECKPOINTS_DIR_NAME);
		File hostRuntimesDir = new File(oldRootDir, HOSTRUNTIMES_DIR_NAME);

		/* Rescue hostRuntimes. */
		File[] hostRuntimes = hostRuntimesDir.listFiles();
		for (File hostRuntime : hostRuntimes) {
			HostRuntimeEntry hostRuntimeEntry = rescueHostRuntime(hostRuntime);
			data.addHostRuntime(hostRuntimeEntry);
		}

		/* Rescue contexts. */
		File[] contexts = contextsDir.listFiles();
		for (File context : contexts) {
			ContextEntry contextEntry = rescueContext(context);
			data.newContext(contextEntry);
		}

		/* Rescue tasks. */
		File[] tasksContexts = tasksDir.listFiles();
		for (File tasksContext : tasksContexts) {
			File[] tasks = tasksContext.listFiles();
			for (File task : tasks) {
				TaskEntryImplementation taskEntry = rescueTask(task);
				TaskDescriptor taskDescriptor = (TaskDescriptor) loadObjectFromFile(new File(task, "taskDescriptor"));
				TaskData taskData = new TaskData(taskDescriptor);
				data.newTask(taskEntry, taskData);
			}
		}

		/* Rescue checkPoints. */
		File[] checkPointsTasksContexts = checkPointsDir.listFiles();
		for (File checkPointsTasksContext : checkPointsTasksContexts) {
			File[] checkPointsTasks = checkPointsTasksContext.listFiles();
			for (File checkPointsTask : checkPointsTasks) {
				File[] checkPoints = checkPointsTask.listFiles();
				for (File checkPoint : checkPoints) {
					CheckPointEntry checkPointEntry = rescueCheckPoint(checkPoint);
					data.newCheckPoint(checkPointEntry);
				}
			}
		}

		return data;
	}

	/**
	 * Delete file or directory (recursively) specified by <code>file</code>.
	 * 
	 * @param file
	 *          File or directory to be removed.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws IllegalArgumentException
	 *           If file is unknown thing...
	 */
	private static void deleteRecursive(File file) {
		/* Check input parameters. */
		if (file == null) {
			throw new NullPointerException("directory is null");
		}

		/* Delete (recursively). */
		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			File[] items = file.listFiles();
			for (File item : items) {
				deleteRecursive(item);
			}
			file.delete();
		}
	}

	/**
	 * Write DOM document to XML file.
	 * 
	 * @param document
	 *          DOM document.
	 * @param xmlFile
	 *          XML file (will be created).
	 * @throws NullPointerException
	 *           If some input parameter is <code>null</code>.
	 * @throws RuntimeException
	 *           If some other unexpected error occurred.
	 */
	private static void writeXmlFile(Document document, File xmlFile) {
		/* Check input parameters. */
		if (document == null) {
			throw new NullPointerException("document is null");
		}
		if (xmlFile == null) {
			throw new NullPointerException("xmlFile is null");
		}

		/* Prepare transformer and do transformation. */
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException("Could not create Transformer for XML. " + "This should not occur.", e);
		}
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		try {
			transformer.transform(new DOMSource(document), new StreamResult(xmlFile));
		} catch (TransformerException e) {
			throw new RuntimeException("Unexpected failure", e);
		}
	}

	/**
	 * Add context.
	 * 
	 * @param context
	 *          <code>ContextEntry</code> of added context.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws RuntimeException
	 *           If some other unexpected error occurred.
	 */
	protected synchronized void addContext(ContextEntry context) {
		/* Check input parameters. */
		if (context == null) {
			throw new NullPointerException("context is null");
		}

		/* Prepare directories. */
		File contextsSubDir = new File(this.contextsDir, context.getContextId());
		File tasksSubDir = new File(this.tasksDir, context.getContextId());
		File checkPointsSubDir = new File(this.checkPointsDir, context.getContextId());
		/* Make directories. */
		contextsSubDir.mkdir();
		tasksSubDir.mkdir();
		checkPointsSubDir.mkdir();

		/* Store informations about context... */

		/* Prepare new document. */
		Document document;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Could not create new DOM Document. " + "This should not occur.", e);
		}

		/* Add root element. */
		Element rootElement = document.createElement("contextEntry");
		document.appendChild(rootElement);
		/* Set attributes. */
		rootElement.setAttribute("open", (context.isOpen()) ? "true" : "false");

		/* Add contextId element. */
		Element contextIdElement = document.createElement("contextId");
		rootElement.appendChild(contextIdElement);
		CDATASection contextIdData = document.createCDATASection(context.getContextId());
		contextIdElement.appendChild(contextIdData);

		/* Add contextName element. */
		Element contextNameElement = document.createElement("contextName");
		rootElement.appendChild(contextNameElement);
		CDATASection contextNameData = document.createCDATASection(context.getContextName());
		contextNameElement.appendChild(contextNameData);

		/* Add contextDescription element. */
		Element contextDescriptionElement = document.createElement("contextDescription");
		rootElement.appendChild(contextDescriptionElement);
		CDATASection contextDescriptionData = document.createCDATASection(context.getContextDescription());
		contextDescriptionElement.appendChild(contextDescriptionData);

		/* Write to XML file. */
		writeXmlFile(document, new File(contextsSubDir, "contextEntry.xml"));

		/* Store MagicObject to file. */
		storeObjectToFile(context.getMagicObject(), new File(contextsSubDir, "magicObject"));
	}

	/**
	 * Remove context.
	 * 
	 * @param contextId
	 *          ID of context.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 */
	protected synchronized void remContext(String contextId) {
		/* Check input parameters. */
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}

		/* Prepare directories. */
		File contextsSubDir = new File(this.contextsDir, contextId);
		File tasksSubDir = new File(this.tasksDir, contextId);
		File checkPointsSubDir = new File(this.checkPointsDir, contextId);
		/* Delete directories. */
		deleteRecursive(contextsSubDir);
		deleteRecursive(tasksSubDir);
		deleteRecursive(checkPointsSubDir);
	}

	/**
	 * Change already stored context.
	 * 
	 * @param context
	 *          <code>ContextEntry</code> of changed context.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 */
	protected synchronized void changeContext(ContextEntry context) {
		/* Check input parameters. */
		if (context == null) {
			throw new NullPointerException("context is null");
		}

		/* Prepare directories. */
		File contextsSubDir = new File(this.contextsDir, context.getContextId());
		/* Remove directories. */
		deleteRecursive(contextsSubDir);

		addContext(context);
	}

	/**
	 * Add task.
	 * 
	 * @param task
	 *          <code>TaskEntry</code> of added task.
	 * @param taskData
	 *          <code>TaskData</code> of added task.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws RuntimeException
	 *           If some other unexpected error occurred.
	 */
	protected synchronized void addTask(TaskEntryImplementation task,
			TaskData taskData) {
		/* Check input parameters. */
		if (task == null) {
			throw new NullPointerException("task is null");
		}

		/* Prepare directories. */
		File tasksSubDir = new File(new File(this.tasksDir, task.getContextId()), task.getTaskId());
		File checkPointsSubDir = new File(new File(this.checkPointsDir, task.getContextId()), task.getTaskId());
		/* Make directories. */
		tasksSubDir.mkdir();
		checkPointsSubDir.mkdir();

		/* Store informations about task... */

		/* Prepare new document. */
		Document document;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Could not create new DOM Document. " + "This should not occur.", e);
		}

		/* Add root element. */
		Element rootElement = document.createElement("taskEntry");
		document.appendChild(rootElement);
		/* Set attributes. */
		rootElement.setAttribute("exclusivity", task.getExclusivity().toString());
		rootElement.setAttribute("serviceFlag", (task.getServiceFlag()) ? "true"
				: "false");
		rootElement.setAttribute("state", task.getState().toString());

		/* Add taskId element. */
		Element taskIdElement = document.createElement("taskId");
		rootElement.appendChild(taskIdElement);
		CDATASection taskIdData = document.createCDATASection(task.getTaskId());
		taskIdElement.appendChild(taskIdData);

		/* Add contextId element. */
		Element contextIdElement = document.createElement("contextId");
		rootElement.appendChild(contextIdElement);
		CDATASection contextIdData = document.createCDATASection(task.getContextId());
		contextIdElement.appendChild(contextIdData);

		/* Add packageName element. */
		Element packageNameElement = document.createElement("packageName");
		rootElement.appendChild(packageNameElement);
		CDATASection packageNameData = document.createCDATASection(task.getPackageName());
		packageNameElement.appendChild(packageNameData);

		/* Add taskName element. */
		Element taskNameElement = document.createElement("taskName");
		rootElement.appendChild(taskNameElement);
		CDATASection taskNameData = document.createCDATASection(task.getTaskName());
		taskNameElement.appendChild(taskNameData);

		/* Add taskDescription element. */
		Element taskDescriptionElement = document.createElement("taskDescription");
		rootElement.appendChild(taskDescriptionElement);
		CDATASection taskDescriptionData = document.createCDATASection(task.getTaskDescription());
		taskDescriptionElement.appendChild(taskDescriptionData);

		/* Add hostName element. */
		Element hostNameElement = document.createElement("hostName");
		rootElement.appendChild(hostNameElement);
		if (task.getHostName() != null) {
			CDATASection hostNameData = document.createCDATASection(task.getHostName());
			hostNameElement.appendChild(hostNameData);
		}

		/* Add directoryPathTask element. */
		Element directoryPathTaskElement = document.createElement("directoryPathTask");
		rootElement.appendChild(directoryPathTaskElement);
		CDATASection directoryPathTaskData = document.createCDATASection(task.getDirectoryPathTask());
		directoryPathTaskElement.appendChild(directoryPathTaskData);

		/* Add directoryPathWorking element. */
		Element directoryPathWorkingElement = document.createElement("directoryPathWorking");
		rootElement.appendChild(directoryPathWorkingElement);
		CDATASection directoryPathWorkingData = document.createCDATASection(task.getDirectoryPathWorking());
		directoryPathWorkingElement.appendChild(directoryPathWorkingData);

		/* Add directoryPathTemporary element. */
		Element directoryPathTemporaryElement = document.createElement("directoryPathTemporary");
		rootElement.appendChild(directoryPathTemporaryElement);
		CDATASection directoryPathTemporaryData = document.createCDATASection(task.getDirectoryPathTemporary());
		directoryPathTemporaryElement.appendChild(directoryPathTemporaryData);

		/* Add taskProperties element. */
		Element taskPropertiesElement = document.createElement("taskProperties");
		rootElement.appendChild(taskPropertiesElement);
		/*
		 * Add taskProperty elements (sub-elements of taskProperties element).
		 */
		Properties taskProperties = task.getTaskProperties();
		Enumeration<?> taskPropertiesEnumeration = taskProperties.propertyNames();
		while (taskPropertiesEnumeration.hasMoreElements()) {
			String key = (String) taskPropertiesEnumeration.nextElement();
			String value = (String) taskProperties.get(key);
			Element taskPropertyElement = document.createElement("taskProperty");
			taskPropertiesElement.appendChild(taskPropertyElement);
			taskPropertyElement.setAttribute("key", key);
			taskPropertyElement.setAttribute("value", value);
		}

		/* Add timeSubmitted element. */
		Element timeSubmittedElement = document.createElement("timeSubmitted");
		rootElement.appendChild(timeSubmittedElement);
		CDATASection timeSubmittedData = document.createCDATASection(String.valueOf(task.getTimeSubmitted()));
		timeSubmittedElement.appendChild(timeSubmittedData);

		/* Add timeScheduled element. */
		Element timeScheduledElement = document.createElement("timeScheduled");
		rootElement.appendChild(timeScheduledElement);
		CDATASection timeScheduledData = document.createCDATASection(String.valueOf(task.getTimeScheduled()));
		timeScheduledElement.appendChild(timeScheduledData);

		/* Add timeStarted element. */
		Element timeStartedElement = document.createElement("timeStarted");
		rootElement.appendChild(timeStartedElement);
		CDATASection timeStartedData = document.createCDATASection(String.valueOf(task.getTimeStarted()));
		timeStartedElement.appendChild(timeStartedData);

		/* Add timeFinished element. */
		Element timeFinishedElement = document.createElement("timeFinished");
		rootElement.appendChild(timeFinishedElement);
		CDATASection timeFinishedData = document.createCDATASection(String.valueOf(task.getTimeFinished()));
		timeFinishedElement.appendChild(timeFinishedData);

		/* Add restartCount element. */
		Element restartCountElement = document.createElement("restartCount");
		rootElement.appendChild(restartCountElement);
		CDATASection restartCountData = document.createCDATASection(String.valueOf(task.getRestartCount()));
		restartCountElement.appendChild(restartCountData);

		/* Add restartMax element. */
		Element restartMaxElement = document.createElement("restartMax");
		rootElement.appendChild(restartMaxElement);
		CDATASection restartMaxData = document.createCDATASection(String.valueOf(task.getRestartMax()));
		restartMaxElement.appendChild(restartMaxData);

		/* Add timeoutRun element. */
		Element timeoutRunElement = document.createElement("timeoutRun");
		rootElement.appendChild(timeoutRunElement);
		CDATASection timeoutRunData = document.createCDATASection(String.valueOf(task.getTimeoutRun()));
		timeoutRunElement.appendChild(timeoutRunData);

		/* Write to XML file. */
		writeXmlFile(document, new File(tasksSubDir, "taskEntry.xml"));

		/* Store taskDescriptor to file. */
		if (taskData.getTaskDescriptor() != null) {
			storeObjectToFile(taskData.getTaskDescriptor(), new File(tasksSubDir, "taskDescriptor"));
		}
	}

	/**
	 * Remove task.
	 * 
	 * @param taskId
	 *          ID of task.
	 * @param contextId
	 *          ID of contex.
	 * @throws NullPointerException
	 *           If some input parameter is <code>null</code>.
	 */
	protected synchronized void remTask(String taskId, String contextId) {
		/* Check input parameters. */
		if (taskId == null) {
			throw new NullPointerException("taskId is null");
		}
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}

		/* Prepare directories. */
		File tasksSubDir = new File(new File(this.tasksDir, contextId), taskId);
		File checkPointsSubDir = new File(new File(this.checkPointsDir, contextId), taskId);
		/* Delete directories. */
		deleteRecursive(tasksSubDir);
		deleteRecursive(checkPointsSubDir);
	}

	/**
	 * Change already stored task.
	 * 
	 * @param task
	 *          <code>TaskEntry</code> of changed task.
	 * @param taskData
	 *          <code>TaskData</code> of changed task.
	 * 
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 */
	protected synchronized void changeTask(TaskEntryImplementation task,
			TaskData taskData) {
		/* Check input parameters. */
		if (task == null) {
			throw new NullPointerException("task is null");
		}

		/* Prepare directories. */
		File tasksSubDir = new File(new File(this.tasksDir, task.getContextId()), task.getTaskId());
		/* Remove directories. */
		deleteRecursive(tasksSubDir);

		this.addTask(task, taskData);
	}

	/**
	 * Add checkPoint.
	 * 
	 * @param checkPoint
	 *          <code>CheckPointEntry</code> of added checkPoint.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 */
	protected synchronized void addCheckPoint(CheckPointEntry checkPoint) {
		/* Check input parameters. */
		if (checkPoint == null) {
			throw new NullPointerException("checkPoint is null");
		}

		/* Prepare directories. */
		File checkPointsSubSubDir = new File(new File(new File(this.checkPointsDir, checkPoint.getContextId()), checkPoint.getTaskId()), checkPoint.getName());
		/* Make directories. */
		checkPointsSubSubDir.mkdir();

		/* Store informations about checkPoint... */

		/* Prepare new document. */
		Document document;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Could not create new DOM Document. " + "This should not occur.", e);
		}

		/* Add root element. */
		Element rootElement = document.createElement("checkPointEntry");
		document.appendChild(rootElement);
		/* Set attributes. */
		// ... no attributes

		/* Add name element. */
		Element nameElement = document.createElement("name");
		rootElement.appendChild(nameElement);
		CDATASection nameData = document.createCDATASection(checkPoint.getName());
		nameElement.appendChild(nameData);

		/* Add taskId element. */
		Element taskIdElement = document.createElement("taskId");
		rootElement.appendChild(taskIdElement);
		CDATASection taskIdData = document.createCDATASection(checkPoint.getTaskId());
		taskIdElement.appendChild(taskIdData);

		/* Add contextId element. */
		Element contextIdElement = document.createElement("contextId");
		rootElement.appendChild(contextIdElement);
		CDATASection contextIdData = document.createCDATASection(checkPoint.getContextId());
		contextIdElement.appendChild(contextIdData);

		/* Add hostName element. */
		Element hostNameElement = document.createElement("hostName");
		rootElement.appendChild(hostNameElement);
		CDATASection hostNameData = document.createCDATASection(checkPoint.getHostName());
		hostNameElement.appendChild(hostNameData);

		/* Write to XML file. */
		writeXmlFile(document, new File(checkPointsSubSubDir, "checkPointEntry.xml"));

		/* Store MagicObject to file. */
		storeObjectToFile(checkPoint.getMagicObject(), new File(checkPointsSubSubDir, "magicObject"));
	}

	/**
	 * Remove checkPoint.
	 * 
	 * @param name
	 *          Name of checkPoint.
	 * @param taskId
	 *          ID of task.
	 * @param contextId
	 *          ID of context.
	 * @throws NullPointerException
	 *           If some input parameter is <code>null</code>.
	 */
	protected synchronized void remCheckPoint(String name, String taskId,
			String contextId) {
		/* Check input parameters. */
		if (name == null) {
			throw new NullPointerException("name is null");
		}
		if (taskId == null) {
			throw new NullPointerException("taskId is null");
		}
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}

		/* Prepare directories. */
		File checkPointsSubSubDir = new File(new File(new File(this.checkPointsDir, contextId), taskId), name);
		/* Delete directories. */
		deleteRecursive(checkPointsSubSubDir);
	}

	/**
	 * Add hostRuntime.
	 * 
	 * @param hostRuntime
	 *          <code>HostRuntimeEntry</code> of added hostRuntime.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 * @throws RuntimeException
	 *           If some other unexpected error occurred.
	 */
	protected synchronized void addHostRuntime(HostRuntimeEntry hostRuntime) {
		/* Check input parameters. */
		if (hostRuntime == null) {
			throw new NullPointerException("hostRuntime is null");
		}

		/* Prepare directories. */
		File hostRuntimesSubDir = new File(this.hostRuntimesDir, hostRuntime.getHostName());
		/* Make directories. */
		hostRuntimesSubDir.mkdir();

		/* Store informations about hostRuntime... */

		/* Prepare new document. */
		Document document;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Could not create new DOM Document. " + "This should not occur.", e);
		}

		/* Add root element. */
		Element rootElement = document.createElement("hostRuntimeEntry");
		document.appendChild(rootElement);
		/* Set attributes. */
		// ... no attributes

		/* Add hostName element. */
		Element hostNameElement = document.createElement("hostName");
		rootElement.appendChild(hostNameElement);
		CDATASection hostNameData = document.createCDATASection(hostRuntime.getHostName());
		hostNameElement.appendChild(hostNameData);

		/* Add reservation element. */
		Element reservationElement = document.createElement("reservation");
		rootElement.appendChild(reservationElement);
		if (hostRuntime.getReservation() != null) {
			CDATASection reservationData = document.createCDATASection(hostRuntime.getReservation());
			reservationElement.appendChild(reservationData);
		}

		/* Write to XML file. */
		writeXmlFile(document, new File(hostRuntimesSubDir, "hostRuntimeEntry.xml"));
	}

	/**
	 * Remove hostRuntime.
	 * 
	 * @param hostName
	 *          Name of host.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 */
	protected synchronized void remHostRuntime(String hostName) {
		/* Check input parameters. */
		if (hostName == null) {
			throw new NullPointerException("hostName is null");
		}

		/* Prepare directories. */
		File hostRuntimesSubDir = new File(this.hostRuntimesDir, hostName);
		/* Delete directories. */
		deleteRecursive(hostRuntimesSubDir);
	}

	/**
	 * Change already stored hostRuntime.
	 * 
	 * @param hostRuntime
	 *          <code>HostRuntimeEntry</code> of changed hostRuntime.
	 * @throws NullPointerException
	 *           If input parameter is <code>null</code>.
	 */
	protected synchronized void changeHostRuntime(HostRuntimeEntry hostRuntime) {
		/* Check input parameters. */
		if (hostRuntime == null) {
			throw new NullPointerException("hostRuntime is null");
		}

		/* Prepare directories. */
		File hostRuntimesSubDir = new File(this.hostRuntimesDir, hostRuntime.getHostName());
		/* Remove directories. */
		deleteRecursive(hostRuntimesSubDir);

		addHostRuntime(hostRuntime);
	}
}
