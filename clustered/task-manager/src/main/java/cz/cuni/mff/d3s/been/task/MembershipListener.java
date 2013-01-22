package cz.cuni.mff.d3s.been.task;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MembershipEvent;
import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.core.ClusterUtils;

/**
 * Listens for cluster membership events.
 *
 * @author Martin Sixta
 */
final class MembershipListener implements com.hazelcast.core.MembershipListener, IClusterService {

	@Override
	public void start() {
		ClusterUtils.getCluster().addMembershipListener(this);
	}

	@Override
	public void stop() {
		ClusterUtils.getCluster().removeMembershipListener(this);
	}

	@Override
	public void memberAdded(MembershipEvent membershipEvent) {
		//TODO: rescan local keys
	}

	@Override
	public void memberRemoved(MembershipEvent membershipEvent) {
		//TODO: rescan local keys
	}
}
