package cz.everbeen.restapi.handlers;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.api.BpkStreamHolder;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.everbeen.restapi.BeenApiOperation;
import cz.everbeen.restapi.ProtocolObjectOperation;
import cz.everbeen.restapi.model.SimpleStreamingOutput;
import cz.everbeen.restapi.protocol.*;
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

	/**
	 * Get the BPK by its ID
	 * @return The BPK
	 */
	@GET
	@Path("/{groupId}/{bpkId}/{version}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public StreamingOutput getBpk(
		@PathParam("groupId") final String groupId,
		@PathParam("bpkId") final String bpkId,
		@PathParam("version") final String version
	) {
		return perform(new BeenApiOperation<SimpleStreamingOutput>() {
			@Override
			public String name() {
				return "downloadBpk";
			}

			@Override
			public SimpleStreamingOutput perform(BeenApi beenApi) throws BeenApiException {
				final BpkIdentifier id = new BpkIdentifier().withGroupId(groupId).withBpkId(bpkId).withVersion(version);
				return new SimpleStreamingOutput(beenApi.downloadBpk(id));
			}

			@Override
			public SimpleStreamingOutput fallbackValue(Throwable error) {
				return null;
			}
		});
	}

	@GET
	@Produces(PROTOCOL_OBJECT_MEDIA)
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
	@Produces(PROTOCOL_OBJECT_MEDIA)
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
	@Path("/{groupId}/{bpkId}/{version}/td")
	public String listTdsForBpk(
			@PathParam("groupId") String groupId,
			@PathParam("bpkId") String bpkId,
			@PathParam("version") String version
	) {
		final BpkIdentifier bpkIdentifier = new BpkIdentifier().withGroupId(groupId).withBpkId(bpkId).withVersion(version);

		return performAndAnswer(new ProtocolObjectOperation() {
			@Override
			public String name() {
				return "listTaskDescriptorsForBpk";
			}

			@Override
			public ProtocolObject perform(BeenApi beenApi) throws BeenApiException {
				return new ProtocolObjectFactory().taskDescriptorList(beenApi.getTaskDescriptors(bpkIdentifier));
			}
		});
	}
}
