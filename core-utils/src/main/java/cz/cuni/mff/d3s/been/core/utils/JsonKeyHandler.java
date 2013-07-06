package cz.cuni.mff.d3s.been.core.utils;

/**
 * 
 * Handler of JSON key/value pairs.
 * 
 * @author Martin Sixta
 */
public interface JsonKeyHandler {
	/**
	 * Handles a key.
	 * 
	 * Should not throw.
	 * 
	 * @param key
	 *          key
	 * @param value
	 *          value
	 * @param json
	 *          the whole JSON string
	 */
	void handle(final String key, final String value, final String json);
}
