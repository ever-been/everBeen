package cz.cuni.mff.d3s.been.hostruntime.task;

import java.util.Collection;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;

/**
 * 
 * Dependency downloader interface
 * 
 * @author donarus
 */
public interface DependencyDownloader {

	/**
	 * Returns list of artifact dependencies.
	 * 
	 * @return list of artifact dependencies
	 */
	Collection<ArtifactIdentifier> getArtifactDependencies();

	/**
	 * Returns list of BPK dependencies.
	 * 
	 * @return list of BPK dependencies
	 */
	Collection<BpkIdentifier> getBkpDependencies();
}
