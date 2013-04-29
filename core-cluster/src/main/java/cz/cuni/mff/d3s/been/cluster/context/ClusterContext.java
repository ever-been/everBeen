package cz.cuni.mff.d3s.been.cluster.context;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.hazelcast.config.Config;
import com.hazelcast.core.*;
import cz.cuni.mff.d3s.been.cluster.Names;

/**
 * 
 * Utility class for often used Hazelcast functions.
 * 
 * 
 * @author Martin Sixta
 */
public class ClusterContext {

	private final Maps maps;
	private final Runtimes runtimes;
	private final Tasks tasks;
	private final TaskContexts taskContexts;
	private final Topics topics;
	private final Services services;
	private final HazelcastInstance hcInstance;

	public ClusterContext(HazelcastInstance hcInstance) {
		this.hcInstance = hcInstance;
		this.maps = new Maps(this);
		this.runtimes = new Runtimes(this);
		this.tasks = new Tasks(this);
		this.taskContexts = new TaskContexts(this);
		this.topics = new Topics(this);
		this.services = new Services(this);
	}

	public ICountDownLatch getCountDownLatch(String name) {
		return getInstance().getCountDownLatch(name);
	}
	public Tasks getTasks() {
		return tasks;
	}

	public TaskContexts getTaskContexts() {
		return taskContexts;
	}

	public Maps getMaps() {
		return maps;
	}

	public Runtimes getRuntimes() {
		return runtimes;
	}

	public Topics getTopics() {
		return topics;
	}

	public Services getServices() {
		return services;
	}

	public HazelcastInstance getInstance() {
		return hcInstance;
	}

	/**
	 * Set of current members of the cluster. Returning set instance is not
	 * modifiable. Every member in the cluster has the same member list in the
	 * same order. First member is the oldest member.
	 * 
	 * @return current members of the cluster
	 */
	public Set<Member> getMembers() {

		return getInstance().getCluster().getMembers();

	}

	/**
	 * Returns UUID of this member.
	 * 
	 * @return UUID of this member.
	 */
	public String getId() {
		return getLocalMember().getUuid();
	}

	/**
	 * 
	 * Returns port of this member.
	 * 
	 * Use {@link cz.cuni.mff.d3s.been.core.ClusterContext#getInetSocketAddress()}
	 * instead.
	 * 
	 * @return port of this member
	 */
	@Deprecated
	public int getPort() {
		return getLocalMember().getInetSocketAddress().getPort();

	}

	/**
	 * Returns host name of this member.
	 * 
	 * Use {@link cz.cuni.mff.d3s.been.core.ClusterContext#getInetSocketAddress()}
	 * instead.
	 * 
	 * @return host name of this member
	 */
	@Deprecated
	public String getHostName() {
		return getLocalMember().getInetSocketAddress().getHostName();
	}

	/**
	 * Returns the InetSocketAddress of this member.
	 * 
	 * @return InetSocketAddress of this member
	 */
	public InetSocketAddress getInetSocketAddress() {
		return getLocalMember().getInetSocketAddress();
	}

	public Member getLocalMember() {
		return getCluster().getLocalMember();
	}

	public Cluster getCluster() {
		return getInstance().getCluster();
	}

	public ClientService getClientService() {
		return getInstance().getClientService();
	}

	public <E> IQueue<E> getQueue(String name) {
		return getInstance().getQueue(name);
	}

	public <E> ITopic<E> getTopic(String name) {
		return getInstance().getTopic(name);
	}

	public <K, V> IMap<K, V> getMap(String name) {
		return getInstance().getMap(name);
	}

	public <K, V> MultiMap<K, V> getMultiMap(String name) {
		return getInstance().getMultiMap(name);
	}

	public <E> IList<E> getList(String name) {
		return getInstance().getList(name);
	}

	public Transaction getTransaction() {
		return getInstance().getTransaction();
	}

	public AtomicNumber getAtomicNumber(String name) {
		return getInstance().getAtomicNumber(name);
	}

	/**
	 * 
	 * Returns all queue, map, set, list, topic, lock, multimap instances created
	 * by Hazelcast.
	 * 
	 * @return the collection of instances created by Hazelcast.
	 */
	public Collection<Instance> getInstances() {
		return getInstance().getInstances();
	}

	/**
	 * 
	 * Returns instances of specified type created by Hazelcast.
	 * 
	 * @return the collection of instances of specified type created by Hazelcast.
	 */

	public Collection<Instance> getInstances(Instance.InstanceType instanceType) {
		Collection<Instance> instances = new ArrayList<>();

		for (Instance instance : getInstances()) {
			if (instance.getInstanceType() == instanceType) {
				instances.add(instance);
			}
		}

		return instances;
	}

	/**
	 * 
	 * Checks for existence of an instance (queue, map, set, list, topic, lock,
	 * multimap).
	 * 
	 * @param instanceType
	 *          type of the instance
	 * @param name
	 *          name of the instance
	 * @return true if the instance exists, false otherwise
	 */
	public boolean containsInstance(Instance.InstanceType instanceType,
			String name) {

		for (Instance instance : getInstances(instanceType)) {
			boolean isName = instance.getId().toString().endsWith(":" + name);
			if (isName) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the configuration of this Hazelcast instance.
	 * 
	 * @return configuration of this Hazelcast instance
	 */
	public Config getConfig() {
		return getInstance().getConfig();
	}

	/**
	 * Registers a service.
	 * 
	 * @param serviceName
	 *          name of the service
	 * @param serviceInfo
	 *          information about the service (service specific)
	 * 
	 *          TODO: check for concurency issues
	 */
	public void registerService(String serviceName, Object serviceInfo) {
		getMap(Names.SERVICES_MAP_NAME).put(serviceName, serviceInfo);
	}

	/**
	 * Un-registers a service.
	 * 
	 * 
	 * @param serviceName
	 *          name of the service
	 * 
	 *          TODO: check for concurency issues
	 */
	public void unregisterService(String serviceName) {
		getMap(Names.SERVICES_MAP_NAME).remove(serviceName);
	}

}
