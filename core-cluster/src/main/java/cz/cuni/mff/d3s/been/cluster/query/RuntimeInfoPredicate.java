package cz.cuni.mff.d3s.been.cluster.query;

import com.hazelcast.core.MapEntry;
import com.hazelcast.query.Predicate;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import org.apache.commons.jxpath.JXPathContext;

/**
* @author Kuba Brecka
*/
public class RuntimeInfoPredicate implements Predicate<String, RuntimeInfo> {
	private final String xpath;

	public RuntimeInfoPredicate(String xpath) {
		this.xpath = xpath;
	}

	@Override
	public boolean apply(MapEntry<String, RuntimeInfo> mapEntry) {
		RuntimeInfo info = mapEntry.getValue();

		JXPathContext context = JXPathContext.newContext(info);
		Object obj = context.getValue(xpath);

		if (obj != null && obj instanceof Boolean) {
			return ((Boolean) obj);
		} else {
			return false;
		}
	}
}
