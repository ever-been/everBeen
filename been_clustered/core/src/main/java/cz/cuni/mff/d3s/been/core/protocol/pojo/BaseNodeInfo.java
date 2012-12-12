package cz.cuni.mff.d3s.been.core.protocol.pojo;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class BaseNodeInfo implements Serializable {

	public enum NodeType {
		HOST_RUNTIME, SOFTWARE_REPOSITORY
	}

	public String nodeId;

	public NodeType type;

	public BaseNodeInfo(String nodeId, NodeType type) {
		this.nodeId = nodeId;
		this.type = type;
	}

	public static class SoftwareRepositoryNodeInfo extends BaseNodeInfo {
		public SoftwareRepositoryNodeInfo(String nodeId) {
			super(nodeId, NodeType.SOFTWARE_REPOSITORY);
		}
		
		public Integer downloadPort;
	}

	public static class HostRuntimeNodeInfo extends BaseNodeInfo {
		public HostRuntimeNodeInfo(String nodeId) {
			super(nodeId, NodeType.HOST_RUNTIME);
		}
	}
	
}
