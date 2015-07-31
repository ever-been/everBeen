package cz.everbeen.restapi.errorhandling;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Generic Jersey error handler (for {@link java.lang.Throwable})
 *
 * @author darklight
 */
public class GenericErrorHandler implements ExceptionMapper<Throwable> {

	@Override
	public Response toResponse(Throwable exception) {
		return ResponseFactory.internalServerError(exception);
	}
}
