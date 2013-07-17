package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.Set;

/**
 * A persistence layer query. Gets replied to with a {@link QueryAnswer}.
 *
 * @author darklight
 */
public interface Query {

	/**
	 * @return The query's ID
	 */
	String getId();

	/**
	 * Get the entity ID associated with the request. Leaving out fields or the entity altogether will result in a more general query.
	 * @return The ID of the entity targeted by the query
	 */
	EntityID getEntityID();

	/**
	 * Get the names of the selector attributes set for this query.
	 *
	 * @return The selector names
	 */
	Set<String> getSelectorNames();

	/**
	 * Get a selector value
	 *
	 * @param selectorName Name of the selector
	 *
	 * @return Value of that selector
	 */
	String getSelector(String selectorName);

	/**
	 * Get the type of this query
	 *
	 * @return Query type
	 */
	public QueryType getType();
}
