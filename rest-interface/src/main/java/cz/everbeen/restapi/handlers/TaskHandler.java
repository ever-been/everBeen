package cz.everbeen.restapi.handlers;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.logging.TaskLogMessage;
import cz.everbeen.restapi.ProtocolObjectOperation;
import cz.everbeen.restapi.protocol.ErrorObject;
import cz.everbeen.restapi.protocol.ProtocolObject;
import cz.everbeen.restapi.protocol.ProtocolObjectFactory;
import cz.everbeen.restapi.protocol.TaskSubmit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

/**
 * The REST handler for task operation
 *
 * @author darklight
 */
@Path("/task")
public class TaskHandler extends Handler {

	private static final Logger log = LoggerFactory.getLogger(TaskHandler.class);

	@GET
	@Path("/{taskId}")
	@Produces(PROTOCOL_OBJECT_MEDIA)
	public String getState(@PathParam("taskId") final String taskId) {
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
				return ProtocolObjectFactory.taskStatus(taskEntry);
			}
		});
	}

	@GET
	@Path("/{taskId}/logs")
	@Produces(PROTOCOL_OBJECT_MEDIA)
	public String getLogs(@PathParam("taskId") final String taskId) {
		return performAndAnswer(new ProtocolObjectOperation() {
			@Override
			public String name() {
				return "getTaskLogs";
			}

			@Override
			public ProtocolObject perform(BeenApi beenApi) throws BeenApiException {
				final Collection<TaskLogMessage> taskLogs = beenApi.getLogsForTask(taskId);
				return ProtocolObjectFactory.taskLogs(taskLogs);
			}
		});
	}

	@GET
	@Produces(PROTOCOL_OBJECT_MEDIA)
	public String getTasks() {
		return performAndAnswer(new ProtocolObjectOperation() {
			@Override
			public String name() {
				return "listTasks";
			}

			@Override
			public ProtocolObject perform(BeenApi beenApi) throws BeenApiException {
				return ProtocolObjectFactory.taskList(beenApi.getTasks());
			}
		});
	}

//	@PUT
//	@Produces(PROTOCOL_OBJECT_MEDIA)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public String run(@QueryParam("taskDescriptor") String taskDescriptorString) {
//		final TaskDescriptor td;
//		try {
//			td = jsonUtils.deserialize(taskDescriptorString, TaskDescriptor.class);
//		} catch (JsonException e) {
//				return new ErrorObject(String.format(
//						"Cannot deserialize a task descriptor from [%s]: %s",
//						taskDescriptorString,
//						e.getMessage()
//				)).toString();
//		}
//		return performAndAnswer(new ProtocolObjectOperation() {
//			@Override
//			public String name() {
//				return "submitTask";
//			}
//
//			@Override
//			public ProtocolObject perform(BeenApi beenApi) throws BeenApiException {
//				return TaskSubmit.fromContextId(beenApi.submitTask(td));
//			}
//		});
//	}

	@PUT
	@Path("/{groupId}/{bpkId}/{version}/{descriptorName}")
	@Produces(PROTOCOL_OBJECT_MEDIA)
	public String run(
		@PathParam("groupId") final String groupId,
		@PathParam("bpkId") final String bpkId,
		@PathParam("version") final String version,
		@PathParam("descriptorName") final String taskDescriptorName
	) {
		return performAndAnswer(getRunTask(groupId, bpkId, version, taskDescriptorName));
	}

	@PUT
	@Path("/{groupId}/{bpkId}/{version}/{descriptorName}/withParams")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(PROTOCOL_OBJECT_MEDIA)
	public String runWithParams(
			@PathParam("groupId") final String groupId,
			@PathParam("bpkId") final String bpkId,
			@PathParam("version") final String version,
			@PathParam("descriptorName") final String taskDescriptorName
	) {
		return performAndAnswer(getRunTask(groupId, bpkId, version, taskDescriptorName));
	}

	private ProtocolObjectOperation getRunTask(
		final String groupId,
		final String bpkId,
		final String version,
		final String taskDescriptorName
	) {
		final BpkIdentifier bpkIdentifier = new BpkIdentifier().withGroupId(groupId).withBpkId(bpkId).withVersion(version);

		return new ProtocolObjectOperation() {
			@Override
			public String name() {
				return "submitTaskTemplateFromBPK";
			}

			@Override
			public ProtocolObject perform(BeenApi beenApi) throws BeenApiException {
				final TaskDescriptor td = beenApi.getTaskDescriptor(bpkIdentifier, ProtocolObjectFactory.revertTaskDescriptorId(taskDescriptorName));
				log.debug("Retrieved task descriptor [{}] for task [{}/{}]", td, bpkIdentifier, taskDescriptorName);
				return TaskSubmit.fromContextId(beenApi.submitTask(td));
			}
		};
	}
}
