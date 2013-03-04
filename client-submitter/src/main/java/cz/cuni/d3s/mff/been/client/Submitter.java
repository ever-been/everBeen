package cz.cuni.d3s.mff.been.client;

import java.net.InetSocketAddress;

import com.hazelcast.core.HazelcastInstance;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.hazelcast.client.ClientConfig;
import com.hazelcast.client.HazelcastClient;

import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.NodeType;
import cz.cuni.mff.d3s.been.core.ClusterContext;
import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * 
 * Simple client which submits tasks to the cluster.
 * 
 * 
 * For debugging purposes!
 * 
 * 
 * @author Martin Sixta
 */
public class Submitter {
	@Option(name = "-h", aliases = { "--host" }, usage = "Hostname of a cluster member to connect to")
	private String host = "localhost";

	@Option(name = "-p", aliases = { "--port" }, usage = "Port of the host")
	private int port = 5701;

	@Option(name = "-td", aliases = { "--task-descriptor" }, required = true, usage = "TaskDescriptor to submit")
	private String tdPath;

	@Option(name = "-gn", aliases = { "--group-name" }, usage = "Group Name")
	private String groupName = "dev";

	@Option(name = "-gp", aliases = { "--group-password" }, usage = "Group Password")
	private String groupPassword = "dev-pass";

	@Option(name = "-ehl", aliases = { "--enable-hazelcast-logging" }, usage = "Turns on Hazelcast logging.")
	private boolean debug = false;

	@Option(name = "-pe", aliases = { "--print-entry" }, usage = "Print the created Task Entry")
	private boolean printEntry = false;


	public static void main(String[] args) {
		new Submitter().doMain(args);
	}

	public Submitter() {
	}

	private void doMain(String[] args) {

		CmdLineParser parser = new CmdLineParser(this);

		try {
			// parse the arguments.
			parser.parseArgument(args);

			// parse task descriptor
			BindingParser<TaskDescriptor> bindingComposer = XSD.TD.createParser(TaskDescriptor.class);
			System.out.println(tdPath);
			TaskDescriptor td = bindingComposer.parse(new java.io.File(tdPath));

			if (debug) {
				System.setProperty("hazelcast.logging.type", "slf4j");
			} else {
				System.setProperty("hazelcast.logging.type", "none");
			}

            // connect to the cluster
            HazelcastInstance instance = Instance.newNativeInstance(host, port, groupName, groupPassword);
			ClusterContext clusterContext =  new ClusterContext(instance);

			// submit
			String taskId = clusterContext.getTasksUtils().submit(td);

			System.out.println("Task was submitted with id: " + taskId);

			if (printEntry) {
				TaskEntry entry = clusterContext.getTasksUtils().getTask(taskId);

				BindingComposer<TaskEntry> composer = XSD.TASKENTRY.createComposer(TaskEntry.class);
				composer.compose(entry, System.out);
			}

		} catch (CmdLineException e) {

			System.err.println(e.getMessage());
			System.err.println("\nUsage:");
			parser.printUsage(System.err);

			return;
		} catch (Exception e) {
			e.printStackTrace();
        } finally {
            Instance.shutdown();
		}
	}
}
