package cz.cuni.mff.d3s.been.swrepository.httpserver;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fallback request handler that is used in case no handler is found for the
 * requested mapping. Just ensures a correct reply, but doesn't send any actual
 * data.
 * 
 * @author darklight
 * 
 */
public class FallbackRequestHandler implements HttpRequestHandler {

	/** Logger used for error messages */
	private static final Logger log = LoggerFactory
			.getLogger(FallbackRequestHandler.class);

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		log.info(String.format("Fallback handler used for request %s.", request));
		// TODO maybe say something smart
		
	}
}
