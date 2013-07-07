package cz.cuni.mff.d3s.been.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility functions for working with system properties.
 * 
 * @author Kuba Brecka
 */
public class SystemProperties {

	/** logging */
	private static final Logger log = LoggerFactory.getLogger(SystemProperties.class);

	/**
	 * Returns integer representation of a system property.
	 * 
	 * The default value will be returned if
	 * <ul>
	 * <li>the system property has no value associated with it</li>
	 * <li>the value cannot be parsed as an integer</li>
	 * </ul>
	 * 
	 * @param name
	 *          name of a system property
	 * @param defaultValue
	 *          default value of the system property
	 * @return Integer representation of a system property
	 */
	public static int getInteger(String name, int defaultValue) {
		String propertyValue = System.getProperty(name, Integer.toString(defaultValue));
		try {
			return Integer.parseInt(propertyValue);
		} catch (NumberFormatException e) {
			String msg = String.format("Cannot parse system property '%s', value: %s", name, propertyValue);
			log.warn(msg, e);
		}

		return defaultValue; // return default value if anything goes wrong
	}
}
