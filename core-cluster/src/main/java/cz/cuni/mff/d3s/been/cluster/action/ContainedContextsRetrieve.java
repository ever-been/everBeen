package cz.cuni.mff.d3s.been.cluster.action;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.benchmark.ResubmitHistory;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskContextStateInfo;
import cz.cuni.mff.d3s.been.socketworks.twoway.Replies;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.socketworks.twoway.Request;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequest;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * @author Kuba Brecka
 */
public class ContainedContextsRetrieve implements Action {
	private final Request request;
	private final ClusterContext ctx;

	public ContainedContextsRetrieve(CheckpointRequest request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	@Override
	public Reply handle() {
		String benchmarkId = this.request.getSelector();
		TaskContextStateInfo info = new TaskContextStateInfo();
		for (TaskContextEntry taskContextEntry : ctx.getBenchmarks().getTaskContextsInBenchmark(benchmarkId)) {
			TaskContextStateInfo.Item i = new TaskContextStateInfo.Item();
			i.state = taskContextEntry.getContextState();
			i.taskContextId = taskContextEntry.getId();
			info.items.add(i);
		}

		String s = null;
		try {
			s = JSONUtils.newInstance().serialize(info);
		} catch (JsonException e) {
			throw new IllegalArgumentException("Cannot serialize TaskContextStateInfo to JSON.", e);
		}
		return Replies.createOkReply(s);
	}
}
