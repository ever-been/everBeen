package cz.everbeen.restapi.handlers;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.api.BpkHolder;
import cz.cuni.mff.d3s.been.api.BpkStreamHolder;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.everbeen.restapi.ClusterApiConnection;
import cz.everbeen.restapi.model.BPKStreamingOutput;
import cz.everbeen.restapi.protocol.BpkList;
import cz.everbeen.restapi.protocol.ErrorObject;
import cz.everbeen.restapi.protocol.UploadStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;

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
	public StreamingOutput getBpk(
		@QueryParam("groupId") String groupId,
		@QueryParam("bpkId") String bpkId,
		@QueryParam("version") String version
	) {
		final BpkIdentifier id = new BpkIdentifier().withGroupId(groupId).withBpkId(bpkId).withVersion(version);
		try {
			return new BPKStreamingOutput(beenApi.downloadBpk(id));
		} catch (BeenApiException e) {
			return null;
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String listBpks() {
		try {
			return serializeModelObject(BpkList.fromIdCollection(beenApi.getBpks()));
		} catch (BeenApiException e) {
			return serializeModelObject(new ErrorObject(e.getMessage()));
		}
	}

	/**
	 * Upload a BPK
	 * @return A JSON response object
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public String putBpk(InputStream bpk) {
		try {
			final BpkHolder holder = new BpkStreamHolder(bpk);
			beenApi.uploadBpk(holder);
			return serializeModelObject(new UploadStatus(true));
		} catch (IOException | BeenApiException e) {
			return serializeModelObject(new UploadStatus(false));
		}
	}
}
