package cz.cuni.mff.d3s.been.hostruntime;

import com.hazelcast.core.IQueue;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.impl.ascii.rest.RestValue;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.core.ClusterUtils;
import cz.cuni.mff.d3s.been.core.JSONUtils;
import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;

/**
 * @author Martin Sixta
 */
final class RestBridgeListener implements ItemListener<RestValue>, IClusterService {

	final IQueue<RestValue> restQueue;
	final ITopic<BaseMessage> globalTopic;

	public RestBridgeListener() {
		restQueue = ClusterUtils.getQueue(Context.GLOBAL_TOPIC_BRIDGE.getName());
		globalTopic = ClusterUtils.getTopic(Context.GLOBAL_TOPIC.getName());
	}

	public void start() {
		restQueue.addItemListener(this, false);
	}

	public void stop() {
		restQueue.removeItemListener(this);
	}

	@Override
	public void itemAdded(ItemEvent<RestValue> item) {
		RestValue rv = restQueue.poll();

		if (rv != null) {
			String text = new String(rv.getValue());
			int hashIndex = text.indexOf("#");

			String messageType = null;
			if (hashIndex >= 0) {
				messageType = text.substring(0, hashIndex);
				text = text.substring(hashIndex + 1, text.length());
			}

			Class<?> messageClass;
			try {
				messageClass = Class.forName(messageType);
				BaseMessage messageValue = (BaseMessage) JSONUtils.deserialize(text, messageClass);
				globalTopic.publish(messageValue);
			} catch (ClassNotFoundException e) {
				// FIXME logging
				e.printStackTrace();
			} catch (JSONUtils.JSONSerializerException e) {
				// FIXME logging
				e.printStackTrace();
			}

		}
	}

	@Override
	public void itemRemoved(ItemEvent<RestValue> item) {

	}
}
