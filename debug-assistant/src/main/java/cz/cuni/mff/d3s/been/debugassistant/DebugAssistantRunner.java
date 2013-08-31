package cz.cuni.mff.d3s.been.debugassistant;

import java.util.Collection;
import java.util.Properties;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;

import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;

/**
 * Created with IntelliJ IDEA. User: Kuba Date: 25.03.13 Time: 14:20 To change
 * this template use File | Settings | File Templates.
 */
public class DebugAssistantRunner {
	private static final Logger log = LoggerFactory.getLogger(DebugAssistantRunner.class);

	@Option(name = "-h", aliases = { "--host" }, usage = "Hostname of a cluster member to connect to")
	private String host = "localhost";

	@Option(name = "-p", aliases = { "--port" }, usage = "Port of the host")
	private int port = 5701;

	@Option(name = "-gn", aliases = { "--group-name" }, usage = "Group Name")
	private String groupName = "dev";

	@Option(name = "-gp", aliases = { "--group-password" }, usage = "Group Password")
	private String groupPassword = "dev-pass";

	/**
	 * Run a software repository node from command-line.
	 * 
	 * @param args
	 *          None recognized
	 */
	public static void main(String[] args) {
		new DebugAssistantRunner().doMain(args);
	}

	public void doMain(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);

		try {
			// parse the arguments.
			parser.parseArgument(args);

		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			return;
		}

		System.setProperty("hazelcast.logging.type", "none");

		HazelcastInstance inst = Instance.newNativeInstance(host, port, groupName, groupPassword);
		ClusterContext clusterCtx = new ClusterContext(inst, new Properties());

		try {

			System.out.println("Suspended tasks:");

			DebugAssistant dbg = new DebugAssistant(clusterCtx);
			Collection<DebugListItem> processes = dbg.listWaitingProcesses();
			for (DebugListItem i : processes) {
				System.out.println("TASK " + i.getTaskId() + ":");
				System.out.println("waiting for debug at " + i.getHostName() + " on port " + i.getDebugPort());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			inst.getLifecycleService().shutdown(); // kills lingering threads
		}

	}
}
