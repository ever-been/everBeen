package cz.cuni.mff.d3s.been.cluster.cshell;

import cz.mff.dpp.args.Option;
import cz.mff.dpp.args.ParseException;
import cz.mff.dpp.args.Parser;
import jline.console.ConsoleReader;
import net.spy.memcached.MemcachedClient;

import java.io.IOException;

/**
 * @author Martin Sixta
 */
public class MemcachedMode extends Mode {


	static class GetInfo {
		@Option(name = "-k", aliases = {"--key"}, required = true)
		private String key;

		public String getKey() {
			return key;
		}
	}

	static class SetInfo {
		@Option(name = "-k", aliases = {"--key"}, required = true)
		private String key;

		@Option(name = "-v", aliases = {"--value"}, required = true)
		private String value;

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}

	HostInfo hostInfo;
	MemcachedClient client = null;

	private enum Action {
		CONNECT, DISCONNECT, SET, GET, BREAK, STATUS;
	}

	private static String[] getActionStrings() {
		Action[] enumValues = Action.values();

		String[] stringValues = new String[enumValues.length];

		for (int i = 0; i < enumValues.length; ++i) {
			stringValues[i] = enumValues[i].name().toLowerCase();

		}

		return stringValues;

	}

	protected MemcachedMode(ConsoleReader reader) {
		super(reader, "memcached> ", getActionStrings());
	}

	@Override
	public Mode takeAction(final String[] args) {
		assert args != null;
		assert args.length > 0;

		Action action = Action.valueOf(args[0].toUpperCase());

		switch (action) {
			case CONNECT:
				connect(args);
				break;
			case DISCONNECT:
				disconnect();
				break;
			case SET:
				set(args);
				break;
			case GET:
				get(args);
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

	private void status() {
		if (client == null) {
			out.println("Disconnected");
		} else {
			out.printf("Connected to %s\n",hostInfo.getInetSocketAddress());
		}
	}


	private void get(String[] args) {
		if (client == null) {
			throw new IllegalArgumentException("Not connected!");
		}

		GetInfo info = new GetInfo();

		Parser parser = new Parser(info);

		try {
			parser.parse(args);
		} catch (ParseException e) {
			parser.usage();
			throw new IllegalArgumentException("Wrong arguments for GET!");
		}


		out.println(client.get(info.getKey()));



	}

	private void set(String[] args) {
		if (client == null) {
			throw new IllegalArgumentException("Not connected!");
		}

		SetInfo info = new SetInfo();

		Parser parser = new Parser(info);

		try {
			parser.parse(args);
		} catch (ParseException e) {
			parser.usage();
			throw new IllegalArgumentException("Wrong arguments for SET!");
		}


		out.println(client.set(info.getKey(), 0, info.getValue()));
	}

	private void disconnect() {
		if (client != null) {
			client.shutdown();
			client = null;
		}
	}

	private void connect(final String[] args) {
		if (client != null) {
			throw new IllegalArgumentException("Already connected!");
		}

		hostInfo = new HostInfo();

		Parser parser = new Parser(hostInfo);

		try {
			parser.parse(args);
		} catch (ParseException e) {
			parser.usage();
			throw new IllegalArgumentException("Cannot connect to memcached server!");
		}

		try {
			client =  new MemcachedClient(hostInfo.getInetSocketAddress());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


	}
}
