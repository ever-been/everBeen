package cz.cuni.mff.d3s.been.cluster.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequest;

/**
 * Utility class for creating an {@link Action} from a task request.
 * 
 * @author Martin Sixta
 */
public class Actions {

	/** slf4j logger */
	private static final Logger log = LoggerFactory.getLogger(Actions.class);

	/**
	 * Creates an {@link Action} from the specified request.
	 * 
	 * @param request
	 *          the task/benchmark request
	 * @param ctx
	 *          the cluster context instance under which the action is to be
	 *          performed
	 * @return the newly created action that will handle the request
	 */
	public static Action createAction(CheckpointRequest request, ClusterContext ctx) {

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
			case STORAGE_PERSIST:
				return new StoragePersistAction(request, ctx);
			case STORAGE_RETRIEVE:
				return new StorageRetrieveAction(request, ctx);
			case RESUBMIT_HISTORY_RETRIEVE:
				return new ResubmitHistoryRetrieve(request, ctx);
			case CONTAINED_CONTEXTS_RETRIEVE:
				return new ContainedContextsRetrieve(request, ctx);
			default:
				String msg = String.format("No such action %s", request.getType());
				log.warn(msg);
				return new ErrorAction(msg);
		}
	}

	/**
	 * Returns the string representation of a latch for latch-related requests.
	 * The string is later used as the name of the key in the corresponding
	 * Hazelcast map.
	 * 
	 * @param request
	 *          the request to process
	 * @return the string representation of the latch
	 */
	public static String latchNameForRequest(CheckpointRequest request) {
		return "latch_" + request.getTaskContextId() + "_" + request.getSelector();
	}

	/**
	 * Returns the string representation of the checkpoints Hazelcast map for the
	 * specified request. All tasks from a task context share the same checkpoints
	 * map.
	 * 
	 * @param request
	 *          the request to process
	 * @return the name of the Hazelcast map for checkpoints
	 */
	public static String checkpointMapNameForRequest(CheckpointRequest request) {
		return "checkpointmap_" + request.getTaskContextId();
	}

}
