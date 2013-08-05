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
 * @author Martin Sixta
 */
abstract class PropertyLoader {

	abstract Properties load() throws IOException;

	static PropertyLoader fromUrl(URL url) {
		return new UrlPropertyReader(url);
	}

	static PropertyLoader fromPath(Path path) {
		return new PathPropertyLoader(path);
	}

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
