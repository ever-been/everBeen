package cz.cuni.mff.d3s.been.cluster.query;

import com.hazelcast.core.MapEntry;
import com.hazelcast.query.Predicate;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import org.apache.commons.jxpath.JXPathContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
* @author Kuba Brecka
*/
public class RuntimeInfoPredicate implements Predicate<String, RuntimeInfo> {

	private static final Logger log = LoggerFactory.getLogger(RuntimeInfoPredicate.class);

	private final String xpath;

	public RuntimeInfoPredicate(String xpath) {
		this.xpath = xpath;
	}

	@Override
	public boolean apply(MapEntry<String, RuntimeInfo> mapEntry) {
		RuntimeInfo info = mapEntry.getValue();

		JXPathContext context = JXPathContext.newContext(info);
		List list = context.selectNodes(".[" + xpath + "]");
		return (list != null) && (list.size() > 0);
	}

}
