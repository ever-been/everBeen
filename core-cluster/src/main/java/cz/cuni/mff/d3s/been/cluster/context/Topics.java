package cz.cuni.mff.d3s.been.cluster.context;

import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;
import cz.cuni.mff.d3s.been.core.protocol.Context;

/**
 * Utility class for topics-related handling.
 * 
 * @author Martin Sixta
 */
public class Topics {

	/** BEEN cluster connection */
	private ClusterContext clusterCtx;

	/**
	 * Package private constructor, creates a new instance that uses the specified
	 * BEEN cluster context.
	 * 
	 * @param clusterCtx
	 *          the cluster context to use
	 */
	Topics(ClusterContext clusterCtx) {
		// package private visibility prevents out-of-package instantiation	
		this.clusterCtx = clusterCtx;
	}

	/**
	 * Returns a Hazelcast topic with the specified name. If such a topic does not
	 * exist, it will be created.
	 * 
	 * @param name
	 *          name of the topic
	 * @param <E>
	 *          type of the topic items
	 * @return the topic with the specified name
	 */
	public <E> ITopic<E> getTopic(String name) {
		return clusterCtx.getTopic(name);
	}

	/**
	 * Published the specified message to the Hazelcast topic with the specified
	 * name.
	 * 
	 * @param name
	 *          name of the topic
	 * @param message
	 *          message to publish
	 * @param <E>
	 *          type of the topic items
	 */
	public <E> void publish(String name, E message) {
		ITopic<E> topic = getTopic(name);
		topic.publish(message);
	}

	/**
	 * Published the specified message to the global Hazelcast topic.
	 * 
	 * @param message
	 *          message to publish
	 * @param <E>
	 *          type of the topic items
	 */
	public <E> void publishInGlobalTopic(E message) {
		publish(Context.GLOBAL_TOPIC.getName(), message);
	}

	/**
	 * Adds a topic listener to the topic with the specified name.
	 * 
	 * @param name
	 *          the name of the topic
	 * @param listener
	 *          the listener to add
	 * @param <E>
	 *          the type of the topic items
	 */
	public <E> void addListener(String name, MessageListener<E> listener) {
		ITopic<E> topic = getTopic(name);
		topic.addMessageListener(listener);
	}

	/**
	 * Removes the topic listener from the topic with the specified name.
	 * 
	 * @param name
	 *          the name of the topic
	 * @param listener
	 *          the listener to add
	 * @param <E>
	 *          the type of the topic items
	 */
	public <E> void removeListener(String name, MessageListener<E> listener) {
		ITopic<E> topic = getTopic(name);
		topic.removeMessageListener(listener);
	}

}
