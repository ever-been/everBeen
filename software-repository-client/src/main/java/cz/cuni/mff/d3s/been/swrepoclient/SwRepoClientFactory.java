package cz.cuni.mff.d3s.been.swrepoclient;

import cz.cuni.mff.d3s.been.datastore.SoftwareStore;

/**
 * Factory for <em>Software Repository</em> clients
 */
public class SwRepoClientFactory {

	private final SoftwareStore softwareCache;

	/**
	 * Create a client factory
	 *
	 * @param softwareCache Software cache for the clients to use
	 */
	public SwRepoClientFactory(SoftwareStore softwareCache) {
		this.softwareCache = softwareCache;
	}

	/**
	 * Create a client
	 *
	 * @param hosts The hosts that the Software Repository is listening on
	 *
	 * @return The client
	 */
	public SwRepoClient getClient(String hosts) {
		return new HttpSwRepoClient(hosts, softwareCache);
	}
}
