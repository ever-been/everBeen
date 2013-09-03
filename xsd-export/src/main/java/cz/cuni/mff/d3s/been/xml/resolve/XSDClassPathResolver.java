package cz.cuni.mff.d3s.been.xml.resolve;

import java.util.ServiceLoader;

import org.xml.sax.EntityResolver;

/**
 * Entity resolver used by custom CatalogResolver. Providers of this interface
 * are scanned whenever a project is used as a JAXB episode.
 * 
 * The intended use-case for implementers is to provide resolution for
 * package-specific schemas. Effectively, anything that gets resolved by
 * exported implementations will be passed to the CatalogResolver in first order
 * of importance (overrides default behavior).
 * 
 * The easiest way to export a schema is to create an implementation in the same
 * package as the XSDs and load them via {@link Class#getResource(String)} using
 * an empty override of the provided {@link SkeletalXSDClassPathResolver}. Note
 * that this way, the resolver class's package has to correspond to the
 * classpath of desired resources in the resulting jar, not the source project.
 * 
 * Note that to become a provider for this interface, a META-INF service entry
 * is needed. See more in {@link ServiceLoader} documentation if unsure about
 * how to proceed.
 * 
 * Finally, do not forget to export the XSDs into the resulting JAR if you want
 * them found.
 * 
 * @author darklight
 * 
 */
public interface XSDClassPathResolver extends EntityResolver {}
