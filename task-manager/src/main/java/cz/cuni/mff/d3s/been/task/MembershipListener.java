package cz.cuni.mff.d3s.been.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.MembershipEvent;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.IMessageSender;

/**
 * Listens for cluster membership events.
 * 
 * @author Martin Sixta
 */
final class MembershipListener implements com.hazelcast.core.MembershipListener, IClusterService {

	private ClusterContext clusterCtx;
	private IMessageSender inprocMessaging;

	public MembershipListener(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
	}

	private static final Logger log = LoggerFactory.getLogger(LocalTaskListener.class);

	@Override
	public void start() {
		clusterCtx.getCluster().addMembershipListener(this);
	}

	@Override
	public void stop() {
		clusterCtx.getCluster().removeMembershipListener(this);
	}

	@Override
	public void memberAdded(MembershipEvent membershipEvent) {
		log.info("Member added: {}", membershipEvent.getMember());
	}

	@Override
	public void memberRemoved(MembershipEvent membershipEvent) {
		log.info("Member removed: {}", membershipEvent.getMember());
	}

	public void withSender(IMessageSender inprocMessaging) {
		this.inprocMessaging = inprocMessaging;
	}
}
