package cz.cuni.mff.d3s.been.swrepoclient;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

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

class HttpSwRepoClient implements SwRepoClient {
	/** HTTP implementation specific log for the sw repo client */
	private static final Logger log = LoggerFactory.getLogger(HttpSwRepoClient.class);

	/** Hostname where the software repository resides */
	private final String hostname;
	/** Port on which the software repository listens */
	private final Integer port;
	/** Data store to use for caching. */
	private final SoftwareStore softwareCache;

	HttpSwRepoClient(String hostname, Integer port, SoftwareStore softwareCache) {
		this.hostname = hostname;
		this.port = port;
		this.softwareCache = softwareCache;
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
	public boolean putArtifact(ArtifactIdentifier artifactIdentifier, File artifactFile) {
		if (artifactIdentifier == null || artifactFile == null) {
			log.error(
					"Refused put because of invalid artifact (was {}) or identifier (was {})",
					artifactFile,
					artifactIdentifier);
			return false;
		}

		if (!artifactFile.exists()) {
			log.error("Uploaded artifact \"{}\" doesn't exist", artifactFile.getAbsolutePath());
			return false;
		}

		HttpPut request = null;
		try {
			final String uriCandidate = createRepoUri() + "/artifact";
			request = new HttpPut(uriCandidate);
		} catch (URISyntaxException e) {
			log.error(
					"Failed to upload Artifact {} because the repository URI was invalid. Cause: {}",
					artifactIdentifier.toString(),
					e.getMessage());
		}

		try {
			request.setHeader(SwRepoClientFactory.ARTIFACT_IDENTIFIER_HEADER_NAME, JSONUtils.serialize(artifactIdentifier));
		} catch (JSONSerializerException e) {
			log.error(
					"Failed to upload Artifact {} due to artifact identifier serialization error - {}",
					artifactIdentifier.toString(),
					e.getMessage());
			return false;
		}

		InputStreamEntity artifactEntity = createFileEntity(artifactFile);
		if (artifactEntity == null) {
			return false;
		}
		request.setEntity(artifactEntity);// entity closes the stream

		HttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			log.error(
					"Failed to upload Artifact {} due to HTTP protocol error - {}",
					artifactIdentifier.toString(),
					e.getMessage());
			return false;
		} catch (IOException e) {
			log.error(
					"Failed to upload artifact {} due to transport I/O error - {}",
					artifactIdentifier.toString(),
					e.getMessage());
			return false;
		}

		if (response.getStatusLine().getStatusCode() / 100 != 2) {
			log.error(
					"Failed to upload artifact {} - server error: {}",
					artifactIdentifier.toString(),
					response.getStatusLine().getReasonPhrase());
			return false;
		}
		return true;
	}

	@Override
	public boolean putBpk(BpkIdentifier bpkMetaInfo, InputStream bpkInputStream) {

		if (bpkMetaInfo == null || bpkInputStream == null) {
			log.error("Failed to upload BPK {} - package object or its meta-info was null.", bpkMetaInfo);
			return false;
		}

		HttpPut request = null;
		try {
			final String uriCandidate = createRepoUri() + "/bpk";
			request = new HttpPut(uriCandidate);
		} catch (URISyntaxException e) {
			log.error(
					"Failed to upload BPK {} to repository because the repository URI was invalid. Cause: {}",
					bpkMetaInfo.toString(),
					e.getMessage());
			return false;
		}

		try {
			request.setHeader(SwRepoClientFactory.BPK_IDENTIFIER_HEADER_NAME, JSONUtils.serialize(bpkMetaInfo));
		} catch (JSONSerializerException e) {
			log.error("Failed to upload BPK {} to repository - Identifier serialization error.", bpkMetaInfo.toString());
			return false;
		}

		InputStreamEntity sentEntity = new InputStreamEntity(bpkInputStream, -1);
		if (sentEntity == null) {
			return false;
		}
		request.setEntity(sentEntity);// entity closes the stream

		HttpClient cli = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			response = cli.execute(request);
		} catch (ClientProtocolException e) {
			log.error("Failed to upload BPK {} due to HTTP protocol error - {}", bpkMetaInfo.toString(), e.getMessage());
			return false;
		} catch (IOException e) {
			log.error("Failed to upload BPK {} due to transport I/O error - {}", bpkMetaInfo.toString(), e.getMessage());
			return false;
		}

		if ((response.getStatusLine().getStatusCode() / 100) != 2) {
			log.error(
					"Failed to upload BPK {} - server error: \"{}\"",
					bpkMetaInfo.toString(),
					response.getStatusLine().getReasonPhrase());
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean putBpk(BpkIdentifier bpkMetaInfo, File bpkFile) {
		try {
			return putBpk(bpkMetaInfo, new FileInputStream(bpkFile));
		} catch (FileNotFoundException e) {
			log.error("Failed to upload BPK {} - file doesn't exist.", bpkMetaInfo.toString());
			return false;
		}
	}

	/**
	 * Create an input stream entity from a file
	 */
	InputStreamEntity createFileEntity(File file) {
		try {
			return new InputStreamEntity(new FileInputStream(file), file.length());
		} catch (IOException e) {
			log.error("Failed to marshall file {} into a HTTP entity - {}", file.getAbsolutePath(), e.getMessage());
			return null;
		}
	}
	/**
	 * Synthesize the URI of the software repository from internals
	 * 
	 * @return the URI of the repository
	 * 
	 * @throws URISyntaxException
	 *           When some of the internals are malformed
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
		HttpUriRequest request = null;
		try {
			request = new HttpGet(createRepoUri() + "/artifact");
		} catch (URISyntaxException e) {
			log.error(
					"Failed to retrieve Artifact {} - unable to synthesize get request URI ({})",
					artifactIdentifier.toString(),
					e.getMessage());
			return null;
		}
		try {
			request.addHeader(SwRepoClientFactory.ARTIFACT_IDENTIFIER_HEADER_NAME, JSONUtils.serialize(artifactIdentifier));
		} catch (JSONSerializerException e) {
			log.error(
					"Failed to retrieve Maven artifact {} - unable to serialize Artifact identifier into request header",
					artifactIdentifier.toString());
			return null;
		}

		HttpClient httpCli = new DefaultHttpClient();
		HttpResponse response;
		try {
			response = httpCli.execute(request);
		} catch (ClientProtocolException e) {
			log.error("Failed to retrieve Artifact {} - unable to synthesize get request URI", artifactIdentifier.toString());
			return null;
		} catch (IOException e) {
			log.error("Failed to retrieve BKP {} - I/O error or connection re-set", artifactIdentifier.toString());
			return null;
		}

		if (response.getStatusLine().getStatusCode() / 100 != 2) {
			log.error(
					"Failed to retrieve Artifact {} - server refusal: \"{}\"",
					artifactIdentifier.toString(),
					response.getStatusLine().getReasonPhrase());
			return null;
		}

		InputStream is = null;
		try {
			is = response.getEntity().getContent();
		} catch (IOException e) {
			log.error(
					"Failed to retrieve Artifact {} - error opening SW repo response for reading",
					artifactIdentifier.toString());
			return null;
		}

		StorePersister sp = softwareCache.getArtifactPersister(artifactIdentifier);
		try {
			sp.dump(is);
		} catch (IOException e) {
			log.error("Failed to cache Artifact {} locally - {}", artifactIdentifier.toString(), e.getMessage());
			IOUtils.closeQuietly(is);
			return null;
		}
		IOUtils.closeQuietly(is);

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
		HttpUriRequest request = null;
		try {
			request = new HttpGet(createRepoUri() + "/bpk");
		} catch (URISyntaxException e) {
			log.error(
					"Failed to retrieve BKP {} - unable to synthesize get request URI ({})",
					bpkIdentifier.toString(),
					e.getMessage());
			return null;
		}
		try {
			request.addHeader(SwRepoClientFactory.BPK_IDENTIFIER_HEADER_NAME, JSONUtils.serialize(bpkIdentifier));
		} catch (JSONSerializerException e) {
			log.error(String.format(
					"Failed to retrieve BPK %s - unable to serialize BPK identifier into request header",
					bpkIdentifier.toString()));
		}

		HttpClient httpCli = new DefaultHttpClient();
		HttpResponse response;
		try {
			response = httpCli.execute(request);
		} catch (ClientProtocolException e) {
			log.error(String.format(
					"Failed to retrieve BKP %s - unable to synthesize get request URI",
					bpkIdentifier.toString()));
			return null;
		} catch (IOException e) {
			log.error(
					String.format("Failed to retrieve BKP %s - I/O error or connection re-set", bpkIdentifier.toString()),
					e);
			return null;
		}

		if (response.getStatusLine().getStatusCode() / 100 != 2) {
			log.error(String.format(
					"Failed to retrieve BPK %s - server refusal: \"%s\"",
					bpkIdentifier.toString(),
					response.getStatusLine().getReasonPhrase()));
			return null;
		}

		InputStream is = null;
		try {
			is = response.getEntity().getContent();
		} catch (IOException e) {
			log.error(String.format(
					"Failed to retrieve BKP %s - error opening SW repo response for reading",
					bpkIdentifier.toString()));
			return null;
		}

		StorePersister sp = softwareCache.getBpkPersister(bpkIdentifier);
		try {
			sp.dump(is);
		} catch (IOException e) {
			log.error("Failed to cache BPK {} locally - {}", bpkIdentifier.toString(), e.getMessage());
		}
		IOUtils.closeQuietly(is);

		return new BpkFromStore(softwareCache.getBpkReader(bpkIdentifier), bpkIdentifier);
	}

	/**
	 * Return a list of all uploaded BPKs.
	 */
	@Override
	public Collection<BpkIdentifier> listBpks() {
		HttpUriRequest request = null;
		try {
			request = new HttpGet(createRepoUri() + "/bpklist");
		} catch (URISyntaxException e) {
			log.error("Failed to list BPKs - unable to synthesize get request URI ({})", e.getMessage());
			return null;
		}

		HttpClient httpCli = new DefaultHttpClient();
		HttpResponse response;
		try {
			response = httpCli.execute(request);
		} catch (ClientProtocolException e) {
			log.error("Failed to list BPKs - unable to execute HTTP request ({})", e.getMessage());
			return null;
		} catch (IOException e) {
			log.error("Failed to list BPKs - I/O error or connection re-set", e);
			return null;
		}

		if (response.getStatusLine().getStatusCode() / 100 != 2) {
			log.error(String.format(
					"Failed to list BPKs - server refusal: \"%s\"",
					response.getStatusLine().getReasonPhrase()));
			return null;
		}

		InputStream is = null;
		try {
			is = response.getEntity().getContent();
		} catch (IOException e) {
			log.error("Failed to list BPKs - error opening SW repo response for reading", e);
			return null;
		}

		String jsonString = null;
		try {
			jsonString = IOUtils.toString(is);
		} catch (IOException e) {
			log.error("Failed to list BPKs - unable to read output from stream", e);
			return null;
		}

		List<BpkIdentifier> list = null;
		try {
			TypeReference t = new TypeReference<List<BpkIdentifier>>() {};
			list = JSONUtils.deserialize(jsonString, t);
		} catch (JSONSerializerException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * Return a list of all uploaded BPKs.
	 */
	@Override
	public Map<String, TaskDescriptor> listTaskDescriptors(BpkIdentifier bpkIdentifier) {
		HttpUriRequest request = null;
		try {
			request = new HttpGet(createRepoUri() + "/tdlist");
		} catch (URISyntaxException e) {
			log.error("Failed to list Task Descriptors - unable to synthesize get request URI ({})", e.getMessage());
			return null;
		}
		try {
			request.addHeader(SwRepoClientFactory.BPK_IDENTIFIER_HEADER_NAME, JSONUtils.serialize(bpkIdentifier));
		} catch (JSONSerializerException e) {
			log.error(String.format(
					"Failed to retrieve Task Descriptors - unable to serialize BPK identifier into request header",
					bpkIdentifier.toString()));
		}

		HttpClient httpCli = new DefaultHttpClient();
		HttpResponse response;
		try {
			response = httpCli.execute(request);
		} catch (ClientProtocolException e) {
			log.error("Failed to list Task Descriptors - unable to execute HTTP request ({})", e.getMessage());
			return null;
		} catch (IOException e) {
			log.error("Failed to list Task Descriptors - I/O error or connection re-set", e);
			return null;
		}

		if (response.getStatusLine().getStatusCode() / 100 != 2) {
			log.error(String.format(
					"Failed to list Task Descriptors - server refusal: \"%s\"",
					response.getStatusLine().getReasonPhrase()));
			return null;
		}

		InputStream is = null;
		try {
			is = response.getEntity().getContent();
		} catch (IOException e) {
			log.error("Failed to list Task Descriptors - error opening SW repo response for reading", e);
			return null;
		}

		String jsonString = null;
		try {
			jsonString = IOUtils.toString(is);
		} catch (IOException e) {
			log.error("Failed to list Task Descriptors - unable to read output from stream", e);
			return null;
		}

		Map<String, String> map = null;
		try {
			TypeReference t = new TypeReference<Map<String, String>>() {};
			map = JSONUtils.deserialize(jsonString, t);
		} catch (JSONSerializerException e) {
			e.printStackTrace();
		}
		Map<String, TaskDescriptor> descriptors = null;
		if (map != null) {
			descriptors = new HashMap<>();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				BindingParser<TaskDescriptor> parser = null;
				try {
					parser = XSD.TASK_DESCRIPTOR.createParser(TaskDescriptor.class);
					descriptors.put(entry.getKey(), parser.parse(new ByteArrayInputStream(entry.getValue().getBytes())));
				} catch (SAXException | ConvertorException | JAXBException e) {
					log.error(String.format("Failed to convert task descriptor %s", entry.getKey()), e);
                    continue;
				}
			}
		}

		return descriptors;
	}
}
