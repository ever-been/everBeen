package cz.cuni.mff.d3s.been.core.runtime;

import java.io.StringWriter;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;

public class RuntimeInfos {

	private RuntimeInfos() {
		// prevents instantiation
	}

    /**
     * Serializes {@link RuntimeInfo} instance to XML string.
     *
     * @param value
     *          to be serialized
     * @return XML representation of given {@link RuntimeInfo}
     * @throws IllegalArgumentException
     *           when given {@link RuntimeInfo} cannot be converted.
     */
	public static String toXml(RuntimeInfo value) throws IllegalArgumentException {
		try {
            BindingComposer<RuntimeInfo> composer = XSD.RUNTIME.createComposer(RuntimeInfo.class);
			StringWriter writer = new StringWriter();
			composer.compose(value, writer);

            return writer.toString();
		} catch (SAXException | JAXBException e) {
            throw new IllegalArgumentException(String.format("Cannot serialize %s instance to XML", RuntimeInfo.class.getName()), e);
		}
	}

}
