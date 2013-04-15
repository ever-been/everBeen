package cz.cuni.mff.d3s.been.nginx;

import cz.cuni.mff.d3s.been.taskapi.Task;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 14.04.13
 * Time: 18:39
 * To change this template use File | Settings | File Templates.
 */
public class NginxClientTask extends Task {

	public static void main(String[] args) {
		new NginxClientTask().doMain(args);
	}

	private void downloadClientScript() {
		// TODO
	}

	private void runClientScript(String address) {
		// TODO
	}

	@Override
	public void run() {
		downloadClientScript();

		this.checkpointIncrement("rendezvous");

		String address = this.waitForCheckpoint("server-running");

		runClientScript(address);

		this.checkpointIncrement("client-finished");
	}
}
