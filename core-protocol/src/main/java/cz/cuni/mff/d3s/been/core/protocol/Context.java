package cz.cuni.mff.d3s.been.core.protocol;

public enum Context {

	GLOBAL_TOPIC("BEEN_GLOBAL_TOPIC", false);

	private String name;

	private boolean nodeSpecific;

	private Context(String name, boolean isNodeSpecific) {
		this.name = name;
		this.nodeSpecific = isNodeSpecific;
	}

	public boolean isNodeSpecific() {
		return nodeSpecific;
	}

	public String getName() {
		return name;
	}

}
