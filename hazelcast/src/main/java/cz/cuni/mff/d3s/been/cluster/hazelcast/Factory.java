package cz.cuni.mff.d3s.been.cluster.hazelcast;

import cz.cuni.mff.d3s.been.cluster.Member;

public class Factory {

	public static Member createMember(Object[] options) throws  IllegalArgumentException{
		boolean isLiteMember = false;

		if (options != null) {
		if (options.length > 1) {
			throw new IllegalArgumentException("Too many options for Hazelcast");
		}

		if (options.length == 1) {
			if (!(options[0] instanceof Boolean)) {
				throw new IllegalArgumentException("Hazecast expects boolean argument");
			}

			isLiteMember = (Boolean)options[0];
		}
		}

		return new HazelcastMember(isLiteMember);


	}
}
