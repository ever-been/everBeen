package cz.cuni.mff.d3s.been.hostruntime.task;

import java.util.Collection;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;

/**
 * @author donarus
 */
public interface DependencyDownloader {

	Collection<ArtifactIdentifier> getArtifactDependencies();

	Collection<BpkIdentifier> getBkpDependencies();
}
