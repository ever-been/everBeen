package cz.cuni.mff.d3s.been.swrepoclient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.bpk.Bpk;
import cz.cuni.mff.d3s.been.bpk.BpkArtifact;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.JSONUtils;
import cz.cuni.mff.d3s.been.core.JSONUtils.JSONSerializerException;
import cz.cuni.mff.d3s.been.swrepository.DataStore;
import cz.cuni.mff.d3s.been.swrepository.StoreReader;
import cz.cuni.mff.d3s.been.util.CopyStream;

class HttpSwRepoClient implements SwRepoClient {
	/** HTTP implementation specific log for the sw repo client */
	private static final Logger log = LoggerFactory.getLogger(HttpSwRepoClient.class);

	/** Hostname where the software repository resides */
	private final String hostname;
	/** Port on which the software repository listens */
	private final Integer port;
	/** Data store to use for caching. */
	private final DataStore softwareCache;

	HttpSwRepoClient(String hostname, Integer port, DataStore softwareCache) {
		this.hostname = hostname;
		this.port = port;
		this.softwareCache = softwareCache;
	}

	@Override
	public Artifact getArtifact(String groupId, String artifactId, String version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bpk getBpk(BpkIdentifier bpkIdentifier) {
		StoreReader bpkReader = null;
		try {
			bpkReader = softwareCache.getBpkReader(bpkIdentifier);
		} catch (IOException e) {
			log.warn("Failed to retrieve BPK {} from cache because \"{}\". Will attempt re-download.", bpkIdentifier.toString(), e.getMessage());
			return getBpkByHTTP(bpkIdentifier);
		}

		InputStream bpkContent = null;
		if (bpkReader != null) {
			try {
				bpkContent = bpkReader.getContentStream();
			} catch (IOException e) {
				log.error("Failed to open cached BPK {} because \"{}\" - the cache is probably corrupt. Will attempt re-download.", bpkIdentifier.toString(), e.getMessage());
				return getBpkByHTTP(bpkIdentifier);
			}
		}

		if (bpkContent != null) {
			// FIXME double caching (File -> Stream -> File) through TMP, consider adding an API method that works with files directly
			File tmpFile = null;
			try {
				tmpFile = File.createTempFile("bpk", bpkIdentifier.toString());
			} catch (IOException e) {
				log.error("Failed to create temporary file for BPK {} because \"{}\"", bpkIdentifier.toString(), e.getMessage());
				return getBpkByHTTP(bpkIdentifier);
			}
			if (
			storeContentToFile(bpkContent, tmpFile)) {
				Bpk bpk = new Bpk();
				bpk.setFile(tmpFile);
				bpk.setIdentifier(bpkIdentifier);
				return bpk;
			}
		}
		return getBpkByHTTP(bpkIdentifier);
	}


	@Override
	public boolean putArtifact(BpkArtifact artifactMetaInfo, File artifactFile) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean putBpk(BpkIdentifier bpkMetaInfo, File bpkFile) {

		if (bpkMetaInfo == null || bpkFile == null) {
			log.error("Failed to upload BPK - package object or its meta-info was null.");
			return false;
		}

		if (!bpkFile.exists()) {
			log.error("Failed to upload BPK %s - file doesn't exist.");
			return false;
		}

		HttpPut request = null;
		try {
			request = new HttpPut(createRepoUri() + "/bpk");
		} catch (URISyntaxException e) {
			log.error(String.format("Failed to upload BPK %s to repository - URI synthesis error.", bpkMetaInfo.toString()));
			return false;
		}

		try {
			request.setHeader(SwRepoClientFactory.BPK_IDENTIFIER_HEADER_NAME, JSONUtils.serialize(bpkMetaInfo));
		} catch (JSONSerializerException e) {
			log.error(String.format("Failed to upload BPK %s to repository - Identifier serialization error.", bpkMetaInfo.toString()));
			return false;
		}

		InputStream sentFileStream = null;
		try {
			sentFileStream = new BufferedInputStream(new FileInputStream(bpkFile));
		} catch (IOException e) {
			log.error(String.format("Failed to upload BPK %s to repository - error reading file %s", bpkMetaInfo.toString(), bpkFile.getAbsolutePath()));
			return false;
		}
		InputStreamEntity sentEntity = new InputStreamEntity(sentFileStream, bpkFile.length());
		request.setEntity(sentEntity);

		HttpClient cli = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			response = cli.execute(request);
		} catch (ClientProtocolException e) {
			log.error(String.format("Failed to upload BPK %s - transport protocol error: \"%s\"", bpkMetaInfo.toString(), e.getMessage()));
			return false;
		} catch (IOException e) {
			log.error(String.format("Failed to upload BPK %s - transport I/O error: \"%s\"", bpkMetaInfo.toString(), e.getMessage()));
			return false;
		}

		if ((response.getStatusLine().getStatusCode() / 100) != 2) {
			log.error(String.format("Failed to upload BPK %s - server error: \"%s\"", bpkMetaInfo.toString(), response.getStatusLine().getReasonPhrase()));
			return false;
		} else {
			return true;
		}
	}
	
	private boolean storeContentToFile(InputStream content, File targetFile) {
		if (targetFile == null) {
			return false;
		}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(targetFile);
			} catch (FileNotFoundException e) {
				log.error("Could not open temporary file \"{}\" for writing - {}", targetFile.getAbsolutePath(), e.getMessage());
				return false;
			}
			CopyStream copyStream = new CopyStream(content, true, fos, true, true);
			try {
				copyStream.copy();
			} catch (IOException e) {
				log.error("Could not copy content to File \"{}\" to cache - {}", targetFile.getAbsolutePath(), e.getMessage());
				return false;
			}
			return true;
	}

	URI createRepoUri() throws URISyntaxException {
		URIBuilder uriBuilder = new URIBuilder();
		uriBuilder.setHost(hostname);
		uriBuilder.setPort(port);
		uriBuilder.setScheme("http");
		return uriBuilder.build();
	}
	
	private Bpk getBpkByHTTP(BpkIdentifier bpkIdentifier) {
		HttpUriRequest request = null;
		try {
			request = new HttpGet(createRepoUri() + "/bpk");
		} catch (URISyntaxException e) {
			log.error(String.format("Failed to retrieve BKP %s - unable to synthesize get request URI", bpkIdentifier.toString()));
			return null;
		}
		try {
			request.addHeader(SwRepoClientFactory.BPK_IDENTIFIER_HEADER_NAME, JSONUtils.serialize(bpkIdentifier));
		} catch (JSONSerializerException e) {
			log.error(String.format("Failed to retrieve BPK %s - unable to serialize BPK identifier into request header", bpkIdentifier.toString()));
		}

		HttpClient httpCli = new DefaultHttpClient();
		HttpResponse response;
		try {
			response = httpCli.execute(request);
		} catch (ClientProtocolException e) {
			log.error(String.format("Failed to retrieve BKP %s - unable to synthesize get request URI", bpkIdentifier.toString()));
			return null;
		} catch (IOException e) {
			log.error(String.format("Failed to retrieve BKP %s - I/O error or connection re-set", bpkIdentifier.toString()));
			return null;
		}

		if (response.getStatusLine().getStatusCode() / 100 != 2) {
			log.error(String.format("Failed to retrieve BPK %s - server refusal: \"%s\"", bpkIdentifier.toString(), response.getStatusLine().getReasonPhrase()));
			return null;
		}

		InputStream is = null;
		try {
			is = response.getEntity().getContent();
		} catch (IOException e) {
			log.error(String.format("Failed to retrieve BKP %s - error opening SW repo response for reading", bpkIdentifier.toString()));
			return null;
		}

		File tempFile = null;
		try {
			tempFile = File.createTempFile("repo_bpk", bpkIdentifier.getBpkId());
		} catch (IOException e) {
			log.error(String.format("Failed to retrieve BKP %s - error opening temp file for writing", bpkIdentifier.toString()));
			try {
				is.close();
			} catch (IOException ee) {
				log.error(String.format("Leaked stream descriptor to SW repo HTTP response for BPK %s", bpkIdentifier.toString()));
			}
			return null;
		}

		if (!storeContentToFile(is, tempFile)) {
			return null;
		}

		Bpk bpk = new Bpk();
		bpk.setFile(tempFile);
		bpk.setIdentifier(bpkIdentifier);
		return bpk;
	}
}
