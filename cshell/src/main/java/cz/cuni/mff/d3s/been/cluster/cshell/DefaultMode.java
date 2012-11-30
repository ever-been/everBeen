package cz.cuni.mff.d3s.been.cluster.cshell;

import jline.console.ConsoleReader;

/**
 * @author Martin Sixta
 */
class DefaultMode extends Mode {

	private enum Action {
		LITE, REST, MEMCACHED, BREAK, NATIVE;
	}

	private static String[] getActionStrings() {
		Action[] enumValues = Action.values();

		String[] stringValues = new String[enumValues.length];

		for (int i = 0; i < enumValues.length; ++i) {
			stringValues[i] = enumValues[i].name().toLowerCase();

		}

		return stringValues;

	}

	public DefaultMode(ConsoleReader reader) {
		super(reader, "> ", getActionStrings());

	}

	@Override
	protected Mode takeAction(String[] args) {

		assert args != null;
		assert args.length > 0;

		Action action = Action.valueOf(args[0].toUpperCase());

		switch (action) {
			case LITE:
				return new LiteMode(reader);
			case NATIVE:
				return new NativeMode(reader);
			case REST:
				return new RestMode(reader);
			case MEMCACHED:
				return new MemcachedMode(reader);
			case BREAK:
				System.exit(0);
				break;
		}


		return this;

	}
}
