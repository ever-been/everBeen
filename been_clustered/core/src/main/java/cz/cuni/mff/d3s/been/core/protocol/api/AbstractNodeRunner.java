package cz.cuni.mff.d3s.been.core.protocol.api;

import cz.cuni.mff.d3s.been.core.protocol.cluster.ClusterMemberFactory;
import cz.cuni.mff.d3s.been.core.protocol.cluster.DataPersistence;
import cz.cuni.mff.d3s.been.core.protocol.cluster.IClusterMemberFactory.ClusterMemberFactoryException;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Member;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Messaging;

public abstract class AbstractNodeRunner {

	public void start() {
		Member member = null;
		try {
			member = createMemberAndConnect();
		} catch (Throwable t) {
			// FIXME log
			t.printStackTrace();
			System.exit(0);
		}

		try {
			createAndStartService(member.getMessaging(), member.getDataPersistence());
		} catch (Throwable e) {
			// FIXME log
			e.printStackTrace();
			System.exit(0);
		}
	}

	protected abstract void createAndStartService(Messaging messaging, DataPersistence dataPersistence);

	private Member createMemberAndConnect()
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
