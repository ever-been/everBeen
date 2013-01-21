package cz.cuni.mff.d3s.been.core;

import com.hazelcast.core.IMap;
import com.hazelcast.core.Instance;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Martin Sixta
 */
public class MapUtils {
	public static Collection<Object> getValues(String mapName) {

		Collection<Object> values = new LinkedList<>();


		if (ClusterUtils.containsInstance(Instance.InstanceType.MAP, mapName)) {
			IMap hrMap = ClusterUtils.getInstance().getMap(mapName);

			for (Object key: hrMap.keySet()) {
				values.add(hrMap.get(key));

			}
		} else {
			throw new IllegalArgumentException("No such map: " + mapName);
		}


		return values;
	}

	public static <K, V> V getValue(String mapName, K key) {

		IMap<String, V> map = getMap(mapName);

		return map.get(key);

	}

	public static void setValue(String mapName, Object key, Object value) {
		getMap(mapName).put(key, value);

	}

	public static <K, V> IMap<K, V> getMap(String mapName) {
		return ClusterUtils.getInstance().getMap(mapName);
	}
}
