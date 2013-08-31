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
 */
public abstract class SkeletalRequestHandler implements HttpRequestHandler {
	private static final Logger log = LoggerFactory.getLogger(SkeletalRequestHandler.class);

	@Override
	public final void handle(HttpRequest request, HttpResponse response, HttpContext context) {
		try {
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
		} catch (Exception e) {
			log.error("Unexpected exception when processing request.", e);
			response.setReasonPhrase(String.format("Unexpected exception when processing request - %s", e.getMessage()));
			response.setStatusCode(400);
		}
	}

	/**
	 * Handle a GET request.
	 * 
	 * @param request
	 *          The request
	 * @param response
	 *          Proposed response
	 */
	protected abstract void handleGet(HttpRequest request, HttpResponse response);

	/**
	 * Handle a PUT request.
	 * 
	 * @param request
	 *          The request
	 * @param response
	 *          Proposed response
	 */
	protected abstract void handlePut(HttpRequest request, HttpResponse response);

	/**
	 * Send a reply that the requested HTTP method is not supported by the server.
	 * 
	 * @param request
	 *          The request which contained the invalid method
	 * @param response
	 *          A response which contains a list of supported methods
	 */
	protected void replyUnsupportedMethod(HttpRequest request, HttpResponse response) {
		String msg = String.format("Unsupported method '%s'.", request.getRequestLine().getMethod());
		log.error(msg);
		response.setReasonPhrase(msg);
		response.setStatusCode(400);
	}

	/**
	 * Tell the client that his request was invalid. Attach an explanation.
	 * 
	 * @param response
	 *          Response to fill
	 * @param permaFailMessage
	 *          Explanation why the request was bad
	 */
	protected void replyBadRequest(HttpResponse response, String permaFailMessage) {
		response.setStatusCode(400);
		response.setReasonPhrase(permaFailMessage);
	}

}
