package cz.cuni.d3s.mff.been.client;

import com.hazelcast.client.ClientConfig;
import com.hazelcast.client.HazelcastClient;


import cz.cuni.mff.d3s.been.core.ClusterUtils;
import cz.cuni.mff.d3s.been.core.TasksUtils;
import cz.cuni.mff.d3s.been.core.jaxb.BindingParser;
import cz.cuni.mff.d3s.been.core.jaxb.ConvertorException;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.td.TaskDescriptor;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.net.InetSocketAddress;



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
	@Option(name = "-h", aliases = {"--host"}, usage = "Hostname of a cluster member to connect to")
	private String host = "localhost";

	@Option(name = "-p", aliases = {"--port"}, usage = "Port of the host")
	private int port = 5701;

	@Option(name = "-td", aliases = {"--task-descriptor"}, required = true, usage = "TaskDescriptor to submit")
	private String tdPath;

	@Option(name = "-gn", aliases = {"--group-name"}, usage = "Group Name")
	private String groupName = "dev";

	@Option(name = "-gp", aliases = {"--group-password"}, usage = "Group Password")
	private String groupPassword = "dev-pass";


	public static void main(String[] args) {

		new Submitter().doMain(args);
	}

	private void doMain(String[] args) {




		CmdLineParser parser = new CmdLineParser(this);

		HazelcastClient hazelcastClient = null;

		try {
			// parse the arguments.
			parser.parseArgument(args);

			// parse task descriptor
			BindingParser<TaskDescriptor> bindingComposer = XSD.TD.createParser(TaskDescriptor.class);
			System.out.println(tdPath);
			TaskDescriptor td = bindingComposer.parse(new java.io.File(tdPath));

			// connect to the cluster
			InetSocketAddress socketAddress = new InetSocketAddress(host, port);

			ClientConfig clientConfig = new ClientConfig();
			clientConfig.getGroupConfig().setName(groupName).setPassword(groupPassword);
			clientConfig.addInetSocketAddress(socketAddress);
			hazelcastClient = HazelcastClient.newHazelcastClient(clientConfig);





			// submit
			String taskId = TasksUtils.submit(td);






		} catch (CmdLineException e) {

			System.err.println(e.getMessage());
			System.err.println("\nUsage:");
			parser.printUsage(System.err);

			return;
		} catch (SAXException  | JAXBException | ConvertorException e) {
			e.printStackTrace();
		} finally {
			if (hazelcastClient != null) {
				hazelcastClient.shutdown();
			}
		}

	}
}
