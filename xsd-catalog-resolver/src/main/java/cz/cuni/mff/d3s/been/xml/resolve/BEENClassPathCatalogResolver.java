package cz.cuni.mff.d3s.been.xml.resolve;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;

/**
 * This is the EverBEEN implementation of the catalog resolver, used for cross-jar <em>XSD</em> loading.
 */
public class BEENClassPathCatalogResolver extends CatalogResolver {

	private static final String BEEN_URI_BASE = "http://been.d3s.mff.cuni.cz/";
	private static final Logger log = LoggerFactory.getLogger(BEENClassPathCatalogResolver.class);

	/**
	 * User entity resolvers used to fetch extra XSDs from the classpath.
	 */
	private final List<EntityResolver> resolvers;

	/**
	 * Create an EverBEEN catalog resolver
	 */
	public BEENClassPathCatalogResolver() {
		resolvers = new ArrayList<EntityResolver>();
		Iterator<XSDClassPathResolver> sl = ServiceLoader.load(XSDClassPathResolver.class).iterator();
		while (sl.hasNext()) {
			resolvers.add(sl.next());
		}
		log.info("Loaded user entity resolvers {}", resolvers.toString());
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId) {
		log.debug("resolveEntity() called with params publicId={}, systemId={}", publicId, systemId);
		InputSource source = attemptResolutionWithUserResolvers(publicId, systemId);
		return (source != null) ? source : super.resolveEntity(publicId, systemId);
	}

	@Override
	public Source resolve(String href, String base) throws TransformerException {
		log.debug("source resolution: href={}, base={}", href, base);
		if (base == null) {
			log.debug("Patching URI base from <null> to <{}>", BEEN_URI_BASE);
			return super.resolve(href, BEEN_URI_BASE);
		} else {
			log.debug("URI base for href <{}> was <{}>, which looked OK", BEEN_URI_BASE);
			return super.resolve(href, base);
		}
	}

	private InputSource attemptResolutionWithUserResolvers(String publicId,
			String systemId) {
		log.debug("resolveEntity() default implementation found nothing, trying user entity resolvers");
		for (EntityResolver resolver : resolvers) {
			final InputSource source = iosafeResolveEntity(resolver, publicId, systemId);
			if (source != null) {
				log.debug("user resolvers found {} for publicId={}", source, publicId);
				return source;
			}
		}
		log.debug("user resolvers found nothing either");
		return null;
	}

	private InputSource iosafeResolveEntity(EntityResolver resolver,
			String publicId, String systemId) {
		try {
			return resolver.resolveEntity(publicId, systemId);
		} catch (IOException | SAXException e) {
			return null;
		}
	}
}
