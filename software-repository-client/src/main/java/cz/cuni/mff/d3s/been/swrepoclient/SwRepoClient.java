package cz.cuni.mff.d3s.been.swrepoclient;

import java.io.File;

import org.apache.maven.artifact.Artifact;

import cz.cuni.mff.d3s.been.bpk.Bpk;
import cz.cuni.mff.d3s.been.bpk.BpkArtifact;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;

/**
 * A client to the software repository, this object servers to retrieve BEEN
 * packages and Maven artifacts.
 * 
 * @author darklight
 * 
 */
public interface SwRepoClient {
	/**
	 * Retrieve a Maven artifact.
	 * 
	 * @param groupId
	 *            The artifact's <code>groupId</code>
	 * @param artifactId
	 *            The artifact's <code>artifactId</code>
	 * @param version
	 *            The artifact's <code>version</code>
	 * 
	 * @return The artifact, or <code>null</code> if it could not be retrieved
	 */
	public Artifact getArtifact(String groupId, String artifactId,
			String version);

	/**
	 * Retrieve a BEEN software package (BPK)
	 * 
	 * @param bpkMetaInfo
	 *            A fully qualified identifier of the BEEN package
	 * 
	 *            The BPK, or <code>null</code> if it could not be retrieved
	 */
	// FIXME By my opinion this class should throws exception with corresponding description instead of returning NULL values... same in other methods
	public Bpk getBpk(BpkIdentifier bpkMetaInfo);

	/**
	 * Store a Maven artifact
	 * 
	 * @param artifactMetaInfo
	 *            Identifier for the stored artifact
	 * @param artifactFile
	 *            Artifact file to store
	 * 
	 * @return <code>true</code> if the Artifact was successfully stored,
	 *         <code>false</code> if not
	 */
	public boolean putArtifact(BpkArtifact artifactMetaInfo, File artifactFile);

	/**
	 * Store a BEEN package
	 * 
	 * @param bpkMetaInfo
	 *            Identifier of the stored package
	 * @param bpkFile
	 *            File to put
	 * 
	 * @return <code>true</code> if the BPK was successfully stored,
	 *         <code>false</code> if not
	 */
	public boolean putBpk(BpkIdentifier bpkIdentifier, File bpkFile);
}
