package cz.cuni.mff.d3s.been.task;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MembershipEvent;

/**
 * Listens for cluster membership events.
 *
 * @author Martin Sixta
 */
final class MembershipListener implements com.hazelcast.core.MembershipListener {
	private final HazelcastInstance hazelcastInstance;

	public MembershipListener(final HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
	}

	public void start() {
		hazelcastInstance.getCluster().addMembershipListener(this);

	}

	public void stop() {
		hazelcastInstance.getCluster().removeMembershipListener(this);
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
