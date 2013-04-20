package cz.cuni.mff.d3s.been.nginx;

import cz.cuni.mff.d3s.been.taskapi.Requestor;
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
		Requestor requestor = new Requestor();

		downloadClientScript();

		requestor.checkPointWait("rendezvous-checkpoint");
		requestor.latchCountDown("rendezvous-latch");
		String serverAddress = requestor.checkPointGet("server-address");

		System.out.println(serverAddress);

		runClientScript(serverAddress);

		requestor.latchCountDown("shutdown-latch");
	}
}
