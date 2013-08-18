package cz.cuni.mff.d3s.been.cluster.action;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.socketworks.twoway.Replies;
import cz.cuni.mff.d3s.been.socketworks.twoway.Reply;
import cz.cuni.mff.d3s.been.socketworks.twoway.Request;
import cz.cuni.mff.d3s.been.task.checkpoints.CheckpointRequest;

/**
 * An {@link Action} that handles a request for waiting until a task has
 * finished.
 * 
 * @author Martin Sixta
 */
final class TaskStatusWaitAction implements Action {

	/** the request to handle */
	private final Request request;

	/** BEEN cluster instance */
	private final ClusterContext ctx;

	/** a blocking queue that is used for waiting */
	BlockingQueue<Reply> queue = new LinkedBlockingQueue<>();

	/**
	 * Default constructor, creates the action with the specified request and
	 * cluster context.
	 * 
	 * @param request
	 *          the request to handle
	 * @param ctx
	 *          the cluster context
	 */
	public TaskStatusWaitAction(CheckpointRequest request, ClusterContext ctx) {
		this.request = request;
		this.ctx = ctx;
	}

	/**
	 * A helper class which implements a listener for a specified Hazelcast map
	 * entry and adds it into the blocking queue when the event occurs.
	 */
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

		/**
		 * Creates a positive reply and add the task entry into the blocking queue.
		 * 
		 * @param entry
		 *          the task entry
		 */
		private void add(TaskEntry entry) {
			Reply reply = createOkReply(entry);
			queue.add(reply);
		}

		/**
		 * Creates a failure reply and add the task entry into the blocking queue.
		 * 
		 * @param msg
		 *          the message of the error
		 */
		private void addFailure(String msg) {
			Reply reply = Replies.createErrorReply(msg);
			queue.add(reply);
		}
	}

	@Override
	public Reply handle() {
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

	/**
	 * Creates a positive reply with the specified task entry.
	 * 
	 * @param entry
	 *          the task entry
	 * @return a new positive reply
	 */
	private Reply createOkReply(TaskEntry entry) {
		return Replies.createOkReply(entry.getState().toString());
	}

}
