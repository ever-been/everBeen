package cz.cuni.mff.d3s.been.submitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.xml.sax.SAXException;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiImpl;
import cz.cuni.mff.d3s.been.bpk.BpkConfigurationException;
import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.ConvertorException;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;

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

	@Option(name = "-td", aliases = { "--task-descriptor" }, usage = "TaskDescriptor to submit")
	private List<String> tdPaths;

	@Option(name = "-tcd", aliases = { "--task-context-descriptor" }, usage = "TaskContextDescriptor to submit")
	private String tcdPath;

	@Option(name = "-bd", aliases = { "--benchmark-descriptor" }, usage = "TaskDescriptor of benchmark to submit")
	private String bdPath;

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

	private BeenApi api;

	private void initializeCluster() {
		if (debug) {
			System.setProperty("hazelcast.logging.type", "slf4j");
		} else {
			System.setProperty("hazelcast.logging.type", "none");
		}

		api = new BeenApiImpl(host, port, groupName, groupPassword);
	}

	private void submitSingleTask(File tdFile) throws JAXBException, SAXException, ConvertorException {
		TaskDescriptor td = createTaskDescriptor(tdFile);
		api.submitTask(td);
	}

	private TaskDescriptor createTaskDescriptor(File tdFile) throws SAXException, JAXBException, ConvertorException {
		// parse task descriptor
		BindingParser<TaskDescriptor> bindingComposer = XSD.TASK_DESCRIPTOR.createParser(TaskDescriptor.class);
		System.out.println(tdPaths);
		return bindingComposer.parse(tdFile);
	}

	private void submitTaskContext(File tcdFile) throws JAXBException, SAXException, ConvertorException {
		BindingParser<TaskContextDescriptor> bindingComposer = XSD.TASK_CONTEXT_DESCRIPTOR.createParser(TaskContextDescriptor.class);
		TaskContextDescriptor taskContextDescriptor = bindingComposer.parse(tcdFile);
		api.submitTaskContext(taskContextDescriptor);
	}

	private void submitBenchmark(File bdFile) throws JAXBException, SAXException, ConvertorException {
		api.submitBenchmark(createTaskDescriptor(bdFile));
	}

	private void doMain(String[] args) {

		CmdLineParser parser = new CmdLineParser(this);

		try {
			// parse the arguments.
			parser.parseArgument(args);

			initializeCluster();

			if (bpkFiles != null) {
				// upload BPK
				for (String bpkFile : bpkFiles) {
					uploadBpk(bpkFile);
				}
			}

			if (bdPath != null) {
				submitBenchmark(new File(bdPath));
			} else if (tcdPath != null) {
				submitTaskContext(new File(tcdPath));
			} else if (tdPaths != null) {
				for (String tdPath : tdPaths) {
					submitSingleTask(new File(tdPath));
				}
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

	private void uploadBpk(String bpkFile) throws BpkConfigurationException, FileNotFoundException {
		FileInputStream fis = new FileInputStream(new File(bpkFile));
		api.uploadBpk(fis);
		System.out.printf("%s uploaded to Software Repository\n", bpkFile);
	}
}
