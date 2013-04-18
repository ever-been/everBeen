package cz.cuni.mff.d3s.been.cluster.action;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.mq.rep.Replay;
import cz.cuni.mff.d3s.been.mq.rep.Replays;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * @author Martin Sixta
 */
final class TaskStatusWaitAction implements Action {
	private final Request request;
	private final ClusterContext ctx;
	BlockingQueue<Replay> queue = new LinkedBlockingQueue<>();

	public TaskStatusWaitAction(Request request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	class TaskWaiter implements EntryListener<String, TaskEntry> {

		@Override
		public void entryAdded(EntryEvent<String, TaskEntry> event) {
			add(event.getValue());
		}

		@Override
		public void entryUpdated(EntryEvent<String, TaskEntry> event) {
			add(event.getValue());
		}

		@Override
		public void entryEvicted(EntryEvent<String, TaskEntry> event) {
			addFailure("EVICTED");
		}

		@Override
		public void entryRemoved(EntryEvent<String, TaskEntry> event) {
			addFailure("REMOVED");
		}

		private void add(TaskEntry entry) {
			Replay replay = createOkReplay(entry);
			queue.add(replay);
		}

		private void addFailure(String msg) {
			Replay replay = Replays.createErrorReplay(msg);
			queue.add(replay);
		}
	}

	@Override
	public Replay goGetSome() {
		String taskId = request.getSelector();

		if (taskId == null || taskId.isEmpty()) {
			return Replays.createErrorReplay("No such '%s' task id", taskId);
		}

		Replay replay = null;

		final TaskWaiter waiter = new TaskWaiter();

		IMap<String, TaskEntry> map = ctx.getTasksUtils().getTasksMap();

		map.addEntryListener(waiter, taskId, true);

		TaskEntry value = map.get(taskId);

		if (value == null) {
			try {
				replay = queue.poll(request.getTimeout(), TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO error msg?
				e.printStackTrace();
			}
		}

		if (replay == null) {
			replay = Replays.createErrorReplay("TIMEOUT");
		}

		map.removeEntryListener(waiter);
		queue.clear();

		return replay;
	}

	private Replay createOkReplay(TaskEntry entry) {
		return Replays.createOkReplay(entry.getState().toString());
	}

}
