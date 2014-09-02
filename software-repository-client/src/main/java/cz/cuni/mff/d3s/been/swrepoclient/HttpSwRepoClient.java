package cz.cuni.mff.d3s.been.swrepoclient;

import static cz.cuni.mff.d3s.been.swrepository.HeaderNames.ARTIFACT_IDENTIFIER_HEADER_NAME;
import static cz.cuni.mff.d3s.been.swrepository.HeaderNames.BPK_IDENTIFIER_HEADER_NAME;
import static cz.cuni.mff.d3s.been.swrepository.UrlPaths.*;
import static cz.cuni.mff.d3s.been.swrepository.Versions.SNAPSHOT_SUFFIX;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;

import javax.xml.bind.JAXBException;

import cz.cuni.mff.d3s.been.util.SocketAddrUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.artifact.Artifact;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.bpk.Bpk;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.ConvertorException;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.datastore.*;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * An HTTP client to the Software Repository
 */
class HttpSwRepoClient implements SwRepoClient {

	/**
	 * HTTP implementation specific log for the sw repo client
	 */
	private static final Logger log = LoggerFactory.getLogger(HttpSwRepoClient.class);

	/**
	 * Hostnames where the software repository resides
	 */
	private final String hosts;

	/**
	 * Data store to use for caching
	 */
	private final SoftwareStore softwareCache;

	/**
	 * JSON utilities for serialization
	 */
	private final JSONUtils jsonUtils;

	/**
	 * Constructs new software repository client
	 * 
	 * @param hosts
	 *          hostnames on which the software repository is running
	 * @param softwareCache
	 *          initialized software cache
	 */
	HttpSwRepoClient(String hosts, SoftwareStore softwareCache) {
		this.hosts = hosts;
		this.softwareCache = softwareCache;
		this.jsonUtils = JSONUtils.newInstance();
	}

	// --------------------------------------
	// API IMPLEMENTATION METHODS
	// --------------------------------------
	@Override
	public
			void
			putArtifact(ArtifactIdentifier artifactIdentifier, InputStream artifactInputStream) throws SwRepositoryClientException {
		if (artifactIdentifier == null) {
			String msg = "Failed to upload Artifact - artifact meta-info was null.";
			log.error(msg);
			throw new SwRepositoryClientException(msg);
		}

		Header header = new Header(ARTIFACT_IDENTIFIER_HEADER_NAME, artifactIdentifier);
		doPutStream(ARTIFACT_URI, artifactInputStream, header);
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
		if (bpkIdentifier.getVersion().endsWith(SNAPSHOT_SUFFIX)) {
			return getBpkByHTTP(bpkIdentifier);
		}

		StoreReader bpkReader = softwareCache.getBpkReader(bpkIdentifier);
		if (bpkReader != null) {
			return new BpkFromStore(bpkReader, bpkIdentifier);
		} else {
			return getBpkByHTTP(bpkIdentifier);
		}
	}

	@Override
	public Bpk getBpkNoCache(final BpkIdentifier bpkIdentifier) {
		Header header = new Header(BPK_IDENTIFIER_HEADER_NAME, bpkIdentifier);

		final InputStream is = doGetInputStream(BPK_URI, header);
		if (is == null) {
			return null;
		}

		return new Bpk() {
			@Override
			public BpkIdentifier getBpkIdentifier() {
				return bpkIdentifier;
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return is;
			}
		};
	}

	@Override
	public void putBpk(BpkIdentifier bpkMetaInfo, InputStream bpkInputStream) throws SwRepositoryClientException {
		if (bpkMetaInfo == null) {
			String msg = "Failed to upload BPK - package meta-info was null.";
			log.error(msg);
			throw new SwRepositoryClientException(msg);
		}

		Header header = new Header(BPK_IDENTIFIER_HEADER_NAME, bpkMetaInfo);
		doPutStream(BPK_URI, bpkInputStream, header);
	}

	@Override
	public Collection<BpkIdentifier> listBpks() {
		return doGetObject(BPK_LIST_URI, new TypeReference<List<BpkIdentifier>>() {});
	}

	@Override
	public Map<String, TaskContextDescriptor> listTaskContextDescriptors(BpkIdentifier bpkIdentifier) {
		Header header = new Header(BPK_IDENTIFIER_HEADER_NAME, bpkIdentifier);
		// 1st argument = TD filename, 2nd argument = TD json

		Map<String, String> jsonDescriptors = doGetObject(
				TASK_CONTEXT_DESCRIPTOR_LIST_URI,
				new TypeReference<Map<String, String>>() {},
				header);

		Map<String, TaskContextDescriptor> convertedDescriptors = new HashMap<>();
		if (jsonDescriptors != null) {
			for (Map.Entry<String, String> entry : jsonDescriptors.entrySet()) {
				BindingParser<TaskContextDescriptor> parser;
				try {
					parser = XSD.TASK_CONTEXT_DESCRIPTOR.createParser(TaskContextDescriptor.class);
					convertedDescriptors.put(entry.getKey(), parser.parse(new ByteArrayInputStream(entry.getValue().getBytes())));
				} catch (SAXException | ConvertorException | JAXBException e) {
					log.error(String.format("Failed to convert task context descriptor %s", entry.getKey()), e);
				}
			}
		}

		return convertedDescriptors;
	}

	@Override
	public Map<String, TaskDescriptor> listTaskDescriptors(BpkIdentifier bpkIdentifier) {
		Header header = new Header(BPK_IDENTIFIER_HEADER_NAME, bpkIdentifier);
		// 1st argument = TD filename, 2nd argument = TD json

		Map<String, String> jsonDescriptors = doGetObject(
				TASK_DESCRIPTOR_LIST_URI,
				new TypeReference<Map<String, String>>() {},
				header);

		Map<String, TaskDescriptor> convertedDescriptors = new HashMap<>();
		if (jsonDescriptors != null) {
			for (Map.Entry<String, String> entry : jsonDescriptors.entrySet()) {
				BindingParser<TaskDescriptor> parser;
				try {
					parser = XSD.TASK_DESCRIPTOR.createParser(TaskDescriptor.class);
					convertedDescriptors.put(entry.getKey(), parser.parse(new ByteArrayInputStream(entry.getValue().getBytes())));
				} catch (SAXException | ConvertorException | JAXBException e) {
					log.error(String.format("Failed to convert task descriptor %s", entry.getKey()), e);
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
	 * @throws URISyntaxException
	 *           When some of the internals are malformed
	 */
	private URI createRepoUri() throws URISyntaxException, UnknownHostException {
		return sockAddrToURIBuilder().build();
	}

	private InetSocketAddress getRepoAddr() throws UnknownHostException {
		return SocketAddrUtils.getFirstReachableAddress(hosts, 1000);
	}

	private URIBuilder sockAddrToURIBuilder() throws UnknownHostException, URISyntaxException {
		final InetSocketAddress socketAddress = getRepoAddr();
		final URIBuilder uriBuilder = new URIBuilder();
		final InetAddress hostAddr = socketAddress.getAddress();
		uriBuilder.setHost(hostAddr.getCanonicalHostName());
		uriBuilder.setPort(socketAddress.getPort());
		uriBuilder.setScheme("http");
		return uriBuilder;
	}


	/**
	 * Ask the repository for a Maven artifact by HTTP
	 * 
	 * @return found artifact or null when artifact was not found
	 */
	private Artifact getArtifactByHTTP(ArtifactIdentifier artifactIdentifier) {
		Header header = new Header(ARTIFACT_IDENTIFIER_HEADER_NAME, artifactIdentifier);

		InputStream is = doGetInputStream(ARTIFACT_URI, header);
		if (is == null) {
			return null;
		}

		StorePersister sp = softwareCache.getArtifactPersister(artifactIdentifier);
		try {
			sp.dump(is);
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
		Header header = new Header(BPK_IDENTIFIER_HEADER_NAME, bpkIdentifier);

		InputStream is = doGetInputStream(BPK_URI, header);
		if (is == null) {
			return null;
		}

		StorePersister sp = softwareCache.getBpkPersister(bpkIdentifier);
		try {
			sp.dump(is);
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

	/**
	 * Do GET request on software repository server and return deserialized object
	 * of given type..
	 * 
	 * @param abstractUri
	 *          abstract part of uri for get request
	 * @param type
	 *          type reference of object which will be returned
	 * @param headers
	 *          request headers
	 * @param <T>
	 *          type of object which will be returned
	 * @return deserialized object of expected type or null if object couldn't be
	 *         deserialized from some reason
	 */
	private <T> T doGetObject(String abstractUri, TypeReference<T> type, Header... headers) {
		try (InputStream is = doGetInputStream(abstractUri, headers)) {

			if (is == null) {
				return null;
			}

			String jsonString = IOUtils.toString(is);
			return jsonUtils.deserialize(jsonString, type);
		} catch (IOException e) {
			log.error(
					"Failed to GET item from software repository - I/O exception occurs when reading response input stream to string",
					e);
			return null;
		} catch (JsonException e) {
			log.error(
					"Failed to GET item from software repository - cannot deserialize return value from json string to object",
					e);
			return null;
		}
	}

	/**
	 * Do GET request on software repository server and return response input
	 * stream
	 * 
	 * @param abstractUri
	 *          abstract part of uri for get request
	 * @param headers
	 *          request headers
	 * @return input stream from http response
	 */
	private InputStream doGetInputStream(String abstractUri, Header... headers) {

		String uri;
		try {
			uri = createRepoUri() + abstractUri;
		} catch (URISyntaxException e) {
			log.error("Failed to GET item from software repository - unable to synthesize GET request URI", e);
			return null;
		} catch (UnknownHostException e) {
			log.error("Failed to GET item from software repository - unable to connect to any of the repository's declared interfaces");
			return null;
		}

		HttpGet request = new HttpGet(uri);

		try {
			for (Header header : headers) {
				request.addHeader(header.key, jsonUtils.serialize(header.value));
			}
		} catch (JsonException e) {
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
			log.error(
					"Failed to GET item from software repository - server refusal: '{}'",
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

	/**
	 * Do PUT request with given object stream as body message.
	 * 
	 * @param abstractUri
	 *          abstract part of uri for get request
	 * @param objectStreamToPut
	 *          stream which will be added to body message
	 * @param headers
	 *          request headers
	 * @throws SwRepositoryClientException
	 *           if PUT operation cannot be performed from some reason. Reason can
	 *           be one of following;
	 *           <ul>
	 *           <li>'objectStreamToPut' is null</li>
	 *           <li>PUT request URI was not correctly synthesized</li>
	 *           <li>Request headers was could not correctly serialized</li>
	 *           <li>http protocol error or connection reset</li>
	 *           <li>response status was not of type 2xx</li>
	 *           </ul>
	 */
	private
			void
			doPutStream(String abstractUri, InputStream objectStreamToPut, Header... headers) throws SwRepositoryClientException {

		if (objectStreamToPut == null) {
			String msg = "Failed to PUT item to software repository - object given to send was null";
			log.error(msg);
			throw new SwRepositoryClientException(msg);
		}

		String uri;
		try {
			uri = createRepoUri() + abstractUri;
		} catch (URISyntaxException e) {
			String msg = "Failed to PUT item to software repository - unable to synthesize PUT request URI";
			log.error(msg, e);
			throw new SwRepositoryClientException(msg, e);
		} catch (UnknownHostException e) {
			String msg = "Failed to PUT item to software repository - unable to connect to any of the repository's declared interfaces";
			log.error(msg, e);
			throw new SwRepositoryClientException(msg, e);
		}

		HttpPut request = new HttpPut(uri);

		try {
			for (Header header : headers) {
				request.addHeader(header.key, jsonUtils.serialize(header.value));
			}
		} catch (JsonException e) {
			String msg = "Failed to PUT item to software repository - cannot serialize request header object to json string";
			log.error(msg, e);
			throw new SwRepositoryClientException(msg, e);
		}

		InputStreamEntity sentEntity = new InputStreamEntity(objectStreamToPut, -1);
		request.setEntity(sentEntity); // entity closes the stream

		HttpClient httpCli = new DefaultHttpClient();

		HttpResponse response;
		try {
			response = httpCli.execute(request);
		} catch (ClientProtocolException e) {
			String msg = "Failed to PUT item to software repository - http protocol error";
			log.error(msg, e);
			throw new SwRepositoryClientException(msg, e);
		} catch (IOException e) {
			String msg = "Failed to PUT item to software repository - I/O error or connection re-set";
			log.error(msg, e);
			throw new SwRepositoryClientException(msg, e);
		}

		if ((response.getStatusLine().getStatusCode() / 100) != 2) {
			String msg = String.format(
					"Failed to PUT item to software repository - server error: '%s'",
					response.getStatusLine().getReasonPhrase());
			log.error(msg);
			throw new SwRepositoryClientException(msg);
		}
	}

	/**
	 * Internal representation of http header.
	 */
	private static final class Header {
		public final String key;
		public final Object value;

		public Header(String key, Object value) {
			this.key = key;
			this.value = value;
		}
	}

}
