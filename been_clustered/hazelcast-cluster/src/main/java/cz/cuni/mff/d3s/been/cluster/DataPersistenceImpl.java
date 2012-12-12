package cz.cuni.mff.d3s.been.cluster;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Instance;

import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.cluster.DataPersistence;

final class DataPersistenceImpl implements DataPersistence {

	public Map<String, List<?>> lists = new HashMap<>();

	private HazelcastInstance hcInstance;

	DataPersistenceImpl(HazelcastInstance hcInstance) {
		this.hcInstance = hcInstance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> List<E> getList(Context ctx) {
		String ctxName = ctx.getName();

		if (!lists.containsKey(ctxName)) {
			if (ctx.isNodeSpecific()) {
				MapConfig mapConfig = hcInstance.getConfig().getMapConfig(ctxName);
				mapConfig.setBackupCounts(0, 0);
			}
			lists.put(ctxName, hcInstance.<E> getList(ctxName));
		}

		return (List<E>) lists.get(ctxName);
	}

	@Override
	public <E> Set<E> getSet(Context ctx) {
		return hcInstance.getSet(ctx.getName());
		// FIXME ... see getList
	}

	@Override
	public <K, V> Map<K, V> getMap(Context ctx) {
		return hcInstance.getMap(ctx.getName());
		// FIXME ... see getList
	}

	@Override
	public Collection<String> list(String type) {
		List<String> list = new LinkedList<>();
		Collection<Instance> instances = hcInstance.getInstances();

		switch (type) {
		case "map":
			for (Instance instance : instances) {
				if (instance.getInstanceType() == Instance.InstanceType.MAP) {
					list.add(instance.getId().toString());
				}
			}
			break;
		case "list":
			for (Instance instance : instances) {
				if (instance.getInstanceType() == Instance.InstanceType.LIST) {
					list.add(instance.getId().toString());
				}
			}
			break;
		case "topic":
			for (Instance instance : instances) {
				if (instance.getInstanceType() == Instance.InstanceType.TOPIC) {
					list.add(instance.getId().toString());
				}
			}
			break;
		case "set":
			for (Instance instance : instances) {
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
