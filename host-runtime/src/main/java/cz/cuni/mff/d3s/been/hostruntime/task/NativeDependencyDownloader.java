package cz.cuni.mff.d3s.been.hostruntime.task;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.bpk.NativeRuntime;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA. User: donarus Date: 4/21/13 Time: 5:47 PM To
 * change this template use File | Settings | File Templates.
 */
public class NativeDependencyDownloader implements DependencyDownloader {

	private NativeRuntime runtime;

	public NativeDependencyDownloader(NativeRuntime runtime) {
		this.runtime = runtime;
	}

	@Override
	public Collection<BpkIdentifier> getBkpDependencies() {
		if (runtime.getBpkDependencies() != null) {
			return runtime.getBpkDependencies().getDependency();
		}

		return new LinkedList<>();
	}

	@Override
	public Collection<ArtifactIdentifier> getArtifactDependencies() {
		return new LinkedList<>();
	}
}
