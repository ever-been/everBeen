package cz.cuni.mff.d3s.been.persistence;

/**
 * Type of the {@link AttributeFilter}
 *
 * @author darklight
 */
enum AttributeFilterType {
	/** Filter out entries whose attribute matches the value in the filter */
	EQUAL,
	/** Filter out entries whose attribute differs from the value in the filter */
	NOT_EQUAL,
	/** Filter out entries whose attribute is above the value in the filter */
	ABOVE,
	/** Filter out entries whose attribute is below the value in the filter */
	BELOW,
	/** Filter out entries whose attribute is between the values provided in the filter */
	BETWEEN,
	/** Filter out entries whose attribute is similar to the value in the filter */
	LIKE
}
