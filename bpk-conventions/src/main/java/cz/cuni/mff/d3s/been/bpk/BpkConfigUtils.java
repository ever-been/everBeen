package cz.cuni.mff.d3s.been.bpk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Utility class for working with BpkConfiguration.
 * 
 * @author Martin Sixta
 */
public class BpkConfigUtils {

	/** slf4j logger */
	private static final Logger log = LoggerFactory.getLogger(BpkConfigUtils.class);

	/**
	 * Where the XSD file for BpkConfiguration is located relative to resources.
	 */
	public static final String SCHEMA_RESOURCE_FILE = "bpk-config.xsd";

	/**
	 * JAXBContext for BpkConfiguration. Don't use directly, use getContext()
	 * instead!
	 */
	private static JAXBContext __context = null;

	/**
	 * Schema for BpkConfiguration. Don't use directly, use getSchema instead!
	 */
	private static Schema __schema = null;

	/**
	 * 
	 * Creating context is not cheap. Let's do it only once.
	 * 
	 * @return JAXB context fot BpkConfiguration
	 * @throws JAXBException
	 *           when context cannot be created
	 */
	private static synchronized JAXBContext getContext() throws JAXBException {
		if (__context == null) {
			__context = JAXBContext.newInstance(BpkConfiguration.class);
		}

		return __context;
	}

	/**
	 * 
	 * Creating schema is not cheap. Let's do it only once.
	 * 
	 * @return Schema for BpkConfiguration
	 * @throws SAXException
	 *           when schema cannot be created
	 */
	private static synchronized Schema getSchema() throws SAXException {
		if (__schema == null) {
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			URL url = BpkConfigUtils.class.getResource(SCHEMA_RESOURCE_FILE);
			log.debug("Loading BPK config schema from {}", url);
			try {
				log.debug("Loaded BPK config schema:{}", url.getContent());
			} catch (IOException e) {
				log.debug("Failed loading BPK config schema because", e);
			}
			__schema = sf.newSchema(url);
		}

		return __schema;
	}

	/**
	 * Converts BpkConfiguration to it's XML representation.
	 * 
	 * @param bpkConfiguration
	 *          configuration to parse
	 * @return XML representation of bpkConfiguration
	 * @throws BpkConfigurationException
	 *           when it rains
	 */
	public static String toXml(BpkConfiguration bpkConfiguration) throws BpkConfigurationException {
		try {

			// create and init marshaller
			Marshaller marshaller = getContext().createMarshaller();
			marshaller.setSchema(getSchema());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// write it
			StringWriter writer = new StringWriter();
			marshaller.marshal(bpkConfiguration, writer);

			return writer.toString();

		} catch (Exception e) {
			String message = "Cannot convert to XML";
			throw new BpkConfigurationException(message, e);
		}
	}

	/**
	 * Parses BpkConfiguration from an InputStream.
	 * 
	 * @param input
	 *          the input stream to parse
	 * @return the parsed BPK configuration
	 * @throws BpkConfigurationException
	 *           when the input is invalid or an I/O error occurs
	 */
	public static BpkConfiguration fromXml(InputStream input) throws BpkConfigurationException {
		try {
			// create and init unmarshaller
			Unmarshaller unmarshaller = getContext().createUnmarshaller();
			unmarshaller.setSchema(getSchema());

			return (BpkConfiguration) unmarshaller.unmarshal(input);

		} catch (Exception e) {
			String message = "Cannot parse XML!";
			e.printStackTrace();
			throw new BpkConfigurationException(message, e);
		}
	}

	/**
	 * Parses BpkConfiguration from a File.
	 * 
	 * @param input
	 *          the input file to parse
	 * @return the parsed BPK configuration
	 * @throws BpkConfigurationException
	 *           when the input is invalid or an I/O error occurs
	 */
	public static BpkConfiguration fromXml(File input) throws BpkConfigurationException {
		try {
			// create and init unmarshaller
			Unmarshaller unmarshaller = getContext().createUnmarshaller();
			unmarshaller.setSchema(getSchema());

			return (BpkConfiguration) unmarshaller.unmarshal(input);

		} catch (Exception e) {
			throw new BpkConfigurationException("Cannot parse BPK configuration XML.", e);
		}
	}

	/**
	 * Parses BpkConfiguration from a Path.
	 * 
	 * @param input
	 *          the path to the input file to parse
	 * @return parsed BPK configuration
	 * @throws BpkConfigurationException
	 *           when the input is invalid or an I/O error occurs
	 */
	public static BpkConfiguration fromXml(Path input) throws BpkConfigurationException {
		return fromXml(input.toFile());
	}

}
