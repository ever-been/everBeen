package cz.cuni.mff.d3s.been.persistence;

import org.codehaus.jackson.annotate.JsonTypeInfo;

import static cz.cuni.mff.d3s.been.persistence.AttributeFilterType.ABOVE;
import static cz.cuni.mff.d3s.been.persistence.AttributeFilterType.BELOW;
import static cz.cuni.mff.d3s.been.persistence.AttributeFilterType.BETWEEN;
import static cz.cuni.mff.d3s.been.persistence.FilterValues.HIGH_BOUND;
import static cz.cuni.mff.d3s.been.persistence.FilterValues.LOW_BOUND;

/**
 * Interval attribute filter saying the attribute value should be between two bounds
 *
 * @author darklight
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class IntervalAttributeFilter extends SkeletalAttributeFilter {

	public IntervalAttributeFilter() {

	}

	IntervalAttributeFilter(Object lowBound, Object highBound) {
		if (lowBound != null) values.put(LOW_BOUND.getKey(), lowBound);
		if (highBound != null) values.put(HIGH_BOUND.getKey(), highBound);
	}

	@Override
	public AttributeFilterType getType() {
		final Object lowBound = values.get(LOW_BOUND.getKey());
		final Object highBound = values.get(HIGH_BOUND.getKey());

		if (lowBound == null) {
			return BELOW;
		}

		if (highBound == null) {
			return ABOVE;
		}

		return BETWEEN;
	}
}
