package cz.cuni.mff.d3s.been.swrepository;

import static cz.cuni.mff.d3s.been.swrepository.HeaderNames.ARTIFACT_IDENTIFIER_HEADER_NAME;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.bpk.ArtifactIdentifier;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;
import cz.cuni.mff.d3s.been.datastore.ArtifactStore;
import cz.cuni.mff.d3s.been.datastore.StorePersister;
import cz.cuni.mff.d3s.been.datastore.StoreReader;
import cz.cuni.mff.d3s.been.swrepository.httpserver.SkeletalRequestHandler;

/**
 * A request handler that deals with artifact requests.
 * 
 * @author darklight
 * 
 */
public class ArtifactRequestHandler extends SkeletalRequestHandler {

	private static final Logger log = LoggerFactory.getLogger(ArtifactRequestHandler.class);

	/** Persistence layer used to retrieve artifacts */
	private final ArtifactStore store;
	private final JSONUtils jsonUtils;

	/**
	 * Create the handler.
	 * 
	 * @param store
	 *          Persistence to use
	 */
	public ArtifactRequestHandler(ArtifactStore store) {
		this.store = store;
		this.jsonUtils = JSONUtils.newInstance();
	}

	@Override
	public void handleGet(HttpRequest request, HttpResponse response) {
		ArtifactIdentifier artifactIdentifier = null;
		try {
			artifactIdentifier = jsonUtils.<ArtifactIdentifier> deserialize(
					request.getFirstHeader(ARTIFACT_IDENTIFIER_HEADER_NAME).getValue(),
					ArtifactIdentifier.class);
		} catch (JsonException e) {
			response.setStatusCode(400);
			log.error("Could not read artifact identifier from request.");
			return;
		}

		final StoreReader artifactReader = store.getArtifactReader(artifactIdentifier);
		if (artifactReader == null) {
			replyBadRequest(
					request,
					response,
					String.format("Could not retrieve reader for artifact identifier %s", artifactIdentifier.toString()));
			return;
		}

		InputStreamEntity bpkEntity = null;
		try {
			bpkEntity = new InputStreamEntity(artifactReader.getContentStream(), artifactReader.getContentLength());
		} catch (IOException e) {
			log.error("Failed to open artifact reader {} - {}", artifactReader.toString(), e.getMessage());
		}
		response.setEntity(bpkEntity);
	}

	@Override
	protected void handlePut(HttpRequest request, HttpResponse response) {
		ArtifactIdentifier artifactIdentifier;
		if (!BasicHttpEntityEnclosingRequest.class.isAssignableFrom(request.getClass())) {
			final String errorMessage = String.format(
					"Put request %s invalid, because it doesn't contain an entity.",
					request.toString());
			log.error(errorMessage);
			replyBadRequest(request, response, errorMessage);
			return;
		}

		BasicHttpEntityEnclosingRequest put = (BasicHttpEntityEnclosingRequest) request;
		try {
			artifactIdentifier = jsonUtils.deserialize(
					request.getFirstHeader(ARTIFACT_IDENTIFIER_HEADER_NAME).getValue(),
					ArtifactIdentifier.class);
		} catch (JsonException e) {
			final String errorMessage = String.format(
					"could not read artifact identifier from request %s.",
					request.toString());
			log.error(errorMessage);
			replyBadRequest(request, response, errorMessage);
			return;
		}

		try {
			final StorePersister artifactPersister = store.getArtifactPersister(artifactIdentifier);
			if (artifactPersister == null) {
				final String errorMessage = String.format(
						"Could not retrieve persister for artifact %s",
						artifactIdentifier.toString());
				log.error(errorMessage);
				replyBadRequest(request, response, errorMessage);
				return;
			}
			final InputStream requestFile = put.getEntity().getContent();
			artifactPersister.dump(requestFile);
		} catch (IOException e) {
			log.error("Could not persist artifact {} due to I/O error - {}.", artifactIdentifier.toString(), e.getMessage());
		}
	}
}
