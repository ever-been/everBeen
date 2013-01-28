package cz.cuni.mff.d3s.been.core;

import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import cz.cuni.mff.d3s.been.core.jaxb.BindingComposer;
import cz.cuni.mff.d3s.been.core.jaxb.XSD;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.StringWriter;
import java.util.Collection;

import static cz.cuni.mff.d3s.been.core.Names.HOSTRUNTIME_MAP_NAME;

/**
 * @author Martin Sixta
 */
public class RuntimesUtils {

	public static Collection<RuntimeInfo> getRuntimes() {
		return getRuntimeMap().values();
	}

	public static RuntimeInfo getRuntimeInfo(String key) {
		return getRuntimeMap().get(key);
	}

	public static void setRuntimeInfo(RuntimeInfo runtimeInfo) {
		getRuntimeMap().put(runtimeInfo.getId(), runtimeInfo);
	}


	public static IMap<String, RuntimeInfo> getRuntimeMap() {
		return MapUtils.getMap(HOSTRUNTIME_MAP_NAME);

	}

	public static IQueue<String> getLocalTaskQueue() {
		return ClusterUtils.getInstance().getQueue(ClusterUtils.getId());
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

			return writer.toString() ;

		}

}
