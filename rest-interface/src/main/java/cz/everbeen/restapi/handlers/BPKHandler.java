package cz.everbeen.restapi.handlers;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.api.BpkHolder;
import cz.cuni.mff.d3s.been.api.BpkStreamHolder;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.everbeen.restapi.ClusterApiConnection;
import cz.everbeen.restapi.model.BPKStreamingOutput;
import cz.everbeen.restapi.protocol.BpkList;
import cz.everbeen.restapi.protocol.ErrorObject;
import cz.everbeen.restapi.protocol.TaskDescriptorList;
import cz.everbeen.restapi.protocol.UploadStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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
			log.error("Failed to fetch BPK", e);
			return null;
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String listBpks() {
		try {
			return serializeModelObject(BpkList.fromIdCollection(beenApi.getBpks()));
		} catch (BeenApiException e) {
			log.error("Failed to fetch BPK list", e);
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
			log.error("Failed to upload BPK", e);
			return serializeModelObject(new UploadStatus(false));
		}
	}

	/**
	 * List available task descriptors in a BPK
	 * @param bpkId ID of the package
	 * @param groupId ID of the package's group
	 * @param version Version of the package
	 * @return A list of available task descriptors contained in the BPK
	 */
	@GET
	@Path("/td")
	public String listRunConfigurationsForBpk(
			@QueryParam("bpkId") String bpkId,
			@QueryParam("groupId") String groupId,
			@QueryParam("version") String version
	) {
		final BpkIdentifier bpkIdentifier = new BpkIdentifier().withGroupId(groupId).withBpkId(bpkId).withVersion(version);
		try {
			final Map<String,TaskDescriptor> tdmap = beenApi.getTaskDescriptors(bpkIdentifier);
			return serializeModelObject(new TaskDescriptorList(tdmap.keySet()));
		} catch (BeenApiException e) {
			log.error("Failed to fetch task descriptors for BPK {}", bpkIdentifier, e);
			return serializeModelObject(new ErrorObject(e.getMessage()));
		}
	}
}
