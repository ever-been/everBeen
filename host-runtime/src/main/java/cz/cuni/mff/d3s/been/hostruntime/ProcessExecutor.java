package cz.cuni.mff.d3s.been.hostruntime;

import java.io.IOException;

public class ProcessExecutor {

	public Process execute(String cmd) throws IOException {
		return Runtime.getRuntime().exec(cmd);
	}

}
