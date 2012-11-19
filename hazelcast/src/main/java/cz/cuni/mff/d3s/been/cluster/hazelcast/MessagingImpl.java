package cz.cuni.mff.d3s.been.cluster.hazelcast;


import com.hazelcast.core.HazelcastInstance;
import cz.cuni.mff.d3s.been.cluster.Message;
import cz.cuni.mff.d3s.been.cluster.MessageListener;
import cz.cuni.mff.d3s.been.cluster.Messaging;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a prototype implementation of BEEN Messaging atop of Hazelcast.
 *
 * THIS IS A PROTOTYPE!
 *
 * @author Martin Sixta
 */
public class MessagingImpl implements Messaging {

	private HazelcastInstance hcInstance;

	// code accessing listeners is not THREAD SAFE! yet
	private Map<MessageListener, HCMessageListener> listeners = null;


	public MessagingImpl(HazelcastInstance hcInstance) {
		this.hcInstance = hcInstance;
		listeners = new HashMap<>();
	}

	@Override
	public void send(String destination, Message message) {
		hcInstance.getTopic(destination).publish(message);
	}

	@Override
	public void addMessageListener(String source, MessageListener listener) {
		HCMessageListener hcListener = new HCMessageListener(listener);
		listeners.put(listener, hcListener);
		hcInstance.getTopic(source).addMessageListener((hcListener));
	}

	@Override
	public void removeMessageListener(String source, MessageListener listener) {
		HCMessageListener hcListener = listeners.get(listener);
		hcInstance.getTopic(source).removeMessageListener(hcListener);
	}




}
