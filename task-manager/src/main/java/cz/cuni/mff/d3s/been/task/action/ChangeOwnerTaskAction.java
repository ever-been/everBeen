package cz.cuni.mff.d3s.been.task.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.Transaction;

import cz.cuni.mff.d3s.been.cluster.NodeType;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.context.Tasks;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * The goal of ChangeOwnerTaskAction is to change the owner of a TaskEntry to
 * the current node.
 * 
 * There might be several occasions when this can happen: 1) Key migration 2) A
 * node crash 3) Luck (LocalKeyScanner got to the entry sooner)
 * 
 * @author Martin Sixta
 */
final class ChangeOwnerTaskAction implements TaskAction {
	private static final Logger log = LoggerFactory.getLogger(ChangeOwnerTaskAction.class);

	private final ClusterContext ctx;
	private final TaskEntry entry;

	public ChangeOwnerTaskAction(ClusterContext ctx, TaskEntry entry) {
		if (ctx.getInstanceType() != NodeType.DATA) {
			throw new AssertionError(String.format(
					"%s must not be used on node of type %s",
					getClass().getName(),
					ctx.getInstanceType()));
		}
		this.ctx = ctx;
		this.entry = entry;
	}

	/**
	 * The goal is to change the owner of a TaskEntry. It's OK to fail.
	 * 
	 * @throws TaskActionException
	 */
	@Override
	public void execute() throws TaskActionException {
		final Tasks tasks = ctx.getTasks();
		// we are sure that the node is of type DATA (see constructor assertion)
		final String nodeId = ctx.getCluster().getLocalMember().getUuid();

		Transaction txn = ctx.getTransaction();
		try {
			txn.begin();
			tasks.assertClusterEqual(entry);
			entry.setOwnerId(nodeId);
			tasks.putTask(entry);
			txn.commit();

		} catch (Throwable e) {
			log.warn("Unable to change ownership of the entry: ", e);
			txn.rollback(); // OK, will get it next time if needed
		}

	}
}
