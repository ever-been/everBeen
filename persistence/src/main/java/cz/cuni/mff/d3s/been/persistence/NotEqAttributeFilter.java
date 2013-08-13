package cz.cuni.mff.d3s.been.persistence;

import org.codehaus.jackson.annotate.JsonTypeInfo;

import static cz.cuni.mff.d3s.been.persistence.FilterValues.HARD_VALUE;

/**
 * Filter saying the given attribute is not equal to given value
 *
 * @author darklight
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
class NotEqAttributeFilter extends SkeletalAttributeFilter {

	public NotEqAttributeFilter(){}

	NotEqAttributeFilter(Object value) {
		values.put(HARD_VALUE.getKey(), value);
	}

	@Override
	public AttributeFilterType getType() {
		return AttributeFilterType.NOT_EQUAL;
	}
}
