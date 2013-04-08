package cz.cuni.mff.d3s.been.hostruntime.cmdline;

import cz.cuni.mff.d3s.been.hostruntime.TaskException;

public interface CmdLineBuilder {

	public TaskCommandLine create() throws TaskException;

}
