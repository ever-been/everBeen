package cz.cuni.mff.d3s.been.core;

import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import java.util.Collection;

import static cz.cuni.mff.d3s.been.core.Names.HOSTRUNTIME_MAP_NAME;

/**
 * @author Martin Sixta
 */
public class RuntimesUtils {

	public static Collection<RuntimeInfo> getRuntimes() {
		return getRuntimeMap().values();
	}

	public static RuntimeInfo getRuntimeInfo(String key) {
		return getRuntimeMap().get(key);
	}

	public static void setRuntimeInfo(RuntimeInfo runtimeInfo) {
		getRuntimeMap().put(runtimeInfo.getId(), runtimeInfo);
	}


	public static IMap<String, RuntimeInfo> getRuntimeMap() {
		return MapUtils.getMap(HOSTRUNTIME_MAP_NAME);

	}

	public static IQueue<String> getLocalTaskQueue() {
		return ClusterUtils.getInstance().getQueue(ClusterUtils.getId());
	}

}
