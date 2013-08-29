package cz.cuni.mff.d3s.been.datastore;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;

/**
 * Data storage for Maven artifacts.
 * 
 * @author darklight
 * 
 */
public interface ArtifactStore {
	/**
	 * Retrieve a Maven artifact from the objectrepository
	 * 
	 * @param artifactIdentifier
	 *          Full identifier of the Maven artifact
	 * 
	 * @return A reader object that enables R/O access to a Maven artifact
	 */
	StoreReader getArtifactReader(ArtifactIdentifier artifactIdentifier);

	/**
	 * Store a Maven artifact into the objectrepository.
	 * 
	 * @param artifactIdentifier
	 *          Full identifier of the maven artifact
	 * 
	 * @return A writer object that enables the user to persist a Maven artifact
	 */
	StorePersister getArtifactPersister(ArtifactIdentifier artifactIdentifier);
}
