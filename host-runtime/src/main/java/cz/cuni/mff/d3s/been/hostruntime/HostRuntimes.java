package cz.cuni.mff.d3s.been.hostruntime;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.UUID;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.hazelcast.core.HazelcastInstance;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreBuilderFactory;
import cz.cuni.mff.d3s.been.detectors.Detector;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

/**
 * @author Martin Sixta
 */
// FIXME Martin Sixta .. why it is named HostRuntimes (name is misleading)
public class HostRuntimes {

	private static HostRuntime hostRuntime = null;

	/**
	 * This method returns singleton instance of {@link HostRuntime}. If runtime
	 * doesn't exists, this method creates one.
	 * 
	 * @param hazelcastInstance
	 *          Hazelcast instance to build on
	 * @param properties
	 *          BEEN properties
	 * 
	 * @return A host runtime instance
	 */
	public static synchronized HostRuntime getRuntime(HazelcastInstance hazelcastInstance, Properties properties) {
		if (hostRuntime == null) {
			ClusterContext clusterContext = new ClusterContext(hazelcastInstance);
			SwRepoClientFactory swRepoClientFactory = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().withProperties(
					properties).buildCache());
			WorkingDirectoryResolver workingDirectoryResolver = new WorkingDirectoryResolver(properties);
			File workingDirectory = workingDirectoryResolver.getHostRuntimeWorkingDirectory();
			File tasksWorkingDIrectory = workingDirectoryResolver.getTasksWorkingDirectory();

			RuntimeInfo info = newRuntimeInfo(clusterContext, workingDirectory, tasksWorkingDIrectory);
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
