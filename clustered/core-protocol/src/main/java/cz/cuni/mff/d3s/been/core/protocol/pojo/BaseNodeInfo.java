package cz.cuni.mff.d3s.been.core.protocol.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

		public int httpPort;

		public String host;
	}


	public static class HostRuntimeNodeInfo extends BaseNodeInfo {

		private List<String> runningTaskNames = new ArrayList<>();

		public HostRuntimeNodeInfo(String nodeId) {
			super(nodeId, NodeType.HOST_RUNTIME);
		}

		public final void addRunningTask(final String taskName) {
			runningTaskNames.add(taskName);
		}

		public final void removeRunningTask(final String taskName) {
			runningTaskNames.remove(taskName);
		}

		/**
		 * return unmodifiable copy of running tasks list - for adding/ removing
		 * tasks call {@link HostRuntimeNodeInfo#addRunningTask(String)} and
		 * {@link HostRuntimeNodeInfo#removeRunningTask(String)}
		 *
		 * @param taskName
		 * @return
		 */
		public final List<String> getRunningTasks() {
			return Collections.unmodifiableList(runningTaskNames);
		}

	}

}
