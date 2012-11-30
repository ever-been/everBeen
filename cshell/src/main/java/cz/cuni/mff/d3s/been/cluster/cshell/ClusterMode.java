package cz.cuni.mff.d3s.been.cluster.cshell;

import com.hazelcast.core.*;
import cz.cuni.mff.d3s.been.hostruntime.HostRuntimeInfo;
import jline.console.ConsoleReader;

import java.util.Collection;
import java.util.Set;

import static cz.cuni.mff.d3s.been.cluster.Names.*;

/**
 * @author Martin Sixta
 */
public abstract class ClusterMode extends Mode {

	private HazelcastInstance hazelcastInstance;

	protected HazelcastInstance getInstance() {
		return hazelcastInstance;
	}

	protected void setInstance(HazelcastInstance instance) {
		this.hazelcastInstance = instance;
	}

	protected final Cluster getCluster() {
		assert hazelcastInstance != null;
		return hazelcastInstance.getCluster();
	}

	protected final Set<Member> getMembers() {
		assert hazelcastInstance != null;
		return getCluster().getMembers();
	}


	private enum Action {
		CONNECT, DISCONNECT, BREAK, LIST, MEMBERS, STATUS, MAP, ECHO;
	}

	private static String[] getActionStrings() {
		Action[] enumValues = Action.values();

		String[] stringValues = new String[enumValues.length];

		for (int i = 0; i < enumValues.length; ++i) {
			stringValues[i] = enumValues[i].name().toLowerCase();

		}

		return stringValues;

	}

	public ClusterMode(ConsoleReader reader, String prompt) {
		super(reader, prompt, getActionStrings());

	}

	@Override
	protected Mode takeAction(String[] args) {
		if (args != null && args.length > 0) {
			Action action = Action.valueOf(args[0].toUpperCase());

			switch (action) {
				case CONNECT:
					connect(args);
					break;
				case DISCONNECT:
					disconnect();
					break;
				case BREAK:
					disconnect();
					return new DefaultMode(reader);
				case LIST:
					list(args);
					break;
				case MEMBERS:
					listMembers(args);
					break;
				case STATUS:
					status(args);
					break;
				case MAP:
					map(args);
					break;
				case ECHO:
					echo(args);
					break;
				default:
					out.println("Not implemented");
					break;

			}

		}
		return this;
	}

	private void echo(String[] args) {
		if (hazelcastInstance == null) {
			throw new IllegalArgumentException("Not connected!");
		}

		if (args.length != 2) {
			throw new IllegalArgumentException("echo message");
		}


		IMap<Integer, String> taskMap = hazelcastInstance.getMap(BEEN_TASK_MAP_NAME);
		taskMap.put(args[1].hashCode(), args[1]);

	}

	private void map(String[] args) {
		if (hazelcastInstance == null) {
			throw new IllegalArgumentException("Not connected!");
		}

		IMap<String, HostRuntimeInfo> hrMap = hazelcastInstance.getMap(HOSTRUNTIME_MAP_NAME);

		for (String key: hrMap.keySet()) {
			out.println(hrMap.get(key));

		}
	}

	protected void listMembers(String[] args) {
		if (hazelcastInstance == null) {
			throw new IllegalArgumentException("Not connected!");
		}

		for (Member member : getMembers()) {
			out.println(member);
		}

	}

	protected void list(String[] args) {

		if (hazelcastInstance == null) {
			throw new IllegalArgumentException("Not connected!");
		}
		if (args == null || args.length == 0 || args.length > 2) {
			throw new IllegalArgumentException("Bad arguments for list");
		}


		Instance.InstanceType instanceType = null;

		if (args.length == 2) {
			try {
				instanceType = Instance.InstanceType.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException e) {


				throw new IllegalArgumentException(getInstanceTypesDebugString());
			}
		}

		printInstances(instanceType);
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
		Collection<Instance> instances = hazelcastInstance.getInstances();
		for (Instance instance : instances) {
			if (instanceType == null || instance.getInstanceType() == instanceType) {
				out.printf("%s [%s]\n", instance.getInstanceType().toString(), instance.getId());
			}
		}

	}


	protected abstract void connect(String[] args);

	protected abstract void status(String[] args);

	private void disconnect() {
		if (hazelcastInstance != null) {
			hazelcastInstance.getLifecycleService().shutdown();
		}
	}
}
