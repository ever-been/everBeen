package cz.cuni.mff.d3s.been.persistence;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * A skeletal implementation of the attribute filter
 *
 * @author darklight
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({
		@JsonSubTypes.Type(value = EqAttributeFilter.class),
		@JsonSubTypes.Type(value = IntervalAttributeFilter.class),
		@JsonSubTypes.Type(value = PatternAttributeFilter.class),
})
@JsonIgnoreProperties({"type"})
abstract class SkeletalAttributeFilter implements AttributeFilter {

	protected Map<String, Object> values = new HashMap<String, Object>();

	/**
	 * Serialization getter for value map
	 *
	 * @return The values
	 */
	public Map<String, Object> getValues() {
		return values;
	}

	/**
	 * Serialization setter for value map
	 *
	 * @param values Values to set
	 */
	public void setValues(Map<String, Object> values) {
		this.values = values;
	}
}
