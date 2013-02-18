package cz.cuni.mff.d3s.been.task;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MembershipEvent;
import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.core.ClusterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for cluster membership events.
 *
 * @author Martin Sixta
 */
final class MembershipListener implements com.hazelcast.core.MembershipListener, IClusterService {

	private static final Logger log = LoggerFactory.getLogger(LocalTaskListener.class);

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
		log.info("Member added: {}", membershipEvent.getMember() );
	}

	@Override
	public void memberRemoved(MembershipEvent membershipEvent) {
		log.info("Member removed: {}", membershipEvent.getMember() );
	}
}
