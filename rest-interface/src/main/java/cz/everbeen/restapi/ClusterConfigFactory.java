package cz.everbeen.restapi;

import cz.everbeen.restapi.protocol.ClusterConfig;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Factory for the {@link cz.everbeen.restapi.protocol.ClusterConfig}
 *
 * @author darklight
 */
public class ClusterConfigFactory implements ObjectFactory {

	@Override
	public Object getObjectInstance(Object o, Name name, Context context, Hashtable<?, ?> hashtable) throws Exception {
		final Enumeration<RefAddr> addrs = ((Reference) o).getAll();
		final Map<String, String> bindings = new TreeMap<String, String>();
		while(addrs.hasMoreElements()) {
			final RefAddr a = addrs.nextElement();
			bindings.put(a.getType(), (String) a.getContent());
		}
		return ClusterConfig.load(bindings);
	}
}
