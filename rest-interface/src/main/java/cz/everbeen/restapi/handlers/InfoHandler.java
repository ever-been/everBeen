package cz.everbeen.restapi.handlers;

import cz.everbeen.restapi.ClusterApiConnection;
import cz.everbeen.restapi.ClusterConnectionException;
import cz.everbeen.restapi.protocol.ErrorObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A dummy REST handler for testing purposes
 * @author darklight
 */
@Path("/info")
public class InfoHandler extends Handler {

	private static final Logger log = LoggerFactory.getLogger(InfoHandler.class);

	@GET
	@Path("/config")
	@Produces(PROTOCOL_OBJECT_MEDIA)
	public String getConfig() {
		return serializeProtocolObject(ClusterApiConnection.getInstance().getConfig());
	}

	@GET
	@Path("/status")
	@Produces(PROTOCOL_OBJECT_MEDIA)
	public String getStatus() {
		try {
			return serializeProtocolObject(ClusterApiConnection.getInstance().getStatus());
		} catch (ClusterConnectionException e){
			return serializeProtocolObject(new ErrorObject(e.getMessage()));
		}
	}
}
