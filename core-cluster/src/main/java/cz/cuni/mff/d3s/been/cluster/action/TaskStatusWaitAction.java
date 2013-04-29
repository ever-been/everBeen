package cz.cuni.mff.d3s.been.cluster.action;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.mq.rep.Replies;
import cz.cuni.mff.d3s.been.mq.rep.Reply;
import cz.cuni.mff.d3s.been.mq.req.Request;

/**
 * @author Martin Sixta
 */
final class TaskStatusWaitAction implements Action {
	private final Request request;
	private final ClusterContext ctx;
	BlockingQueue<Reply> queue = new LinkedBlockingQueue<>();

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
			Reply reply = createOkReply(entry);
			queue.add(reply);
		}

		private void addFailure(String msg) {
			Reply reply = Replies.createErrorReply(msg);
			queue.add(reply);
		}
	}

	@Override
	public Reply goGetSome() {
		String taskId = request.getSelector();

		if (taskId == null || taskId.isEmpty()) {
			return Replies.createErrorReply("No such '%s' task id", taskId);
		}

		Reply reply = null;

		final TaskWaiter waiter = new TaskWaiter();

		IMap<String, TaskEntry> map = ctx.getTasks().getTasksMap();

		map.addEntryListener(waiter, taskId, true);

		TaskEntry value = map.get(taskId);

		if (value == null) {
			try {
				reply = queue.poll(request.getTimeout(), TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO error msg?
				e.printStackTrace();
			}
		}

		if (reply == null) {
			reply = Replies.createErrorReply("TIMEOUT");
		}

		map.removeEntryListener(waiter);
		queue.clear();

		return reply;
	}

	private Reply createOkReply(TaskEntry entry) {
		return Replies.createOkReply(entry.getState().toString());
	}

}
