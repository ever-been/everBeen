package cz.cuni.mff.d3s.been.hostruntime.task;

import java.util.Collection;
import java.util.LinkedList;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.bpk.JavaRuntime;

/**
 * @author donarus
 */
public class JVMDependencyDownloader implements DependencyDownloader {

	private JavaRuntime runtime;

	public JVMDependencyDownloader(JavaRuntime runtime) {
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
		if (runtime.getBpkArtifacts() != null) {
			return runtime.getBpkArtifacts().getArtifact();
		}

		return new LinkedList<>();
	}
}
