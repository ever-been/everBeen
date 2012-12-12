package cz.cuni.mff.d3s.been.cluster;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;
import com.hazelcast.impl.ascii.rest.RestValue;

import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.JSONSerializer;
import cz.cuni.mff.d3s.been.core.protocol.JSONSerializer.JSONSerializerException;
import cz.cuni.mff.d3s.been.core.protocol.cluster.MessageListener;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Messaging;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;

/**
 * This is a prototype implementation of BEEN Messaging atop of Hazelcast.
 * 
 * THIS IS A PROTOTYPE!
 * 
 * @author Martin Sixta
 */
public class MessagingImpl implements Messaging {

	private HazelcastInstance hcInstance;

	// code accessing listeners is not THREAD SAFE! yet (should be thread safe
	// now .... see concurrent hashmap (defined in ctor))
	private Map<MessageListener, HCMessageListener> listeners = null;

	public MessagingImpl(HazelcastInstance hcInstance) {
		this.hcInstance = hcInstance;
		addRestBridgeListener();
		listeners = new ConcurrentHashMap<>();
	}

	private void addRestBridgeListener() {
		String mapName = Context.GLOBAL_TOPIC_BRIDGE.getName();
		final IQueue<RestValue> q = hcInstance.getQueue(mapName);
		q.addItemListener(new ItemListener<RestValue>() {
			@Override
			public void itemRemoved(ItemEvent<RestValue> item) {
			}

			@Override
			public void itemAdded(ItemEvent<RestValue> item) {
				RestValue rv = q.poll();
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
						BaseMessage messageValue = (BaseMessage) JSONSerializer.deserialize(text, messageClass);
						send(Context.GLOBAL_TOPIC, messageValue);
					} catch (ClassNotFoundException e) {
						// FIXME logging
						e.printStackTrace();
					} catch (JSONSerializerException e) {
						// FIXME logging
						e.printStackTrace();
					}

				}
			}
		}, false);
	}

	@Override
	public void addMessageListener(Context source, MessageListener listener) {
		HCMessageListener hcListener = new HCMessageListener(listener);
		listeners.put(listener, hcListener);
		hcInstance.<BaseMessage> getTopic(source.getName()).addMessageListener((hcListener));
	}

	@Override
	public void removeMessageListener(Context source, MessageListener listener) {
		HCMessageListener hcListener = listeners.get(listener);
		hcInstance.<BaseMessage> getTopic(source.getName()).removeMessageListener(hcListener);
	}

	@Override
	public void send(Context context, BaseMessage message) {
		hcInstance.getTopic(context.getName()).publish(message);
	}

}
