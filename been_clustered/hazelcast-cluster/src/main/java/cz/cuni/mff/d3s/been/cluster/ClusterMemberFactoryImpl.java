package cz.cuni.mff.d3s.been.cluster;

import cz.cuni.mff.d3s.been.core.protocol.cluster.IClusterMemberFactory;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Member;

public class ClusterMemberFactoryImpl implements IClusterMemberFactory {

	@Override
	public Member getMember(String... options) throws IllegalArgumentException, ClusterMemberFactoryException {
		// FIXME IGNORING OPTIONS FOR NOW
		return new HazelcastMember();
	}

}
