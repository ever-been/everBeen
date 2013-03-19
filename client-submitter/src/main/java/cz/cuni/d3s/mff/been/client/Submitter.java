package cz.cuni.d3s.mff.been.client;

import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.hazelcast.core.HazelcastInstance;

import cz.cuni.mff.d3s.been.bpk.BpkConfiguration;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkResolver;
import cz.cuni.mff.d3s.been.bpk.MetaInf;
import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.sri.SWRepositoryInfo;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreFactory;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

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

	@Option(name = "-bpk", aliases = { "--been-package" }, usage = "Upload BPK to Software Repository first.")
	private String bpkFile;

	public static void main(String[] args) {
		new Submitter().doMain(args);
	}

	public Submitter() {}

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
			ClusterContext clusterContext = new ClusterContext(instance);

			// upload BPK
			if (bpkFile != null) {
				uploadBpk(bpkFile, clusterContext);
			}

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

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Instance.shutdown();
		}
	}

	private void uploadBpk(String bpkFile, ClusterContext clusterContext) throws Exception {

		SWRepositoryInfo swInfo = clusterContext.getServicesUtils().getSWRepositoryInfo();
		SwRepoClient client = new SwRepoClientFactory(SoftwareStoreFactory.getDataStore()).getClient(swInfo.getHost(), swInfo.getHttpServerPort());

		BpkIdentifier bpkIdentifier = new BpkIdentifier();

		BpkConfiguration bpkConfiguration = BpkResolver.resolve(new File(bpkFile));
		MetaInf metaInf = bpkConfiguration.getMetaInf();
		bpkIdentifier.setGroupId(metaInf.getGroupId());
		bpkIdentifier.setBpkId(metaInf.getBpkId());
		bpkIdentifier.setVersion(metaInf.getVersion());

		client.putBpk(bpkIdentifier, new File(bpkFile));

		System.out.printf("%s uploaded to Software Repository\n", bpkFile);
	}
}
