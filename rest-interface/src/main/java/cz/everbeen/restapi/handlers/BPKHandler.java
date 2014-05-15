package cz.everbeen.restapi.handlers;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.everbeen.restapi.ClusterApiConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

/**
 * REST handler for BPKs
 *
 * @author darklight
 */
@Path("/bpk")
public class BPKHandler extends Handler {

	private static final Logger log = LoggerFactory.getLogger(BPKHandler.class);

	private final BeenApi beenApi;

	public BPKHandler() {
		beenApi = ClusterApiConnection.getInstance().getApi();
		log.info("{} initialized", getClass().getSimpleName());
	}

	/**
	 * Get the BPK by its ID
	 * @return The BPK
	 */
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public StreamingOutput getBpk() {
		// TODO call BeenApi
		return null;
	}

	/**
	 * Upload a BPK
	 * @return A JSON response object
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public String putBpk() {
		// TODO describe response in JavaDoc
		// TODO call BeenApi
		return null;
	}
}
