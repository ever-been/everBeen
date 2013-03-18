package cz.cuni.mff.d3s.been.cluster.context;

import com.hazelcast.core.IMap;
import com.hazelcast.core.Instance;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 
 * Utility class for often map related functions.
 * 
 * @author Martin Sixta
 */
public class Maps {

	private final ClusterContext clusterCtx;

	Maps(ClusterContext clusterCtx) {
		// package private visibility prevents out-of-package instantiation
		this.clusterCtx = clusterCtx;
	}

	/**
	 * 
	 * Returns collection of all values in a map.
	 * 
	 * Use with care, such a map can be big.
	 * 
	 * @param mapName
	 *          name of the map
	 * @return Collection of all values of the map (possibly empty)
	 * @throws IllegalArgumentException
	 *           if such a map does not exists
	 */
	public Collection<Object> getValues(String mapName) throws IllegalArgumentException {
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

	/**
	 * Returns value from a map.
	 * 
	 * 
	 * @param mapName
	 *          name of the map
	 * @param key
	 *          key of the map
	 * @param <K>
	 *          type of a map key
	 * @param <V>
	 *          type of a map value
	 * @return value of the map, or null if no such key exists
	 */
	public <K, V> V getValue(String mapName, K key) {
		IMap<String, V> map = clusterCtx.getMap(mapName);
		return map.get(key);
	}

	/**
	 * Sets a value to a map.
	 * 
	 * 
	 * @param mapName
	 *          name of the map
	 * @param key
	 *          key of the map
	 * @param value
	 *          value of the entry
	 */
	public void setValue(String mapName, Object key, Object value) {
		clusterCtx.getMap(mapName).put(key, value);
	}

}
