package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A common base for {@link QueryBuilder} and {@link ResultQueryBuilder}.
 *
 * @author darklight
 */
class QueryBuilderBase {
	EntityID entityID = new EntityID().withKind("result");
	Map<String, SkeletalAttributeFilter> selectors = new HashMap<String, SkeletalAttributeFilter>();
	Set<String> mappings = new HashSet<String>();
}
