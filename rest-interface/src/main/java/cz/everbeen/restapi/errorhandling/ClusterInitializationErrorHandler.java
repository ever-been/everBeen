package cz.everbeen.restapi.errorhandling;

import cz.everbeen.restapi.RestApiContextInitializationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Jersey error handler for {@link cz.everbeen.restapi.RestApiContextInitializationException}
 *
 * @author darklight
 */
public class ClusterInitializationErrorHandler implements ExceptionMapper<RestApiContextInitializationException> {

	@Override
	public Response toResponse(RestApiContextInitializationException exception) {
		return ResponseFactory.internalServerError(exception, "Could not initialize REST API configuration");
	}
}
