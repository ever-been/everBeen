package cz.cuni.mff.d3s.been.cluster.cshell.rest;


import cz.mff.dpp.args.Argument;
import cz.mff.dpp.args.Option;

import org.javalite.http.Http;
import org.javalite.http.Request;

import java.io.PrintWriter;

class MapCommand {

	private URIFactory factory;
	private PrintWriter out;

	public MapCommand(URIFactory factory, PrintWriter out) {
		this.factory = factory;
		this.out = out;
	}
	enum Action {SET, GET};

	@Argument(index = 1, required = true)
	private Action action;

	@Option(name = "-n", aliases = {"--name"}, required = true)
	private String name;

	@Option(name = "-k", aliases = {"--key"}, required = true)
	private String key;

	@Option(name = "-v", aliases = {"--value"})
	private String value;


	void execute() {
		String uri = factory.getMapKey(name, key);
		Request response = null;

		switch (action) {
			case GET:

				response = Http.get(uri);

				break;
			case SET:

				if (value == null) {
					out.println("Map: set action needs value");
					break;
				}

				response = Http.post(uri, value);

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
