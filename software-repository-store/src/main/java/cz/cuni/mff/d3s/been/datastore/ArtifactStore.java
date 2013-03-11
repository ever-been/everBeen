package cz.cuni.mff.d3s.been.datastore;


/**
 * Data storage for Maven artifacts.
 * 
 * @author darklight
 * 
 */
public interface ArtifactStore {
	/**
	 * Retrieve a Maven artifact from the repository
	 * 
	 * @param groupId
	 *          The artifact's <code>groupId</code>
	 * @param artifactId
	 *          The artifact's <code>artifactId</code>
	 * @param version
	 *          The artifact's <code>version</code>
	 * 
	 * @return A reader object that enables R/O access to a Maven artifact
	 */
	StoreReader getArtifactReader(
			String groupId,
			String artifactId,
			String version);

	/**
	 * Store a Maven artifact into the repository.
	 * 
	 * @param groupId
	 *          The artifact's <code>groupId</code>
	 * @param artifactId
	 *          The artifact's <code>artifactId</code>
	 * @param version
	 *          The artifact's <code>version</code>
	 * 
	 * @return A writer object that enables the user to persist a Maven artifact
	 */
	StorePersister getArtifactPersister(
			String groupId,
			String artifactId,
			String version);
}
