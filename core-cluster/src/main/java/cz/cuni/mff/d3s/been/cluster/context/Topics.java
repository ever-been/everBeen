package cz.cuni.mff.d3s.been.cluster.context;

import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;
import cz.cuni.mff.d3s.been.core.protocol.Context;

/**
 * @author Martin Sixta
 */
public class Topics {

	private ClusterContext clusterCtx;

	Topics(ClusterContext clusterCtx) {
		// package private visibility prevents out-of-package instantiation	
		this.clusterCtx = clusterCtx;
	}

	public <E> ITopic<E> getTopic(String name) {
		return clusterCtx.getTopic(name);
	}

	public <E> void publish(String name, E message) {
		ITopic<E> topic = getTopic(name);
		topic.publish(message);
	}

	public <E> void publishInGlobalTopic(E message) {
		publish(Context.GLOBAL_TOPIC.getName(), message);
	}

	public <E> void addListener(String name, MessageListener<E> listener) {
		ITopic<E> topic = getTopic(name);
		topic.addMessageListener(listener);
	}

	public <E> void removeListener(String name, MessageListener<E> listener) {
		ITopic<E> topic = getTopic(name);
		topic.removeMessageListener(listener);
	}

}
