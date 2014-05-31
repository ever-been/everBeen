package cz.everbeen.restapi.handlers;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.util.JsonException;
import cz.everbeen.restapi.ProtocolObjectOperation;
import cz.everbeen.restapi.protocol.ErrorObject;
import cz.everbeen.restapi.protocol.ProtocolObject;
import cz.everbeen.restapi.protocol.TaskStatus;
import cz.everbeen.restapi.protocol.TaskSubmit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The REST handler for task operation
 *
 * @author darklight
 */
@Path("/task")
public class TaskHandler extends Handler {

	private static final Logger log = LoggerFactory.getLogger(TaskHandler.class);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getState(@QueryParam("taskId") final String taskId) {
		return performAndAnswer(new ProtocolObjectOperation() {
			@Override
			public String name() {
				return "getTaskStatus";
			}

			@Override
			public ProtocolObject perform(BeenApi beenApi) throws BeenApiException {
				final TaskEntry taskEntry = beenApi.getTask(taskId);
				if (taskEntry == null) {
					return new ErrorObject("No such task");
				}
				return new TaskStatus(
					taskEntry.getBenchmarkId(),
					taskEntry.getTaskContextId(),
					taskEntry.getId(),
					taskEntry.getState().name()
				);
			}
		});
	}

	@PUT
	@Produces(PROTOCOL_OBJECT_MEDIA)
	@Consumes(MediaType.APPLICATION_JSON)
	public String run(@QueryParam("taskDescriptor") String taskDescriptorString) {
		final TaskDescriptor td;
		try {
			td = jsonUtils.deserialize(taskDescriptorString, TaskDescriptor.class);
		} catch (JsonException e) {
				return new ErrorObject(String.format(
						"Cannot deserialize a task descriptor from [%s]: %s",
						taskDescriptorString,
						e.getMessage()
				)).toString();
		}
		return performAndAnswer(new ProtocolObjectOperation() {
			@Override
			public String name() {
				return "submitTask";
			}

			@Override
			public ProtocolObject perform(BeenApi beenApi) throws BeenApiException {
				return TaskSubmit.fromId(beenApi.submitTask(td));
			}
		});
	}

	@PUT
	@Produces(PROTOCOL_OBJECT_MEDIA)
	public String run(
		@QueryParam("groupId") final String groupId,
		@QueryParam("bpkId") final String bpkId,
		@QueryParam("version") final String version,
		@QueryParam("descriptorName") final String taskDescriptorName
	) {
		final BpkIdentifier bpkIdentifier = new BpkIdentifier().withGroupId(groupId).withBpkId(bpkId).withVersion(version);

		return performAndAnswer(new ProtocolObjectOperation() {
			@Override
			public String name() {
				return "submitTaskTemplateFromBPK";
			}

			@Override
			public ProtocolObject perform(BeenApi beenApi) throws BeenApiException {
				return TaskSubmit.fromId(beenApi.submitTask(beenApi.getTaskDescriptor(bpkIdentifier, taskDescriptorName)));
			}
		});
	}
}
