package cz.cuni.mff.d3s.been.util;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility functions for working with system properties.
 * 
 * @author Kuba Brecka
 */
public class PropertyReader {

	/** logging */
	private static final Logger log = LoggerFactory.getLogger(PropertyReader.class);

	private static final PropertyReader sysprops = new PropertyReader(System.getProperties());

	private final Properties properties;

	private PropertyReader(Properties properties) {
		this.properties = properties;
	}

	/**
	 * @return The {@link PropertyReader} for system properties
	 */
	public static PropertyReader system() {
		return sysprops;
	}

	/**
	 * Create a property reader over a pre-defined set of properties
	 *
	 * @param properties {@link Properties} to read
	 *
	 * @return The property reader
	 */
	public static PropertyReader on(Properties properties) {
		return new PropertyReader(properties);
	}

	/**
	 * Get a {@link String} from properties
	 * 
	 * Default value is returned if the property cannot be found
	 * 
	 * @param name
	 *          Name of the property
	 * @param defaultValue
	 *          The property's default value
	 * 
	 * @return String value of wanted property
	 */
	public String getString(String name, String defaultValue) {
		final String propString = properties.getProperty(name, defaultValue);
		return (propString == null || "".equals(propString)) ? defaultValue : propString;
	}

	/**
	 * Get an {@link Integer} from properties
	 * 
	 * The default value will be returned if
	 * <ul>
	 * <li>the system property has no value associated with it</li>
	 * <li>the value cannot be parsed as an integer</li>
	 * </ul>
	 * 
	 * @param name
	 *          Name of desired property
	 * @param defaultValue
	 *          Default value of the property
	 * 
	 * @return Integer value of wanted property
	 */
	public Integer getInteger(String name, Integer defaultValue) {
		final String propString= properties.getProperty(name);
		if (propString == null) {
			return defaultValue;
		}
		final String trimmedPropString = propString.trim();

		try {
			return Integer.parseInt(trimmedPropString);
		} catch (NumberFormatException e) {
			log.warn("Cannot convert value '{}' of property '{}' to Integer", trimmedPropString, name, e);
		}
		return defaultValue; // return default value if anything goes wrong
	}

	/**
	 * Get a {@link Long} from properties
	 * 
	 * The default value will be returned if
	 * <ul>
	 * <li>the system property has no value associated with it</li>
	 * <li>the value cannot be parsed as a long</li>
	 * </ul>
	 * 
	 * @param name
	 *          Name of desired property
	 * @param defaultValue
	 *          Value to assign if lookup or conversion fails
	 * 
	 * @return Long value of wanted property
	 */
	public Long getLong(String name, Long defaultValue) {
		final String propString = properties.getProperty(name);
		if (propString == null) {
			return defaultValue;
		}
		final String trimmedPropString = propString.trim();

		try {
			return Long.parseLong(trimmedPropString);
		} catch (NumberFormatException e) {
			log.warn("Cannot convert value '{}' of property '{}' to Long", trimmedPropString, name);
		}
		return defaultValue;
	}

	/**
	 * Get a {@link Float} from properties
	 *
	 * The default value will be returned if
	 * <ul>
	 * <li>the system property has no value associated with it</li>
	 * <li>the value cannot be parsed as a float</li>
	 * </ul>
	 *
	 * @param name
	 *          Name of desired property
	 * @param defaultValue
	 *          Value to assign if lookup or conversion fails
	 *
	 * @return Float value of wanted property
	 */
	public Float getFloat(String name, Float defaultValue) {
		final String propString = properties.getProperty(name);
		if (propString == null) {
			return defaultValue;
		}
		final String trimmedPropString = propString.trim();

		try {
			return Float.parseFloat(trimmedPropString);
		} catch (NumberFormatException e) {
			log.warn("Cannot convert value '{}' of property '{}' to Float", trimmedPropString, name);
		}
		return defaultValue;
	}

	/**
	 * Get a {@link Boolean} from properties
	 * 
	 * The default value will be returned if
	 * <ul>
	 * <li>the system property has no value associated with it</li>
	 * <li>the value cannot be parsed as a boolean</li>
	 * </ul>
	 * 
	 * @param name
	 *          Name of desired property
	 * @param defaultValue
	 *          Value to assign if lookup or conversion fails
	 * 
	 * @return Boolean value of wanted property
	 */
	public Boolean getBoolean(String name, Boolean defaultValue) {
		final String propString = properties.getProperty(name);
		if (propString == null) {
			return defaultValue;
		}
		final String trimmedPropString = propString.trim();

		// we are parsing it explicitly instead of Boolean.parseBoolean to log warning in case
		// the string contains nonsense
		if (trimmedPropString.equalsIgnoreCase("true")) {
			return true;
		} else if (trimmedPropString.equalsIgnoreCase("false")) {
			return false;
		} else {
			log.warn("Cannot convert value '{}' of property '{}' to Boolean", trimmedPropString, name);
			return defaultValue;
		}
	}
}
