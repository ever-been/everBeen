package cz.everbeen.restapi.handlers;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.everbeen.restapi.ClusterApiConnection;
import cz.everbeen.restapi.ClusterConnectionException;
import cz.everbeen.restapi.ProtocolObjectOperation;
import cz.everbeen.restapi.protocol.ErrorObject;
import cz.everbeen.restapi.protocol.ProtocolObject;
import cz.everbeen.restapi.protocol.ProtocolObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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

	@GET
	@Path("/members")
	@Produces(PROTOCOL_OBJECT_MEDIA)
	public String getMembers() {
		return performAndAnswer(new ProtocolObjectOperation() {
			@Override
			public String name() {
				return "listClusterMembers";
			}

			@Override
			public ProtocolObject perform(BeenApi beenApi) throws BeenApiException {
				return ProtocolObjectFactory.clusterMembers(beenApi.getClusterMembers());
			}
		});
	}

	@GET
	@Path("/services")
	@Produces(PROTOCOL_OBJECT_MEDIA)
	public String getServices() {
		return performAndAnswer(new ProtocolObjectOperation() {
			@Override
			public String name() {
				return "listClusterServices";
			}

			@Override
			public ProtocolObject perform(BeenApi beenApi) throws BeenApiException {
				return ProtocolObjectFactory.clusterServices(beenApi.getClusterServices());
			}
		});
	}
}
