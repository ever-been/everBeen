/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Antonin Tomecek
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
package cz.cuni.mff.been.taskmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import cz.cuni.mff.been.common.rsl.AndCondition;
import cz.cuni.mff.been.common.rsl.Condition;
import cz.cuni.mff.been.common.rsl.EqualsCondition;
import cz.cuni.mff.been.jaxb.td.TaskExclusivity;
import cz.cuni.mff.been.softwarerepository.PackageType;
import cz.cuni.mff.been.task.Task;

/**
 * Class representation of the XML task descriptor with methods for
 * manipulation.
 * 
 * @author Antonin Tomecek
 */
@Deprecated
public class DeprecatedTaskDescriptor implements Serializable {

	private static final long serialVersionUID = 2839578401666122477L;

	/**
	 * Boot tasks that may be started via createBootTask() method
	 */
	public enum BootTask {
		BENCHMARK_MANAGER("benchmarkmanager", "1.0"),
		BENCHMARK_MANAGER_NG("benchmarkmanagerng", "1.0"),
		HOST_MANAGER("hostmanager", "1.0"),
		SOFTWARE_REPOSITORY("softwarerepository", "1.0"),
		RESULTS_REPOSITORY("resultsrepository", "1.0"),
		RESULTS_REPOSITORYNG("resultsrepositoryng", "1.0"),
		TEST_MANAGER("testmanager", "1.0"),
		DETECTOR_TASK("detectortask", "1.0"),
		COMMAND_LINE_INTERFACE("clinterface", "1.0");

		private final String serviceName;
		
		private final String version;

		private BootTask(String serviceName, String version) {
			this.serviceName = serviceName;
			this.version = version;
		}

		/**
		 * Package name getter.
		 * 
		 * @return BEEN package name of given boot task
		 */
		public String getName() {
			return serviceName;
		}
		
		/**
		 * Version string getter.
		 * 
		 * @return Version of package which should be used for this BootTask. 
		 */
		public String getVersion() {
			return version;
		}
	}

	@Deprecated
	private static final String DEFAULT_PATH_PREFIX = "/legacy/";

	/* Log created by validate() method. */
	private String validateLog = "";

	/* Task ID [#REQUIRED] */
	private String taskId = null;

	/* Context ID [#REQUIRED] */
	private String contextId = null;

	/* Human readable name of task. */
	private String taskName = "";

	/* Human readable description of task. */
	private String taskDescription = "";

	/* Array of command-line arguments [#IMPLIED] */
	private String[] taskArguments = new String[0];

	/* Array of command-line options for JVM (for running new task)
	 * [#IMPLIED] */
	private String[] javaOptions = new String[0];

	/* Flag telling that this task should be exclusive. */
	private TaskExclusivity taskExclusivity = TaskExclusivity.NON_EXCLUSIVE;

	/* Special property pairs (key, value) for tasks. */
	private Properties taskProperties = new Properties();

	/** Address of the task in the visual tree of tasks. */
	private String treeAddress;

	/**
	 * Class used for storing definition of special task property with object
	 * as value. 
	 */
	public static class TaskPropertyObject implements Serializable {

		private static final long	serialVersionUID	= -8083118215631216295L;

		/* Key of property. */
		private String key;

		/* Value of property. */
		private Serializable value;

		/** Constructor of this class.
		 * 
		 * @param key Key of this property.
		 * @param value Value of this property.
		 */
		public TaskPropertyObject(String key, Serializable value) {
			this.setKey(key);
			this.setValue(value);
		}

		/**
		 * Sets key of this property.
		 * 
		 * @param key New key of this property.
		 */
		public void setKey(String key) {
			this.key = key;
		}

		/**
		 * Sets value of this property
		 * 
		 * @param value New value of this property.
		 */
		public void setValue(Serializable value) {
			this.value = value;
		}

		/**
		 * Returns key of this property.
		 * 
		 * @return Key of this property.
		 */
		public String getKey() {
			return this.key;
		}

		/**
		 * Returnts value of this property.
		 * 
		 * @return Value of this property.
		 */
		public Serializable getValue() {
			return this.value;
		}
	}

	/* List of task properties with object as value. */
	private ArrayList<TaskPropertyObject> taskPropertyObjectList
	= new ArrayList<TaskPropertyObject>();

	/* Name of BEEN package to use for this task. */
	private String packageName = null;

	/* RSL description of BEEN package to use for this task. */
	private Condition packageRsl = null;

	/* If set, use the same Host Runtime as used for another task. */
	private String hostRuntimesAsTask = null;

	/* Hostnames of possible Host Runtime to use for running this task. */
	private String[] hostRuntimesName = null;

	/* RSL description of Host Runtime for running of this task. */
	private Condition hostRuntimesRsl = null;

	/* List of dependencies based on check points. */
	private Dependency[] dependencyCheckPoints = new Dependency[0];

	/* How many restarts for this task are allowed. */
	private int restartMax = 0;

	/* How long (in milliseconds) this task can run (from started to finished
	 * state). Zero if not restricted.
	 */
	private long timeoutRun = 0;

	/* Flag indicating if the task want to enable detailed load monitoring. */
	private boolean detailedLoad = false;

	/* How often do detailed load monitoring (in milliseconds). Value 0 means "use default". */
	private long detailedLoadInterval = 0;

	/**
	 * Check if this taskDescriptor is valid.
	 * 
	 * @return <code>true</code> if valid, <code>false</code> otherwise.
	 */
	public boolean validate() {
		boolean valid = true;

		if (this.taskId == null) {
			this.validateLog = this.validateLog.concat("taskId is not set\n");
			valid = false;
		}

		if (this.contextId == null) {
			this.validateLog = this.validateLog.concat("contextId is not "
				+ "set\n");
			valid = false;
		}

		if ((this.packageName == null) && (this.packageRsl == null)) {
			this.validateLog = this.validateLog.concat("neither packageName "
				+ "and packageRsl is not set\n");
			valid = false;
		}

		if ((this.hostRuntimesAsTask == null)
			&& (this.hostRuntimesName == null)
			&& (this.hostRuntimesRsl == null)) {
			this.validateLog = this.validateLog.concat("neither "
				+ "hostRuntimesName and hostRuntimesRsl is not set\n");
			valid = false;
		}

		/* Check values set in checkPoints. */
		for (Dependency dependencyCheckPoint : this.dependencyCheckPoints) {
			if ((dependencyCheckPoint.getTaskId() == null)
				|| (dependencyCheckPoint.getType() == null)) {
				this.validateLog = this.validateLog.concat(
					"DependencyCheckpoint with entered values (taskId \""
					+ dependencyCheckPoint.getTaskId() + "\", type \""
					+ dependencyCheckPoint.getType() + "\") is not "
					+ "allowed");
				valid = false;
			}
		}

		if ( this.treeAddress == null ) {
			this.validateLog += "Tree address not specified.\n";
			valid = false;
		}

		return valid;
	}

	/**
	 * Return log from last calling of method validate(). The validate() method
	 * should be called before.
	 * 
	 * @return Log from last calling of method validate().
	 */
	public String validateGetLog() {
		return this.validateLog;
	}

	/**
	 * Sets value of the "taskId" attribute of the "taskDescriptor" element.
	 * 
	 * @param taskId ID of task.
	 * @throws NullPointerException If input parameter is <code>null</code>.
	 */
	public void setTaskId(String taskId) {
		if (taskId == null) {
			throw new NullPointerException("taskId is null");
		}

		this.taskId = taskId;
	}

	/**
	 * Sets value of the "name" attribute of the "taskDescriptor" element.
	 * 
	 * @param name Human readable name.
	 * @throws NullPointerException If input parameter is <code>null</code>.
	 */
	public void setTaskName(String name) {
		if (name == null) {
			throw new NullPointerException("name is null");
		}

		this.taskName = name;
	}

	/**
	 * Sets value of the "description" attribute of the "taskDescriptor"
	 * element.
	 * 
	 * @param description Human readable description.
	 * @throws NullPointerException If input parameter is <code>null</code>.
	 */
	public void setTaskDescription(String description) {
		if (description == null) {
			throw new NullPointerException("description is null");
		}

		this.taskDescription = description;
	}

	/**
	 * Sets value of the "contextId" attribute of the "taskDescriptor" element.
	 * 
	 * @param contextId ID of context.
	 * @throws NullPointerException If input parameter is <code>null</code>.
	 */
	public void setContextId(String contextId) {
		if (contextId == null) {
			throw new NullPointerException("contextId is null");
		}

		this.contextId = contextId;
	}

	/**
	 * Adds one argument to the end of currently specified command-line
	 * arguments (i.e. "arguments" attribute of the "taskDescriptor"
	 * element).
	 * 
	 * @param argument Argument to add.
	 */
	public void addArguments(String argument) {
		ArrayList<String> currentArguments;

		currentArguments = new ArrayList<String>(
			Arrays.asList(this.taskArguments));
		currentArguments.add(argument);

		this.taskArguments = currentArguments.toArray(
			new String[currentArguments.size()]);
	}

	/**
	 * Sets value of the "arguments" attribute of the "taskDescriptor" element.
	 * 
	 * @param arguments A space-delimited list of command-line arguments
	 * 	or null for none.
	 * @param append If true then specifiead arguments are added to existing list
	 *            else it is overwriten.
	 */
	public void setArguments(String arguments, boolean append) {
		if (!append) {  /* delete current arguments */
			this.taskArguments = new String[0];
		}

		int beginPosition = 0; /* Begin of the processed argument */
		int endPosition = 0; /* End of the processed argument */

		if (arguments == null) {
			return;
		}

		for (;; ) {
			endPosition = arguments.indexOf(' ', beginPosition);
			if (endPosition == beginPosition) {
				/* Character on beginPosition is a space */
				// do nothing
			} else if (endPosition == -1) {
				/* Only one argument remains in arguments string */
				this.addArguments(arguments.substring(
					beginPosition));

				break;  // end of string reached
			} else {
				this.addArguments(arguments.substring(
					beginPosition, endPosition));
			}

			beginPosition = endPosition + 1;
		}
	}

	/**
	 * Adds one option to the end of currently specified list of java options.
	 * 
	 * @param option
	 *            Java option to add.
	 */
	public void addJavaOption(String option) {
		ArrayList<String> currentJavaOptions;

		currentJavaOptions = new ArrayList<String>(Arrays
			.asList(this.javaOptions));
		currentJavaOptions.add(option);

		this.javaOptions = currentJavaOptions
		.toArray(new String[currentJavaOptions.size()]);
	}

	/**
	 * Sets value of the "options" attribute of the "java" element.
	 * 
	 * @param options Command-line options for JVM.
	 * @param append If true then specified options are added to existing
	 * 	list else it is overwriten.
	 */
	public void setJavaOptions(String options, boolean append) {
		if (!append) { /* delete current options */
			this.javaOptions = new String[0];
		}

		int beginPosition = 0; /* Begin of the processed option */
		int endPosition = 0; /* End of the processed option */

		if (options == null) {
			return;
		}

		for (;; ) {
			endPosition = options.indexOf(' ', beginPosition);
			if (endPosition == beginPosition) {
				/* Character on beginPosition is a space */
				// do nothing
			} else if (endPosition == -1) {
				/* Only one option remains in options string */
				this.addJavaOption(options.substring(
					beginPosition));

				break;  // end of string reached
			} else {
				this.addJavaOption(options.substring(
					beginPosition, endPosition));
			}

			beginPosition = endPosition + 1;
		}
	}

	/**
	 * Tree address setter.
	 * 
	 * @param treeAddress String representation of the tree address of the new task.
	 */
	public void setTreeAddress( String treeAddress ) {
		if (null == treeAddress) {
			throw new NullPointerException("treeAddress is null");
		}
		this.treeAddress = treeAddress;
	}

	/**
	 * Sets task's exclusivity flag.
	 * 
	 * @param exclusivity Value of task's exclusivity flag.
	 */
	public void setTaskExclusive(TaskExclusivity exclusivity) {
		this.taskExclusivity = exclusivity;
	}

	/**
	 * Adds one property (key-value pair) for this task.
	 * 
	 * @param key Key of new property.
	 * @param value Value of new property.
	 */
	public void addTaskProperties(String key, String value) {
		this.taskProperties.setProperty(key, value);
	}

	/**
	 * Sets key-value pairs of task's properties.
	 * 
	 * @param properties New definitions of properties.
	 */
	public void setTaskProperties(Properties properties) {
		this.taskProperties = properties;
	}

	/**
	 * Adds one property (with object as value) for this task.
	 *
	 * @param key Key of new property.
	 * @param value Value of new property.
	 */
	public void addTaskPropertyObject(String key, Serializable value) {
		TaskPropertyObject property = new TaskPropertyObject(key, value);

		this.taskPropertyObjectList.add(property);
	}

	/**
	 * Sets content of "name" element within "package" element.
	 * 
	 * @param name Name of package to use for this task.
	 * @throws NullPointerException If input parameter is <code>null</code>
	 * @throws IllegalArgumentException If input parameter is empty string.
	 */
	public void setPackageName(String name) {
		if (name == null) {
			throw new NullPointerException("name is null");
		} else if (name.length() == 0) {
			throw new IllegalArgumentException("name is empty string");
		}

		this.packageName = name;
	}

	/**
	 * Sets content of "rsl" element within "package" element.
	 * 
	 * @param rsl RSL description of package.
	 * @throws NullPointerException If input parameter is <code>null</code>
	 * @throws IllegalArgumentException If input parameter is empty string.
	 */
	public void setPackageRsl(Condition rsl) {
		if (rsl == null) {
			throw new NullPointerException("rsl is null");
		}

		this.packageRsl = rsl;
	}

	/**
	 * Adds one name to list of usable host for running of this task. (I.e.
	 * content of "name" element within "hostRuntimes" element.)
	 * 
	 * @param name Name of host for running of this task.
	 */
	protected void addHostRuntimesName(String name) {
		ArrayList<String> currentHostRuntimesName;

		if (this.hostRuntimesName == null) {
			currentHostRuntimesName = new ArrayList<String>();
		} else {
			currentHostRuntimesName = new ArrayList<String>(
				Arrays.asList(this.hostRuntimesName));
		}

		currentHostRuntimesName.add(name);

		this.hostRuntimesName = currentHostRuntimesName
		.toArray(new String[currentHostRuntimesName.size()]);
	}

	/**
	 * Sets hostRuntime in way: "use the same hostRuntime as task with ID
	 * <code>taskId</code>".
	 * It also adds START-dependency on referenced task to avoid starting
	 * this task before the referenced task.
	 * 
	 * @param taskId ID of task determining hostRuntime.
	 */
	public void setHostRuntimesAsTask(String taskId) {
		addDependencyCheckPoint(new Dependency(taskId,Task.CHECKPOINT_NAME_STARTED));
		this.hostRuntimesAsTask = taskId;
	}

	/**
	 * Sets content of "name" element within "hostRuntimes" element.
	 * (Owerwrites all previously specified names.)
	 * 
	 * @param names Names of hosts usable for running of this task.
	 */
	public void setHostRuntimesName(String[] names) {
		this.hostRuntimesName = names;
	}

	/**
	 * Sets content of "rsl" element within "hostRuntimes" element.
	 * 
	 * @param rsl RSL condition for host.
	 */
	public void setHostRuntimesRsl(Condition rsl) {
		if (rsl == null) {
			throw new NullPointerException("rsl is null");
		}

		this.hostRuntimesRsl = rsl;
	}

	/**
	 * Adds one dependency based on the check point.
	 * 
	 * @param dependencyCheckPoint Dependency check point to add.
	 */
	public void addDependencyCheckPoint(Dependency dependencyCheckPoint) {
		ArrayList<Dependency> currentDependencyCheckPoint;

		currentDependencyCheckPoint = new ArrayList<Dependency>(Arrays
			.asList(this.dependencyCheckPoints));

		Dependency newDependencyCheckPoint = 
			new Dependency(dependencyCheckPoint.getTaskId(),
				dependencyCheckPoint.getType(),
				dependencyCheckPoint.getValue());

		currentDependencyCheckPoint.add(newDependencyCheckPoint);

		this.dependencyCheckPoints = currentDependencyCheckPoint
		.toArray(new Dependency[currentDependencyCheckPoint.size()]);
	}

	/**
	 * Sets values of all attributes of all dependencies based on check points.
	 *  
	 * @param dependencyCheckPoints Array with definitions of
	 * 	DependencyCheckPoints.
	 */
	public void setDependencyCheckPoints(Dependency[] dependencyCheckPoints) {
		if (dependencyCheckPoints == null) {
			/* Delete current list of DependencyCheckPoints. */
			this.dependencyCheckPoints = new Dependency[0];
		} else {
			this.dependencyCheckPoints = dependencyCheckPoints;
		}
	}
	
	/**
	 * Returns the address of this task in the visual task tree.
	 * 
	 * @return A path to the tree node that should represent this task.
	 */
	public String getTreeAddress() {
		return treeAddress;
	}

	/**
	 * Returns value of the "taskId" attribute of the "taskDescriptor" element.
	 * 
	 * @return Value of the "taskId" attribute.
	 */
	public String getTaskId() {
		return this.taskId;
	}

	/**
	 * Returns value of the "name" attribute of the "taskDescriptor" element.
	 * 
	 * @return Human readable name.
	 */
	public String getTaskName() {
		return this.taskName;
	}

	/**
	 * Returns value of the "description" attribute of the "taskDescriptor"
	 * element.
	 * 
	 * @return Human readable value.
	 */
	public String getTaskDescription() {
		return this.taskDescription;
	}

	/**
	 * Returns value of the "contextId" attribute of the "taskDescriptor"
	 * element.
	 * 
	 * @return Value of the "contextId" attribute.
	 */
	public String getContextId() {
		return this.contextId;
	}

	/**
	 * Returns command-line arguments from the "arguments" attribute of the
	 * "taskDescriptor" element.
	 * 
	 * @return Array corresponding to the "arguments" attribute.
	 */
	public String[] getTaskArguments() {
		return this.taskArguments;
	}

	/**
	 * Returns value of the "javaOptions" attribute of the "java" element.
	 * 
	 * @return Array corresponding to the "javaOptions" attribute.
	 */
	public String[] getJavaOptions() {
		return this.javaOptions;
	}

	/**
	 * Returns state of task exclusive flag (i.e. "exclusive" attribute of
	 * "task" element).
	 * 
	 * @return The task exclusive flag.
	 */
	public TaskExclusivity getTaskExclusive() {
		return this.taskExclusivity;
	}

	/**
	 * Returns definition of the "taskProperties".
	 * 
	 * @return Properties defined for this task.
	 */
	public Properties getTaskProperties() {
		return (Properties) this.taskProperties.clone();
	}

	/**
	 * Returns array of all defined taskPropertyObjects.
	 * 
	 * @return Array of taskPropertyObjects.
	 */
	public TaskPropertyObject[] getTaskPropertyObjects() {
		return taskPropertyObjectList.toArray(new TaskPropertyObject[taskPropertyObjectList.size()]);
	}

	/**
	 * Returns content of "name" element within "package" element.
	 * 
	 * @return Content of "name" element within "package" element (may be
	 * 	<code>null</code> if name is not evaluated from RSL yet or no matching
	 * 	was founded).
	 */
	public String getPackageName() {
		return this.packageName;
	}

	/**
	 * Returns content of "rsl" element within "package" element.
	 * 
	 * @return Content of "rsl" element within "package" element.
	 */
	public Condition getPackageRsl() {
		return this.packageRsl;
	}

	/**
	 * Gets hostRuntime in way: "use the same hostRuntime as task with ID
	 * <code>taskId</code>" (if it was specified).
	 * 
	 * @return ID of task determining hostRuntime.
	 */
	public String getHostRuntimesAsTask() {
		return this.hostRuntimesAsTask;
	}

	/**
	 * Returns contents of "name" elements within "hostRuntimes" element.
	 * 
	 * @return Contents of "name" elements within "hostRuntimes" element (may
	 * 	be <code>null</code> if names are not evaluated from RSL yet or no
	 * 	matching were founded).
	 */
	public String[] getHostRuntimesName() {
		return this.hostRuntimesName;
	}

	/**
	 * Returns content of "rsl" element within "hostRuntimes" element.
	 * 
	 * @return Content of "rsl" elements within "histRuntimes" element.
	 */
	public Condition getHostRuntimesRsl() {
		return this.hostRuntimesRsl;
	}

	/**
	 * Returns definitions of all DependencyCheckPoints.
	 * 
	 * @return Array of objects representing DependencyCheckPoint.
	 */
	public Dependency[] getDependencyCheckPoints() {
		Dependency[] dependencyCheckPoints = new Dependency[this.dependencyCheckPoints.length];

		for (int i = 0; i < this.dependencyCheckPoints.length; i++) {
			dependencyCheckPoints[i] = (Dependency) this.dependencyCheckPoints[i]
			                                                                   .clone();
		}

		return dependencyCheckPoints;
	}

	/** @return how many restarts for this task are allowed */
	public int getRestartMax() {
		return restartMax;
	}

	/**
	 * Sets how many restarts for this task are allowed.
	 * 
	 * @param restartMax number of allowed restarts
	 */
	public void setRestartMax(int restartMax) {
		this.restartMax = restartMax;
	}

	/**
	 * @return how long (in milliseconds) this task can run (from started to
	 * finished state). Zero if not restricted
	 */
	public long getTimeoutRun() {
		return timeoutRun;
	}

	/**
	 * Sets how long (in milliseconds) this task can run (from started to finished
	 * state).
	 * 
	 * @param timeoutRun how long (in milliseconds) this task can run (from
	 *         started to finished state); zero if not restricted
	 */
	public void setTimeoutRun(long timeoutRun) {
		this.timeoutRun = timeoutRun;
	}

	/**
	 * @return flag indicating if the task want to enable detailed load
	 *          monitoring
	 */
	public boolean getDetailedLoad() {
		return detailedLoad;
	}

	/**
	 * Sets the flag indicating if the task want to enable detailed load
	 * monitoring.
	 * 
	 * @param detailedLoad flag indicating if the task want to enable detailed
	 *         load monitoring
	 */
	public void setDetailedLoad(boolean detailedLoad) {
		this.detailedLoad = detailedLoad;
	}

	/**
	 * Return how often do detailed load monitoring (in milliseconds). Value 0
	 * means "use default".
	 * 
	 * @return How often do detailed load monitoring (in milliseconds). Value 0
	 *          means "use default".
	 */
	public long getDetailedLoadInterval() {
		return this.detailedLoadInterval;
	}

	/**
	 * Set how often do detailed load monitoring (in milliseconds). Value 0 means
	 * "use default".
	 * 
	 * @param detailedLoadInterval How often do detailed load monitoring (in
	 * 	milliseconds). Value 0 means "use default".
	 */
	public void setDetailedLoadInterval(long detailedLoadInterval) {
		this.detailedLoadInterval = detailedLoadInterval;
	}

	/**
	 * Constructor required by Serializable.
	 */
	public DeprecatedTaskDescriptor() {

	}

	/**
	 * Create new TaskDescriptor.
	 * 
	 * @param taskId Identification of the task.
	 * @param contextId ID of the context task should run in.
	 * @param packageRsl RSL condition for package.
	 * @param hostRuntimesRsl RSL condition for hostRuntimes.
	 * @param treeAddress Address of the task in the visual tree.
	 */
	private DeprecatedTaskDescriptor(
		String taskId,
		String contextId,
		Condition packageRsl,
		Condition hostRuntimesRsl,
		String treeAddress
	) {
		setTaskId(taskId);
		setContextId(contextId);
		setPackageRsl(packageRsl);
		setHostRuntimesRsl(hostRuntimesRsl);
		setTreeAddress( treeAddress );
	}

/*	TODO NEVER USED!
 	/**
	 * Create new TaskDescriptor.
	 * 
	 * @param taskId Identification of the task.
	 * @param contextId ID of the context task should run in.
	 * @param packageRsl RSL condition for package.
	 * @param hostTaskId ID of task determining hostRuntime.
	 * @param treeAddress Address of the task in the visual tree.
	 */
/*	private TaskDescriptor(
		String taskId,
		String contextId,
		Condition packageRsl,
		String hostTaskId,
		String treeAddress
	) {
		setTaskId(taskId);
		setContextId(contextId);
		setPackageRsl(packageRsl);
		setHostRuntimesAsTask(hostTaskId);
		this.treeAddress = treeAddress;
	}
*/

	/**
	 * Creates task descriptor for BEEN service.
	 * 
	 * @param beenService	String identification of BEEN service
	 * @param host	Host on which the service should run
	 * @param treeAddress Address of the task in the visual tree.
	 * 
	 */
	private DeprecatedTaskDescriptor( BootTask beenService, String host, String treeAddress ) {
		setTaskId(beenService.getName() + "-tid");
		setTaskName(beenService.getName());
		setContextId(TaskManagerInterface.SYSTEM_CONTEXT_ID);

		// must be set, otherwise package name resolution would be done with
		// SoftwareRepository
		// but when SoftwareRepository is being started, there is no one to
		// resolve package name ;)
		setPackageName(beenService.getName() + "-" + beenService.getVersion() + ".bpk");

		// must be set, otherwise host name resolution would be done with
		// HostManager
		// but when HostManager is being sarted, there is no one to resolve host
		// name
		setHostRuntimesName(new String[] { host });
		setTreeAddress( treeAddress );
	}

	/**
	 * Brief implementation of toString(), used for debugging purposes
	 */
	@Override
	public String toString() {
		return "(" + contextId + ","  + taskId + "," + taskName + ")";
	}

	/**
	 * Factory method for creating BEEN boot tasks. Boot packages are placed
	 * in HostRuntime's boot directory.
	 * 
	 * As among boot tasks are also HostManager and SoftwareManager i.e. BEEN
	 * services that expand RSL for regular tasks, boot tasks don't use
	 * RSL in their tasks descriptors neither for hosts not for package
	 * specification. Instead directly package name and host name will be
	 * specified in the task descriptor
	 * 
	 * @param bootTask	name of boot tasks to start
	 * @param host		host name on which the boot task should run
	 * @param treeAddress Address of the task in the visual tree.
	 * 
	 */
	public static DeprecatedTaskDescriptor createBootTask(
		String bootTask,
		String host,
		String treeAddress
	) {
		for ( BootTask service : BootTask.values() ) {
			if ( bootTask.equals( service.getName() ) ) {
				return new DeprecatedTaskDescriptor( service, host, treeAddress );
			}
		}

		throw new IllegalArgumentException( "Not a boot task: '"	+ bootTask + "' " );
	}

	/**
	 * Creates task descriptor for a detector task
	 * Can't be done via {@link #createBootTask(String, String)} call because we
	 * need detetctor task identifier to determine task package, but also unqiue
	 * task identifier so that it every time in separate directory 
	 * 
	 * @param name 	unique name of detector task
	 * @param host	host on which the task should run
	 * @param treeAddress Address of the task in the visual tree.
	 * @return	task descriptor for the detector task
	 */
	public static DeprecatedTaskDescriptor createDetector( String name, String host, String treeAddress ) {
		final DeprecatedTaskDescriptor desc = 
			createBootTask( BootTask.DETECTOR_TASK.getName(), host, treeAddress );
		desc.setTaskId( name );
		return desc;
	}

	/**
	 * Utility method for creating tasks for regular tasks.
	 * The method creates TaskDescriptor using RSL for matching hosts and 
	 * task package. Task package match is based on task name.
	 * 
	 * @param taskID	identifier of a task to created
	 * @param taskName	name of task to create
	 * @param context	identifier of a context to create
	 * @param hostRSL	RSL to match target hosts
	 * @param treeAddress Address of the task in the visual tree.
	 * @return	corresponding TaskDescriptor for the task
	 */
	public static DeprecatedTaskDescriptor createTask(
		String taskID,
		String taskName,
		String context,
		Condition hostRSL,
		String treeAddress
	) {
		AndCondition packageCondition = new AndCondition(
			new Condition[] {
				new EqualsCondition<PackageType>("type", PackageType.TASK),
				new EqualsCondition<String>("name", taskName)
			}
		);

		DeprecatedTaskDescriptor desc = new DeprecatedTaskDescriptor(
			taskID.toString(),
			context,
			packageCondition,
			hostRSL,
			treeAddress
		);

		desc.setTaskName(taskName);

		return desc;
	}
	
/* 
 * TODO LEGACY constructors follow. All of them should be removed as the rest of the code gets
 * updated.
 */
 
	/**
	 * Creates task descriptor for a detector task
	 * Can't be done via {@link #createBootTask(String, String)} call because we
	 * need detetctor task identifier to determine task package, but also unqiue
	 * task identifier so that it every time in separate directory 
	 * 
	 * @param name 	unique name of detector task
	 * @param host	host on which the task should run
	 * @return	task descriptor for the detector task
	 */
	@Deprecated
	public static DeprecatedTaskDescriptor createDetector( String name, String host ) {
		final DeprecatedTaskDescriptor desc = createBootTask(
			BootTask.DETECTOR_TASK.getName(), host, DEFAULT_PATH_PREFIX + host + '/' + name	
		);
		desc.setTaskId( name );
		System.err.println( "FIXME: Deprecated constructor used." );
		return desc;
	}
 
	/**
	 * Utility method for creating tasks for regular tasks.
	 * The method creates TaskDescriptor using RSL for matching hosts and 
	 * task package. Task package match is based on task name.
	 * 
	 * @param taskID	identifier of a task to created
	 * @param taskName	name of task to create
	 * @param context	identifier of a context to create
	 * @param hostRSL	RSL to match target hosts
	 * @return	corresponding TaskDescriptor for the task
	 */
	@Deprecated
	public static DeprecatedTaskDescriptor createTask(
		String taskID,
		String taskName,
		String context,
		Condition hostRSL
	) {
		AndCondition packageCondition = new AndCondition(
			new Condition[] {
				new EqualsCondition<PackageType>("type", PackageType.TASK),
				new EqualsCondition<String>("name", taskName)
			}
		);

		DeprecatedTaskDescriptor desc = new DeprecatedTaskDescriptor(
			taskID.toString(),
			context,
			packageCondition,
			hostRSL,
			DEFAULT_PATH_PREFIX + taskName + '/' + taskID
		);

		desc.setTaskName(taskName);

		return desc;
	}
	
	/**
	 * Factory method for creating BEEN boot tasks. Boot packages are placed
	 * in HostRuntime's boot directory.
	 * 
	 * As among boot tasks are also HostManager and SoftwareManager i.e. BEEN
	 * services that expand RSL for regular tasks, boot tasks don't use
	 * RSL in their tasks descriptors neither for hosts not for package
	 * specification. Instead directly package name and host name will be
	 * specified in the task descriptor
	 * 
	 * @param bootTask	name of boot tasks to start
	 * @param host		host name on which the boot task should run
	 * 
	 * @see BootTask
	 */
	@Deprecated
	public static DeprecatedTaskDescriptor createBootTask(
		String bootTask,
		String host
	) {
		for ( BootTask service : BootTask.values() ) {
			if ( bootTask.equals( service.getName() ) ) {
				return new DeprecatedTaskDescriptor( service, host, DEFAULT_PATH_PREFIX + service.getName() );
			}
		}

		throw new IllegalArgumentException( "Not a boot task: '"	+ bootTask + "' " );
	}

}
