/*
 *  BEEN: Benchmarking Environment
 *  ==============================
 *
 *  File author: David Majda
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
package cz.cuni.mff.been.hostruntime;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cz.cuni.mff.been.common.Debug;
import cz.cuni.mff.been.common.RMI;
import cz.cuni.mff.been.common.anttasks.AntTaskException;
import cz.cuni.mff.been.common.anttasks.Chmod;
import cz.cuni.mff.been.common.anttasks.Delete;
import cz.cuni.mff.been.common.serialize.Deserialize;
import cz.cuni.mff.been.common.serialize.DeserializeException;
import cz.cuni.mff.been.debugassistant.DebugAssistantInterface;
import cz.cuni.mff.been.debugassistant.DebugAssistantService;
import cz.cuni.mff.been.debugassistant.SuspendedTask;
import cz.cuni.mff.been.hostmanager.IllegalOperationException;
import cz.cuni.mff.been.hostmanager.load.LoadMonitorException;
import cz.cuni.mff.been.hostmanager.load.LoadMonitorInterface;
import cz.cuni.mff.been.jaxb.XSDRoot;
import cz.cuni.mff.been.jaxb.td.Arguments;
import cz.cuni.mff.been.jaxb.td.Java;
import cz.cuni.mff.been.jaxb.td.JavaOptions;
import cz.cuni.mff.been.jaxb.td.TaskDescriptor;
import cz.cuni.mff.been.jaxb.td.TaskExclusivity;
import cz.cuni.mff.been.jaxb.td.TaskPropertyObject;
import cz.cuni.mff.been.jaxb.td.TaskPropertyObjects;
import cz.cuni.mff.been.logging.LogLevel;
import cz.cuni.mff.been.logging.LogStorageException;
import cz.cuni.mff.been.softwarerepository.PackageType;
import cz.cuni.mff.been.task.Service;
import cz.cuni.mff.been.task.Task;
import cz.cuni.mff.been.task.TaskException;
import cz.cuni.mff.been.taskmanager.CheckPoint;
import cz.cuni.mff.been.taskmanager.HostRuntimesPortInterface;
import cz.cuni.mff.been.taskmanager.data.TaskState;

/**
 * The class representing a task instance in the host runtime.
 * 
 * The class is capable of executing the task package and providing the task
 * with the necessary environment.
 * 
 * @author Antonin Tomecek
 * @author David Majda
 */
public class TaskImplementation extends UnicastRemoteObject implements TaskInterface
{
	private static final long serialVersionUID = -2011676092980850313L;

	/** Task descriptor of this task. */
	private TaskDescriptor taskDescriptor;
	/** Task property objects as read from the task descriptor. */
	private Map<String, Serializable> taskPropertyObjects;
	/** Task configuration as read from the task package. */
	private PackageConfiguration taskPackageConfiguration;

	/** Host Runtime in which this task is running. */
	private HostRuntimeImplementation hostRuntime;

	/**
	 * The base directory of the task. Assigned by host runtime, cached here for
	 * ease of use. Survives until explicit task destruction during context
	 * cleanup.
	 */
	private String baseDirectory;
	/**
	 * Task directory. The files from the task package are available here.
	 * Assigned by host runtime, cached here for ease of use. Deleted on task
	 * termination.
	 */
	private String taskDirectory;
	/**
	 * Working directory. The results of task work are stored there. Assigned by
	 * host runtime, cached here for ease of use. Survives until explicit task
	 * destruction during context cleanup.
	 */
	private String workingDirectory;
	/**
	 * Temporary directory. For arbitrary use by the task. Assigned by host
	 * runtime, cached here for ease of use. Deleted on task termination.
	 */
	private String temporaryDirectory;
	/**
	 * Service directory. For arbitrary use by the host but not the task.
	 * Assigned by host runtime, cached here for ease of use. Deleted on task
	 * execution.
	 */
	private String serviceDirectory;

	/** Determines whether we want to measure detailed load for this task. */
	private boolean measureDetailedLoad;

	/** Thread object which executes the task process. */
	private TaskProcessExecutor executor;
	
	/** An indication whether the task is running. */
	private volatile boolean isRunning = true;
	/** The exit code, set when the task is not running. */
	private volatile int exitValue = 0;
	
	
	/** Type of the process output. */
	private enum ProcessOutputType { STANDARD, ERROR }

	/** When to forward to host runtime. */
	private enum OutputForwardingType { CONTINUOUSLY, ON_TERMINATION }

	// Shortcuts to values that are used multiple times.
	
	private String taskId;
	private String contextId;
	private String taskPortUri;
	private String hostRuntimeName;
	private HostRuntimesPortInterface taskManagerRuntimePort;

	
	//----------------------------------------------------------------------
	// Utility Functions

	
	/**
	 * Displays a standard message.
	 * 
	 * TODO The output display should be unified.
	 */
	private void displayMessage (String message)
	{
		System.out.println ("[" + contextId + ":" + taskId + "] " + message);
	}

	
	/**
	 * Reports a failure message.
	 * 
	 * Should only be used when there is no other way to report the failure.
	 * 
	 * TODO The failure reporting should be unified.
	 */
	private void reportFailure (String message, Exception exception)
	{
		if (message == null) message = "An unspecified failure inside a task.";
		System.err.println ("[" + contextId + ":" + taskId + "] " + message);
		if (exception != null)
		{
			System.err.println (exception.getMessage ());
			exception.printStackTrace (System.err);
		}
	}

	
	/**
	 * A function to terminate the runtime on fatal error.
	 * 
	 * TODO Host runtime termination should be handled in a more intelligent manner.
	 */
	private void fatalError ()
	{
		System.exit (1);
	}
	
	
	/**
	 * Safely creates a directory.
	 * 
	 * Creates a directory including its parents. Does not fail if the directory exists.
	 * 
	 * TODO This does not belong into TaskImplementation, there should be a utility class !
	 * 
	 * @param dirName		The directory to create.
	 * @throws IOException	Whatever can happen if the directory could not be created.
	 */
	private static void safeCreateDirectory (String dirName) throws IOException
	{
		File dirPath = new File (dirName);
		if (!dirPath.exists ())
		{
			if (!dirPath.mkdirs ())
			{
				throw new IOException ("Failed to create directory " + dirName + ".");
			}
		}
	}

	
	/**
	 * Sets the particular checkpoint of the task to the given value.
	 * 
	 * @param name
	 * @param value
	 */
	public void signalCheckPoint (String name, Serializable value)
	{
		try
		{
			CheckPoint checkPoint = new CheckPoint (taskId, contextId, name, value);
			checkPoint.setHostName (hostRuntimeName);
			taskManagerRuntimePort.checkPointReached (checkPoint);
			displayMessage ("Checkpoint \"" + name + "\" set to \"" + value + "\".");
		}
		catch (RemoteException e)
		{
			reportFailure ("Error reporting the checkpoint value.", e);
			fatalError ();
		}
	}

	
	//----------------------------------------------------------------------
	// Log Record Class

	
	/**
	 * A log record structure for temporary local storage.
	 * 
	 * @author David Majda
	 */
	private static class LogRecord
	{
		/** Level of the log event. */
		private LogLevel level;
		/** Time of the log event. */
		private Date timestamp;
		/** Text of the message. */
		private String message;

		public LogRecord(LogLevel level, Date timestamp, String message)
		{
			this.level = level;
			this.timestamp = timestamp;
			this.message = message;
		}
	}

		
	/** The buffer for temporary local storage of log records. */ 
	private List<LogRecord> localLogStorage = new LinkedList<LogRecord> ();


	/**
	 * For tasks that are sensitive to disruptions, log messages are stored
	 * locally and forwarded on task termination. Other tasks send their
	 * log messages straight to the host runtime.
	 * 
	 * @param level			Log level of the message.
	 * @param timestamp		Timestamp of the log event.
	 * @param message		The potentially multiline log message.
	 * 
	 * @throws RemoteException
	 */
	public void log (LogLevel level, Date timestamp, String message) throws RemoteException
	{
		if (taskDescriptor.getExclusive () == TaskExclusivity.NON_EXCLUSIVE)
		{
			taskManagerRuntimePort.log (contextId, taskId, level, timestamp, message);
		}
		else
		{
			localLogStorage.add (new LogRecord (level, timestamp, message));
		}
	}

	
	/**
	 * Flushes whatever log records were stored locally.
	 */
	private void flushLocalLogStorage ()
	{
		try
		{
			for (LogRecord logRecord : localLogStorage)
			{
				taskManagerRuntimePort.log (contextId, taskId, logRecord.level, logRecord.timestamp, logRecord.message);
			}
		}
		catch (RemoteException e)
		{
			reportFailure ("Error forwarding log messages to storage.", e);
		}
	}

	
	//----------------------------------------------------------------------
	// Process Output Processor Class

	
	/**
	 * The standard output and the error output of the executing task are
	 * collected by a pair of process output processors. The processors write
	 * the output to a file and send it to the host runtime (which sends it
	 * further to the Task Manager).
	 * 
	 * Communication with host runtime can be postponed for sensitive tasks.
	 * When that is the case, the output file is used as the temporary storage.
	 *
	 * The class relies on being an inner class of the task implementation.
	 * Among other, the host runtime reference and the task directories are used.
	 * 
	 * @author David Majda
	 */
	private class ProcessOutputProcessor extends Thread
	{
		/** Name of the file with standard output of the task process. */
		private static final String STANDARD_OUTPUT_FILE = "standard-output";
		/** Name of the file with error output of the task process. */
		private static final String ERROR_OUTPUT_FILE = "error-output";

		/** Maximum transfer unit size. */
		private static final int BUFFER_SIZE = 4096;

		/** Stream to read the logs from. */
		private InputStream inputStream;
		/** File to write the logs to. */
		private String outputFile;
		/** Selection of the process output type. */
		private ProcessOutputType outputType;
		/** Selection of the output forwarding type. */
		private OutputForwardingType forwardingType;

		
		/**
		 * Constructs the output processor.
		 * 
		 * The output processor takes care of closing the input stream on reaching its end.
		 * 
		 * @param inputStream		The stream to read the logs from.
		 * @param outputType		The type of output being recorded.
		 * @param forwardingType	The mode of output forwarding to use. 
		 */
		public ProcessOutputProcessor (
				InputStream inputStream, 
				ProcessOutputType outputType, 
				OutputForwardingType forwardingType)
		{
			this.inputStream = inputStream;
			
			// The output file name is determined by the output type.
			String outputSuffix = null;
			switch (outputType)
			{
				case STANDARD: outputSuffix = STANDARD_OUTPUT_FILE;
				               break;
				case ERROR:    outputSuffix = ERROR_OUTPUT_FILE;
	   		 				   break;
			}
			outputFile = workingDirectory + File.separator + outputSuffix;
			
			this.outputType = outputType;
			this.forwardingType = forwardingType;
		}

		
		/**
		 * Forwards the output buffer to the host runtime.
		 * Factored out to avoid repeating the same code twice.
		 * 
		 * TODO The host runtime API should accept both output types with one parametrized method.
		 */
		private void forwardOutput (byte [] buffer, int bytesRead) throws LogStorageException, RemoteException
		{
			String output = new String (buffer, 0, bytesRead);
			
			try
			{
				switch (outputType)
				{
					case STANDARD:	taskManagerRuntimePort.addStandardOutput (contextId, taskId, output);
									break;
					case ERROR:		taskManagerRuntimePort.addErrorOutput (contextId, taskId, output);
									break;
				}
			}
			catch (LogStorageException e)
			{
				reportFailure ("Failed to forward task output.", e);
				throw (e);
			}
		}

		
		@Override
		public void run ()
		{
			FileOutputStream outputStream = null;
			BufferedInputStream forwardStream = null;

			byte [] buffer = new byte [BUFFER_SIZE];

			try
			{
				try
				{
					// We do not use output buffering because we want
					// the file to be in sync with the process output.
					outputStream = new FileOutputStream (outputFile);
				}
				catch (FileNotFoundException e)
				{
					reportFailure ("Failed to create the output file stream", e);
					throw (e);
				}
				
				while (true)
				{
					// Negative number of bytes read means end of stream was reached. 
					int bytesRead = inputStream.read (buffer);
					if (bytesRead == -1) break;
	
					// Copy input to output.
					outputStream.write (buffer, 0, bytesRead);
		
					// If forwarding online, forward to the host runtime.
					if (forwardingType == OutputForwardingType.CONTINUOUSLY) forwardOutput (buffer, bytesRead);
				}
				
				// End of stream was reached. If the stream was not forwarded to host runtime continuously, forward it now.
	
				if (forwardingType == OutputForwardingType.ON_TERMINATION)
				{
					try
					{
						forwardStream = new BufferedInputStream (new FileInputStream (outputFile));
					}
					catch (FileNotFoundException e)
					{
						reportFailure ("Failed to open task output during forwarding.", e);
						throw (e);
					}
				
					while (true)
					{
						// Negative number of bytes read means end of stream was reached. 
						int bytesRead = inputStream.read (buffer);
						if (bytesRead == -1) break;
						
						forwardOutput (buffer, bytesRead);
					}
				}
			}
			catch (Exception e)
			{
				// We catch all exceptions because we do not want them to escape outside the thread.
				reportFailure ("Error in task output processor.", e);
			}
			finally
			{
				try
				{
					// The various streams are all closed in one place to avoid too deep block nesting.
					inputStream.close ();
					if (outputStream != null) outputStream.close ();
					if (forwardStream != null) forwardStream.close ();
				}
				catch (IOException e)
				{
					reportFailure ("Error closing task output.", e);
				}
			}
		}
	}


	//----------------------------------------------------------------------
	// Task Process Executor Class
	

	/**
	 * The task process is run in a dedicated thread. The implementation
	 * of the thread performs all actions related to the execution of
	 * the task process (attaching and detaching output processors,
	 * starting and stopping load monitors, etc.).
	 * 
	 * @author David Majda
	 */
	private class TaskProcessExecutor extends Thread
	{
		/** Flag indicating that the task process was killed due to timeout. */
		private boolean processTimedOut;
		/** Flag indicating that the task process was killed by call to kill. */
		private boolean processKilled;

		/** Process in which the task is running. */
		private Process process = null;
		/** Thread object which executes the timeout monitor. */
		private TaskProcessKiller killer = null;

		/** What is the debug port should the task be debugged. */
		private int processDebugPort = 0;
		/** Whether the task is suspended waiting for the debug assistant. */
		private boolean processRunSuspended = false;

		
		/**
		 * A utility thread which is created when a task has specified timeout for
		 * its run. It simply sleeps through the timeout and kills the task
		 * process afterwards.
		 * 
		 * @author David Majda
		 */
		private class TaskProcessKiller extends Thread
		{
			/** Task process timeout in milliseconds. */
			private long timeout;

			public TaskProcessKiller (long timeout)
			{
				this.timeout = timeout;
			}

			@Override
			public void run ()
			{
				try
				{
					sleep (timeout);
					
					// If we arrive here, it means that the timeout was not interrupted.
					processTimedOut = true;
					process.destroy ();
					
					displayMessage ("Task killed after a timeout of " + timeout + " milliseconds.");
				}
				catch (InterruptedException e)
				{
					// This is where we end up when the killer thread is interrupted prematurely.
				}
			}
		}

		
		private void prepareForDebugging ()
		{
			if (Debug.isDebugModeOn ())
			{
				processDebugPort = hostRuntime.getNextTaskDebugPort ();
				displayMessage ("Task can be debugged on port " + processDebugPort + ".");
	
				try
				{
					DebugAssistantInterface da = (DebugAssistantInterface) hostRuntime.getTaskManager ().serviceFind (DebugAssistantService.SERVICE_NAME, Service.RMI_MAIN_IFACE);
					if (da == null)
					{
						displayMessage ("Debug assistant is not online, task will be run normally.");
					}
					else
					{
						String hostName = InetAddress.getLocalHost ().getCanonicalHostName ();
						String taskName = taskDescriptor.getName ();
						SuspendedTask suspendedTask = new SuspendedTask (contextId, taskId, taskName, hostName, processDebugPort);
						da.registerSuspendedTask (suspendedTask);
						processRunSuspended = true;
						
						displayMessage ("The task will be suspended, resume it in the debug assistant console.");
					}
				}
				catch (Exception e)
				{
					reportFailure ("An error occured while submitting the task for debugging.", e);
				}
			}
		}

		
		/**
		 * Constructs the command line of the task based on the task configuration.
		 * 
		 */
		private List<String> buildCommandLine ()
		{
			List<String> result = new LinkedList<String> ();

			// We certainly want to use java for java tasks :-) ...
			result.add ("java");

			// The task can have specific java options in its descriptor.
			// Copy them to the command line when this is the case.
			if (taskDescriptor.isSetJava ())
			{
				Java java = taskDescriptor.getJava ();
				if (java.isSetJavaOptions ())
				{
					JavaOptions javaOptions = java.getJavaOptions ();
					if (javaOptions.isSetJavaOption ())
					{
						result.addAll (javaOptions.getJavaOption ());
					}
				}
			}

			// Add the debugging options if debugging is enabled.
			if (processDebugPort != 0)
			{
				result.add ("-Xdebug");
				result.add ("-Xrunjdwp:transport=dt_socket,address=" + processDebugPort + ",server=y,suspend=" + (processRunSuspended ? "y" : "n"));
			}

			// Add the property that tells the task where its task port is. 
			result.add ("-Dhostruntime.tasksport.uri=" + taskPortUri);

			// Add the properties that tell the task where its directories are.
			result.add ("-Dhostruntime.directory.task=" + taskDirectory);
			result.add ("-Dhostruntime.directory.working=" + workingDirectory);
			result.add ("-Dhostruntime.directory.temporary=" + temporaryDirectory);
			
			// If we have been run with some system package overrides, let the child inherit those.
			result.add ("-Djava.endorsed.dirs=" + System.getProperty ("java.endorsed.dirs"));

			// Define the directory for the XSD files used by JAXB.
			result.add ("-D" + XSDRoot.XSD_ROOT + '=' + taskDirectory);

			// Now comes constructing the class path.
			// We start with our class path.
			String taskClassPath = System.getProperty ("java.class.path");
			// The task directory can contain additional classes.
			taskClassPath += File.pathSeparatorChar + taskDirectory;
			// The task configuration contains class path as well. 
			if (taskPackageConfiguration.getTaskLanguage () == PackageConfiguration.TaskLanguage.JAVA)
			{
				taskClassPath += File.pathSeparatorChar + taskPackageConfiguration.getJavaClassPath ();
			}
			else if (taskPackageConfiguration.getTaskLanguage () == PackageConfiguration.TaskLanguage.JYTHON)
			{
				taskClassPath += File.pathSeparatorChar + taskPackageConfiguration.getJythonClassPath ();
			}
			result.add ("-cp");
			result.add (taskClassPath);

			// The class that is started inside the virtual machine depends on what code we want to start.
			// Each task loader class also has a different argument telling it what to run. 
			String taskLoaderClass = null;
			String taskLoaderArgument = null;
			if (taskPackageConfiguration.getTaskLanguage () == PackageConfiguration.TaskLanguage.JAVA)
			{
				taskLoaderClass = TaskLoader.class.getName ();
				taskLoaderArgument = taskPackageConfiguration.getJavaMainClass ();
			}
			else if (taskPackageConfiguration.getTaskLanguage () == PackageConfiguration.TaskLanguage.SHELL)
			{
				taskLoaderClass = ShellTaskLoader.class.getName ();
				taskLoaderArgument = taskPackageConfiguration.getShellScriptFile ();
			}
			else if (taskPackageConfiguration.getTaskLanguage () == PackageConfiguration.TaskLanguage.JYTHON)
			{
				taskLoaderClass = JythonTaskLoader.class.getName ();
				taskLoaderArgument = taskPackageConfiguration.getJythonScriptFile ();
			}
			result.add (taskLoaderClass);
			result.add (taskLoaderArgument);

			// Finally, any additional command line arguments that the task might have.
			if (taskDescriptor.isSetArguments ())
			{
				Arguments arguments = taskDescriptor.getArguments ();
				if (arguments.isSetArgument ())
				{
					result.addAll (arguments.getArgument ());
				}
			}

			return (result);
		}

	
		/**
		 * Initializes detailed load monitoring if the task requires that.
		 */
		private void initializeLoadMonitor ()
		{
			if (measureDetailedLoad)
			{
				LoadMonitorInterface loadMonitor = hostRuntime.getLoadMonitor ();
				try
				{
					long interval = 0;
					if (taskDescriptor.isSetLoadMonitoring ()) interval = taskDescriptor.getLoadMonitoring ().getDetailedLoadInterval ();
					
					if (interval > 0) loadMonitor.startDetailedMode (contextId, taskId, interval);
								 else loadMonitor.startDetailedMode (contextId, taskId);
				}
				catch (IllegalOperationException e)
				{
					reportFailure (null, e);
					throw new AssertionError ("Invalid load monitor mode.");
				}
				catch (LoadMonitorException e)
				{
					reportFailure ("Load monitor failure.", e);
					fatalError ();
				}
				catch (RemoteException e)
				{
					reportFailure (null, e);
					throw new AssertionError ("Unexpected remote call failure on local call.");
				}
			}
		}

		
		/**
		 * Terminates detailed load monitoring if the task requires that.
		 */
		private void terminateLoadMonitor ()
		{
			if (measureDetailedLoad)
			{
				try
				{
					hostRuntime.getLoadMonitor ().stopDetailedMode ();
				}
				catch (IllegalOperationException e)
				{
					reportFailure (null, e);
					throw new AssertionError ("Invalid load monitor mode.");
				}
				catch (LoadMonitorException e)
				{
					reportFailure ("Load monitor failure.", e);
					fatalError ();
				}
				catch (RemoteException e)
				{
					reportFailure (null, e);
					throw new AssertionError ("Unexpected remote call failure on local call.");
				}
			}
		}

		
		/**
		 * Initializes the timeout monitor if the task has a timeout configured.
		 */
		private void initializeTimeoutMonitor ()
		{
			if (taskDescriptor.isSetFailurePolicy ())
			{
				long timeout = taskDescriptor.getFailurePolicy ().getTimeoutRun ();
				if (timeout > 0)
				{
					killer = new TaskProcessKiller (timeout);
					killer.start ();
				}
			}
		}

		
		/**
		 * Terminates the timeout monitor if it was launched.
		 */
		private void shutdownTimeoutMonitor ()
		{
			if (killer != null)
			{
				// It might be necessary to interrupt the sleep.
				// Interrupting should be safe even for dead thread.
				killer.interrupt ();
				
				// Join the timeout monitor thread.
				try { killer.join (); }
				catch (InterruptedException e) { reportFailure ("Unexpected interruption while shutting down the timeout monitor.", e); }

				// Conserve memory.
				killer = null;
			}
		}
		

		/**
		 * Terminates the task process forcefully.
		 */
		private void killTask ()
		{
			processKilled = true;
			process.destroy ();
			displayMessage ("Task killed externally.");
		}


		/**
		 * Waits for the task process to finish.
		 */
		private void waitForTask ()
		{
			try { process.waitFor (); }
			catch (InterruptedException e) { reportFailure ("Unexpected interruption while shutting down the timeout monitor.", e); }
		}


		@Override
		public void run ()
		{
			try
			{
				int runCount = 0;
				int runMax = 0;
				
				boolean executeTask = true;
				boolean wasSuccessful = false;
	
				// For tasks that can be restarted, the task descriptor tells us how many times.
				if (taskDescriptor.isSetFailurePolicy ()) runMax = taskDescriptor.getFailurePolicy ().getRestartMax ();
	
				// Prepare for debugging.
				prepareForDebugging ();
				// Construct the task command line.
				List<String> commandLineList = buildCommandLine ();
				String [] commandLineArray = commandLineList.toArray (new String [commandLineList.size ()]);
				
				// TODO Before rewrite, the load monitor was initialized and terminated for each execution.
				// There did not seem to be any particular reason for that, so now it is only initialized
				// and shut down once across potentially multiple executions due to restarts.
				initializeLoadMonitor ();
				
				while (executeTask)
				{
					// The task temporary directory is created for each execution.
					safeCreateDirectory (temporaryDirectory);
					
					// Just execute the task process.
					try
					{
						// The process working directory must be set to the task directory otherwise class path does not work.
						process = Runtime.getRuntime ().exec (commandLineArray, null, new File (taskDirectory));
					}
					catch (IOException e)
					{
						reportFailure ("Error executing the task process.", e);
						// TODO A forced system exit was here. This should not happen anywhere in the runtime.
						throw (e);
					}
	
					// We do not feed any input to the task, we can therefore close its input immediately.
					process.getOutputStream ().close ();
					// The standard output and the error output of the task are handled by output processors.
					// For tasks that are not exclusive, the output is forwarded immediately.
					// For exclusive tasks, the output is forwarded on termination.
					OutputForwardingType forwardingType =
						(taskDescriptor.getExclusive () == TaskExclusivity.NON_EXCLUSIVE)
						? OutputForwardingType.CONTINUOUSLY
						: OutputForwardingType.ON_TERMINATION; 
					ProcessOutputProcessor outputProcessor = new ProcessOutputProcessor (process.getInputStream (), ProcessOutputType.STANDARD, forwardingType);
					ProcessOutputProcessor errorProcessor = new ProcessOutputProcessor (process.getErrorStream (), ProcessOutputType.ERROR, forwardingType);
					outputProcessor.start ();
					errorProcessor.start ();
	
					if (runCount == 0)
					{
						displayMessage ("Task process started.");
						signalCheckPoint (Task.CHECKPOINT_NAME_STARTED, null);
					}
					else
					{
						displayMessage ("Task process restarted.");
						taskManagerRuntimePort.taskRestarted (taskId, contextId);
					}
	
					// Wait for the process to complete with timeout if applicable.
					initializeTimeoutMonitor ();
					try { process.waitFor (); }
					catch (InterruptedException e) { reportFailure ("Unexpected interruption while waiting for task process.", e); }
					shutdownTimeoutMonitor ();

					// Cache the exit value so that we can get rid of the process object.
					// We clean up the object references aggressively to conserve memory. 
					exitValue = process.exitValue ();
					process = null;

					displayMessage ("Task process terminated with exit code " + exitValue + ".");
	
					// Join the process output processors, thus closing the streams as well.
					// Again, clean up the object references aggressively to conserve memory. 
					outputProcessor.join ();
					outputProcessor = null;
					errorProcessor.join ();
					errorProcessor = null;
					
					// The task temporary directory is deleted immediately.
					Delete.deleteDirectory (temporaryDirectory);
	
					// See whether the task was executed successfully.
					wasSuccessful = (exitValue == 0) && (!processTimedOut) && (!processKilled);
					executeTask = (!wasSuccessful) && (runCount < runMax);
					runCount++;
				}
	
				terminateLoadMonitor ();
				flushLocalLogStorage ();
	
				// The task directory is deleted when the task will not execute.
				Delete.deleteDirectory (taskDirectory);
	
				// The ordering of the final notifications is tricky !
				// First, the task has to be marked as no longer running to avoid illegal state exceptions.
				// Second, the finish checkpoint must be signaled, so that the host runtime sees it.
				// Third, the host runtime and the task manager must be notified.
				// This can result in closing the context.
				isRunning = false;
				if (!processKilled) signalCheckPoint (Task.CHECKPOINT_NAME_FINISHED, exitValue);
				hostRuntime.notifyTaskFinished (TaskImplementation.this, processKilled ? TaskState.ABORTED : TaskState.FINISHED);
	
				displayMessage ("Task execution finished.");
				
				// Forget yourself to conserve memory.
				executor = null;
			}
			catch (Exception e)
			{
				reportFailure ("An error during task execution.", e);
			}
		}
	}

	
	//----------------------------------------------------------------------
	// Task Implementation Class Body

	
	/**
	 * Prepares the task directories that stay with the task.
	 */
	private void prepareTaskDirectories () throws TaskException
	{
		try
		{
			// Creating the base directory is not strictly necessary
			// because the other functions create leading directories.
			safeCreateDirectory (baseDirectory);

			// This is where the task results go.
			safeCreateDirectory (workingDirectory);
			
			// Three directories are not created here.
			// The task directory is created during installation.
			// The service directory is created during extraction.
			// The temporary directory is created during execution.
			
			// The task directory could have remained from previous executions of the same task.
			// It must be deleted so that it does not interfere with package installation.
			try { Delete.deleteDirectory (taskDirectory); }
			catch (AntTaskException e) { }
		}
		catch (IOException e)
		{
			throw new TaskException (e);
		}
	}

	
	/**
	 * Extract the task package into the service directory. 
	 */
	private void extractTaskPackage () throws TaskException
	{
		try
		{
			String packageName = taskDescriptor.getPackage ().getName ();
			hostRuntime.getPackageCacheManager ().extractPackage (packageName, serviceDirectory, PackageType.TASK);
		}
		catch (HostRuntimeException e) { throw new TaskException (e); }
		catch (IOException e) { throw new TaskException (e); }
	}


	/**
	 * Reads the package configuration from the extracted package files. 
	 */
	private void readPackageConfiguration () throws TaskException
	{
		taskPackageConfiguration = new PackageConfiguration (
			serviceDirectory + File.separator + "config.xml",
			hostRuntime.getRootDirectory () + File.separator + "package-configuration.dtd");
	}

	
	/**
	 * Moves files from the package to the task directory and sets their access rights.
	 */
	private void installPackageFiles () throws TaskException
	{
		try
		{
			// The files in question are the files in the package files directory.
			// We just move this directory so that it becomes the task directory.
			File files = new File (serviceDirectory + File.separator + "files");
			if (!files.renameTo (new File (taskDirectory)))
			{
				throw new TaskException ("Error installing the package files.");
			}
	
			// Set the access rights as necessary.
			// TODO Does everything need to be executable ?
			Chmod.recursiveDirectoryChmod (taskDirectory, "u+rwx");
		}
		catch (AntTaskException e) { throw new TaskException (e); }
	}

	
	/**
	 * Fetches the property objects from the task descriptor.
	 * 
	 * @throws TaskException
	 */
	private void extractPropertyObjects () throws TaskException
	{
		taskPropertyObjects = new TreeMap<String, Serializable> ();
		try
		{
			if (taskDescriptor.isSetTaskPropertyObjects ())
			{
				TaskPropertyObjects propertyObjects = taskDescriptor.getTaskPropertyObjects ();
				if (propertyObjects.isSetTaskPropertyObject ())
				{
					for (TaskPropertyObject propertyObject : propertyObjects.getTaskPropertyObject ())
					{
						taskPropertyObjects.put (
								propertyObject.getKey (),
								propertyObject.isSetStrVal ()
								? Deserialize.fromString (propertyObject.getStrVal ())
								: propertyObject.isSetBinVal ()
								? Deserialize.fromBase64 (propertyObject.getBinVal ())
								: null);
					}
				}
			}
		}
		catch (DeserializeException e)
		{
			throw new TaskException (e);
		}
	}

	
	/**
	 * Executes the given task.
	 * 
	 * The task has to be cleaned afterwards using its destroy method ! 
	 * 
	 * @param taskDescriptor		The task descriptor of this task.
	 * @param hostRuntime			The host runtime in which to run this task.
	 * @param measureDetailedLoad	Whether to measure detailed load for this task.
	 * 
	 * @throws TaskException
	 * @throws RemoteException
	 */
	protected TaskImplementation(
			TaskDescriptor taskDescriptor,
			HostRuntimeImplementation hostRuntime,
			boolean measureDetailedLoad)
			throws TaskException, RemoteException
	{
		this.taskDescriptor = taskDescriptor;
		this.hostRuntime = hostRuntime;
		this.measureDetailedLoad = measureDetailedLoad;

		// Cache some values that are really used everywhere in the code.
		
		taskId = taskDescriptor.getTaskId ();
		contextId = taskDescriptor.getContextId ();
		
		taskManagerRuntimePort = hostRuntime.getHostRuntimesPort ();
		hostRuntimeName = taskDescriptor.getHostRuntimes ().getName ().get (0);
		
		taskPortUri = RMI.URL_PREFIX + "/been/hostruntime/tasksport" + taskId;

		baseDirectory = hostRuntime.getBaseDirectoryForTask (contextId, taskId);
		taskDirectory = hostRuntime.getTaskDirectoryForTask (contextId, taskId);
		workingDirectory = hostRuntime.getWorkingDirectoryForTask (contextId, taskId);
		temporaryDirectory = hostRuntime.getTemporaryDirectoryForTask (contextId, taskId);
		serviceDirectory = hostRuntime.getServiceDirectoryForTask (contextId, taskId);

		displayMessage ("Task starting.");
		
		// Get whatever properties the task has in the task descriptor. 
		extractPropertyObjects ();

		// Prepare the essential directories.
		prepareTaskDirectories ();
		
		// Unpacking and installation of the package contents takes place in the service directory, which is not needed afterwards. 
		try { safeCreateDirectory (serviceDirectory); }
		catch (IOException e) { throw new TaskException (e); }
		
		extractTaskPackage ();
		readPackageConfiguration ();
		installPackageFiles ();
		
		try { Delete.deleteDirectory (serviceDirectory); }
		catch (AntTaskException e) { reportFailure (null, e); throw new TaskException (e); }

		// Create and bind the task port. This needs to be done before the task starts executing.
		TasksPortImplementation taskPort = new TasksPortImplementation (this);
		try { Naming.rebind (taskPortUri, taskPort); }
		catch (MalformedURLException e) { throw new TaskException (e); }

		// Execute the beast :-) ...
		executor = new TaskProcessExecutor ();
		executor.start ();
	}

	
	/**
	 * @see cz.cuni.mff.been.hostruntime.TaskInterface#isRunning()
	 */
	public boolean isRunning () { return (isRunning); }
	
	/**
	 * Forces an immediate termination of the executing task.
	 * 
	 * @see cz.cuni.mff.been.hostruntime.TaskInterface#kill()
	 */
	public void kill () { if (isRunning) executor.killTask (); }
	
	/**
	 * @see cz.cuni.mff.been.hostruntime.TaskInterface#waitFor()
	 */
	public void waitFor () { if (isRunning) executor.waitForTask (); }

	/**
	 * @see cz.cuni.mff.been.hostruntime.TaskInterface#getExitValue()
	 */
	public int getExitValue () { return (exitValue); }
	
	
	/**
	 * @see cz.cuni.mff.been.hostruntime.TaskInterface#destroy()
	 */
	public void destroy () throws TaskException
	{
		if (isRunning) throw new IllegalStateException ("You must call this method only on tasks that are not running.");

		// Note that the executor thread can still be running at this time,
		// but it is in the final execution phase and does not need
		// either the directory or the registration.
		
		try
		{
			Delete.deleteDirectory (baseDirectory);
			Naming.unbind (taskPortUri);
		}
		catch (Exception e)
		{
			reportFailure ("Error destroying task.", e);
			throw new TaskException ("Error destroying task.", e);
		}
	}

	
	//----------------------------------------------------------------------
	// Dumb Getters And Setters

	/** @see cz.cuni.mff.been.hostruntime.TaskInterface#getTaskDirectory() */
	public String getTaskDirectory () throws RemoteException
	{ return taskDirectory; }

	/** @see cz.cuni.mff.been.hostruntime.TaskInterface#getWorkingDirectory() */
	public String getWorkingDirectory () throws RuntimeException
	{ return workingDirectory; }

	/** @see cz.cuni.mff.been.hostruntime.TaskInterface#getTemporaryDirectory() */
	public String getTemporaryDirectory () throws RuntimeException
	{ return temporaryDirectory; }

	/** @see cz.cuni.mff.been.hostruntime.TaskInterface#getTaskID() */
	public String getTaskID () throws RemoteException
	{ return taskDescriptor.getTaskId (); }

	/** @see cz.cuni.mff.been.hostruntime.TaskInterface#getContextID() */
	public String getContextID () throws RemoteException
	{ return taskDescriptor.getContextId (); }

	/** @see cz.cuni.mff.been.hostruntime.TaskInterface#isDetailedLoad() */
	public boolean isDetailedLoad () throws RemoteException
	{ return measureDetailedLoad; }

	/** @return Task descriptor of this task. */
	public TaskDescriptor getTaskDescriptor () { return taskDescriptor; }

	/** @return A map of task property objects. */
	public Map<String, Serializable> getTaskPropertyObjects () { return taskPropertyObjects; }

	/** @return Host Runtime in which this task is running */
	public HostRuntimeImplementation getHostRuntime () { return (hostRuntime); }
}
