package cz.cuni.mff.d3s.been.hostruntime;


import com.hazelcast.config.Config;
import com.hazelcast.config.UrlXmlConfig;
import com.hazelcast.core.*;

import static cz.cuni.mff.d3s.been.cluster.Names.*;

import java.net.InetSocketAddress;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.Set;


/**
 *
 * PROTOTYPE for playing around with ideas.
 *
 * @author Martin Sixta
 */
public class HostRuntime implements MessageListener, EntryListener<Integer, String>, ItemListener<String> {

	private HazelcastInstance hazelcastInstance = null;

	private HostRuntimeInfo info;

	Random generator = new Random();

	private void connect() {
		assert hazelcastInstance == null : "Already connected to the cluster!";

		URL url = this.getClass().getResource("/hazelcast.xml");

		Config config;
		try {
			config = new UrlXmlConfig(url);
		} catch (IOException e) {
			e.printStackTrace();
			config = null;
		}
		hazelcastInstance = Hazelcast.newHazelcastInstance(config);
		info = getHostRuntimeInfo();
	}

	/**
	 *
	 *
	 */
	private void join() {
		ITopic topic = hazelcastInstance.getTopic(BEEN_TOPIC_MAIN_NAME);
		topic.addMessageListener(this);

		IMap<Integer, String> taskMap = hazelcastInstance.getMap(BEEN_TASK_MAP_NAME);
		taskMap.addLocalEntryListener(this);

		IQueue<String> taskQueue = hazelcastInstance.getQueue(info.getUuid());
		taskQueue.addItemListener(this, true);

	}

	private void register() {
		HostRuntimeInfo info = getHostRuntimeInfo();
		hazelcastInstance.getMap(HOSTRUNTIME_MAP_NAME).put(info.getUuid(), info);
	}

	HostRuntimeInfo getHostRuntimeInfo() {
		Member member = hazelcastInstance.getCluster().getLocalMember();

		String name = hazelcastInstance.getName();
		InetSocketAddress address = member.getInetSocketAddress();
		String uuid = member.getUuid();

		return new HostRuntimeInfo(name, address, uuid);

	}

	public static void main(String[] args) {

		HostRuntime runtime = new HostRuntime();
		runtime.connect();

		System.out.println(runtime.getHostRuntimeInfo());

		runtime.join();

		runtime.register();




	}

	@Override
	public void onMessage(Message message) {
		System.out.println("HR#BEEN_TOPIC: " + message);
	}

	@Override
	public void entryAdded(EntryEvent<Integer, String> entryEvent) {
		String value = entryEvent.getValue();

		//Select a host
		IMap<String, HostRuntimeInfo> hrMap = hazelcastInstance.getMap(HOSTRUNTIME_MAP_NAME);

		Set<String> keys = hrMap.keySet();
		String[] myKeys = new String[keys.size()];

		int randomHost = generator.nextInt(keys.size());

		myKeys = keys.toArray(myKeys);


		HostRuntimeInfo selectedHost = hrMap.get(myKeys[randomHost]);

		System.out.printf("Selected host %s for %s\n", selectedHost, value);

		IQueue<String> taskQueue = hazelcastInstance.getQueue(selectedHost.getUuid());

		try {
			taskQueue.put(value);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


	}

	@Override
	public void entryRemoved(EntryEvent<Integer, String> entryEvent) {

	}

	@Override
	public void entryUpdated(EntryEvent<Integer, String> entryEvent) {

	}

	@Override
	public void entryEvicted(EntryEvent<Integer, String> entryEvent) {

	}

	@Override
	public void itemAdded(ItemEvent<String> item) {
		System.out.println("Executing echo: " + item.getItem());
	}

	@Override
	public void itemRemoved(ItemEvent<String> item) {

	}
}
