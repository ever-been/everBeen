package cz.cuni.mff.d3s.been.persistence;

import cz.cuni.mff.d3s.been.core.persistence.Entity;
import org.codehaus.jackson.map.ObjectReader;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A {@link QueryAnswer} that carries data
 *
 * @author darklight
 */
public class DataQueryAnswer extends SkeletalQueryAnswer {

	private final Collection<String> data;

	DataQueryAnswer(QueryStatus status, Collection<String> data) {
		super(status);
		this.data = data;
	}

	@Override
	public boolean isData() {
		return true;
	}

	@Override
	public Collection<String> getData() {
		return data;
	}
}
