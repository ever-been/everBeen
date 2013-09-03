package cz.cuni.mff.d3s.been.hostruntime.task;

import java.io.File;

import org.apache.commons.exec.CommandLine;

/**
 * 
 * Task's command line.
 * 
 * @author Tadeas Palusga
 */
public class TaskCommandLine extends CommandLine {

	/** debug mode */
	protected boolean debugListeningMode;

	/** debug port */
	protected int debugPort;

	/** suspended flag */
	protected boolean suspended = false;

	/**
	 * Creates new TaskCommandLine for a task.
	 * 
	 * @param executable
	 *          target executable of the task
	 */
	public TaskCommandLine(File executable) {
		super(executable);
	}

	/**
	 * Creates new TaskCommandLine for a task.
	 * 
	 * @param executable
	 *          target executable of the task
	 */
	public TaskCommandLine(String executable) {
		super(executable);
	}

	/**
	 * Whether the task should be suspended on start
	 * 
	 * @return true if the task should be suspended on start
	 */
	public boolean isSuspended() {
		return suspended;
	}

	/**
	 * Returns debug port on which the java task will be running.
	 * 
	 * @return port
	 */
	public int getDebugPort() {
		return debugPort;
	}

	/**
	 * Tells if command line has been created with debug parameter which tells
	 * that the task should be listening for debug connections.
	 * 
	 * @return whether the task is in listening debug mode
	 */
	public boolean isDebugListeningMode() {
		return debugListeningMode;
	}

}
