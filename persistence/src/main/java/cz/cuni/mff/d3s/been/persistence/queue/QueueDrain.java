package cz.cuni.mff.d3s.been.persistence.queue;

import com.hazelcast.core.IQueue;

import cz.cuni.mff.d3s.been.cluster.Service;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.persistence.Digester;
import cz.cuni.mff.d3s.been.persistence.ItemCounterListener;
import cz.cuni.mff.d3s.been.persistence.PersistAction;
import cz.cuni.mff.d3s.been.persistence.Poll;
import cz.cuni.mff.d3s.been.persistence.Take;

/**
 * A generic drain for a generic distributed queue.
 * 
 * @author darklight
 */
abstract class QueueDrain<T> implements Service {

	private final String queueName;
	private final ClusterContext ctx;
	private final PersistAction<T> persistAction;
	private IQueue<T> queue;
	private Digester<T> digester;
	private ItemCounterListener<T> itemListener;

	protected QueueDrain(ClusterContext ctx, String queueName, PersistAction<T> persistAction) {
		this.ctx = ctx;
		this.queueName = queueName;
		this.persistAction = persistAction;
	}

	@Override
	public void start() throws ServiceException {
		queue = ctx.getQueue(queueName);
		digester = Digester.create(createTakeAction(), createPollAction(), persistAction, createFailAction());
		itemListener = ItemCounterListener.create(digester);

		digester.start();
		queue.addItemListener(itemListener, false);
	}

	@Override
	public void stop() {
		queue.removeItemListener(itemListener);
		digester.stop();
	}

	protected Take<T> createTakeAction() {
		return new QueueTake<T>(queue);
	}

	protected Poll<T> createPollAction() {
		return new QueuePoll<T>(queue);
	}

	protected QueueFailAction<T> createFailAction() {
		return new QueueFailAction<T>(queue);
	}
}
