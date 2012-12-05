package cz.cuni.mff.d3s.been.core.protocol.pojo;

public class NodeInfo {
	
	public enum NodeType {
		HOST_RUNTIME
	}

	public String uuid;
	
	public NodeType hostRuntime;
	
	/* FIXME
	 * 
	 * ??
	 * available hardware info
	 * ??
	 * available software info
	 * ??
	 * registered
	 * ??
	 * running tasks
	 * ??
	 * last hour errors
	 * ??
	 * 
	 */
}
