package cz.everbeen.restapi.errorhandling;

import cz.everbeen.restapi.protocol.ErrorObject;
import cz.everbeen.restapi.protocol.ExceptionWrapper;
import cz.everbeen.restapi.protocol.ProtocolObject;
import cz.everbeen.restapi.protocol.ProtocolObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Factory for {@link javax.ws.rs.core.Response} objects
 * Used in {@link javax.ws.rs.ext.ExceptionMapper} implementations
 *
 * @author darklight
 */
public final class ResponseFactory {

	private static final Logger log;
	private static final ProtocolObjectSerializer protocolObjectSerializer;

	private static final String REASON = "reason";

	static {
		log = LoggerFactory.getLogger(ResponseFactory.class);
		protocolObjectSerializer = new ProtocolObjectSerializer();
	}

	private ResponseFactory() {}

	public static Response internalServerError(Throwable t) {
		return internalServerError(t, null);
	}

	public static Response internalServerError(Throwable t, String detail) {
		final Response.ResponseBuilder responseBuilder =
			(
				detail == null ?
				Response.serverError() :
				Response.serverError().header(REASON, detail)
			)
			.status(Response.Status.INTERNAL_SERVER_ERROR)
			.type(ProtocolObjectSerializer.MIME_TYPE);
		try {
			return responseBuilder
				.entity(
						protocolObjectSerializer.serialize(
								ExceptionWrapper.fromException(t)
						)
				)
				.build();
		} catch (IOException ioe) {
			log.error("Failed to preSerialize exception wrapper", ioe);
			return responseBuilder.entity(
				new ErrorObject(t.getMessage()).toString()
			).build();
		}
	}
}
