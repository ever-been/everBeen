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

import static cz.cuni.mff.d3s.been.core.Names.HOSTRUNTIMES_MAP_NAME;

/**
 * @author Martin Sixta
 */
public class RuntimesUtils {

	/**
	 * @return collection clone (changes not reflected) of all registered host
	 *         runtimes. </br>
	 * 
	 *         <b>Warning!</b> modifying the returned list does not affect the
	 *         original list.
	 */
	public static Collection<RuntimeInfo> getRuntimes() {
		return getRuntimeMap().values();
	}

	/**
	 * @return clone of {@link RuntimeInfo} registered in cluster. <br/>
	 * 
	 *         <b>Warning!</b> modifying the returned value does not change the
	 *         original value.
	 */
	public static RuntimeInfo getRuntimeInfo(String key) {
		return getRuntimeMap().get(key);
	}

	/**
	 * Stores given {@link RuntimeInfo} in cluster.
	 * @param runtimeInfo
	 */
	public static void storeRuntimeInfo(RuntimeInfo runtimeInfo) {
		getRuntimeMap().put(runtimeInfo.getId(), runtimeInfo);
	}

	/**
	 * Removes stored {@link RuntimeInfo} identified by given id from cluster.
	 * @param id
	 */
	public static void removeRuntimeInfo(String id) {
		getRuntimeMap().remove(id);
	}

	/**
	 * @return modifiable map of all registered Host Runtimes.
	 */
	public static IMap<String, RuntimeInfo> getRuntimeMap() {
		return MapUtils.getMap(HOSTRUNTIMES_MAP_NAME);
	}

	/**
	 * FIXME Martin Sixta ?? I do not understand what the "LocalTaskQueue" really is. Please document it.
	 * @return
	 */
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
