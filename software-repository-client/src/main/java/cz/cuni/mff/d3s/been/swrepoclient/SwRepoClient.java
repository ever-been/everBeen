package cz.cuni.mff.d3s.been.swrepoclient;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import org.apache.maven.artifact.Artifact;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.Bpk;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;

/**
 * A client to the software repository, this object servers to retrieve BEEN
 * packages and Maven artifacts.
 *
 * @author darklight
 */
public interface SwRepoClient {

	/**
	 * Retrieve a Maven artifact.
	 *
	 * @param artifactIdentifier A fully qualified identifier of the maven artifact
	 * @return The artifact, or <code>null</code> if it could not be retrieved
	 */
	public Artifact getArtifact(ArtifactIdentifier artifactIdentifier);

	/**
	 * Retrieve a BEEN software package (BPK)
	 *
	 * @param bpkMetaInfo A fully qualified identifier of the BEEN package
	 *                    <p/>
	 *                    The BPK, or <code>null</code> if it could not be retrieved
	 */
	public Bpk getBpk(BpkIdentifier bpkMetaInfo);

	/**
	 * Store a Maven artifact
	 *
	 * @param artifactIdentifier  Identifier for the stored artifact
	 * @param artifactInputStream Input stream with the Artifact data
	 * @return <code>true</code> if the Artifact was successfully stored,
	 *         <code>false</code> otherwise
	 */
	public boolean putArtifact(ArtifactIdentifier artifactIdentifier, InputStream artifactInputStream);

	/**
	 * Store a BEEN package from the input stream.
	 *
	 * @param bpkIdentifier  Identifier for the stored artifact
	 * @param bpkInputStream Input stream with the BPK data
	 * @return <code>true</code> if the Bpk was successfully stored,
	 *         <code>false</code> otherwise
	 */
	public boolean putBpk(BpkIdentifier bpkIdentifier, InputStream bpkInputStream);

	/**
	 * List all uploaded BPK packages.
	 *
	 * @return collection of {@link BpkIdentifier} objects
	 */
	public Collection<BpkIdentifier> listBpks();

	/**
	 * List task descriptors of all uploaded BPK packages.
	 *
	 * @param bpkIdentifier identifier of bpk package for which the descriptors will be
	 *                      collected
	 * @return collection of {@link TaskDescriptor} objects
	 */
	public Map<String, TaskDescriptor> listTaskDescriptors(BpkIdentifier bpkIdentifier);

	/**
	 * List task descriptors of all uploaded BPK packages.
	 *
	 * @param bpkIdentifier identifier of bpk package for which the descriptors will be
	 *                      collected
	 * @return collection of {@link TaskContextDescriptor} objects
	 */
	public Map<String, TaskContextDescriptor> listTaskContextDescriptors(BpkIdentifier bpkIdentifier);

}
