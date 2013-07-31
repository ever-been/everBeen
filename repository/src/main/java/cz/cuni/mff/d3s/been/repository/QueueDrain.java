package cz.cuni.mff.d3s.been.repository;

import com.hazelcast.core.IQueue;

import cz.cuni.mff.d3s.been.cluster.Service;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.persistence.SuccessAction;

/**
 * A generic drain for a generic distributed queue.
 * 
 * @author darklight
 */
abstract class QueueDrain<T> implements Service {

	private final Float failRateThreshold;
	private final Long suspendTimeOnHighFailRate;
	private final String queueName;
	private final ClusterContext ctx;
	private final SuccessAction<T> successAction;
	private IQueue<T> queue;
	private Digester<T> digester;
	private ItemCounterListener<T> itemListener;

	protected QueueDrain(ClusterContext ctx, String queueName, SuccessAction<T> successAction, Float failRateThreshold, Long suspendTimeOnHighFailRate) {
		this.ctx = ctx;
		this.queueName = queueName;
		this.successAction = successAction;
		this.failRateThreshold = failRateThreshold;
		this.suspendTimeOnHighFailRate = suspendTimeOnHighFailRate;
	}

	@Override
	public void start() throws ServiceException {
		queue = ctx.getQueue(queueName);
		digester = Digester.create(createTakeAction(), createPollAction(), successAction, createFailAction(), failRateThreshold, suspendTimeOnHighFailRate);
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
