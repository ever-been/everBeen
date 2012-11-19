package cz.cuni.mff.d3s.been.cluster;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DataPersistence {

	<E> Set<E> getSet(String name);

	<E> List<E> getList(String name);

	<K, V> Map<K, V> getMap(String name);


	Collection<String> list(String name);

}
