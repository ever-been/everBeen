package cz.cuni.mff.d3s.been.hostruntime.task;

import java.util.Collection;
import java.util.LinkedList;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.bpk.NativeRuntime;

/**
 * @author donarus
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
