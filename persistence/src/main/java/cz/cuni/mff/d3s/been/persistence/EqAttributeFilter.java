package cz.cuni.mff.d3s.been.persistence;

import static cz.cuni.mff.d3s.been.persistence.AttributeFilterType.EQUAL;
import static cz.cuni.mff.d3s.been.persistence.FilterValues.HARD_VALUE;

/**
 * Equality attribute filter (attribute value equals)
 *
 * @author darklight
 */
class EqAttributeFilter extends SkeletalAttributeFilter {

	EqAttributeFilter(Object value) {
		values.put(HARD_VALUE.getKey(), value);
	}

	@Override
	public AttributeFilterType getType() {
		return EQUAL;
	}
}
