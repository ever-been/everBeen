package cz.cuni.mff.d3s.been.bpk;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

/**
 * Utility class for working with BpkConfiguration.
 *
 * @author Martin Sixta
 */
public class BpkConfigUtils {
	/**
	 * Where the XSD file for BpkConfiguration is located relative to resources.
	 */
	public static String SCHEMA_RESOURCE_FILE = "xsd/bpk-config.xsd";

	/**
	 * JAXBContext for BpkConfiguration. Don't use directly, use getContext() instead!
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
	 * @return  JAXB context fot BpkConfiguration
	 * @throws JAXBException when context cannot be created
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
	 * @return  Schema for BpkConfiguration
	 * @throws SAXException when schema cannot be created
	 */
	private static synchronized Schema getSchema() throws SAXException {
		if (__schema == null) {
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			URL url = BpkConfigUtils.class.getClassLoader().getResource(SCHEMA_RESOURCE_FILE);
			__schema = sf.newSchema(url);
		}

		return __schema;
	}

	/**
	 * Converts BpkConfiguration to it's XML representation.
	 *
	 * @param bpkConfiguration configuration to parse
	 * @return XML representation of bpkConfiguration
	 * @throws BpkConfigurationException when it rains
	 */
	public static String toXml(BpkConfiguration bpkConfiguration) throws  BpkConfigurationException {
		try {

			// create and init marshaller
			Marshaller marshaller = getContext().createMarshaller();
			marshaller.setSchema(getSchema());
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );

			// write it
			StringWriter writer = new StringWriter();
			marshaller.marshal(bpkConfiguration, writer);

			return writer.toString();

		} catch (Exception e) {
			String message = "Cannot convert to XML";
			throw new  BpkConfigurationException(message, e);
		}
	}

	/**
	 * Parses BpkConfiguration from an InputStream.
	 *
	 * @param input
	 * @return
	 * @throws BpkConfigurationException
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
			throw new  BpkConfigurationException(message, e);
		}
	}

	/**
	 * Parses BpkConfiguration from a File.
	 *
	 * @param input
	 * @return
	 * @throws BpkConfigurationException
	 */
	public static BpkConfiguration fromXml(File input) throws BpkConfigurationException {
		try {
			// create and init unmarshaller
			Unmarshaller unmarshaller = getContext().createUnmarshaller();
			unmarshaller.setSchema(getSchema());

			return (BpkConfiguration) unmarshaller.unmarshal(input);

		} catch (Exception e) {
			String message = "Cannot parse XML!";
			throw new  BpkConfigurationException(message, e);
		}
	}
}
