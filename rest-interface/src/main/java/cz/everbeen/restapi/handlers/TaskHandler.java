package cz.everbeen.restapi.handlers;

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

}
