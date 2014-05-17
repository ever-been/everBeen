package cz.everbeen.restapi;

import cz.everbeen.restapi.protocol.ErrorObject;
import cz.everbeen.restapi.protocol.ProtocolObject;

/**
 * Default implementation of {@link cz.everbeen.restapi.BeenApiOperation}
 * @author darklight
 */
public abstract class ProtocolObjectOperation implements BeenApiOperation<ProtocolObject> {

	@Override
	public ProtocolObject fallbackValue(Throwable error) {
		return new ErrorObject(error.getMessage());
	}
}
