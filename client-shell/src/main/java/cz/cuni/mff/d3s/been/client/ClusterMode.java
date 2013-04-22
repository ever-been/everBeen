package cz.cuni.mff.d3s.been.client;

import java.util.Collection;
import java.util.Map;

import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import jline.console.ConsoleReader;

import com.hazelcast.core.Instance;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.runtime.RuntimeInfos;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * @author Martin Sixta
 */
class ClusterMode extends AbstractMode {

	private final ClusterContext clusterContext;

	private enum Action {
		HELP, TASKS, TASKCONTEXTS, RUNTIMES, BREAK, INSTANCES;
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

	}

	@Override
	public AbstractMode takeAction(String[] args) {

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
			case RUNTIMES:
				handleRuntimes(args);
				break;
			case INSTANCES:
				handleInstances(args);
				break;
			case BREAK:
				System.exit(0);
				break;
		}

		return this;

	}

	private void handleHelp(String[] args) {
		out.println("Available commands:");
		out.println("help");
		out.println("tasks [task-id]");
		out.println("taskcontexts");
		out.println("runtimes");
		out.println("instances [instance-type] [instance-name]");
	}

	private void handleRuntimes(String[] args) {
		if (args.length == 1) {
			Collection<RuntimeInfo> runtimes = clusterContext.getRuntimesUtils().getRuntimes();
			for (RuntimeInfo runtime : runtimes) {
				out.println(RuntimeInfos.toXml(runtime));
			}
		}
	}

	private void handleTasks(String[] args) {
		if (args.length == 1) {
			Collection<TaskEntry> entries = clusterContext.getTasksUtils().getTasks();
			for (TaskEntry entry : entries) {
				out.println("Task ID: " + entry.getId());
				out.println("Task Context ID: " + entry.getTaskContextId());
				out.println("Runtime ID: " + entry.getRuntimeId());
				out.println("Name: " + entry.getTaskDescriptor().getName());
				out.println("BPK ID: " + entry.getTaskDescriptor().getBpkId());
				out.println("State: " + entry.getState());
				out.println("-----------------------------------");
			}
		} else if (args.length == 2) {
			TaskEntry entry = clusterContext.getTasksUtils().getTask(args[1]);
			out.println(TaskEntries.toXml(entry));
		}
	}

	private void handleTaskContexts(String[] args) {
		if (args.length == 1) {
			Collection<TaskContextEntry> entries = clusterContext.getTaskContextsUtils().getTaskContexts();
			for (TaskContextEntry entry : entries) {
				out.println("Task Context ID: " + entry.getId());
				out.println("Name: " + entry.getTaskContextDescriptor().getName());
				out.println("Contained tasks: ");
				for (String taskId : entry.getContainedTask()) {
					out.println("  " + taskId);
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
