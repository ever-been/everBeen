package cz.cuni.mff.d3s.been.hostruntime;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.*;

import com.hazelcast.core.HazelcastInstance;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreBuilderFactory;
import cz.cuni.mff.d3s.been.detectors.Detector;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

/**
 * @author Martin Sixta
 */
public class HostRuntimes {

	private static HostRuntime hostRuntime = null;

	/**
	 * This method returns singleton instance of {@link HostRuntime}. If runtime
	 * doesn't exists, this method creates one.
	 *
	 * @param properties
	 *          BEEN properties
	 * 
	 * @return A host runtime instance
	 */
	public static synchronized HostRuntime getRuntime(ClusterContext clusterContext, Properties properties) {
		if (hostRuntime == null) {
			SwRepoClientFactory swRepoClientFactory = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().withProperties(
					properties).buildCache());
			WorkingDirectoryResolver workingDirectoryResolver = new WorkingDirectoryResolver(properties);
			File workingDirectory = workingDirectoryResolver.getHostRuntimeWorkingDirectory();
			File tasksWorkingDirectory = workingDirectoryResolver.getTasksWorkingDirectory();

			RuntimeInfo info = newRuntimeInfo(clusterContext, workingDirectory, tasksWorkingDirectory);
			hostRuntime = new HostRuntime(clusterContext, swRepoClientFactory, info);
		}
		return hostRuntime;
	}

	/**
	 * Creates new {@link RuntimeInfo} and initializes all possible values.
	 * 
	 * 
	 * @param clusterContext
	 *          Connection to the cluster
	 * @param workingDirectory
	 *          Directory to use to hold Host Runtime files
	 * @param tasksWorkingDirectory
	 *          Directory where tasks files to keep in
	 * @return Detailed information about the Host Runtime
	 */
	public static RuntimeInfo newRuntimeInfo(ClusterContext clusterContext, File workingDirectory,
			File tasksWorkingDirectory) {
		RuntimeInfo ri = new RuntimeInfo();

		ri.setWorkingDirectory(workingDirectory.getAbsolutePath());
		ri.setTasksWorkingDirectory(tasksWorkingDirectory.getAbsolutePath());

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

		return ri;
	}
}
