package cz.cuni.mff.d3s.been.cluster;

import java.io.IOException;
import java.net.URL;

import com.hazelcast.config.Config;
import com.hazelcast.config.UrlXmlConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import cz.cuni.mff.d3s.been.core.protocol.cluster.DataPersistence;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Member;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Messaging;


final class HazelcastMember implements Member {

	private HazelcastInstance hcInstance = null;

	private Messaging hcMessaging;
	
	private DataPersistence hcDataPersistence;

	private Long id;

	@Override
	public Messaging getMessaging() {
		if (hcMessaging == null) {
			hcMessaging = new MessagingImpl(hcInstance);
		}

		return hcMessaging;
	}

	@Override
	public DataPersistence getDataPersistence() {
		if (hcDataPersistence == null) {
			hcDataPersistence = new DataPersistenceImpl(hcInstance);
		}

		return hcDataPersistence;
	}

	@Override
	public void connect() {
		URL url = this.getClass().getResource("/hazelcast.xml");

		Config config;
		try {
			config = new UrlXmlConfig(url);
		} catch (IOException e) {
			e.printStackTrace();
			config = null;
		}
		hcInstance = Hazelcast.newHazelcastInstance(config);
	}

	@Override
	public void disconnect() {
		Hazelcast.shutdownAll();
	}
	
	@Override
	public String getNodeId() {
		if (id == null) {
			id = hcInstance.getIdGenerator("BEEN_NODE_ID").newId();
		}
		return String.valueOf(id);
	}
}
