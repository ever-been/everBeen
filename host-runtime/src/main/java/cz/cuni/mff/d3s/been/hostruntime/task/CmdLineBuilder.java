package cz.cuni.mff.d3s.been.hostruntime.task;

import cz.cuni.mff.d3s.been.hostruntime.TaskException;

public interface CmdLineBuilder {

	public TaskCommandLine build() throws TaskException;

}
