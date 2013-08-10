package cz.cuni.mff.d3s.been.persistence;

import org.codehaus.jackson.annotate.JsonTypeInfo;

import static cz.cuni.mff.d3s.been.persistence.AttributeFilterType.LIKE;
import static cz.cuni.mff.d3s.been.persistence.FilterValues.PATTERN;

/**
 * Attribute filter saying an attribute's value should resemble a pattern
 *
 * @author darklight
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
class PatternAttributeFilter extends SkeletalAttributeFilter {

	public PatternAttributeFilter() {

	}

	PatternAttributeFilter(String pattern) {
		values.put(PATTERN.getKey(), pattern);
	}

	@Override
	public AttributeFilterType getType() {
		return LIKE;
	}
}
