package cz.cuni.mff.d3s.been.persistence;

/**
 * A simple {@link QueryAnswer} (status-only)
 *
 * @author darklight
 */
class SimpleQueryAnswer extends SkeletalQueryAnswer {

	SimpleQueryAnswer(QueryStatus status) {
		super(status);
	}
}
