package cz.cuni.mff.d3s.been.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Defines interface and utility factory functions for loading properties files
 * from different resources.
 * 
 * @author Martin Sixta
 */
abstract class PropertyLoader {

	/**
	 * Loads {@link Properties} from an external resource.
	 * 
	 * @return properties loaded from an external source
	 * @throws IOException
	 *           when the resource cannot be loaded
	 */
	abstract Properties load() throws IOException;

	/**
	 * Creates {@link PropertyLoader} for an {@link URL} resource
	 * 
	 * @param url
	 *          URL location of the resource
	 * @return loader bind to the <code>url</code>
	 */
	static PropertyLoader fromUrl(URL url) {
		return new UrlPropertyReader(url);
	}

	/**
	 * Creates {@link PropertyLoader} for a {@link Path}
	 * 
	 * @param path
	 *          location of the resource
	 * @return loader bind to the <code>path</code>
	 */
	static PropertyLoader fromPath(Path path) {
		return new PathPropertyLoader(path);
	}

	/**
	 * Implementation of a {@link PropertyLoader} which creates Properties from a
	 * {@link Path}.
	 */
	private static class PathPropertyLoader extends PropertyLoader {
		private final Path path;

		private PathPropertyLoader(Path path) {
			this.path = path;
		}

		@Override
		Properties load() throws IOException {
			Properties properties = new Properties();

			try (final BufferedReader in = Files.newBufferedReader(path, Charset.defaultCharset())) {
				properties.load(in);
			}

			return properties;

		}
	}

	/**
	 * Implementation of a {@link PropertyLoader} which creates Properties from an
	 * {@link URL}.
	 */
	private static class UrlPropertyReader extends PropertyLoader {
		private final URL url;

		private UrlPropertyReader(URL url) {
			this.url = url;
		}

		@Override
		Properties load() throws IOException {
			Properties properties = new Properties();

			try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {

				properties.load(in);

				return properties;
			}
		}
	}

}
