package cz.everbeen.restapi.handlers;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.api.BpkHolder;
import cz.cuni.mff.d3s.been.api.BpkStreamHolder;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.everbeen.restapi.BeenApiOperation;
import cz.everbeen.restapi.ClusterApiConnection;
import cz.everbeen.restapi.ProtocolObjectOperation;
import cz.everbeen.restapi.model.BPKStreamingOutput;
import cz.everbeen.restapi.protocol.*;
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

	/**
	 * Get the BPK by its ID
	 * @return The BPK
	 */
	@GET
	@Path("/{groupId}/{bpkId}/{version}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public StreamingOutput getBpk(
		@QueryParam("groupId") final String groupId,
		@QueryParam("bpkId") final String bpkId,
		@QueryParam("version") final String version
	) {
		return perform(new BeenApiOperation<BPKStreamingOutput>() {
			@Override
			public String name() {
				return "downloadBpk";
			}

			@Override
			public BPKStreamingOutput perform(BeenApi beenApi) throws BeenApiException {
				final BpkIdentifier id = new BpkIdentifier().withGroupId(groupId).withBpkId(bpkId).withVersion(version);
				return new BPKStreamingOutput(beenApi.downloadBpk(id));
			}

			@Override
			public BPKStreamingOutput fallbackValue(Throwable error) {
				return null;
			}
		});
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String listBpks() {
		return performAndAnswer(new ProtocolObjectOperation() {
			@Override
			public String name() {
				return "listBpks";
			}

			@Override
			public ProtocolObject perform(BeenApi beenApi) throws BeenApiException {
				return ProtocolObjectFactory.bpkList(beenApi.getBpks());
			}
		});
	}

	/**
	 * Upload a BPK
	 * @return A JSON response object
	 */
	@PUT
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public String putBpk(final InputStream bpk) {
		return performAndAnswer(new ProtocolObjectOperation() {
			@Override
			public String name() {
				return "uploadBpk";
			}

			@Override
			public ProtocolObject perform(BeenApi beenApi) throws BeenApiException {
				try {
					beenApi.uploadBpk(new BpkStreamHolder(bpk));
					return UploadStatus.ok();
				} catch (IOException e) {
					log.error("Failed to receive provided BPK stream", e);
					return UploadStatus.fail();
				}
			}
		});
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
	public String listTdsForBpk(
			@QueryParam("bpkId") String bpkId,
			@QueryParam("groupId") String groupId,
			@QueryParam("version") String version
	) {
		final BpkIdentifier bpkIdentifier = new BpkIdentifier().withGroupId(groupId).withBpkId(bpkId).withVersion(version);

		return performAndAnswer(new ProtocolObjectOperation() {
			@Override
			public String name() {
				return "listTaskDescriptorsForBpk";
			}

			@Override
			public ProtocolObject perform(BeenApi beenApi) throws BeenApiException {
				return new TaskDescriptorList(beenApi.getTaskDescriptors(bpkIdentifier).keySet());
			}
		});
	}
}
