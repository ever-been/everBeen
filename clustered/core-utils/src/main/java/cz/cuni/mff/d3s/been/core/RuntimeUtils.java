package cz.cuni.mff.d3s.been.core;

import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.Factory;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.ri.Java;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.StringWriter;
import java.util.Collection;

import static cz.cuni.mff.d3s.been.core.Names.HOSTRUNTIME_MAP_NAME;

/**
 * @author Martin Sixta
 */
public class RuntimeUtils {
	public static RuntimeInfo getRuntimeInfo() {
		RuntimeInfo ri = Factory.RUNTIME.createRuntimeInfo();
		Java java = Factory.RUNTIME.createJava();

		java.setVersion(System.getProperty("java.version"));
		java.setVendor(System.getProperty("java.vendor"));


		Runtime runtime = Runtime.getRuntime();

		ri.setOs(System.getProperty("os.name"));
		ri.setMemory(runtime.totalMemory());
		ri.setJavaInfo(java);

		ri.setId(ClusterUtils.getId());

		return ri;
	}


	public static Collection<RuntimeInfo> getRuntimes() {
		return getRuntimeMap().values();
	}

	public static RuntimeInfo getRuntimeInfo(String key) {
		return getRuntimeMap().get(key);
	}


	public static IMap<String, RuntimeInfo> getRuntimeMap() {
		return MapUtils.getMap(HOSTRUNTIME_MAP_NAME);

	}

	public static IQueue<String> getLocalTaskQueue() {
		return ClusterUtils.getInstance().getQueue(ClusterUtils.getId());
	}


	public static String toXml(RuntimeInfo info) {

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
