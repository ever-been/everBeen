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

	public static String toXml(RuntimeInfo value) {
		BindingComposer<RuntimeInfo> composer = null;
		StringWriter writer = null;
		try {
			composer = XSD.RUNTIME.createComposer(RuntimeInfo.class);

			writer = new StringWriter();

			composer.compose(value, writer);

		} catch (SAXException | JAXBException e) {
			e.printStackTrace();
			return "";
		}

		return writer.toString();

	}

}
