package cz.cuni.mff.d3s.been.swrepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	 *            The artifact's <code>groupId</code>
	 * @param artifactId
	 *            The artifact's <code>artifactId</code>
	 * @param version
	 *            The artifact's <code>version</code>
	 * 
	 * @return An opened reader stream on the artifact's file or
	 *         <code>null</code> if the file doesn't exist
	 * 
	 * @throws IOException
	 *             When the artifact file exists but can't be opened for reading
	 */
	InputStream getArtifactReaderStream(String groupId, String artifactId,
			String version) throws IOException;

	/**
	 * Store a Maven artifact into the repository.
	 * 
	 * @param groupId
	 *            The artifact's <code>groupId</code>
	 * @param artifactId
	 *            The artifact's <code>artifactId</code>
	 * @param version
	 *            The artifact's <code>version</code>
	 * 
	 * @return An opened writer stream on the artifact's file
	 * 
	 * @throws IOException
	 *             When the artifact file can't be created or opened for writing
	 */
	OutputStream getArtifactPersister(String groupId, String artifactId,
			String version) throws IOException;
}
