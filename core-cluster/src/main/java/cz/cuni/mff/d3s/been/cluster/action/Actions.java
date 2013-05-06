package cz.cuni.mff.d3s.been.cluster.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * @author Martin Sixta
 */
public class Actions {
	private static final Logger log = LoggerFactory.getLogger(Actions.class);

	public static Action createAction(Request request, ClusterContext ctx) {

		switch (request.getType()) {

			case WAIT:
				return new MapWaitAction(request, ctx);
			case GET:
				return new MapGetAction(request, ctx);
			case SET:
				return new MapSetAction(request, ctx);
			case LATCH_DOWN:
				return new LatchDownAction(request, ctx);
			case LATCH_WAIT:
				return new LatchWaitAction(request, ctx);
			case LATCH_SET:
				return new LatchSetAction(request, ctx);
			case LATCH_HAS_COUNT:
				return new LatchHasCountAction(request, ctx);
			case CONTEXT_SUBMIT:
				return new ContextSubmitAction(request, ctx);
			case CONTEXT_WAIT:
				return new ContextWaitAction(request, ctx);
			default:
				String msg = String.format("No such action %s", request.getType());
				log.warn(msg);
				return new ErrorAction(msg);
		}
	}

	public static String latchNameForRequest(Request request) {
		return "latch_" + request.getTaskContextId() + "_" + request.getSelector();
	}

	public static String checkpointMapNameForRequest(Request request) {
		return "checkpointmap_" + request.getTaskContextId();
	}
}
