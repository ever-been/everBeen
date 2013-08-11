package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.storage.QueryExecutor;

import java.util.Set;

/**
 * A database-specific query interpreter injected into {@link cz.cuni.mff.d3s.been.persistence.QueryTranslator}. {@link cz.cuni.mff.d3s.been.persistence.QueryTranslator} will perform calls on this object when reading a {@link cz.cuni.mff.d3s.been.persistence.Query} to construct a database-specific implementation.
 *
 * @author darklight
 */
public interface QueryRedactor {

	/**
	 * This query contains an 'exact match' selector
	 *
	 * @param attributeName Attribute this selector restricts
	 * @param value Value this attribute's value must be equal to
	 */
	void equalitySelector(String attributeName, Object value);

	/**
	 * This query contains a 'different from' selector
	 *
	 * @param attributeName Attribute this selector restricts
	 * @param value Value this attribute's value must be different from
	 */
	void inequalitySelector(String attributeName, Object value);

	/**
	 * This query contains a 'like' selector (regex)
	 *
	 * @param attributeName Attribute the selector restricts
	 * @param pattern Pattern this attribute must match
	 */
	void patternSelector(String attributeName, String pattern);

	/**
	 * This query contains a partial 'interval' selector with only a low bound
	 *
	 * @param attributeName Attribute the selector restricts
	 * @param lowBound Value this attribute must be greater or equal to
	 */
	void aboveSelector(String attributeName, Object lowBound);

	/**
	 * This query contains a partial 'interval' selector with only a high bound
	 *
	 * @param attributeName Attribute the selector restricts
	 * @param highBound Value this attribute must be inferior to
	 */
	void belowSelector(String attributeName, Object highBound);

	/**
	 * This query contains a full 'interval' selector
	 *
	 * @param attributeName Attribute the selector restricts
	 * @param lowBound Value this attribute must be greater or equal to
	 * @param highBound Value this attribute must be inferior to
	 */
	void intervalSelector(String attributeName, Object lowBound, Object highBound);

	/**
	 * This query only wants certain attributes
	 *
	 * @param attributes Attributes this query maps (others will be omitted)
	 */
	void map(Set<String> attributes);
}
