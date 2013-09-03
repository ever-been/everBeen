package cz.cuni.mff.d3s.been.persistence;

/**
 * A fluent builder for {@link AttributeFilter} objects
 *
 * @param <T> Type of the {@link QueryBuilderBase} extension that requested this builder
 *
 * @author darklight
 */
public class AttributeFilterBuilder<T extends QueryBuilderBase> {
	private final T queryBuilder;
	private final String attributeName;

	AttributeFilterBuilder(T queryBuilder, String attributeName) {
		this.queryBuilder = queryBuilder;
		this.attributeName = attributeName;
	}

	/**
	 * Put an equality selector on the attribute. Only entries whose attribute value is equal to the specified value will be selected.
	 *
	 * @param value Value the attribute must have
	 *
	 * @return The query builder, with added selector
	 */
	public T equal(Object value) {
		queryBuilder.selectors.put(attributeName, createEqFilter(value));
		return queryBuilder;
	}

	/**
	 * Put a non-equality selector on the attribute. Only entries whose attribute value is different from the specified value will be selected.
	 *
	 * @param value Value the attribute must not have
	 *
	 * @return The query builder, with added selector
	 */
	public T differentFrom(Object value) {
		queryBuilder.selectors.put(attributeName, createNotEqFilter(value));
		return queryBuilder;
	}

	/**
	 * Put a 'like' selector on the attribute. Only entries matching the pattern (regex) will be selected.
	 *
	 * @param pattern Pattern the attribute value must match to be selected
	 *
	 * @return The query builder, with added selector
	 */
	public T like(String pattern) {
		queryBuilder.selectors.put(attributeName, createPatternFilter(pattern));
		return queryBuilder;
	}

	/**
	 * Put a '>=' selector on the attribute. Only entries whose value is superior or equal to provided value will be selected.
	 *
	 * @param value Minimal value for the attribute for its entry to pass into selection
	 *
	 * @return The query builder, with added selector
	 */
	public T above(Object value) {
		queryBuilder.selectors.put(attributeName, createIntervalFilter(value, null));
		return queryBuilder;
	}

	/**
	 * Put a '<' selector on the attribute. Only entries whose value is inferior to provided value will be selected
	 *
	 * @param value Minimal value of the attribute for its entry to be excluded from selection
	 *
	 * @return The query builder, with added selector
	 */
	public T below(Object value) {
		queryBuilder.selectors.put(attributeName, createIntervalFilter(null, value));
		return queryBuilder;
	}

	/**
	 * Put a 'in [lowBound, highBound)' selector on the attribute. Only entries whose value is inside the semi-open interval will be selected
	 *
	 * @param lowBound Minimal value of the attribute for its entry to be selected
	 * @param highBound Minimal value of the attribute for its entry to be excluded from selection
	 *
	 * @return The query builder, with added selector
	 */
	public T between(Object lowBound, Object highBound) {
		queryBuilder.selectors.put(attributeName, createIntervalFilter(lowBound, highBound));
		return queryBuilder;
	}

	private SkeletalAttributeFilter createEqFilter(Object value) {
		return new EqAttributeFilter(value);
	}

	private SkeletalAttributeFilter createNotEqFilter(Object value) {
		return new NotEqAttributeFilter(value);
	}

	private SkeletalAttributeFilter createPatternFilter(String pattern) {
		return new PatternAttributeFilter(pattern);
	}

	private SkeletalAttributeFilter createIntervalFilter(Object lo, Object hi) {
		return new IntervalAttributeFilter(lo, hi);
	}
}
