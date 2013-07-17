package cz.cuni.mff.d3s.been.persistence;

import java.util.Collection;

/**
 * A skeletal implementation of {@link QueryAnswer}
 *
 * @author darklight
 */
abstract class SkeletalQueryAnswer implements QueryAnswer {
	private final QueryStatus status;

	SkeletalQueryAnswer(QueryStatus status) {
		this.status = status;
	}

	@Override
	public QueryStatus getStatus() {
		return status;
	}

	@Override
	public boolean isData() {
		return false;
	}

	@Override
	public Collection<String> getData() {
		throw new UnsupportedOperationException(String.format("%s does not carry any data", getClass().getSimpleName()));
	}
}
