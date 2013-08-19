package cz.cuni.mff.d3s.been.client;

import java.util.Collection;
import java.util.Map;

import jline.console.ConsoleReader;

import com.hazelcast.core.Instance;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import cz.cuni.mff.d3s.been.api.BeenApiFactory;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.debugassistant.DebugListItem;
import cz.cuni.mff.d3s.been.logging.TaskLogMessage;
import cz.cuni.mff.d3s.been.persistence.DAOException;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * @author Martin Sixta
 */
class ClusterMode extends AbstractMode {

	private final ClusterContext clusterContext;
	private final BeenApi api;
	private final JSONUtils jsonUtils;

	private enum Action {
		HELP, TASKS, TASKCONTEXTS, RUNTIMES, BENCHMARKS, BREAK, INSTANCES, BPKS, LOGS, DEBUG;
	}

	private static String[] getActionStrings() {
		Action[] enumValues = Action.values();

		String[] stringValues = new String[enumValues.length];

		for (int i = 0; i < enumValues.length; ++i) {
			stringValues[i] = enumValues[i].name().toLowerCase();

		}

		return stringValues;

	}

	public ClusterMode(ConsoleReader reader, ClusterContext clusterContext) {
		super(reader, "> ", getActionStrings());
		this.clusterContext = clusterContext;
		this.api = BeenApiFactory.fromContext(clusterContext);
		this.jsonUtils = JSONUtils.newInstance();
	}

	@Override
	public AbstractMode takeAction(String[] args) throws DAOException, BeenApiException {

		assert args != null;
		assert args.length > 0;

		Action action = Action.valueOf(args[0].toUpperCase());

		switch (action) {
			case HELP:
				handleHelp(args);
				break;
			case TASKS:
				handleTasks(args);
				break;
			case TASKCONTEXTS:
				handleTaskContexts(args);
				break;
			case BENCHMARKS:
				handleBenchmarks(args);
				break;
			case RUNTIMES:
				handleRuntimes(args);
				break;
			case INSTANCES:
				handleInstances(args);
				break;
			case LOGS:
				handleLogs(args);
				break;
			case BPKS:
				handleBpks(args);
				break;
			case DEBUG:
				handleDebug(args);
				break;
			case BREAK:
				System.exit(0);
				break;
		}

		return this;
	}

	private void handleBpks(String[] args) throws BeenApiException {
		for (BpkIdentifier bpk : api.getBpks()) {
			out.println("Group ID: " + bpk.getGroupId());
			out.println("Bpk ID: " + bpk.getBpkId());
			out.println("Version: " + bpk.getVersion());
			out.println("-----------------------------------");
		}
	}

	private void handleDebug(String[] args) throws BeenApiException {
		for (DebugListItem item : api.getDebugWaitingTasks()) {
			out.printf(
					"id: %s, host: %s, port: %s, suspended: %s\n",
					item.getTaskId(),
					item.getHostName(),
					item.getDebugPort(),
					item.isSuspended());
		}
	}

	private void handleLogs(String[] args) throws DAOException, BeenApiException {

		if (args.length == 2) {
			Collection<TaskLogMessage> logs = null;
			logs = api.getLogsForTask(args[1]);
			for (TaskLogMessage msg : logs) {
				try {
					out.printf("\t%s\n", jsonUtils.serialize(msg));
				} catch (JsonException e) {
					out.println("\tERROR: Cannot deserialize the message!");
				}
			}
		}

	}

	private void handleHelp(String[] args) {
		out.println("Available commands:");
		out.println("help");
		out.println("tasks [task-id]");
		out.println("taskcontexts");
		out.println("runtimes");
		out.println("instances [instance-type] [instance-name]");
		out.println("logs taskId");
	}

	private void handleRuntimes(String[] args) throws BeenApiException {
		if (args.length == 1) {
			Collection<RuntimeInfo> runtimes = api.getRuntimes();
			for (RuntimeInfo runtime : runtimes) {
				out.println("Runtime ID: " + runtime.getId());
				out.println("Operating system: " + runtime.getOperatingSystem().getName());
				out.printf("Exclusivity: %s\n", runtime.getExclusivity());
				out.printf("Exclusive ID: %s\n", runtime.getExclusiveId());
				out.printf("Tasks: %d\n", runtime.getTaskCount());
				out.println("-----------------------------------");
			}
		}
	}

	private void handleTasks(String[] args) throws BeenApiException {
		if (args.length == 1) {
			Collection<TaskEntry> entries = api.getTasks();

			for (TaskEntry entry : entries) {
				out.println("Task ID: " + entry.getId());
				out.println("Task Context ID: " + entry.getTaskContextId());
				out.println("Runtime ID: " + entry.getRuntimeId());
				out.println("Name: " + entry.getTaskDescriptor().getName());
				out.println("BPK ID: " + entry.getTaskDescriptor().getBpkId());
				out.println("State: " + entry.getState());
				if (entry.isSetArgs()) {
					out.println("Arguments: ");
					for (String arg : entry.getArgs().getArg()) {
						out.printf("\t%s\n", arg);
					}
				}

				if (entry.isSetWorkingDirectory()) {
					out.println("Working Directory: " + entry.getWorkingDirectory());
				}
				out.println("-----------------------------------");
			}
		} else if (args.length == 2) {
			TaskEntry entry = api.getTask(args[1]);
			out.println(TaskEntries.toXml(entry));
		}
	}

	private void handleTaskContexts(String[] args) throws BeenApiException {
		if (args.length == 1) {
			Collection<TaskContextEntry> entries = api.getTaskContexts();
			for (TaskContextEntry entry : entries) {
				out.println("Task Context ID: " + entry.getId());
				if (entry.isSetTaskContextDescriptor()) {
					out.println("Name: " + entry.getTaskContextDescriptor().getName());
				}
				out.println("Contained tasks: ");
				for (String taskId : entry.getContainedTask()) {
					out.println("  " + taskId);
				}
				out.println("-----------------------------------");
			}
		}
	}

	private void handleBenchmarks(String[] args) throws BeenApiException {
		if (args.length == 1) {
			Collection<BenchmarkEntry> entries = api.getBenchmarks();

			for (BenchmarkEntry entry : entries) {
				out.println("Benchmark ID: " + entry.getId());
				out.println("Generator Task ID: " + entry.getGeneratorId());
				out.println("Contained contexts: ");
				try {
					for (TaskContextEntry tcEntry : api.getTaskContextsInBenchmark(entry.getId())) {
						out.println("  " + tcEntry.getId());
					}
				} catch (BeenApiException e) {
					out.printf(String.format(
							"ERROR: Failed to collect task contexts for benchmark with id '%s'. Reason: %s",
							entry.getId(),
							e.getMessage()));
					return;
				}
				out.println("-----------------------------------");
			}
		}
	}

	protected void handleInstances(String[] args) {

		Instance.InstanceType instanceType = null;

		if (args.length == 3) {
			if (args[1].toUpperCase().equals("MAP")) {
				Map<Object, Object> m = clusterContext.getMap(args[2]);
				for (Map.Entry<Object, Object> entry : m.entrySet()) {
					out.println(entry.getKey() + ": " + entry.getValue());
				}
			} else {
				throw new IllegalArgumentException("Only 'MAP' is supported.");
			}
			return;
		}

		if (args.length == 2) {
			try {
				instanceType = Instance.InstanceType.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(getInstanceTypesDebugString());
			}
		}

		Collection<Instance> instances;
		if (instanceType == null) {
			instances = clusterContext.getInstances();
		} else {
			instances = clusterContext.getInstances(instanceType);
		}

		for (Instance instance : instances) {
			out.printf("%s [%s]\n", instance.getInstanceType().toString(), instance.getId());
		}

	}

	private String getInstanceTypesDebugString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Invalid list action! Choose one of [");
		for (Instance.InstanceType type : Instance.InstanceType.values()) {
			sb.append(" ");
			sb.append(type.toString());
		}
		sb.append(" ]");

		return sb.toString();

	}
}
