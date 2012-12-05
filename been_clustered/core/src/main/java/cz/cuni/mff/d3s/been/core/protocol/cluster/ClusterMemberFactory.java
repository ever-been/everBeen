package cz.cuni.mff.d3s.been.core.protocol.cluster;

public class ClusterMemberFactory implements IClusterMemberFactory {

	public static final String HAZELCAST = "hazelcast";

	@Override
	public Member getMember(String... options)
			throws IllegalArgumentException, ClusterMemberFactoryException {
		ClassLoader cl = ClusterMemberFactory.class.getClassLoader();
		String implClassName = "cz.cuni.mff.d3s.been.cluster.ClusterMemberFactoryImpl";
		try {
			return ((IClusterMemberFactory) cl.loadClass(implClassName)
					.newInstance()).getMember(options);
		} catch (ClassNotFoundException e) {
			throw new ClusterMemberFactoryException(String.format(
					"Cluster member factory class '%s' not found",
					implClassName), e);
		} catch (IllegalAccessException e) {
			throw new ClusterMemberFactoryException(
					String.format(
							"Parameter-less Constructor of '%s' was not fount or is not accessible",
							implClassName), e);
		} catch (InstantiationException e) {
			throw new ClusterMemberFactoryException(String.format(
					"Cluster member factory class '%s' can't be instantiated",
					implClassName), e);
		}
	}
}
