package cz.everbeen.restapi.protocol;

import com.hazelcast.core.Member;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A factory for {@link cz.everbeen.restapi.protocol.ProtocolObject} instances
 *
 * @author darklight
 */
public final class ProtocolObjectFactory {

	private static final String BPK_ID_PATTERN = "%s:%s:%s";

	/**
	 * Create a {@link cz.everbeen.restapi.protocol.BpkList} from a collection of {@link cz.cuni.mff.d3s.been.bpk.BpkIdentifier}s
	 * @param bpkIds The BPK identifiers
	 * @return The bpk list
	 */
	public static BpkList bpkList(Collection<BpkIdentifier> bpkIds) {
		final Collection<String> tmpIds = new ArrayList<String>(bpkIds.size());
		for (BpkIdentifier bpkId: bpkIds) tmpIds.add(bpkIdToString(bpkId));
		return new BpkList(tmpIds);
	}

	/**
	 * Create a list of REST-compatible task-descriptor identifiers from the BEEN-provided descriptor map
	 * @param taskDescriptorMap Task descriptor map downloaded from BEEN cluster
	 * @return A list of REST-compatible task descriptor IDs
	 */
	public static TaskDescriptorList taskDescriptorList(Map<String,TaskDescriptor> taskDescriptorMap) {
		final List<String> tdlist = new ArrayList<String>(taskDescriptorMap.entrySet().size());
		for (String tdid: taskDescriptorMap.keySet()) {
			tdlist.add(tdid.replace('/', ':'));
		}
		return new TaskDescriptorList(tdlist);
	}

	/**
	 * Create a BEEN-compatible task descriptor ID from a REST-compatible task descriptor ID
	 * @param restCompatibleTaskDescriptorId The REST-compatible task descriptor ID
	 * @return Reverted, BEEN-compatible task descriptor ID
	 */
	public static String revertTaskDescriptorId(String restCompatibleTaskDescriptorId) {
		return restCompatibleTaskDescriptorId.replace(':', '/');
	}

	/**
	 * Create a task status from a task state
	 * @param task Task state in the cluster
	 * @return Task status response
	 */
	public static TaskStatus taskStatus(TaskEntry task) {
		return new TaskStatus(
			task.getBenchmarkId(),
			task.getTaskContextId(),
			task.getId(),
			task.getState().name()
		);
	}

	/**
	 * Convert multiple task entries from the BEEN cluster to a task list protocol object
	 * @param taskEntries Task entries from the cluster
	 * @return Task list protocol object
	 */
	public static TaskList taskList(Collection<TaskEntry> taskEntries) {
		final List<TaskStatus> tasks = new ArrayList<TaskStatus>(taskEntries.size());
		for (TaskEntry entry: taskEntries) {
			tasks.add(taskStatus(entry));
		}
		return new TaskList(tasks);
	}

	/**
	 * Convert BEEN member list to a cluster members protocol object
	 * @param members The members from the cluster
	 * @return The cluster members protocol object
	 */
	public static ClusterMembers clusterMembers(Collection<Member> members) {
		final List<ClusterMember> cmembers = new ArrayList<ClusterMember>(members.size());
		for (Member m: members) {
			cmembers.add(clusterMember(m));
		}
		return new ClusterMembers(cmembers);
	}

	/**
	 * Convert a hazelcast member to a cluster member description
	 * @param member The member to convert
	 * @return A cluster member description
	 */
	private static ClusterMember clusterMember(Member member) {
		return new ClusterMember(member.getUuid(), member.getInetSocketAddress().toString(), member.isLiteMember());
	}

	private static String bpkIdToString(BpkIdentifier bpkId) {
		return String.format(BPK_ID_PATTERN, bpkId.getGroupId(), bpkId.getBpkId(), bpkId.getVersion());
	}
}
