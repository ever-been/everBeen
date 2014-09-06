package cz.cuni.mff.d3s.been.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Collection conversion utilities
 *
 * @author darklight
 */
public final class CollectionUtils {

	/**
	 * Create the reverse mapping for an existing map
	 *
	 * @param map Map to invert
	 *
	 * @param <K> Original map key type
	 * @param <V> Original map value type
	 *
	 * @return Reversed map
	 */
	public static final <K, V> Map<V, Collection<K>> reverseMapping(Map<K, V> map) {
		final Map<V, Collection<K>> reverseMap = new HashMap<V, Collection<K>>();
		for (Map.Entry<K, V> mapEntry: map.entrySet()) {
			Collection<K> keysForValue = reverseMap.get(mapEntry.getValue());
			if (keysForValue == null) {
				keysForValue = new LinkedList<K>();
				reverseMap.put(mapEntry.getValue(), keysForValue);
			}
			keysForValue.add(mapEntry.getKey());
		}
		return reverseMap;
	}
}
