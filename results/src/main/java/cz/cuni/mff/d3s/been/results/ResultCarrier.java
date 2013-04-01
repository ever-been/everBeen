package cz.cuni.mff.d3s.been.results;

/**
 * A serialized result along with meta-information specifying how it should be
 * saved.
 * 
 * @author darklight
 * 
 */
public final class ResultCarrier {

	final ResultContainerId containerId;
	final String data;

	public ResultCarrier(ResultContainerId containerId, String data) {
		this.containerId = containerId;
		this.data = data;
	}

	public ResultContainerId getContainerId() {
		return containerId;
	}

	public String getData() {
		return data;
	}
}
