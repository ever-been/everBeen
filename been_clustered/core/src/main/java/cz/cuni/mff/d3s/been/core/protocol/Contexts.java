package cz.cuni.mff.d3s.been.core.protocol;

public final class Contexts {

	public static final String GLOBAL_CONTEXT = "BEEN_GLOBAL";

	private static final String NODE_CONTEXT_TEMPLATE = "BEEN_NODE_%s";

	public static String nodeContext(String hostRuntimeId) {
		return String.format(NODE_CONTEXT_TEMPLATE, hostRuntimeId);
	}

}
