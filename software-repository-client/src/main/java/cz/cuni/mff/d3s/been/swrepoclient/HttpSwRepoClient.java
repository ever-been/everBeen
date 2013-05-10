package cz.cuni.mff.d3s.been.swrepoclient;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.Bpk;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.ConvertorException;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils.JSONSerializerException;
import cz.cuni.mff.d3s.been.datastore.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.artifact.Artifact;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

class HttpSwRepoClient implements SwRepoClient {

	/**
	 * HTTP implementation specific log for the sw repo client
	 */
	private static final Logger log = LoggerFactory.getLogger(HttpSwRepoClient.class);

	/**
	 * Hostname where the software repository resides
	 */
	private final String hostname;

	/**
	 * Port on which the software repository listens
	 */
	private final Integer port;

	/**
	 * Data store to use for caching.
	 */
	private final SoftwareStore softwareCache;

	HttpSwRepoClient(String hostname, Integer port, SoftwareStore softwareCache) {
		this.hostname = hostname;
		this.port = port;
		this.softwareCache = softwareCache;
	}

	// --------------------------------------
	// API IMPLEMENTATION METHODS
	// --------------------------------------
	@Override
	public boolean putArtifact(ArtifactIdentifier artifactIdentifier, InputStream artifactInputStream) {
		if (artifactIdentifier == null) {
			log.error("Failed to upload Artifact {} - artifact meta-info was null.", artifactIdentifier);
			return false;
		}

		Header header = new Header(SwRepoClientFactory.ARTIFACT_IDENTIFIER_HEADER_NAME, artifactIdentifier);
		return doPutStream("/artifact", artifactInputStream, header);
	}

	@Override
	public Artifact getArtifact(ArtifactIdentifier artifactIdentifier) {
		StoreReader artifactReader = softwareCache.getArtifactReader(artifactIdentifier);
		if (artifactReader != null) {
			return new ArtifactFromStore(artifactIdentifier, artifactReader);
		} else {
			return getArtifactByHTTP(artifactIdentifier);
		}
	}

	@Override
	public Bpk getBpk(BpkIdentifier bpkIdentifier) {
		StoreReader bpkReader = softwareCache.getBpkReader(bpkIdentifier);
		if (bpkReader != null) {
			return new BpkFromStore(bpkReader, bpkIdentifier);
		} else {
			return getBpkByHTTP(bpkIdentifier);
		}
	}

	@Override
	public boolean putBpk(BpkIdentifier bpkMetaInfo, InputStream bpkInputStream) {
		if (bpkMetaInfo == null) {
			log.error("Failed to upload BPK {} - package meta-info was null.", bpkMetaInfo);
			return false;
		}

		Header header = new Header(SwRepoClientFactory.BPK_IDENTIFIER_HEADER_NAME, bpkMetaInfo);
		return doPutStream("/bpk", bpkInputStream, header);
	}

	/**
	 * Return a list of all uploaded BPKs.
	 */
	@Override
	public Collection<BpkIdentifier> listBpks() {
		return doGetObject("/bpklist", new TypeReference<List<BpkIdentifier>>() {
		});
	}

	/**
	 * Return a list of all uploaded BPKs.
	 */
	@Override
	public Map<String, TaskDescriptor> listTaskDescriptors(BpkIdentifier bpkIdentifier) {
		Header header = new Header(SwRepoClientFactory.BPK_IDENTIFIER_HEADER_NAME, bpkIdentifier);
		// 1st argument = TD filename, 2nd argument = TD json

		Map<String, String> jsonDescriptors = doGetObject("/tdlist", new TypeReference<Map<String, String>>() {
		}, header);

		Map<String, TaskDescriptor> convertedDescriptors = new HashMap<>();
		if (jsonDescriptors != null) {
			for (Map.Entry<String, String> entry : jsonDescriptors.entrySet()) {
				BindingParser<TaskDescriptor> parser = null;
				try {
					parser = XSD.TASK_DESCRIPTOR.createParser(TaskDescriptor.class);
					convertedDescriptors
							.put(entry.getKey(), parser.parse(new ByteArrayInputStream(entry.getValue().getBytes())));
				} catch (SAXException | ConvertorException | JAXBException e) {
					log.error(String.format("Failed to convert task descriptor %s", entry.getKey()), e);
					continue;
				}
			}
		}

		return convertedDescriptors;
	}

	// =====================================
	// PRIVATE METHODS
	// =====================================

	/**
	 * Synthesize the URI of the software repository from internals
	 *
	 * @return the URI of the repository
	 * @throws URISyntaxException When some of the internals are malformed
	 */
	private URI createRepoUri() throws URISyntaxException {
		URIBuilder uriBuilder = new URIBuilder();
		uriBuilder.setHost(hostname);
		uriBuilder.setPort(port);
		uriBuilder.setScheme("http");
		return uriBuilder.build();
	}

	/**
	 * Ask the repository for a Maven artifact by HTTP
	 */
	private Artifact getArtifactByHTTP(ArtifactIdentifier artifactIdentifier) {
		Header header = new Header(SwRepoClientFactory.ARTIFACT_IDENTIFIER_HEADER_NAME, artifactIdentifier);

		InputStream is = doGetInputStream("/artifact", header);
		if (is == null) {
			return null;
		}

		StorePersister sp = softwareCache.getArtifactPersister(artifactIdentifier);
		try {
			sp.dump(is);
		} catch (IOException e) {
			log.error("Failed to cache Artifact {} locally - {}", artifactIdentifier.toString(), e.getMessage());
			return null;
		} finally {
			IOUtils.closeQuietly(is);
		}

		StoreReader sr = softwareCache.getArtifactReader(artifactIdentifier);
		if (sr == null) {
			return null;
		} else {
			return new ArtifactFromStore(artifactIdentifier, sr);
		}
	}

	/**
	 * Ask the repository for a BPK by HTTP
	 */
	private Bpk getBpkByHTTP(BpkIdentifier bpkIdentifier) {
		Header header = new Header(SwRepoClientFactory.BPK_IDENTIFIER_HEADER_NAME, bpkIdentifier);

		InputStream is = doGetInputStream("/bpk", header);
		if (is == null) {
			return null;
		}

		StorePersister sp = softwareCache.getBpkPersister(bpkIdentifier);
		try {
			sp.dump(is);
		} catch (IOException e) {
			log.error("Failed to cache BPK {} locally - {}", bpkIdentifier.toString(), e.getMessage());
			return null;
		} finally {
			IOUtils.closeQuietly(is);
		}

		StoreReader sr = softwareCache.getBpkReader(bpkIdentifier);
		if (sr == null) {
			return null;
		} else {
			return new BpkFromStore(softwareCache.getBpkReader(bpkIdentifier), bpkIdentifier);
		}
	}


	private <T> T doGetObject(String abstractUri, TypeReference<T> type, Header... headers) {
		try (InputStream is = doGetInputStream(abstractUri, headers)) {

			if (is == null) {
				return null;
			}

			String jsonString = IOUtils.toString(is);
			return JSONUtils.deserialize(jsonString, type);
		} catch (IOException e) {
			log.error(
					"Failed to GET item from software repository - I/O exception occurs when reading response input stream to string",
					e);
			return null;
		} catch (JSONSerializerException e) {
			log.error(
					"Failed to GET item from software repository - cannot deserialize return value from json string to object",
					e);
			return null;
		}
	}

	private InputStream doGetInputStream(String abstractUri, Header... headers) {

		String uri;
		try {
			uri = createRepoUri() + abstractUri;
		} catch (URISyntaxException e) {
			log.error("Failed to GET item from software repository - unable to synthesize GET request URI", e);
			return null;
		}

		HttpUriRequest request = new HttpGet(uri);

		try {
			for (Header header : headers) {
				request.addHeader(header.key, JSONUtils.serialize(header.value));
			}
		} catch (JSONSerializerException e) {
			log.error(
					"Failed to GET item from software repository - cannot serialize request header object to json string",
					e);
			return null;
		}

		HttpClient httpCli = new DefaultHttpClient();

		HttpResponse response;
		try {
			response = httpCli.execute(request);
		} catch (ClientProtocolException e) {
			log.error("Failed to GET item from software repository - http protocol error", e);
			return null;
		} catch (IOException e) {
			log.error("Failed to GET item from software repository - I/O error or connection re-set", e);
			return null;
		}

		if (response.getStatusLine().getStatusCode() / 100 != 2) {
			log.error("Failed to GET item from software repository - server refusal: '%s'",
					response.getStatusLine().getReasonPhrase());
			return null;
		}

		try {
			return response.getEntity().getContent();
		} catch (IOException e) {
			log.error("Failed to GET item from software repository - content stream cannot be opened", e);
			return null;
		}

	}

	private boolean doPutStream(String abstractUri, InputStream objectStreamToPut, Header... headers) {
		if (objectStreamToPut == null) {
			log.error("Failed to PUT item to software repository - object given to send was null");
			return false;
		}

		String uri;
		try {
			uri = createRepoUri() + abstractUri;
		} catch (URISyntaxException e) {
			log.error("Failed to PUT item to software repository - unable to synthesize PUT request URI", e);
			return false;
		}


		HttpPut request = new HttpPut(uri);

		try {
			for (Header header : headers) {
				request.addHeader(header.key, JSONUtils.serialize(header.value));
			}
		} catch (JSONSerializerException e) {
			log.error(
					"Failed to PUT item to software repository - cannot serialize request header object to json string",
					e);
			return false;
		}

		InputStreamEntity sentEntity = new InputStreamEntity(objectStreamToPut, -1);
		request.setEntity(sentEntity); // entity closes the stream

		HttpClient httpCli = new DefaultHttpClient();

		HttpResponse response;
		try {
			response = httpCli.execute(request);
		} catch (ClientProtocolException e) {
			log.error("Failed to PUT item to software repository - http protocol error", e);
			return false;
		} catch (IOException e) {
			log.error("Failed to PUT item to software repository - I/O error or connection re-set", e);
			return false;
		}

		if ((response.getStatusLine().getStatusCode() / 100) != 2) {
			log.error("Failed to PUT item to software repository - server error: '%s'",
					response.getStatusLine().getReasonPhrase());
			return false;
		} else {
			return true;
		}
	}

	private static final class Header {
		public String key;
		public Object value;

		public Header(String key, Object value) {
			this.key = key;
			this.value = value;
		}
	}

}
