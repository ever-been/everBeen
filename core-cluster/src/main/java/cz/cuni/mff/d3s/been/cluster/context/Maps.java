package cz.cuni.mff.d3s.been.cluster.context;

import java.util.Collection;
import java.util.LinkedList;

import com.hazelcast.core.IMap;
import com.hazelcast.core.Instance;

/**
 * @author Martin Sixta
 */
public class Maps {

	private final ClusterContext clusterCtx;

	Maps(ClusterContext clusterCtx) {
		// package private visibility prevents out-of-package instantiation
		this.clusterCtx = clusterCtx;
	}

	public Collection<Object> getValues(String mapName) {
		Collection<Object> values = new LinkedList<>();

		if (clusterCtx.containsInstance(Instance.InstanceType.MAP, mapName)) {
			IMap<?, ?> hrMap = clusterCtx.getInstance().getMap(mapName);

			for (Object key : hrMap.keySet()) {
				values.add(hrMap.get(key));
			}
		} else {
			throw new IllegalArgumentException("No such map: " + mapName);
		}

		return values;
	}

	public <K, V> V getValue(String mapName, K key) {
		IMap<String, V> map = clusterCtx.getMap(mapName);
		return map.get(key);
	}

	public void setValue(String mapName, Object key, Object value) {
		clusterCtx.getMap(mapName).put(key, value);
	}

}
