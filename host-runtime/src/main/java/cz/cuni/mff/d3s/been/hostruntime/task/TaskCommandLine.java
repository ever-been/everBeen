package cz.cuni.mff.d3s.been.hostruntime.task;

import java.io.File;

import org.apache.commons.exec.CommandLine;

public class TaskCommandLine extends CommandLine {

	public TaskCommandLine(File executable) {
		super(executable);
	}

	public TaskCommandLine(String executable) {
		super(executable);
	}

	boolean debugListeningMode;

	int debugPort;

	boolean suspended = false;

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
