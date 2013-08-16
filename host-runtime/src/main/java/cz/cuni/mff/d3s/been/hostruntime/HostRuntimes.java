package cz.cuni.mff.d3s.been.hostruntime;

import static cz.cuni.mff.d3s.been.hostruntime.HostRuntimeConfiguration.*;

import java.net.InetSocketAddress;
import java.util.*;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.PropertyReader;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreBuilder;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreBuilderFactory;
import cz.cuni.mff.d3s.been.detectors.Detector;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

/**
 * 
 * {@link HostRuntime} factory class.
 * 
 * @author Martin Sixta
 */
public class HostRuntimes {

	/**
	 * Creates {@link HostRuntime}.
	 * 
	 * @param clusterContext
	 *          Connection to the cluster
	 * @param properties
	 *          BEEN properties
	 * 
	 * @return A new host runtime instance
	 */

	public static synchronized
			HostRuntime
			createRuntime(final ClusterContext clusterContext, final Properties properties) {

		SwRepoClientFactory swRepoClientFactory = createSwRepoClientFactory(properties);

		RuntimeInfo info = createRuntimeInfo(clusterContext, properties);

		return new HostRuntime(clusterContext, swRepoClientFactory, info);
	}

	/**
	 * Creates new {@link RuntimeInfo} and initializes all possible values.
	 * 
	 * 
	 * @param clusterContext
	 *          Connection to the cluster
	 * @param properties
	 *          configuration properties
	 * 
	 * @return Detailed information about the Host Runtime
	 */
	private static RuntimeInfo createRuntimeInfo(final ClusterContext clusterContext, final Properties properties) {
		final WorkingDirectoryResolver resolver = new WorkingDirectoryResolver(properties);
		final PropertyReader propertyReader = PropertyReader.on(properties);

		RuntimeInfo ri = new RuntimeInfo();

		ri.setWorkingDirectory(resolver.getHostRuntimeWorkingDirectory().getAbsolutePath());
		ri.setTasksWorkingDirectory(resolver.getTasksWorkingDirectory().getAbsolutePath());

		String nodeId = UUID.randomUUID().toString();
		ri.setId(nodeId);

		final InetSocketAddress address = clusterContext.getInetSocketAddress();
		ri.setHost(address.getHostName());
		ri.setPort(address.getPort());
		ri.setType(clusterContext.getInstanceType().toString());

		Calendar c = GregorianCalendar.getInstance();
		c.setTime(new Date());
		ri.setStartUpTime(new XMLGregorianCalendarImpl((GregorianCalendar) c));

		Detector detector = new Detector();
		detector.detectAll(ri);
		ri.setExclusivity(TaskExclusivity.NON_EXCLUSIVE.toString());

		int maxTasks = propertyReader.getInteger(MAX_TASKS, DEFAULT_MAX_TASKS);
		int threshold = propertyReader.getInteger(MEMORY_THRESHOLD, DEFAULT_MEMORY_THRESHOLD);
		if (threshold < 20 && threshold > 100) {
			threshold = DEFAULT_MEMORY_THRESHOLD;
		}

		ri.withMaxTasks(maxTasks).withMemoryThreshold(threshold);

		return ri;
	}

	/**
	 * Auxiliary helper function which creates {@link SwRepoClientFactory}.
	 * 
	 * @param properties
	 *          configuration properties
	 * @return A new {@link SwRepoClientFactory}
	 */
	private static SwRepoClientFactory createSwRepoClientFactory(final Properties properties) {
		SoftwareStoreBuilder builder = SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().withProperties(properties);
		return new SwRepoClientFactory(builder.buildCache());
	}
}
