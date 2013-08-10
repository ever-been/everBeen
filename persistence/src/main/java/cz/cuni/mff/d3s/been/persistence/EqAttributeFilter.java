package cz.cuni.mff.d3s.been.persistence;

import org.codehaus.jackson.annotate.JsonTypeInfo;

import static cz.cuni.mff.d3s.been.persistence.AttributeFilterType.EQUAL;
import static cz.cuni.mff.d3s.been.persistence.FilterValues.HARD_VALUE;

/**
 * Equality attribute filter (attribute value equals)
 *
 * @author darklight
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
class EqAttributeFilter extends SkeletalAttributeFilter {

	public EqAttributeFilter() {
	}

	EqAttributeFilter(Object value) {
		values.put(HARD_VALUE.getKey(), value);
	}

	@Override
	public AttributeFilterType getType() {
		return EQUAL;
	}
}
