package cz.everbeen.restapi.handlers;

import cz.everbeen.restapi.ClusterApiConnection;
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
public class InfoHandler {

	private static final Logger log = LoggerFactory.getLogger(InfoHandler.class);

	//private final BeenApi api;

	@GET
	@Path("/config")
	@Produces(MediaType.APPLICATION_JSON)
	public String getConfig() {
		return ClusterApiConnection.getInstance().getConfig().toString();
	}

	@GET
	@Path("/status")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStatus() {
		return ClusterApiConnection.getInstance().getStatus().toString();
	}
}
