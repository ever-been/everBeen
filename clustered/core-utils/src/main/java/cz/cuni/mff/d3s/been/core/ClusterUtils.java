package cz.cuni.mff.d3s.been.core;

import com.hazelcast.core.*;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Set;

/**
 * @author Martin Sixta
 */
public class ClusterUtils {

	public static HazelcastInstance getInstance() {
		  return cz.cuni.mff.d3s.been.cluster.Instance.getInstance();
	}

	public static Set<Member> getMembers() {

		return getInstance().getCluster().getMembers();

	}

	public static boolean containsInstance(Instance.InstanceType instanceType, String name) {
		Collection<Instance> instances = getInstance().getInstances();
		for (Instance instance : instances) {
			boolean isType = instanceType == null || instance.getInstanceType() == instanceType;
			boolean isName = instance.getId().toString().endsWith(":" + name);
			if (isType && isName) {
				return true;
			}
		}

		return false;
	}

	public static String getId() {
		return getLocalMember().getUuid();
	}

	public static int getPort() {
		return getLocalMember().getInetSocketAddress().getPort();

	}

	public static String getHostName() {
		return getLocalMember().getInetSocketAddress().getHostName();
	}

	public static InetSocketAddress getInetSocketAddress() {
		return getLocalMember().getInetSocketAddress();
	}


	public static Member getLocalMember() {
		return getInstance().getCluster().getLocalMember();
	}

	public static <E> IQueue<E> getQueue(String name) {
		return getInstance().getQueue(name);
	}

	public static <E> ITopic<E> getTopic(String name) {
		return getInstance().getTopic(name);
	}

	public static <K, V> IMap<K, V> getMap(String name) {
		return getInstance().getMap(name);
	}

	public static <E> IList<E> getList(String name) {
		return getInstance().getList(name);
	}


}
