package cz.cuni.mff.d3s.been.persistence;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * A simple {@link QueryAnswer} (status-only)
 *
 * @author darklight
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
class SimpleQueryAnswer extends SkeletalQueryAnswer {

	/**
	 * Only Jackson should use this constructor
	 */
	SimpleQueryAnswer(){}

	SimpleQueryAnswer(QueryStatus status) {
		super(status);
	}
}
