package cz.cuni.mff.d3s.been.demo;

import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.datastore.DataStoreFactory;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

public class SRClientTestApp {

	@Option(name = "-s", aliases = { "--store" }, usage = "Which file you want to store in software repository", required = true)
	private File putFile;

	@Option(name = "-h", aliases = { "--host" }, usage = "Hostname of cumputer, where SW Repository is running")
	private String host = "localhost";

	@Option(name = "-p", aliases = { "--port" }, usage = "Port on which SW Repository is running")
	private int port = 8000;

	@Option(name = "-b", aliases = { "--bpk-id" }, usage = "Bpk id of stored artifact", required = true)
	private String bpkId;

	@Option(name = "-g", aliases = { "--group-id" }, usage = "Group id of stored artifact", required = true)
	private String groupId;

	@Option(name = "-v", aliases = { "--version" }, usage = "Version of stored artifact", required = true)
	private String version;

	public static void main(String[] args) {
		new SRClientTestApp().doMain(args);
	}

	private void doMain(String[] args) {
		parseCmdLineArguments(args);
		SwRepoClient client = new SwRepoClientFactory(DataStoreFactory.getDataStore()).getClient(host, port);

		if (putFile != null) {
			BpkIdentifier bpkFile = new BpkIdentifier();
			bpkFile.setBpkId(bpkId);
			bpkFile.setGroupId(groupId);
			bpkFile.setVersion(version);
			client.putBpk(bpkFile, putFile);
		}

	}
	private void parseCmdLineArguments(final String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			parser.printUsage(System.err);
			System.exit(0);
		}

	}

}
