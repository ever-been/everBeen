/**
 * This package contains Hazelcast cluster connection utilities and general boot/shutdown process tools for EverBEEN services.
 *
 * The major step in connecting to the cluster is the instantiation of {@link com.hazelcast.core.HazelcastInstance} and {@link cz.cuni.mff.d3s.been.cluster.context.ClusterContext} through the {@link cz.cuni.mff.d3s.been.cluster.Instance} facade.
 *
 * The {@link cz.cuni.mff.d3s.been.cluster.IClusterService} interface, used by all major EverBEEN services, is also defined in this package. All {@link cz.cuni.mff.d3s.been.cluster.IClusterService} implementation use {@link cz.cuni.mff.d3s.been.cluster.context.ClusterContext} to coordinate their effort with the rest of the EverBEEN cluster.
 *
 * The last key piece of this package is the {@link cz.cuni.mff.d3s.been.cluster.Reaper} class, along with its {@link cz.cuni.mff.d3s.been.cluster.ClusterReaper} override. The purpose of {@link cz.cuni.mff.d3s.been.cluster.Reaper} is to correctly stop EverBEEN services on <code>SIGINT</code> or {@code SIGTERM} (by being registered to a shutdown hook). The {@link cz.cuni.mff.d3s.been.cluster.ClusterReaper} is supposed to be registered first (and, therefore, called last) to ensure the {@link com.hazelcast.core.HazelcastInstance} is only stopped once all EverBEEN services have terminated.
 */
package cz.cuni.mff.d3s.been.cluster;
