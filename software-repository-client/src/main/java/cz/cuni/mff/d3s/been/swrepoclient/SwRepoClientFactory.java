package cz.cuni.mff.d3s.been.swrepoclient;

import cz.cuni.mff.d3s.been.datastore.SoftwareStore;

public class SwRepoClientFactory {

	private final SoftwareStore softwareCache;

	public SwRepoClientFactory(SoftwareStore softwareCache) {
		this.softwareCache = softwareCache;
	}

	public SwRepoClient getClient(String hostname, int port) {
		return new HttpSwRepoClient(hostname, port, softwareCache);
	}
}
