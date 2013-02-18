package cz.cuni.mff.d3s.been.core;

import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;

/**
 * @author Martin Sixta
 */
public class TopicUtils {
	public static <E> ITopic<E> getTopic(String name) {
		return ClusterUtils.getInstance().getTopic(name);
	}

	public static <E> void publish(String name, E message) {
		ITopic<E> topic = getTopic(name);
		topic.publish(message);
	}

	public static <E> void addListener(String name, MessageListener<E> listener) {
		ITopic<E> topic = getTopic(name);
		topic.addMessageListener(listener);
	}

	public static <E> void removeListener(String name, MessageListener<E> listener) {
		ITopic<E> topic = getTopic(name);
		topic.removeMessageListener(listener);
	}

}
