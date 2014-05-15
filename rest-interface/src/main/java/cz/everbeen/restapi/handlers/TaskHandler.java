package cz.everbeen.restapi.handlers;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.everbeen.restapi.ClusterApiConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * The REST handler for task operation
 *
 * @author darklight
 */
@Path("/task")
public class TaskHandler extends Handler {

	private static final Logger log = LoggerFactory.getLogger(TaskHandler.class);

	private final BeenApi beenApi;

	public TaskHandler() {
		this.beenApi = ClusterApiConnection.getInstance().getApi();
	}
}
