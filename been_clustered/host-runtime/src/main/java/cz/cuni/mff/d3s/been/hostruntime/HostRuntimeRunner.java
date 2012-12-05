package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.core.protocol.cluster.ClusterMemberFactory;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Member;
import cz.cuni.mff.d3s.been.core.protocol.cluster.IClusterMemberFactory.ClusterMemberFactoryException;

/**
 * 
 * @author donarus
 * 
 */
public class HostRuntimeRunner {
	
	public static final int EC_CREATE_MEMBER_ERROR = 500;
	public static final int EC_CREATE_HOST_RUNTIME_ERROR = 501;

	/**
	 * Host runtime main method. Tries to create new cluster {@link Member},
	 * connect it into the cluster and start {@link HostRuntime2} over this
	 * {@link Member}.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// PREPARE CLUSTER MEMBER FOR NEW HOSTRUNTIME
		Member member = null;
		try {
			member = createMemberAndConnect();
		} catch (Throwable t) {
			// FIXME log
			System.exit(EC_CREATE_MEMBER_ERROR);
		}

		try {
			new HostRuntime(member.getMessaging(), new TaskRunner(), member.getNodeId());
		} catch (Throwable e) {
			// FIXME log
			System.exit(EC_CREATE_HOST_RUNTIME_ERROR);
		}
	}

	private static Member createMemberAndConnect()
			throws ClusterMemberFactoryException {
		Member member = null;
		try {
			member = new ClusterMemberFactory().getMember();
		} catch (ClusterMemberCreationException e) {
			throw new ClusterMemberCreationException(
					"Can't create new cluster member.", e);
		}

		try {
			member.connect();
		} catch (Throwable t) {
			throw new ClusterMemberCreationException(
					"Member can't connect to cluster", t);
		}

		return member;
	}

	@SuppressWarnings("serial")
	private static class ClusterMemberCreationException extends
			RuntimeException {

		public ClusterMemberCreationException(String message, Throwable cause) {
			super(message, cause);
		}

	}

}
