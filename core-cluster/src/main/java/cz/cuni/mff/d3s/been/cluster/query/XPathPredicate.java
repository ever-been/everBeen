package cz.cuni.mff.d3s.been.cluster.query;

import org.apache.commons.jxpath.JXPathContext;

import com.hazelcast.core.MapEntry;
import com.hazelcast.query.Predicate;

import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;

/**
 *
 * Predicate for filtering RuntimeInfo based on XPath expression.
 *
 * @author Martin Sixta
 */
public final class XPathPredicate implements Predicate<String, RuntimeInfo> {
	private final String xpath;

	public XPathPredicate(String xpath) {
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
