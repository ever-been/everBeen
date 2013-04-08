package cz.cuni.mff.d3s.been.hostruntime;

import com.hazelcast.core.IQueue;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.impl.ascii.rest.RestValue;

import cz.cuni.mff.d3s.been.cluster.Service;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;

/**
 * This class should not be used in real environment while not properly tested
 * and refactored.
 * 
 * @author Tadeáš Palusga
 * 
 */
@Deprecated
final class RestBridgeListener implements ItemListener<RestValue>, Service {

	final IQueue<RestValue> restQueue;
	final ITopic<BaseMessage> globalTopic;

	public RestBridgeListener(ClusterContext clusterCtx) {
		restQueue = clusterCtx.getQueue(Context.GLOBAL_TOPIC_BRIDGE.getName());
		globalTopic = clusterCtx.getTopic(Context.GLOBAL_TOPIC.getName());
	}

	@Override
	public void start() {
		restQueue.addItemListener(this, false);
	}

	@Override
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
				BaseMessage messageValue = (BaseMessage) JSONUtils.deserialize(
						text,
						messageClass);
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
