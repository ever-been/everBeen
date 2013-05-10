package cz.cuni.mff.d3s.been.cluster.context;

import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;

/**
 * @author Martin Sixta
 */
public class Benchmarks {
	private final ClusterContext clusterContext;

	public Benchmarks(ClusterContext clusterContext) {
		this.clusterContext = clusterContext;
	}

	public IMap<String, BenchmarkEntry> getBenchmarksMap() {
		return clusterContext.getMap(Names.BENCHMARKS_MAP_NAME);
	}

	public void put(BenchmarkEntry entry) {
		getBenchmarksMap().put(entry.getId(), entry);
	}

	public BenchmarkEntry get(String id) {
		return getBenchmarksMap().get(id);
	}
}
