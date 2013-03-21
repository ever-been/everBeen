package cz.cuni.mff.d3s.been.hostruntime;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.maven.artifact.Artifact;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.Bpk;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.cluster.context.Services;
import cz.cuni.mff.d3s.been.core.sri.SWRepositoryInfo;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

/**
 * 
 * Serves as a mediator to Software Repository for Host Runtime.
 * 
 * @author Martin Sixta
 */
class SoftwareResolver {
	private Services services;
	private SwRepoClientFactory clientFactory;

	/**
	 * Creates new instance of the class.
	 * 
	 * @param services
	 *          Connection to the cluster.
	 * @param clientFactory
	 *          Software Repository Client Factory
	 */
	public SoftwareResolver(Services services, SwRepoClientFactory clientFactory) {
		this.services = services;
		this.clientFactory = clientFactory;
	}

	/**
	 * Returns bpk handle.
	 * 
	 * The process may include downloading files from Software Repository.
	 * 
	 * @param bpkIdentifier
	 *          what to download
	 * @throws TaskException
	 *           when bpk cannot be obtained
	 */
	public Bpk getBpk(BpkIdentifier bpkIdentifier) throws TaskException {

		Bpk bpk = getClient().getBpk(bpkIdentifier);
		if (bpk == null) {
			throw new TaskException(String.format("Missing bpk '%s' in software repository. ", bpkIdentifier));
		}

		return bpk;
	}

	/**
	 * Returns bpk handle.
	 * 
	 * The process may include downloading files from Software Repository.
	 * 
	 * Unfortunately TaskDescriptor does not use BpkIdentifier directly, but it's
	 * pain in the a*s to create it manually. So this function is provided.
	 * 
	 * 
	 * @param td
	 *          what to download
	 * @throws TaskException
	 *           when bpk cannot be obtained
	 */
	@Deprecated
	public Bpk getBpk(TaskDescriptor td) throws TaskException {
		BpkIdentifier bpkIdentifier = createBpkIdentifier(td);
		return getBpk(bpkIdentifier);
	}

	public Artifact getArtifact(ArtifactIdentifier artifactIdentifier) throws TaskException {
		Artifact artifact = getClient().getArtifact(artifactIdentifier);

		if (artifact == null) {
			// TODO
			throw new TaskException(String.format("Missing Artifact '%s' in software repository. ", artifact.toString()));
		}

		return artifact;
	}

	public Collection<Artifact> resolveArtifacts(
			Collection<ArtifactIdentifier> identifiers) throws TaskException {
		SwRepoClient client = getClient();

		Collection<Artifact> artifacts = new LinkedList<>();

		for (ArtifactIdentifier identifier : identifiers) {
			Artifact artifact = client.getArtifact(identifier);

			if (artifact == null) {
				artifacts.clear();
				throw new TaskException(String.format("Cannot resolve artifact %s", artifact));
			}
		}

		return artifacts;
	}

	public void resolveBpks() throws TaskException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	/**
	 * Creates BpkIdentifier from values in a TaskDescriptor.
	 * 
	 * @param td
	 *          where to take values from
	 * @return BpkIdentifier corresponding to the TaskDescriptor
	 */
	private BpkIdentifier createBpkIdentifier(TaskDescriptor td) {
		BpkIdentifier bpkIdentifier = new BpkIdentifier();
		bpkIdentifier.setGroupId(td.getGroupId());
		bpkIdentifier.setBpkId(td.getBpkId());
		bpkIdentifier.setVersion(td.getVersion());

		return bpkIdentifier;
	}

	/**
	 * 
	 * Gets Software Repository Client.
	 * 
	 * @return client to Software Repository
	 * 
	 * @throws TaskException
	 *           when client cannot be obtained
	 */
	private SwRepoClient getClient() throws TaskException {
		SWRepositoryInfo swRepositoryInfo = services.getSWRepositoryInfo();

		if (swRepositoryInfo == null) {
			throw new TaskException("No Software Repository found!");
		}
		String host = swRepositoryInfo.getHost();
		int port = swRepositoryInfo.getHttpServerPort();

		SwRepoClient swRepoClient = clientFactory.getClient(host, port);
		return swRepoClient;
	}

}
