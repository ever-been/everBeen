package cz.cuni.mff.d3s.been.persistence;

import static cz.cuni.mff.d3s.been.persistence.FilterValues.HIGH_BOUND;
import static cz.cuni.mff.d3s.been.persistence.FilterValues.LOW_BOUND;

/**
 * Interval attribute filter saying the attribute value should be between two bounds
 *
 * @author darklight
 */
public class IntervalAttributeFilter extends SkeletalAttributeFilter {

	IntervalAttributeFilter(String lowBound, String highBound) {
		if (lowBound != null) values.put(LOW_BOUND.getKey(), lowBound);
		if (highBound != null) values.put(HIGH_BOUND.getKey(), highBound);
	}
}
