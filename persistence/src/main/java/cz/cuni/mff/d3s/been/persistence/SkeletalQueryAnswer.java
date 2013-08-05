package cz.cuni.mff.d3s.been.persistence;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.Collection;

/**
 * A skeletal implementation of {@link QueryAnswer}
 *
 * @author darklight
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes({
		@JsonSubTypes.Type(value = DataQueryAnswer.class),
		@JsonSubTypes.Type(value = SimpleQueryAnswer.class)
})
@JsonIgnoreProperties({"data", "carryingData"})
abstract class SkeletalQueryAnswer implements QueryAnswer {
	private QueryStatus status;

	/**
	 * Only Jackson should use this constructor
	 */
	SkeletalQueryAnswer() {
		this.status = null;
	}

	SkeletalQueryAnswer(QueryStatus status) {
		this.status = status;
	}

	@Override
	public QueryStatus getStatus() {
		return status;
	}

	void setStatus(QueryStatus status) {
		this.status = status;
	}

	@Override
	public boolean isCarryingData() {
		return false;
	}

	@Override
	public Collection<String> getData() {
		throw new UnsupportedOperationException(String.format("%s does not carry any data.", getClass().getSimpleName()));
	}
}
