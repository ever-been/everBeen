package cz.cuni.mff.d3s.been.persistence;

/**
 * Keys for the filter values
 *
 * @author darklight
 */
enum FilterValues {
	LOW_BOUND("@lo"),
	HIGH_BOUND("@hi"),
	PATTERN("@like"),
	HARD_VALUE("@eq");

	private final String key;

	private FilterValues(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
