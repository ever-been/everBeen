/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: Andrej Podzimek
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

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import cz.cuni.mff.been.common.rsl.AndCondition;
import cz.cuni.mff.been.common.rsl.Condition;
import cz.cuni.mff.been.common.rsl.EqualsCondition;
import cz.cuni.mff.been.common.serialize.Deserialize;
import cz.cuni.mff.been.common.serialize.DeserializeException;
import cz.cuni.mff.been.common.serialize.Serialize;
import cz.cuni.mff.been.jaxb.Factory;
import cz.cuni.mff.been.jaxb.td.Arguments;
import cz.cuni.mff.been.jaxb.td.Dependencies;
import cz.cuni.mff.been.jaxb.td.DependencyCheckPoint;
import cz.cuni.mff.been.jaxb.td.FailurePolicy;
import cz.cuni.mff.been.jaxb.td.HostRuntimes;
import cz.cuni.mff.been.jaxb.td.Java;
import cz.cuni.mff.been.jaxb.td.JavaOptions;
import cz.cuni.mff.been.jaxb.td.LoadMonitoring;
import cz.cuni.mff.been.jaxb.td.Package;
import cz.cuni.mff.been.jaxb.td.StrVal;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.jaxb.td.TaskProperties;
import cz.cuni.mff.been.jaxb.td.TaskProperty;
import cz.cuni.mff.been.jaxb.td.TaskPropertyObject;
import cz.cuni.mff.been.jaxb.td.TaskPropertyObjects;
import cz.cuni.mff.been.softwarerepository.PackageType;

/**
 * This static class creates common task descriptor types and validates task descriptors.
 * 
 * @author Andrej Podzimek
 */
public final class TaskDescriptorHelper {
	
	/**
	 * Boot tasks that may be started via createBootTask() method
	 */
	public enum BootTask {
		
		/** WillBeen's new Benchmark Manager. */
		BENCHMARK_MANAGER_NG( "benchmarkmanagerng"),
		
		/** The original Host Manager. */
		HOST_MANAGER( "hostmanager"),
		
		/** The original Host Manager. */
		DEBUG_ASSISTANT( "debugassistant"),
		
		/** The original Software Repository. */
		SOFTWARE_REPOSITORY( "softwarerepository"),
		
		/** WillBeen's new Results Repository. */
		RESULTS_REPOSITORY_NG( "resultsrepositoryng"),
		
		/** Hardware detector, uses JNI. */
		DETECTOR_TASK( "detectortask"),
		
		/** WillBeen's new Command Line Interface. */
		COMMAND_LINE_INTERFACE( "clinterface");

		/** Maps task names to enum values. */
		private static final TreeMap< String, BootTask > bootTaskMap;
		
		static {
			bootTaskMap = new TreeMap< String, BootTask >();
			
			for ( BootTask value : BootTask.values() ) {
				bootTaskMap.put( value.getName(), value );
			}
		}
		
		/** Name of the service. */
		private final String name;
		
		/**
		 * Initializes the enum member with a name and a version.
		 * 
		 * @param name Name of the service.
		 * @param version Verion of the service (package).
		 */
		private BootTask( String name) {
			this.name = name;
		}

		/**
		 * Task name getter.
		 * 
		 * @return BEEN task name of the given boot task.
		 */
		public String getName() {
			return name;
		}
		
		
		/**
		 * Enum member getter.
		 * 
		 * @param name Name of the member to find.
		 * @return A member of that name or null if no such member exists.
		 */
		public static BootTask forName( String name ) {
			return bootTaskMap.get( name );
		}
	}

	/** A prefix that should be used to construct tree paths for legacy tasks. */
	@Deprecated
	private static final String LEGACY_PATH_PREFIX = "/legacy/";
	
	/** Suffix added to names of boot tasks to create their IDs. */
	private static final String BOOT_TASK_ID_SUFFIX = "-tid";
	
	/** Standard suffic of BEEN's package files. */
	private static final String PACKAGE_FILE_SUFFIX = ".bpk";

	/**
	 * No, don't do this.
	 */
	private TaskDescriptorHelper() {
	}

	/**
	 * Check if this taskDescriptor is valid.
	 * 
	 * @return {@code true} if valid, {@code false} otherwise.
	 */
	public static StringBuilder validate( TaskDescriptor taskDescriptor ) {
		StringBuilder validateLog;

		validateLog = new StringBuilder();
		
		if ( !taskDescriptor.isSetTaskId() ) {
			validateLog.append( "Attribute 'taskId' is not set in <taskDescriptor>.\n" );
		}

		if ( !taskDescriptor.isSetContextId() ) {
			validateLog.append( "Attribute 'contextId' is not set in <taskDescriptor>.\n" );
		}
		
		if ( !taskDescriptor.isSetTreeAddress() ) {
			validateLog.append( "Attribute 'treeAddress' is not set in <taskDescriptor>.\n" );
		}

		if ( taskDescriptor.isSetPackage() ) {
			Package pacKage = taskDescriptor.getPackage();
			if ( !( pacKage.isSetName() || pacKage.isSetRSL() ) ) {
				validateLog.append( "Neither <name> nor <rsl> is set in <package>.\n" );
			}
		} else {
			validateLog.append( "<package> is not set in <taskDescriptor>.\n" );
		}

		if ( taskDescriptor.isSetHostRuntimes() ) {
			HostRuntimes hostRuntimes = taskDescriptor.getHostRuntimes();
			if ( !(
				hostRuntimes.isSetAsTask() ||
				hostRuntimes.isSetName() ||
				hostRuntimes.isSetRSL()
			) ) {
				validateLog.append(
					"Neither <hostRuntimesName> nor <hostRuntimesRsl> nor <asTask> " +
					"is set in <hostRuntimes>.\n"
				);
			}
		} else {
			validateLog.append( "<hostRuntimes> is not set in <taskDescriptor>.\n" );
		}

		if ( taskDescriptor.isSetDependencies() ) {
			Dependencies dependencies = taskDescriptor.getDependencies();
			if ( dependencies.isSetDependencyCheckPoint() ) {
				for (
					DependencyCheckPoint dependencyCheckPoint :
						dependencies.getDependencyCheckPoint()
				) {
					if ( !dependencyCheckPoint.isSetTaskId() ) {
						validateLog.append(
							"Attribute 'taskId' is not set in <dependencyCheckPoint>.\n"
						);
					}
					if ( !dependencyCheckPoint.isSetType() ) {
						validateLog.append(
							"Attribute 'type' is not set in <dependencyCheckPoint>.\n"
						);
					}
				}
			}
		}
		
		return validateLog;
	}
	
	/**
	 * Adds task properties to a task descriptor.
	 * 
	 * @param taskDescriptor The task descriptor to modify.
	 * @param properties Properties to add.
	 */
	public static void addTaskProperties(
		TaskDescriptor taskDescriptor,
		Entry< ?, ? > ... properties
	) {
		if ( properties.length > 0 ) {
			List< TaskProperty > taskPropertyList;
			if ( taskDescriptor.isSetTaskProperties() ) {
				taskPropertyList = taskDescriptor.getTaskProperties().getTaskProperty();
			} else {
				TaskProperties taskProperties = Factory.TD.createTaskProperties();
				taskDescriptor.setTaskProperties( taskProperties );
				taskPropertyList = taskProperties.getTaskProperty();
			}
			
			for ( Entry< ?, ? > property : properties ) {
				TaskProperty taskProperty = Factory.TD.createTaskProperty();
				taskProperty.setKey( property.getKey().toString() );
				taskProperty.setValue( property.getValue().toString() );
				taskPropertyList.add( taskProperty );
			}
		}
	}
	
	/**
	 * Adds task property objects to a task descriptor.
	 * 
	 * @param taskDescriptor The task descriptor to modify.
	 * @param objects Property objects to add.
	 * @throws IOException When serialization to Base64 fails.
	 */
	public static void addTaskPropertyObjects(
		TaskDescriptor taskDescriptor,
		Entry< ?, ? > ... objects
	) throws IOException {
		if ( objects.length > 0 ) {
			List< TaskPropertyObject > propertyObjectList;
			if ( taskDescriptor.isSetTaskPropertyObjects() ) {
				propertyObjectList =
					taskDescriptor.getTaskPropertyObjects().getTaskPropertyObject();
			} else {
				TaskPropertyObjects propertyObjects = Factory.TD.createTaskPropertyObjects();
				taskDescriptor.setTaskPropertyObjects( propertyObjects );
				propertyObjectList = propertyObjects.getTaskPropertyObject();
			}
			for ( Entry< ?, ? > object : objects ) {
				TaskPropertyObject propertyObject = Factory.TD.createTaskPropertyObject();
				propertyObject.setKey( object.getKey().toString() );
				propertyObject.setBinVal( Serialize.toBase64( (Serializable) object.getValue() ) );
				propertyObjectList.add( propertyObject );
			}	
		}
	}
	
	/**
	 * Adds a dependency checkpoint to a task descriptor.
	 * 
	 * @param taskDescriptor The task descriptor to modify.
	 * @param taskId Task id to set.
	 * @param type Checkpoint type to set.
	 * @param value Checkpoint String value to set.
	 */
	public static void addDependencyCheckpoint(
		TaskDescriptor taskDescriptor,
		String taskId,
		String type,
		String value
	) {
		List< DependencyCheckPoint > dependencyList;
		DependencyCheckPoint dependency;
		if ( taskDescriptor.isSetDependencies() ) {
			dependencyList = taskDescriptor.getDependencies().getDependencyCheckPoint();
		} else {
			Dependencies dependencies = Factory.TD.createDependencies();
			taskDescriptor.setDependencies( dependencies );
			dependencyList = dependencies.getDependencyCheckPoint();
		}
		dependency = Factory.TD.createDependencyCheckPoint();
		dependency.setTaskId( taskId );
		dependency.setType( type );
		dependency.setValue( value );
		dependencyList.add( dependency );
	}
	
	/**
	 * Adds a dependency checkpoint to a task descriptor.
	 * 
	 * @param taskDescriptor The task descriptor to modify.
	 * @param taskId Task id to set.
	 * @param type Checkpoint type to set.
	 * @param value Checkpoint String value to set.
	 * @throws IOException When serialization to Base64 fails.
	 */
	public static void addDependencyCheckpoint(
		TaskDescriptor taskDescriptor,
		String taskId,
		String type,
		Serializable value
	) throws IOException {
		List< DependencyCheckPoint > dependencyList;
		DependencyCheckPoint dependency;
		if ( taskDescriptor.isSetDependencies() ) {
			dependencyList = taskDescriptor.getDependencies().getDependencyCheckPoint();
		} else {
			Dependencies dependencies = Factory.TD.createDependencies();
			taskDescriptor.setDependencies( dependencies );
			dependencyList = dependencies.getDependencyCheckPoint();
		}
		dependency = Factory.TD.createDependencyCheckPoint();
		dependency.setTaskId( taskId );
		dependency.setType( type );
		dependency.setBinVal( Serialize.toBase64( value ) );
		dependencyList.add( dependency );
	}
	
	/**
	 * Adds Java options to a task descriptor.
	 * 
	 * @param taskDescriptor The task descriptor to modify.
	 * @param options Options to set.
	 */
	public static void addJavaOptions(
		TaskDescriptor taskDescriptor,
		String ... options
	) {
		if ( options.length > 0 ) {
			Java java;
			JavaOptions javaOptions;
			List< String > javaOptionsList;
			if ( taskDescriptor.isSetJava() ) {
				java = taskDescriptor.getJava();
				if ( java.isSetJavaOptions() ) {
					javaOptions = java.getJavaOptions();
				} else {
					javaOptions = Factory.TD.createJavaOptions();
					java.setJavaOptions(javaOptions);
				}
			} else {
				java = Factory.TD.createJava();
				javaOptions = Factory.TD.createJavaOptions();
				java.setJavaOptions( javaOptions );
				taskDescriptor.setJava( java );
			}
			javaOptionsList = javaOptions.getJavaOption();
			for ( String option : options ) {
				javaOptionsList.add( option );
			}
		}
	}
	
	/**
	 * Adds a dependency checkpoint to a task descriptor.
	 * 
	 * @param taskDescriptor The task descriptor to modify.
	 * @param taskId Task id to set.
	 * @param type Checkpoint type to set.
	 */
	public static void addDependencyCheckpoint(
		TaskDescriptor taskDescriptor,
		String taskId,
		String type		
	) {
		addDependencyCheckpoint( taskDescriptor, taskId, type, (String) null );
	}
	
	/**
	 * Returns the String value computed from a checkpoint value.
	 * 
	 * @param checkPoint The dependency checkpoint from which the value will be extracted.
	 * @return A String representing the value of the checkpoint.
	 * @throws DeserializeException When data deserialization is necessary and fails.
	 */
	public static String checkpointValueToString( DependencyCheckPoint checkPoint )
	throws DeserializeException {
		if ( checkPoint.isSetValue() ) {
			return checkPoint.getValue();
		} else if ( checkPoint.isSetBinVal() ) {
			return String.valueOf( Deserialize.fromBase64( checkPoint.getBinVal() ) );
		} else if ( checkPoint.isSetStrVal() ) {
			return String.valueOf( Deserialize.fromString( checkPoint.getStrVal() ) );
		} else {
			return null;
		}
	}
	
	/**
	 * Returns a Serializable value computed from a property object value.
	 * 
	 * @param property The property object from which the value will be extracted.
	 * @return A String representing the value of the property object.
	 * @throws DeserializeException When data deserialization is neecessary and fails.
	 */
	public static Serializable propertyObjectValue( TaskPropertyObject property )
	throws DeserializeException {
		if ( property.isSetBinVal() ) {
			return Deserialize.fromBase64( property.getBinVal() );
		} else if ( property.isSetStrVal() ) {
			return Deserialize.fromString( property.getStrVal() );
		} else {
			return null;																			// This should not happen.
		}
	}
	
	/**
	 * Clones a Task Descriptor.
	 * Immutable fields (String) are copied by reference. All other fields are copied by value.
	 * 
	 * @param template The original.
	 * @return The deep copy.
	 */
	public static TaskDescriptor clone( TaskDescriptor template ) {
		TaskDescriptor newTaskDescriptor;
		
		newTaskDescriptor = Factory.TD.createTaskDescriptor();
		
		if ( template.isSetTaskId() ) {																// Should be always set.
			newTaskDescriptor.setTaskId( template.getTaskId() );
		}
		if ( template.isSetContextId() ) {															// Should be always set.
			newTaskDescriptor.setContextId( template.getContextId() );
		}
		if ( template.isSetTreeAddress() ) {														// Should be always set.
			newTaskDescriptor.setTreeAddress( template.getTreeAddress() );
		}
		if ( template.isSetName() ) {
			newTaskDescriptor.setName( template.getName() );
		}
		if ( template.isSetDescription() ) {
			newTaskDescriptor.setDescription( template.getDescription() );
		}
		if ( template.isSetLongDescription() ) {
			newTaskDescriptor.setLongDescription( template.getLongDescription() );
		}
		if ( template.isSetExclusive() ) {
			newTaskDescriptor.setExclusive( template.getExclusive() );
		}
		if ( template.isSetPackage() ) {															// Should always be set.
			Package pacKage = template.getPackage();
			Package newPackage = Factory.TD.createPackage();
			if ( pacKage.isSetName() ) {
				newPackage.setName( pacKage.getName() );
			}
			if ( pacKage.isSetRSL() ) {
				newPackage.setRSL( pacKage.getRSL() );
			}
			newTaskDescriptor.setPackage( newPackage );
		}
		if ( template.isSetHostRuntimes() ) {
			HostRuntimes hostRuntimes = template.getHostRuntimes();
			HostRuntimes newHostRuntimes = Factory.TD.createHostRuntimes();
			if ( hostRuntimes.isSetAsTask() ) {
				newHostRuntimes.setAsTask( hostRuntimes.getAsTask() );
			}
			if ( hostRuntimes.isSetRSL() ) {
				newHostRuntimes.setRSL( hostRuntimes.getRSL() );
			}
			if ( hostRuntimes.isSetName() ) {
				newHostRuntimes.getName().addAll( hostRuntimes.getName() );
			}
			newTaskDescriptor.setHostRuntimes( newHostRuntimes );
		}
		if ( template.isSetArguments() ) {
			Arguments arguments = template.getArguments();
			Arguments newArguments = Factory.TD.createArguments();
			if ( arguments.isSetArgument() ) {
				newArguments.getArgument().addAll( arguments.getArgument() );
			}
			newTaskDescriptor.setArguments( newArguments );
		}
		if ( template.isSetJava() ) {
			Java java = template.getJava();
			Java newJava = Factory.TD.createJava();
			if ( java.isSetJavaOptions() ) {
				JavaOptions javaOptions = java.getJavaOptions();
				JavaOptions newJavaOptions = Factory.TD.createJavaOptions();
				if ( javaOptions.isSetJavaOption() ) {
					newJavaOptions.getJavaOption().addAll( javaOptions.getJavaOption() );
				}
				newJava.setJavaOptions( newJavaOptions );
			}
			newTaskDescriptor.setJava( newJava );
		}
		if ( template.isSetTaskProperties() ) {
			TaskProperties taskProperties = template.getTaskProperties();
			TaskProperties newTaskProperties = Factory.TD.createTaskProperties();
			if ( taskProperties.isSetTaskProperty() ) {
				TaskProperty newTaskProperty;
				List< TaskProperty > newTaskPropertyList = newTaskProperties.getTaskProperty();
				for ( TaskProperty taskProperty : taskProperties.getTaskProperty() ) {
					newTaskProperty = Factory.TD.createTaskProperty();
					if ( taskProperty.isSetKey() ) {												// Should always be set.
						newTaskProperty.setKey( taskProperty.getKey() );
					}
					if ( taskProperty.isSetValue() ) {												// Should always be set.
						newTaskProperty.setValue( taskProperty.getValue() );
					}
					if ( taskProperty.isSetLongValue() ) {
						newTaskProperty.setLongValue( taskProperty.getLongValue() );
					}
					newTaskPropertyList.add( newTaskProperty );
				}
			}
			newTaskDescriptor.setTaskProperties( newTaskProperties );
		}
		if ( template.isSetTaskPropertyObjects() ) {
			TaskPropertyObjects taskPropertyObjects = template.getTaskPropertyObjects();
			TaskPropertyObjects newTaskPropertyObjects =
				Factory.TD.createTaskPropertyObjects();
			if ( taskPropertyObjects.isSetTaskPropertyObject() ) {
				TaskPropertyObject newTaskPropertyObject;
				List< TaskPropertyObject > newTaskPropertyObjectList =
					newTaskPropertyObjects.getTaskPropertyObject();
				for (
					TaskPropertyObject taskPropertyObject :
					taskPropertyObjects.getTaskPropertyObject()
				) {
					newTaskPropertyObject = Factory.TD.createTaskPropertyObject();
					if ( taskPropertyObject.isSetKey() ) {											// Should always be set.
						newTaskPropertyObject.setKey( taskPropertyObject.getKey() );
					}
					if ( taskPropertyObject.isSetBinVal() ) {
						byte[] binVal = taskPropertyObject.getBinVal();
						byte[] newBinVal = new byte[ binVal.length ];
						System.arraycopy( binVal, 0, newBinVal, 0, binVal.length );
						newTaskPropertyObject.setBinVal( newBinVal );
					}
					if ( taskPropertyObject.isSetStrVal() ) {
						StrVal strVal = taskPropertyObject.getStrVal();
						StrVal newStrVal = Factory.TD.createStrVal();
						if ( strVal.isSetValue() ) {												// Should always be set.
							newStrVal.setValue( strVal.getValue() );
						}
						if ( strVal.isSetClazz() ) {												// Should always be set.
							newStrVal.setClazz( strVal.getClazz() );
						}
						newTaskPropertyObject.setStrVal( newStrVal );
					}
					newTaskPropertyObjectList.add( newTaskPropertyObject );
				}
			}
			newTaskDescriptor.setTaskPropertyObjects( newTaskPropertyObjects );
		}
		if ( template.isSetDependencies() ) {
			Dependencies dependencies = template.getDependencies();;
			Dependencies newDependencies = Factory.TD.createDependencies();
			if ( dependencies.isSetDependencyCheckPoint() ) {
				DependencyCheckPoint newDependencyCheckPoint;
				List< DependencyCheckPoint > newDependencyCheckPointList =
					newDependencies.getDependencyCheckPoint();
				for (
					DependencyCheckPoint dependencyCheckPoint :
					dependencies.getDependencyCheckPoint()
				) {
					newDependencyCheckPoint = Factory.TD.createDependencyCheckPoint();
					if ( dependencyCheckPoint.isSetTaskId() ) {										// Should always be set.
						newDependencyCheckPoint.setTaskId( dependencyCheckPoint.getTaskId() );
					}
					if ( dependencyCheckPoint.isSetType() ) {										// Should always be set.
						newDependencyCheckPoint.setType( dependencyCheckPoint.getType() );
					}
					if ( dependencyCheckPoint.isSetValue() ) {
						newDependencyCheckPoint.setValue( dependencyCheckPoint.getValue() );
					}
					if ( dependencyCheckPoint.isSetBinVal() ) {
						byte[] binVal = dependencyCheckPoint.getBinVal();
						byte[] newBinVal = new byte[ binVal.length ];
						System.arraycopy( binVal, 0, newBinVal, 0, binVal.length );
						newDependencyCheckPoint.setBinVal( newBinVal );
					}
					if ( dependencyCheckPoint.isSetStrVal() ) {
						StrVal strVal = dependencyCheckPoint.getStrVal();
						StrVal newStrVal = Factory.TD.createStrVal();
						if ( strVal.isSetValue() ) {
							newStrVal.setValue( strVal.getValue() );
						}
						if ( strVal.isSetClazz() ) {
							newStrVal.setClazz( strVal.getClazz() );
						}
						newDependencyCheckPoint.setStrVal( newStrVal );
					}
					newDependencyCheckPointList.add( newDependencyCheckPoint );
				}
			}
			newTaskDescriptor.setDependencies( newDependencies );
		}
		if ( template.isSetFailurePolicy() ) {
			FailurePolicy failurePolicy = template.getFailurePolicy();
			FailurePolicy newFailurePolicy = Factory.TD.createFailurePolicy();
			if ( failurePolicy.isSetRestartMax() ) {
				newFailurePolicy.setRestartMax( failurePolicy.getRestartMax() );
			}
			if ( failurePolicy.isSetTimeoutRun() ) {
				newFailurePolicy.setTimeoutRun( failurePolicy.getTimeoutRun() );
			}
			newTaskDescriptor.setFailurePolicy( newFailurePolicy );
		}
		if ( template.isSetLoadMonitoring() ) {
			LoadMonitoring loadMonitoring = template.getLoadMonitoring();
			LoadMonitoring newLoadMonitoring = Factory.TD.createLoadMonitoring();
			if ( loadMonitoring.isSetDetailedLoad() ) {
				newLoadMonitoring.setDetailedLoad( loadMonitoring.isDetailedLoad() );
			}
			if ( loadMonitoring.isSetDetailedLoadInterval() ) {
				newLoadMonitoring.setDetailedLoadInterval(
					loadMonitoring.getDetailedLoadInterval()
				);
			}
			if ( loadMonitoring.isSetLoadUnits() ) {
				newLoadMonitoring.setLoadUnits( loadMonitoring.getLoadUnits() );
			}
			newTaskDescriptor.setLoadMonitoring( newLoadMonitoring );
		}
		
		return newTaskDescriptor;
	}

	/**
	 * Factory method for creating BEEN boot tasks. Boot packages are placed in HostRuntime's
	 * boot directory.
	 * 
	 * As among boot tasks are also HostManager and SoftwareRepository (BEEN services that expand
	 * RSL for regular tasks), boot tasks use RSL neither for hosts nor for package specification.
	 * Instead, package name and host name will be specified directly in the task descriptor
	 * 
	 * @param bootTask Enum member of the boot task to create.
	 * @param hostName Name of the host on which the boot task should run.
	 * @param treeAddress The tree address the new task will occupy.
	 */
	public static TaskDescriptor createBootTask(
		BootTask bootTask,
		String hostName,
		String treeAddress
	) {
		if ( null == bootTask ) throw new NullPointerException( "bootTask is null" );
		if ( null == hostName ) throw new NullPointerException( "hostName is null" );
		if ( null == treeAddress ) throw new NullPointerException( "treeAddress is null" );
		
		String name = bootTask.getName();
		TaskDescriptor result = Factory.TD.createTaskDescriptor();
		Package pacKage = Factory.TD.createPackage();
		HostRuntimes hostRuntimes = Factory.TD.createHostRuntimes();
		
		result.setTaskId( name + BOOT_TASK_ID_SUFFIX );
		result.setName( name );
		result.setTreeAddress( treeAddress );
		result.setContextId( TaskManagerInterface.SYSTEM_CONTEXT_ID );
		pacKage.setName( name + PACKAGE_FILE_SUFFIX );
		result.setPackage( pacKage );
		hostRuntimes.getName().add( hostName );
		result.setHostRuntimes( hostRuntimes );
		return result;
	}

	/**
	 * Creates task descriptor for a detector task. Can't be done via
	 * {@link #createBootTask(BootTask, String)} call because we need detetctor task identifier
	 * to determine task package, but also unqiue task identifier so that it runs in a different
	 * directory each time. 
	 * 
	 * @param taskID Unique id of detector task.
	 * @param hostName Host on which the task should run.
	 * @return Task Descriptor for the detector task.
	 */
	public static TaskDescriptor createDetector( String taskID, String hostName, String treeAddress ) {
		if ( null == taskID ) throw new NullPointerException( "taskID is null" );					// Whe only check what we set.
		
		final TaskDescriptor result = createBootTask( BootTask.DETECTOR_TASK, hostName, treeAddress );
		result.setTaskId( taskID );
		return result;
	}

	/**
	 * Utility method for creating descriptors for regular tasks. The method creates
	 * a TaskDescriptor using RSL for matching hosts and task package. Task package match
	 * is based on task name.
	 * 
	 * @param taskID Identifier of the task to create.
	 * @param taskName Name of the task to create.
	 * @param contextID	Identifier of a context to create.
	 * @param hostRSL RSL to match target hosts. This is the only argument that can be null.
	 * @param treeAddress Address of the task in the visual tree.
	 * @return A new TaskDescriptor for the task.
	 */
	public static TaskDescriptor createTask(
		String taskID,
		String taskName,
		String contextID,
		Condition hostRSL,
		String treeAddress
	) {
		if ( null == taskID ) throw new NullPointerException( "taskID is null" );
		if ( null == taskName ) throw new NullPointerException( "taskName is null" );
		if ( null == contextID ) throw new NullPointerException( "contextID is null" );
		if ( null == treeAddress ) throw new NullPointerException( "treeAddress is null" );
		
		AndCondition packageCondition = new AndCondition(
			new Condition[] {
				new EqualsCondition< PackageType >( "type", PackageType.TASK ),
				new EqualsCondition< String >( "name", taskName )
			}
		);
		TaskDescriptor result = Factory.TD.createTaskDescriptor();
		Package pacKage = Factory.TD.createPackage();
		HostRuntimes hostRuntimes = Factory.TD.createHostRuntimes();
		
		result.setTaskId( taskID );
		result.setContextId( contextID );
		pacKage.setRSL( packageCondition );
		result.setPackage( pacKage );
		hostRuntimes.setRSL( hostRSL );
		result.setHostRuntimes( hostRuntimes );
		result.setTreeAddress( treeAddress );
		result.setName( taskName );
		return result;
	}

	/* ********************************************************* */
	/* Legacy, deprecated and (hopefully) unused methods follow. */
	/* ********************************************************* */

	/**
	 * Utility method for creating descriptors for regular tasks. The method creates
	 * a TaskDescriptor using RSL for matching hosts and task package. Task package match
	 * is based on task name.
	 * 
	 * @param taskID Identifier of the task to create.
	 * @param taskName Name of the task to create.
	 * @param contextID	Identifier of a context to create.
	 * @param hostRSL RSL to match target hosts.
	 * @return A new TaskDescriptor for the task.
	 */
	@Deprecated
	public static TaskDescriptor createTask(
		String taskID,
		String taskName,
		String contextID,
		Condition hostRSL
	) {
		return createTask(
			taskID,
			taskName,
			contextID,
			hostRSL,
			LEGACY_PATH_PREFIX + taskName + '/' + taskID
		);
	}
	
	/**
	 * Factory method for creating BEEN boot tasks. Boot packages are placed in HostRuntime's
	 * boot directory.
	 * 
	 * As among boot tasks are also HostManager and SoftwareRepository (BEEN services that expand
	 * RSL for regular tasks), boot tasks use RSL neither for hosts nor for package specification.
	 * Instead, package name and host name will be specified directly in the task descriptor
	 * 
	 * @param bootTask Enum member of the boot task to create.
	 * @param host Name of the host on which the boot task should run.
	 * @return A Task Descriptor for the boot task.
	 */
	@Deprecated
	public static TaskDescriptor createBootTask( BootTask bootTask, String host ) {
		System.err.println( "FIXME: Deprecated factory method used." );
		return createBootTask( bootTask, host, LEGACY_PATH_PREFIX + bootTask.getName() );
	}

	/**
	 * Creates task descriptor for a detector task. Can't be done via
	 * {@link #createBootTask(BootTask, String)} call because we need detetctor task identifier
	 * to determine task package, but also unqiue task identifier so that it runs in a different
	 * directory each time. 
	 * 
	 * @param name Unique name of detector task.
	 * @param host Host on which the task should run.
	 * @return Task Descriptor for the detector task.
	 */
	@Deprecated
	public static TaskDescriptor createDetector( String name, String host ) {
		System.err.println( "FIXME: Deprecated factory method used." );
		return createDetector( name, host, LEGACY_PATH_PREFIX + host + '/' + name );
	}
}
