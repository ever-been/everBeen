package cz.cuni.mff.d3s.been.swrepository.httpserver;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A skeletal implementation of the {@link HttpRequestHandler}.
 * 
 * @author darklight
 * 
 */
public abstract class SkeletalRequestHandler implements HttpRequestHandler {
	private static final Logger log = LoggerFactory
			.getLogger(SkeletalRequestHandler.class);

	@Override
	public final void handle(HttpRequest request, HttpResponse response, HttpContext context) {
		switch (request.getRequestLine().getMethod()) {
		case "GET":
			handleGet(request, response);
			break;
		case "PUT":
			handlePut(request, response);
			break;
		default:
			replyUnsupportedMethod(request, response);
		}
	}
	
	/**
	 * Handle a GET request.
	 * 
	 * @param request
	 *            The request
	 * @param response
	 *            Proposed response
	 */
	protected abstract void handleGet(HttpRequest request, HttpResponse response);

	/**
	 * Handle a PUT request.
	 * 
	 * @param request
	 *            The request
	 * @param response
	 *            Proposed response
	 */
	protected abstract void handlePut(HttpRequest request, HttpResponse response);

	/**
	 * Send a file-not-found response.
	 * 
	 * @param request
	 *            Request which resulted in this error
	 * @param response
	 *            Response to send
	 */
	protected void replyFileNotFound(HttpRequest request, HttpResponse response) {
		response.setStatusCode(404);
	}

	/**
	 * Send a reply that the requested HTTP method is not supported by the
	 * server.
	 * 
	 * @param request
	 *            The request which contained the invalid method
	 * @param response
	 *            A response which contains a list of supported methods
	 */
	protected void replyUnsupportedMethod(HttpRequest request, HttpResponse response) {
		// TODO send a permanent fail response with a list of supported methods
	}
	
	/**
	 * Tell the client that his request was invalid. Attach an explanation.
	 * 
	 * @param request The bad request
	 * @param response Response to fill
	 * @param permaFailMessage Explanation why the request was bad
	 */
	protected void replyBadRequest(HttpRequest request, HttpResponse response, String permaFailMessage) {
		response.setStatusCode(400);
		response.setReasonPhrase(permaFailMessage);
	}

	/**
	 * Log an error in the header commit.
	 * 
	 * @param request
	 *            Request that caused this error
	 * @param response
	 *            Response whose header commit failed
	 */
	protected void logHeaderError(HttpRequest request, HttpResponse response) {
		log.error(String.format(
				"Error writing response headers: [request=%s, response=%s]",
				request, response));
	}

}
