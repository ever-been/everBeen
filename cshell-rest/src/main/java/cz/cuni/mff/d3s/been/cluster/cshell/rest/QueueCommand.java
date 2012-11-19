package cz.cuni.mff.d3s.been.cluster.cshell.rest;


import cz.mff.dpp.args.Argument;
import cz.mff.dpp.args.Constraint;
import cz.mff.dpp.args.Option;
import org.javalite.http.Http;
import org.javalite.http.Request;

import java.io.PrintWriter;

class QueueCommand {
	private URIFactory factory;
	private PrintWriter out;

	public QueueCommand(URIFactory factory, PrintWriter out) {
		this.factory = factory;
		this.out = out;
	}
	enum Action {OFFER, POLL};

	@Argument(index = 1, required = true)
	private Action action;

	@Option(name = "-n", aliases = {"--name"}, required = true)
	private String name;

	@Option(name = "-p", aliases = {"--poll"})
	@Constraint(min = "0")
	private int poll;

	@Option(name = "-v", aliases = {"--value"})
	private String value;


	void execute() {

		Request response = null;
		String uri = null;

		switch (action) {
			case OFFER:
				if (value == null) {
					out.println("Queue: offer action needs value");
					return;
				}

				uri = factory.getQueue(name);
				response = Http.post(uri, value);

				break;
			case POLL:

				uri = factory.getQueuePoll(name, poll);
				response = Http.delete(uri);

				break;
			default:
				out.println("Map: unknown action");
				break;
		}

		if (response != null) {
			out.println(response.responseMessage());
			out.println(response.responseCode());
			String text = response.text();
			if (text != null && !text.isEmpty()){
				out.println(text);
			}
		}

	}
}
