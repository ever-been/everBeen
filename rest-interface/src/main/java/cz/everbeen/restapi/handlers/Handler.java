package cz.everbeen.restapi.handlers;

import cz.everbeen.restapi.model.ErrorObject;
import cz.everbeen.restapi.model.ModelObject;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * A generic base for a Jersey handler.
 *
 * @author darklight
 */
abstract class Handler {
	private final ObjectMapper omap = new ObjectMapper();

	protected final String serializeModelObject(ModelObject modelObject) {
		try {
			return omap.writeValueAsString(modelObject);
		} catch (IOException e) {
			return new ErrorObject(e.getMessage()).toString();
		}
	}
}
