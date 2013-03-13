package cz.cuni.mff.d3s.been.swrepository;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.JSONUtils;
import cz.cuni.mff.d3s.been.core.JSONUtils.JSONSerializerException;
import cz.cuni.mff.d3s.been.datastore.BpkStore;
import cz.cuni.mff.d3s.been.datastore.StorePersister;
import cz.cuni.mff.d3s.been.datastore.StoreReader;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;
import cz.cuni.mff.d3s.been.swrepository.httpserver.SkeletalRequestHandler;

/**
 * {@link HttpRequestHandler} for BPK requests.
 * 
 * @author darklight
 * 
 */
public class BpkRequestHandler extends SkeletalRequestHandler {
	private static final Logger log = LoggerFactory.getLogger(BpkRequestHandler.class);
	private final BpkStore store;

	/**
	 * Create the request handler over a persistence store.
	 * 
	 * @param store
	 *          The persistence layer for BPK storage
	 */
	public BpkRequestHandler(BpkStore store) {
		this.store = store;
	}

	@Override
	public void handleGet(HttpRequest request, HttpResponse response) {
		BpkIdentifier bpkIdentifier = null;
		try {
			bpkIdentifier = JSONUtils.<BpkIdentifier> deserialize(
					request.getFirstHeader(SwRepoClientFactory.BPK_IDENTIFIER_HEADER_NAME).getValue(),
					BpkIdentifier.class);
		} catch (JSONSerializerException e) {
			response.setStatusCode(400);
			log.error("Could not read BPK identifier from request.");
			return;
		}

		final StoreReader bpkReader = store.getBpkReader(bpkIdentifier);
		if (bpkReader == null) {
			replyBadRequest(request, response, String.format(
					"Could not retrieve reader for BPK identifier %s",
					bpkIdentifier.toString()));
			return;
		}

		InputStreamEntity bpkEntity = null;
		try {
			bpkEntity = new InputStreamEntity(bpkReader.getContentStream(), bpkReader.getContentLength());
		} catch (IOException e) {
			log.error(
					"Failed to open BPK reader {} - {}",
					bpkReader.toString(),
					e.getMessage());
		}
		response.setEntity(bpkEntity);
	}

	@Override
	protected void handlePut(HttpRequest request, HttpResponse response) {
		BpkIdentifier bpkIdentifier;
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
			bpkIdentifier = JSONUtils.deserialize(
					request.getFirstHeader(SwRepoClientFactory.BPK_IDENTIFIER_HEADER_NAME).getValue(),
					BpkIdentifier.class);
		} catch (JSONSerializerException e) {
			final String errorMessage = String.format(
					"could not read BPK identifier from request %s.",
					request.toString());
			log.error(errorMessage);
			replyBadRequest(request, response, errorMessage);
			return;
		}

		try {
			final StorePersister bpkPersister = store.getBpkPersister(bpkIdentifier);
			if (bpkPersister == null) {
				final String errorMessage = String.format(
						"Could not retrieve persister for BPK %s",
						bpkIdentifier.toString());
				log.error(errorMessage);
				replyBadRequest(request, response, errorMessage);
				return;
			}
			final InputStream requestFile = put.getEntity().getContent();
			bpkPersister.dump(requestFile);
		} catch (IOException e) {
			log.error(
					"Could not persist BPK {} due to I/O error - {}.",
					bpkIdentifier.toString(),
					e.getMessage());
		}
	}
}
