package cz.cuni.mff.d3s.been.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.MembershipEvent;

import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.mq.IMessageSender;

/**
 * Listens for cluster membership events.
 * 
 * @author Martin Sixta
 */
final class MembershipListener extends TaskManagerService implements com.hazelcast.core.MembershipListener {

	private ClusterContext clusterCtx;
	private IMessageSender sender;

	public MembershipListener(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
	}

	private static final Logger log = LoggerFactory.getLogger(LocalTaskListener.class);

	@Override
	public void start() throws ServiceException {
		sender = createSender();
		clusterCtx.getCluster().addMembershipListener(this);
	}

	@Override
	public void stop() {
		clusterCtx.getCluster().removeMembershipListener(this);
		sender.close();
	}

	@Override
	public void memberAdded(MembershipEvent membershipEvent) {
		log.info("Member added: {}", membershipEvent.getMember());
	}

	@Override
	public void memberRemoved(MembershipEvent membershipEvent) {
		log.info("Member removed: {}", membershipEvent.getMember());
	}

}
