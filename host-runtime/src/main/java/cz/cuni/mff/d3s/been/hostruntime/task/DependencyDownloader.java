package cz.cuni.mff.d3s.been.hostruntime.task;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA. User: donarus Date: 4/21/13 Time: 5:42 PM To
 * change this template use File | Settings | File Templates.
 */
public interface DependencyDownloader {

    Collection<ArtifactIdentifier> getArtifactDependencies();

    Collection<BpkIdentifier> getBkpDependencies();
}
