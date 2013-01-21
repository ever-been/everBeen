package cz.cuni.mff.d3s.been.core.protocol.cluster;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.cuni.mff.d3s.been.core.protocol.Context;

public interface DataPersistence {

	<E> Set<E> getSet(Context ctx);

	<E> List<E> getList(Context ctx);

	<K, V> Map<K, V> getMap(Context ctx);

	Collection<String> list(String type);

}
