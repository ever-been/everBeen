package cz.cuni.mff.d3s.been.core;

import java.io.StringWriter;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.Factory;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.ri.Java;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;

/**
 * @author Martin Sixta
 * 
 *         FIXME MartinSixta: I think that {@link RuntimesUtils} should be
 *         merged into {@link RuntimeInfoUtils} (all methods in RuntimesUtils
 *         have something in common with {@link RuntimeInfo})
 */
public class RuntimeInfoUtils {

	/**
	 * Creates new {@link RuntimeInfo} and initializes all possible values.
	 * 
	 * @param id
	 *          cluster-unique id of parent HostRuntime
	 * @return initialized RuntimeInfo
	 */
	public RuntimeInfo newInfo(String id) {
		RuntimeInfo ri = Factory.RUNTIME.createRuntimeInfo();

		ri.setId(id);

		Java java = Factory.RUNTIME.createJava();
		java.setVersion(System.getProperty("java.version"));
		java.setVendor(System.getProperty("java.vendor"));
		ri.setJavaInfo(java);

		ri.setOs(System.getProperty("os.name"));

		ri.setMemory(Runtime.getRuntime().totalMemory());

		return ri;
	}

	/**
	 * Serializes {@link RuntimeInfo} instance to XML string.
	 * 
	 * @param info
	 *          to be serialized
	 * @return XML representation of given {@link RuntimeInfo}
	 * @throws IllegalArgumentException
	 *           when given {@link RuntimeInfo} cannot be converted.
	 */
	public String toXml(RuntimeInfo info) throws IllegalArgumentException {

		// TODO: better exception handling
		StringWriter sw = new StringWriter();

		try {
			BindingComposer<RuntimeInfo> bindingComposer = XSD.RUNTIME.createComposer(RuntimeInfo.class);
			bindingComposer.compose(info, sw);
		} catch (SAXException | JAXBException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(String.format("Cannot serialize %s instance to XML", RuntimeInfo.class.getName()));
		}

		return sw.toString();
	}

}
