package cz.cuni.mff.d3s.been.cluster.cshell;

import cz.cuni.mff.d3s.been.cluster.cshell.rest.MapCommand;
import cz.cuni.mff.d3s.been.cluster.cshell.rest.QueueCommand;
import cz.cuni.mff.d3s.been.cluster.cshell.rest.URIFactory;
import cz.mff.dpp.args.ParseException;
import cz.mff.dpp.args.Parser;
import jline.console.ConsoleReader;

/**
 * @author Martin Sixta
 */
class RestMode extends Mode {

	private URIFactory factory = new URIFactory();

	private enum Action {
		CONNECT, MAP, QUEUE, BREAK, STATUS;
	}

	private static String[] getActionStrings() {
		Action[] enumValues = Action.values();

		String[] stringValues = new String[enumValues.length];

		for (int i = 0; i < enumValues.length; ++i) {
			stringValues[i] = enumValues[i].name().toLowerCase();

		}

		return stringValues;

	}

	public RestMode(ConsoleReader reader) {
		super (reader, "rest> ", getActionStrings());
	}

	@Override
	protected Mode takeAction(String[] args) {
		assert args != null;
		assert args.length > 0;

		Action action = Action.valueOf(args[0].toUpperCase());


		switch (action) {
			case CONNECT:
				connect(args);
				break;

			case MAP:
				map(args);
				break;
			case QUEUE:
				queue(args);
				break;
			case BREAK:
				disconnect();
				return new DefaultMode(reader);
			case STATUS:
				status();
				break;
			default:
				throw new IllegalArgumentException("Unimplemented action: " + action.toString() + "!");
		}



		return this;
	}

	private void connect(String[] args) {
		factory = new URIFactory();

		Parser parser = new Parser(factory);

		try {
			parser.parse(args);
		} catch (ParseException e) {
			parser.usage();
			 factory = null;
		}
	}

	private void map(String[] args) {
		if (factory == null) {
			throw new IllegalArgumentException("Not connected!");
		}

		MapCommand cmd = new MapCommand(factory, out);
		Parser mapParser = new Parser(cmd);

		try {
			mapParser.parse(args);
			cmd.execute();
		} catch (ParseException e) {
			mapParser.usage();
		}

	}

	private void status() {
		if (factory == null) {
			out.println("Disconnected");
		} else {
			out.printf("Connected to %s:%d\n", factory.getHost(), factory.getPort());
		}

	}

	private void disconnect() {
		factory = null;
	}

	private void queue(String[] args) {
		QueueCommand cmd = new QueueCommand(factory, out);
		Parser mapParser = new Parser(cmd);

		try {
			mapParser.parse(args);
			cmd.execute();
		} catch (ParseException e) {
			mapParser.usage();
		}
	}
}
