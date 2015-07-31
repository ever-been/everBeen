package cz.cuni.mff.d3s.been.taskapi;

import cz.cuni.mff.d3s.been.results.ResultMapping;

import java.util.Collection;
import java.util.Map;

/**
 * Generic dataset containing data and RTTI.
 *
 * @author darklight
 */
public class DataSet {
	private final ResultMapping resultMapping;
	private final Collection<Map<String, Object>> data;

	public DataSet(ResultMapping resultMapping, Collection<Map<String, Object>> data) {
		this.resultMapping = resultMapping;
		this.data = data;
	}

	public Collection<Map<String, Object>> getData() {
		return data;
	}

	public ResultMapping getResultMapping() {
		return resultMapping;
	}
}
