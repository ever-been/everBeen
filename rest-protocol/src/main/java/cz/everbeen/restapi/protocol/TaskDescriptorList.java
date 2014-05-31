package cz.everbeen.restapi.protocol;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collection;
import java.util.Collections;

/**
 * A list of task descriptor identifiers
 * @author darklight
 */
public class TaskDescriptorList implements ProtocolObject {

	@JsonProperty("ids")
	private final Collection<String> taskDescriptorIds;

	@JsonCreator
	public TaskDescriptorList(
		@JsonProperty("ids") Collection<String> taskDescriptorIds
	) {
		this.taskDescriptorIds = Collections.unmodifiableCollection(taskDescriptorIds);
	}

	public Collection<String> getIds() {
		return taskDescriptorIds;
	}
}
