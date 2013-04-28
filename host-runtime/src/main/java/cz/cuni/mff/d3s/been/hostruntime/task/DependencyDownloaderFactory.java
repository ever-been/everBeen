package cz.cuni.mff.d3s.been.hostruntime.task;

import cz.cuni.mff.d3s.been.bpk.BpkRuntime;
import cz.cuni.mff.d3s.been.bpk.JavaRuntime;
import cz.cuni.mff.d3s.been.bpk.NativeRuntime;
import cz.cuni.mff.d3s.been.hostruntime.TaskException;

/**
 * Created with IntelliJ IDEA. User: donarus Date: 4/21/13 Time: 5:46 PM To
 * change this template use File | Settings | File Templates.
 */
public class DependencyDownloaderFactory {

	/**
	 * Selects and creates correct
	 * {@link DependencyDownloader}
	 * implementation based on runtime type.<br>
	 * <br>
	 * {@link cz.cuni.mff.d3s.been.bpk.JavaRuntime} -&gt;
	 * {@link JVMDependencyDownloader}<br>
	 * {@link cz.cuni.mff.d3s.been.bpk.NativeRuntime} -&gt;
	 * {@link NativeDependencyDownloader}
	 * 
	 * @param runtime
	 * 
	 * @return correct
	 *         {@link DependencyDownloader}
	 *         implementation
	 * @throws cz.cuni.mff.d3s.been.hostruntime.TaskException
	 *           if
	 *           {@link DependencyDownloader}
	 *           implementation for given runtime not defined
	 */
	public static DependencyDownloader create(BpkRuntime runtime) throws TaskException {
		if (runtime instanceof JavaRuntime) {
			return new JVMDependencyDownloader((JavaRuntime) runtime);
		} else if (runtime instanceof NativeRuntime) {
			return new NativeDependencyDownloader((NativeRuntime) runtime);
		} else {
			String msg = String.format("Cannot create artifact downloader for unknown runtime: %s", runtime.getClass());
			throw new TaskException(msg);
		}
	}
}
