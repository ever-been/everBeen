package cz.cuni.mff.d3s.been.xml.resolve;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class SkeletalXSDClassPathResolver implements XSDClassPathResolver {

	private static final Pattern BEEN_XSD_PUBLIC_ID = Pattern.compile("http://been.d3s.mff.cuni.cz/([a-zA-Z][a-zA-Z0-9-]*)");
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		log.debug("Resolving entity with publicId={}", publicId);
		final Matcher m = BEEN_XSD_PUBLIC_ID.matcher(publicId);
		if (!m.matches()) {
			return null;
		}

		final String fileName = m.group(1) + ".xsd";
		final String syntheticSystemId = getClass().getPackage().getName() + File.separator + fileName;
		log.debug("Attempting class path resolution for resource {}", syntheticSystemId);
		final InputStream resource = getClass().getResourceAsStream(fileName);
		if (resource == null) {
			log.debug("Found nothing for publicId={}", publicId);
			return null;
		}

		log.debug("Found {} for publicId={}", resource, publicId);
		final InputSource result = new InputSource(resource);
		result.setSystemId(syntheticSystemId);
		return result;
	}
}
