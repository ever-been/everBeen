package cz.cuni.mff.d3s.been.swrepository;

import static cz.cuni.mff.d3s.been.swrepository.HeaderNames.BPK_IDENTIFIER_HEADER_NAME;
import static cz.cuni.mff.d3s.been.swrepository.UrlPaths.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.jaxb.ConvertorException;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.core.utils.JsonException;
import cz.cuni.mff.d3s.been.datastore.BpkStore;
import cz.cuni.mff.d3s.been.datastore.StorePersister;
import cz.cuni.mff.d3s.been.datastore.StoreReader;
import cz.cuni.mff.d3s.been.swrepository.httpserver.SkeletalRequestHandler;

/**
 * {@link HttpRequestHandler} for BPK requests.
 * 
 * @author darklight
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
		if (request.getRequestLine().getUri().equals(BPK_URI)) {
			handleGetSingleBpk(request, response);
			return;
		}

		if (request.getRequestLine().getUri().equals(BPK_LIST_URI)) {
			handleListBpks(response);
			return;
		}

		if (request.getRequestLine().getUri().equals(TASK_DESCRIPTOR_LIST_URI)) {
			handleListTaskDescriptors(request, response);
			return;
		}

		if (request.getRequestLine().getUri().equals(TASK_CONTEXT_DESCRIPTOR_LIST_URI)) {
			handleListTaskContextDescriptors(request, response);
			return;
		}

		response.setStatusCode(400);
		log.error("Unknown request");
		return;
	}

	private void handleListBpks(HttpResponse response) {
		List<BpkIdentifier> list = store.listBpks();

		StringEntity entity = null;
		try {
			String jsonString = JSONUtils.serialize(list);
			entity = new StringEntity(jsonString);
		} catch (UnsupportedEncodingException e) {
			response.setStatusCode(400);
			log.error("Cannot create string entity.", e);
			return;
		} catch (JsonException e) {
			response.setStatusCode(400);
			log.error("Cannot serialize BPK list.", e);
			return;
		}
		response.setEntity(entity);
	}

	private void handleGetSingleBpk(HttpRequest request, HttpResponse response) {
		BpkIdentifier bpkIdentifier = null;
		try {
			bpkIdentifier = JSONUtils.<BpkIdentifier> deserialize(
					request.getFirstHeader(BPK_IDENTIFIER_HEADER_NAME).getValue(),
					BpkIdentifier.class);
		} catch (JsonException e) {
			response.setStatusCode(400);
			log.error("Could not read BPK identifier from request.");
			return;
		}

		final StoreReader bpkReader = store.getBpkReader(bpkIdentifier);
		if (bpkReader == null) {
			replyBadRequest(
					request,
					response,
					String.format("Could not retrieve reader for BPK identifier %s", bpkIdentifier.toString()));
			return;
		}

		InputStreamEntity bpkEntity = null;
		try {
			bpkEntity = new InputStreamEntity(bpkReader.getContentStream(), bpkReader.getContentLength());
		} catch (IOException e) {
			log.error("Failed to open BPK reader {} - {}", bpkReader.toString(), e.getMessage());
		}
		response.setEntity(bpkEntity);
	}

	private void handleListTaskDescriptors(HttpRequest request, HttpResponse response) {
		BpkIdentifier bpkIdentifier = null;
		try {
			bpkIdentifier = JSONUtils.<BpkIdentifier> deserialize(
					request.getFirstHeader(BPK_IDENTIFIER_HEADER_NAME).getValue(),
					BpkIdentifier.class);
		} catch (JsonException e) {
			response.setStatusCode(400);
			log.error("Could not read BPK identifier from request.");
			return;
		}

		final Map<String, String> descriptors;
		try {
			descriptors = store.getTaskDescriptors(bpkIdentifier);
		} catch (IOException | JAXBException | SAXException | ConvertorException e) {
			replyBadRequest(
					request,
					response,
					String.format("Could not convert Task Descriptors from XMLs in BPK file %s", bpkIdentifier.toString()));
			return;
		}

		if (descriptors == null) {
			replyBadRequest(
					request,
					response,
					String.format("Could not retrieve descriptors reader for BPK identifier %s", bpkIdentifier.toString()));
			return;
		}

		StringEntity entity = null;
		try {
			String jsonString = JSONUtils.serialize(descriptors);
			entity = new StringEntity(jsonString);
		} catch (UnsupportedEncodingException e) {
			response.setStatusCode(400);
			log.error("Cannot create string entity.", e);
			return;
		} catch (JsonException e) {
			response.setStatusCode(400);
			log.error("Cannot serialize TaskDescriptors map.", e);
			return;
		}

		response.setEntity(entity);
	}

	private void handleListTaskContextDescriptors(HttpRequest request, HttpResponse response) {
		BpkIdentifier bpkIdentifier = null;
		try {
			bpkIdentifier = JSONUtils.<BpkIdentifier> deserialize(
					request.getFirstHeader(BPK_IDENTIFIER_HEADER_NAME).getValue(),
					BpkIdentifier.class);
		} catch (JsonException e) {
			response.setStatusCode(400);
			log.error("Could not read BPK identifier from request.");
			return;
		}

		final Map<String, String> descriptors;
		try {
			descriptors = store.getTaskContextDescriptors(bpkIdentifier);
		} catch (IOException | JAXBException | SAXException | ConvertorException e) {
			replyBadRequest(
					request,
					response,
					String.format("Could not convert Task Context Descriptors from XMLs in BPK file %s", bpkIdentifier.toString()));
			return;
		}

		if (descriptors == null) {
			replyBadRequest(
					request,
					response,
					String.format("Could not retrieve descriptors reader for BPK identifier %s", bpkIdentifier.toString()));
			return;
		}

		StringEntity entity = null;
		try {
			String jsonString = JSONUtils.serialize(descriptors);
			entity = new StringEntity(jsonString);
		} catch (UnsupportedEncodingException e) {
			response.setStatusCode(400);
			log.error("Cannot create string entity.", e);
			return;
		} catch (JsonException e) {
			response.setStatusCode(400);
			log.error("Cannot serialize TaskContextDescriptors map.", e);
			return;
		}

		response.setEntity(entity);
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
					request.getFirstHeader(BPK_IDENTIFIER_HEADER_NAME).getValue(),
					BpkIdentifier.class);
		} catch (JsonException e) {
			final String errorMessage = String.format("could not read BPK identifier from request %s.", request.toString());
			log.error(errorMessage);
			replyBadRequest(request, response, errorMessage);
			return;
		}

		if (!bpkIdentifier.getVersion().endsWith(Versions.SNAPSHOT_SUFFIX)) {
			if (store.exists(bpkIdentifier)) {
				final String errorMessage = String.format(
						"could not upload BPK '%s' - BPK already exists and only *-SNAPSHOT versions are allowed to be reuploaded.",
						request.toString());
				log.error(errorMessage);
				replyBadRequest(request, response, errorMessage);
				return;
			}
		}

		try {
			final StorePersister bpkPersister = store.getBpkPersister(bpkIdentifier);
			if (bpkPersister == null) {
				final String errorMessage = String.format("Could not retrieve persister for BPK %s", bpkIdentifier.toString());
				log.error(errorMessage);
				replyBadRequest(request, response, errorMessage);
				return;
			}
			final InputStream requestFile = put.getEntity().getContent();
			bpkPersister.dump(requestFile);
		} catch (IOException e) {
			log.error("Could not persist BPK {} due to I/O error - {}.", bpkIdentifier.toString(), e.getMessage());
		}
	}
}
