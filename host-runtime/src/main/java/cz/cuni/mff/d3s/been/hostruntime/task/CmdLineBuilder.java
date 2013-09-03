package cz.cuni.mff.d3s.been.hostruntime.task;

import cz.cuni.mff.d3s.been.hostruntime.TaskException;

/**
 * Command line builder interface.
 * 
 * @author Tadeas Palusga
 */
public interface CmdLineBuilder {

	/**
	 * Builds command line.
	 * 
	 * @return task's command line
	 * 
	 * @throws TaskException
	 *           when command line cannot be generated.
	 */
	public TaskCommandLine build() throws TaskException;

}
