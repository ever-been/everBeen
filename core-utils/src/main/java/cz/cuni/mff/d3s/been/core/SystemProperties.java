package cz.cuni.mff.d3s.been.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kuba Brecka
 */
public class SystemProperties {

	private static final Logger log = LoggerFactory.getLogger(SystemProperties.class);

	public static int getInteger(String name, int defaultValue) {
		try {
			return Integer.parseInt(System.getProperty("been.context.ttl", "300"));
		} catch (NumberFormatException e) {
			log.info(String.format("Cannot parse system property '%s', value: %d", name, defaultValue), e);
		}

		return defaultValue;
	}
}
