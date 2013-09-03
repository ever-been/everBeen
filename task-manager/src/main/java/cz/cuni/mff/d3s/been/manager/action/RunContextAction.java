package cz.cuni.mff.d3s.been.manager.action;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;

/**
 * @author Martin Sixta
 */
public class RunContextAction implements TaskAction {
	private final ClusterContext ctx;
	private final String contextId;

	/**
	 * Creates RunContextAction.
	 * 
	 * @param ctx
	 *          connection to the cluster
	 * @param contextId
	 *          targeted context id
	 */
	public RunContextAction(final ClusterContext ctx, final String contextId) {
		this.ctx = ctx;
		this.contextId = contextId;
	}

	@Override
	public void execute() throws TaskActionException {
		ctx.getTaskContexts().runContext(contextId);
	}
}
