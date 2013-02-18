package cz.cuni.d3s.mff.been.client;

import com.hazelcast.core.Instance;
import cz.cuni.mff.d3s.been.core.ClusterUtils;
import cz.cuni.mff.d3s.been.core.RuntimesUtils;
import cz.cuni.mff.d3s.been.core.TasksUtils;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import jline.console.ConsoleReader;

import java.util.Collection;

/**
 * @author Martin Sixta
 */
class ClusterMode extends AbstractMode {

	private enum Action {
		TASKS, RUNTIMES, BREAK, INSTANCES;
	}

	private static String[] getActionStrings() {
		Action[] enumValues = Action.values();

		String[] stringValues = new String[enumValues.length];

		for (int i = 0; i < enumValues.length; ++i) {
			stringValues[i] = enumValues[i].name().toLowerCase();

		}

		return stringValues;

	}

	public ClusterMode(ConsoleReader reader) {
		super(reader, "> ", getActionStrings());

	}

	@Override
	public AbstractMode takeAction(String[] args) {

		assert args != null;
		assert args.length > 0;

		Action action = Action.valueOf(args[0].toUpperCase());

		switch (action) {
			case TASKS:
				handleTasks(args);
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

	private void handleRuntimes(String[] args) {
		if (args.length == 1) {
			Collection<RuntimeInfo> runtimes = RuntimesUtils.getRuntimes();
			for (RuntimeInfo runtime: runtimes) {
				out.println(RuntimesUtils.toXml(runtime));
			}
		}
	}

	private void handleTasks(String[] args) {
		if (args.length == 1) {
			Collection<TaskEntry> entries = TasksUtils.getTasks();
			for (TaskEntry entry: entries) {
				out.println(TasksUtils.toXml(entry));
			}
		}
	}

	protected void handleInstances(String[] args) {

		Instance.InstanceType instanceType = null;

		if (args.length == 2) {
			try {
				instanceType = Instance.InstanceType.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(getInstanceTypesDebugString());
			}
		}

		Collection<Instance> instances;
		if (instanceType == null) {
			instances = ClusterUtils.getInstances();
		} else {
			instances = ClusterUtils.getInstances(instanceType);
		}

		for (Instance instance: instances) {
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

	private void printInstances(Instance.InstanceType instanceType) {
		Collection<Instance> instances = ClusterUtils.getInstance().getInstances();
		for (Instance instance : instances) {
			if (instanceType == null || instance.getInstanceType() == instanceType) {
				out.printf("%s [%s]\n", instance.getInstanceType().toString(), instance.getId());
			}
		}
	}
}
