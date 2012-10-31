package cz.cuni.mff.been.utils;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtils extends org.apache.commons.io.IOUtils {

	private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);

	private IOUtils() {
		// instantiation not available
	}

	/**
	 * close {@link Closeable} object quietly (exception is not thrown on
	 * failure). Found to be really useful for closing resources in finally block.
	 * 
	 * @param closeable
	 *          object implementing {@link Closeable} interface
	 */
	public static void closeCloseableQuitely(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				logger.warn("Could not close closeable object. Exception is ignored", e);
			}
		}
	}

}
