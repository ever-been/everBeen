package cz.cuni.mff.d3s.been.core.protocol.cluster;

public interface IClusterMemberFactory {

	Member getMember(String... options) throws IllegalArgumentException, ClusterMemberFactoryException;

	@SuppressWarnings("serial")
	public static class ClusterMemberFactoryException extends Exception {

		ClusterMemberFactoryException(String message, Throwable cause) {
			super(message, cause);
		}

	}
}
