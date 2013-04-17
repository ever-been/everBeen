package cz.cuni.mff.d3s.been.submitter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cz.cuni.mff.d3s.been.core.jaxb.ConvertorException;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import cz.cuni.mff.d3s.been.core.taskcontext.*;
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
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;

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
	private List<String> tdPaths;

	@Option(name = "-tcd", aliases = { "--task-context-descriptor" }, usage = "TaskContextDescriptor to submit")
	private String tcdPath;

	@Option(name = "-gn", aliases = { "--group-name" }, usage = "Group Name")
	private String groupName = "dev";

	@Option(name = "-gp", aliases = { "--group-password" }, usage = "Group Password")
	private String groupPassword = "dev-pass";

	@Option(name = "-ehl", aliases = { "--enable-hazelcast-logging" }, usage = "Turns on Hazelcast logging.")
	private boolean debug = false;

	@Option(name = "-pe", aliases = { "--print-entry" }, usage = "Print the created Task Entry")
	private boolean printEntry = false;

	@Option(name = "-bpk", aliases = { "--been-package" }, usage = "Upload BPK to Software Repository first.")
	private List<String> bpkFiles;

	public static void main(String[] args) {
		new Submitter().doMain(args);
	}

	private ClusterContext clusterContext;

	private void initializeCluster() {
		if (debug) {
			System.setProperty("hazelcast.logging.type", "slf4j");
		} else {
			System.setProperty("hazelcast.logging.type", "none");
		}

		// connect to the cluster
		HazelcastInstance instance = Instance.newNativeInstance(host, port, groupName, groupPassword);
		clusterContext = new ClusterContext(instance);
	}

	private void submitSingleTask(File tdFile) throws JAXBException, SAXException, ConvertorException {
		TaskDescriptor td = createTaskDescriptor(tdFile);

		// submit
		String taskId = clusterContext.getTasksUtils().submit(td);

		System.out.println("Task was submitted with id: " + taskId);

		if (printEntry) {
			TaskEntry entry = clusterContext.getTasksUtils().getTask(taskId);

			BindingComposer<TaskEntry> composer = XSD.TASKENTRY.createComposer(TaskEntry.class);
			composer.compose(entry, System.out);
		}
	}

	private TaskDescriptor createTaskDescriptor(File tdFile) throws SAXException, JAXBException, ConvertorException {
		// parse task descriptor
		BindingParser<TaskDescriptor> bindingComposer = XSD.TD.createParser(TaskDescriptor.class);
		System.out.println(tdPaths);
		return bindingComposer.parse(tdFile);
	}

	private void submitTaskContext(File tcdFile) throws JAXBException, SAXException, ConvertorException {
		Map<String, TaskDescriptor> descriptors = new HashMap<>();

		for (String tdPath : tdPaths) {
			TaskDescriptor td = createTaskDescriptor(new File(tdPath));
			descriptors.put(td.getName(), td);
		}

		// create TCD
		BindingParser<TaskContextDescriptor> bindingComposer = XSD.TCD.createParser(TaskContextDescriptor.class);
		TaskContextDescriptor taskContextDescriptor = bindingComposer.parse(tcdFile);

		// create TCE
		TaskContextEntry taskContextEntry = new TaskContextEntry();
		taskContextEntry.setId(UUID.randomUUID().toString());

		for (Task t : taskContextDescriptor.getTask()) {
			String type = t.getType();
			TaskDescriptor td = descriptors.get(type);
			TaskEntry taskEntry = clusterContext.getTasksUtils().createAndPut(td);
			taskEntry.setTaskContextId(taskContextEntry.getId());

			taskContextEntry.getContainedTask().add(taskEntry.getId());
		}

		// submit it
		clusterContext.getTasksUtils().submit(taskContextEntry);
	}

	private void doMain(String[] args) {

		CmdLineParser parser = new CmdLineParser(this);

		try {
			// parse the arguments.
			parser.parseArgument(args);

			initializeCluster();

			// upload BPK
			for (String bpkFile : bpkFiles) {
				uploadBpk(bpkFile, clusterContext);
			}

			if (tcdPath == null) {
				for (String tdPath : tdPaths) {
					submitSingleTask(new File(tdPath));
				}
			} else {
				submitTaskContext(new File(tcdPath));
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