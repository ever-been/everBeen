package cz.cuni.mff.d3s.been.persistence;

import static cz.cuni.mff.d3s.been.persistence.FilterValues.PATTERN;

/**
 * Attribute filter saying an attribute's value should resemble a pattern
 *
 * @author darklight
 */
class PatternAttributeFilter extends SkeletalAttributeFilter {

	PatternAttributeFilter(String pattern) {
		values.put(PATTERN.getKey(), pattern);
	}
}
