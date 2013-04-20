package cz.cuni.mff.d3s.been.cluster.action;

/**
 * @author Martin Sixta
 */
final class MapActionUtils {
	static final String SEPARATOR = "#";

	static String[] parseSelector(String selector) throws IllegalArgumentException {
		if (selector == null) {
			throw new IllegalArgumentException("Wrong selector specified: %s. Format is 'map#key'");

		}
		String[] args = selector.split(SEPARATOR);
		if (args.length != 2 || args[0].isEmpty() || args[1].isEmpty()) {
			throw new IllegalArgumentException("Wrong selector specified: %s. Format is 'map#key'");
		}

		return args;

	}
}
