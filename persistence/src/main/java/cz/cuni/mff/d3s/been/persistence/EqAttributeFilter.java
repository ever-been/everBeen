package cz.cuni.mff.d3s.been.persistence;

import static cz.cuni.mff.d3s.been.persistence.FilterValues.HARD_VALUE;

/**
 * Equality attribute filter (attribute value equals)
 *
 * @author darklight
 */
class EqAttributeFilter extends SkeletalAttributeFilter {

	EqAttributeFilter(String value) {
		values.put(HARD_VALUE.getKey(), value);
	}
}
