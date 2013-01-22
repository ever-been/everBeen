package cz.cuni.mff.d3s.been.core;

import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.Factory;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.ri.Java;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.StringWriter;

/**
 * @author Martin Sixta
 */
public class RuntimeInfoUtils {
	public static RuntimeInfo newInfo(String id) {
		RuntimeInfo ri = Factory.RUNTIME.createRuntimeInfo();
		cz.cuni.mff.d3s.been.core.ri.Java java = Factory.RUNTIME.createJava();

		java.setVersion(System.getProperty("java.version"));
		java.setVendor(System.getProperty("java.vendor"));


		Runtime runtime = Runtime.getRuntime();

		ri.setOs(System.getProperty("os.name"));
		ri.setMemory(runtime.totalMemory());
		ri.setJavaInfo(java);

		ri.setId(id);

		return ri;
	}

	public static String toXml(RuntimeInfo info) throws IllegalArgumentException {

		// TODO: better exception handling
		StringWriter sw = new StringWriter();

		try {
			BindingComposer<RuntimeInfo> bindingComposer = XSD.RUNTIME.createComposer(RuntimeInfo.class);
			bindingComposer.compose(info, sw);


		} catch (SAXException | JAXBException e ) {
			e.printStackTrace();
			throw new IllegalArgumentException("Cannot convert");
		}

		return sw.toString();


	}
}
