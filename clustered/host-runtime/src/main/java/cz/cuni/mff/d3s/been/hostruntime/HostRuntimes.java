package cz.cuni.mff.d3s.been.hostruntime;

import java.util.UUID;

import com.hazelcast.core.HazelcastInstance;

/**
 * @author Martin Sixta
 */
// FIXME Martin Sixta .. why it is named HostRuntimes (name is misleading)
public class HostRuntimes {
	
	private static HostRuntime hostRuntime = null;
	
	private static String uuid = null;

	/**
	 * This method returns singleton instance of {@link HostRuntime}. If runtime doesn't exists, this method creates one.  
	 * @param hazelcastInstance
	 * @return
	 */
	public static synchronized HostRuntime getRuntime(HazelcastInstance hazelcastInstance) {
		if (hostRuntime == null) {
			uuid = UUID.randomUUID().toString();
			hostRuntime = new HostRuntime(uuid);
		}
		return hostRuntime;
	}
}
