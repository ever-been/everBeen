package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.core.persistence.Entity;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.ObjectReader;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A {@link QueryAnswer} that carries data
 *
 * @author darklight
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DataQueryAnswer extends SkeletalQueryAnswer {

	private Collection<String> data;

	/**
	 * Only Jackson should use this constructor
	 */
	DataQueryAnswer() {
		this.data = null;
	}

	DataQueryAnswer(QueryStatus status, Collection<String> data) {
		super(status);
		this.data = data;
	}

	/**
	 * Binding for Jackson serialization
	 *
	 * @return Data
	 */
	public Collection<String> getObjects() {
		return data;
	}

	/**
	 * Private binding for Jackson deserialization
	 *
	 * @param objects Data to set
	 */
	void setObjects(Collection<String> objects) {
		this.data = objects;
	}

	@Override
	public boolean isCarryingData() {
		return true;
	}

	@Override
	public Collection<String> getData() {
		// public implementation for users
		return data;
	}
}
