package cz.cuni.mff.d3s.been.persistence;

import static cz.cuni.mff.d3s.been.persistence.FilterValues.*;

/**
 * A translation factory that serves the purpose of translating {@link cz.cuni.mff.d3s.been.persistence.Query} objects into database-specific queries.
 *
 * @author darklight
 */
public final class QueryTranslator {

	/**
	 * Translate a {@link cz.cuni.mff.d3s.been.persistence.Query} using a native (database-specific) {@link QueryRedactor}
	 *
	 * @param query Query to interpret
	 * @param interpreter Interpreter to inform of the query's specifics
	 *
	 * @throws cz.cuni.mff.d3s.been.persistence.UnsupportedQueryException When the query cannot be interpreted
	 */
	public void interpret(Query query, QueryRedactor interpreter) throws UnsupportedQueryException {
		for (String selectorName: query.getSelectorNames()) {
			final AttributeFilter filter = query.getSelector(selectorName);
			if (! (filter instanceof SkeletalAttributeFilter)) {
				throw new UnsupportedQueryException(query, String.format("Unsupported selector for attribute '%s'", selectorName));
			}

			final SkeletalAttributeFilter skelf = (SkeletalAttributeFilter) filter;
			switch (skelf.getType()) {
				case ABOVE:
					interpreter.aboveSelector(
							selectorName,
							skelf.getValues().get(LOW_BOUND.getKey())
					);
					break;
				case BELOW:
					interpreter.belowSelector(
							selectorName,
							skelf.getValues().get(HIGH_BOUND.getKey()));
					break;
				case BETWEEN:
					interpreter.intervalSelector(
							selectorName,
							skelf.getValues().get(LOW_BOUND.getKey()),
							skelf.getValues().get(HIGH_BOUND.getKey()));
					break;
				case LIKE:
					interpreter.patternSelector(
							selectorName,
							(String) skelf.getValues().get(PATTERN.getKey()));
					break;
				case EQUAL:
					interpreter.equalitySelector(
							selectorName,
							skelf.getValues().get(HARD_VALUE.getKey()));
					break;
				default:
					throw new UnsupportedQueryException(query, String.format("Filter type '%s' on attribute '%s' not supported.", skelf.getType().name(), selectorName));
			}
		}
	}
}
