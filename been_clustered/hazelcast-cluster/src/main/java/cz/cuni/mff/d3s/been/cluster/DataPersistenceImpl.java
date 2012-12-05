package cz.cuni.mff.d3s.been.cluster;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Instance;

import cz.cuni.mff.d3s.been.core.protocol.cluster.DataPersistence;

final class DataPersistenceImpl implements DataPersistence {

	private HazelcastInstance hcInstance;

	DataPersistenceImpl(HazelcastInstance hcInstance) {
		this.hcInstance = hcInstance;
	}

	@Override
	public <E> Set<E> getSet(String name) {
		return hcInstance.getSet(name);
	}

	@Override
	public <E> List<E> getList(String name) {
		return hcInstance.getList(name);
	}

	@Override
	public <K, V> Map<K, V> getMap(String name) {
		return hcInstance.getMap(name);
	}

	@Override
	public Collection<String> list(String name) {
		List<String> list = new LinkedList<>();
		Collection<Instance> instances = hcInstance.getInstances();

		switch (name) {
			case "map":
				for (Instance instance: instances) {
					if (instance.getInstanceType() == Instance.InstanceType.MAP) {
						list.add(instance.getId().toString());
					}
				}
				break;
			case "list":
				for (Instance instance: instances) {
					if (instance.getInstanceType() == Instance.InstanceType.LIST) {
						list.add(instance.getId().toString());
					}
				}
				break;
			case "topic":
				for (Instance instance: instances) {
					if (instance.getInstanceType() == Instance.InstanceType.TOPIC) {
						list.add(instance.getId().toString());
					}
				}
				break;
			case "set":
				for (Instance instance: instances) {
					if (instance.getInstanceType() == Instance.InstanceType.SET) {
						list.add(instance.getId().toString());
					}
				}
				break;
			default:
				break;

		}
		return list;
	}
}
