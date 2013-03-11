package cz.cuni.mff.d3s.been.swrepoclient;

import cz.cuni.mff.d3s.been.datastore.DataStore;

public class SwRepoClientFactory {
	/**
	 * Name of the HTTP header that contains the identifier of a
	 * requested/uploaded Maven Artifact
	 */
	public static final String ARTIFACT_IDENTIFIER_HEADER_NAME = "Artifact-Identifier";
	/**
	 * Name of the HTTP header that contains the identifier of a
	 * requested/uploaded BEEN package
	 */
	public static final String BPK_IDENTIFIER_HEADER_NAME = "Bpk-Identifier";

	final DataStore softwareCache;

	public SwRepoClientFactory(DataStore softwareCache) {
		this.softwareCache = softwareCache;
	}

	public SwRepoClient getClient(String hostname, int port) {
		return new HttpSwRepoClient(hostname, port, softwareCache);
	}
}
