package cz.cuni.mff.d3s.been.cluster.context;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.Config;
import com.hazelcast.core.*;

import cz.cuni.mff.d3s.been.cluster.NodeType;
import cz.cuni.mff.d3s.been.core.service.ServiceInfo;

/**
 * Utility class for often used Hazelcast functions.
 * 
 * @author Martin Sixta
 */
public class ClusterContext {

	/** utility class for Hazelcast maps manipulation */
	private final Maps maps;

	/** utility class for host runtimes handling */
	private final Runtimes runtimes;

	/** utility class for tasks */
	private final Tasks tasks;

	/** utility class for task contexts */
	private final TaskContexts taskContexts;

	/** utility class for Hazelcast topics handling */
	private final Topics topics;

	/** utility class for BEEN services */
	private final Services services;

	/** utility class for dealing with benchmarks */
	private final Benchmarks benchmarks;

	/**  utility class for persistence */
	private final Persistence persistence;

	/** the current Hazelcast node instance */
	private final HazelcastInstance hcInstance;

	/** does this cluster context use a HazelcastClient instead of a full node? */
	private final boolean usesHazelcastClient;

	/** configurable properties */
	private final Properties properties;

	/** scheduler for running periodic jobs */
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	/**
	 * Default constructor, creates a BEEN cluster context instance with the
	 * specified instance of Hazelcast and the specified configurable properties.
	 * 
	 * @param hcInstance
	 *          the Hazelcast instance to use
	 * @param properties
	 *          the properties to use
	 */
	public ClusterContext(HazelcastInstance hcInstance, Properties properties) {
		this.hcInstance = hcInstance;
		this.properties = properties;

		this.maps = new Maps(this);
		this.runtimes = new Runtimes(this);
		this.tasks = new Tasks(this);
		this.taskContexts = new TaskContexts(this);
		this.topics = new Topics(this);
		this.services = new Services(this);
		this.benchmarks = new Benchmarks(this);
		this.persistence = new Persistence(this);

		if (hcInstance instanceof HazelcastClient) {
			this.usesHazelcastClient = true;
		} else {
			usesHazelcastClient = false;
		}
	}

	/**
	 * Returns a Hazelcast latch with the specified name. If such a latch does not
	 * exist, it will be created.
	 * 
	 * @param name
	 *          name of the latch
	 * @return the latch with the specified name
	 */
	public ICountDownLatch getCountDownLatch(String name) {
		return getInstance().getCountDownLatch(name);
	}

	/**
	 * Returns the instance of the {@link Tasks} utility class.
	 * 
	 * @return the tasks utility class
	 */
	public Tasks getTasks() {
		return tasks;
	}

	/**
	 * Returns the instance of the {@link TaskContexts} utility class.
	 * 
	 * @return the task contexts utility class
	 */
	public TaskContexts getTaskContexts() {
		return taskContexts;
	}

	/**
	 * Returns an instance of the {@link Maps} utility class.
	 * 
	 * @return the maps utility class
	 */
	public Maps getMaps() {
		return maps;
	}

	/**
	 * Returns an instance of the {@link Runtimes} utility class.
	 * 
	 * @return the runtimes utility class
	 */
	public Runtimes getRuntimes() {
		return runtimes;
	}

	/**
	 * Returns an instance of the {@link Topics} utility class.
	 * 
	 * @return the topics utility class
	 */
	public Topics getTopics() {
		return topics;
	}

	/**
	 * Returns an instance of the {@link Benchmarks} utility class.
	 * 
	 * @return the benchmarks utility class
	 */
	public Benchmarks getBenchmarks() {
		return benchmarks;
	}

	/**
	 * Returns an instance of the {@link Services} utility class.
	 * 
	 * @return the services utility class
	 */
	public Services getServices() {
		return services;
	}

	/**
	 * Returns an instance of the {@link Persistence} utility class.
	 * 
	 * @return the persistence utility class
	 */
	public Persistence getPersistence() {
		return persistence;
	}

	/**
	 * Returns the currently used Hazelcast instance that works as a connection to
	 * the BEEN cluster.
	 * 
	 * @return the current Hazelcast instance
	 */
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
	 * Returns type of the current instance
	 * 
	 * @return instance type
	 */
	public NodeType getInstanceType() {
		return cz.cuni.mff.d3s.been.cluster.Instance.getNodeType();
	}

	/**
	 * Returns the InetSocketAddress of this node.
	 * <p/>
	 * In case of DATA/LITE member it's the address the node is connected to the
	 * cluster.
	 * <p/>
	 * In case of a NATIVE node hostname with 0 port is returned.
	 * 
	 * @return InetSocketAddress Of the cluster member or hostname of a native
	 *         client
	 */
	public InetSocketAddress getInetSocketAddress() {
		if (getInstanceType() == NodeType.NATIVE) {
			try {
				return new InetSocketAddress(InetAddress.getLocalHost(), 0);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return new InetSocketAddress("localhost", 0);
			}
		} else {
			return getCluster().getLocalMember().getInetSocketAddress();
		}
	}

	/**
	 * Returns the Hazelcast {@link Cluster} object that represents the currently
	 * connected cluster.
	 * 
	 * @return the cluster object
	 */
	public Cluster getCluster() {
		return getInstance().getCluster();
	}

	/**
	 * Returns the Hazelcast {@link ClientService} object that offers services for
	 * handling client connections and disconnections.
	 * 
	 * @return the client service object
	 */
	public ClientService getClientService() {
		return getInstance().getClientService();
	}

	/**
	 * Returns a Hazelcast queue with the specified name. If such a queue does not
	 * exist, it will be created.
	 * 
	 * @param name
	 *          name of the queue
	 * @param <E>
	 *          type of the queue items
	 * @return the queue with the specified name
	 */
	public <E> IQueue<E> getQueue(String name) {
		return getInstance().getQueue(name);
	}

	/**
	 * Returns a Hazelcast topic with the specified name. If such a topic does not
	 * exist, it will be created.
	 * 
	 * @param name
	 *          name of the topic
	 * @param <E>
	 *          type of the topic items
	 * @return the topic with the specified name
	 */
	public <E> ITopic<E> getTopic(String name) {
		return getInstance().getTopic(name);
	}

	/**
	 * Returns a Hazelcast map with the specified name. If such a map does not
	 * exist, it will be created.
	 * 
	 * @param name
	 *          name of the map
	 * @param <K>
	 *          type of the map keys
	 * @param <V>
	 *          type of the map values
	 * @return the map with the specified name
	 */
	public <K, V> IMap<K, V> getMap(String name) {
		return getInstance().getMap(name);
	}

	/**
	 * Returns a Hazelcast multimap with the specified name. If such a multimap
	 * does not exist, it will be created.
	 * 
	 * @param name
	 *          name of the multimap
	 * @param <K>
	 *          type of the multimap keys
	 * @param <V>
	 *          type of the multimap values
	 * @return the multimap with the specified name
	 */
	public <K, V> MultiMap<K, V> getMultiMap(String name) {
		return getInstance().getMultiMap(name);
	}

	/**
	 * Returns a Hazelcast list with the specified name. If such a list does not
	 * exist, it will be created.
	 * 
	 * @param name
	 *          name of the list
	 * @param <E>
	 *          type of the list items
	 * @return the list with the specified name
	 */
	public <E> IList<E> getList(String name) {
		return getInstance().getList(name);
	}

	/**
	 * Returns the Hazelcast transaction object for the current thread.
	 * 
	 * @return the transaction object
	 */
	public Transaction getTransaction() {
		return getInstance().getTransaction();
	}

	/**
	 * Returns a Hazelcast atomic number with the specified name. If such an
	 * atomic number does not exist, it will be created.
	 * 
	 * @param name
	 *          the name of the atomic number
	 * @return the atomic number with the specified name
	 */
	public AtomicNumber getAtomicNumber(String name) {
		return getInstance().getAtomicNumber(name);
	}

	/**
	 * Returns all queue, map, set, list, topic, lock, multimap instances created
	 * by Hazelcast.
	 * 
	 * @return the collection of instances created by Hazelcast.
	 */
	public Collection<Instance> getInstances() {
		return getInstance().getInstances();
	}

	/**
	 * Returns instances of specified type created by Hazelcast.
	 * 
	 * @param instanceType
	 *          the type of the instances to list
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
	 * Checks for existence of an instance (queue, map, set, list, topic, lock,
	 * multimap).
	 * 
	 * @param instanceType
	 *          type of the instance
	 * @param name
	 *          name of the instance
	 * @return true if the instance exists, false otherwise
	 */
	public boolean containsInstance(Instance.InstanceType instanceType, String name) {

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
	 * @param serviceInfo
	 *          service info object to be registered
	 * @param ttlSeconds
	 *          the time-to-live in seconds after which the entry in the map
	 *          should be evicted
	 */
	// TODO: check for concurency issues
	public void storeServiceInfo(ServiceInfo serviceInfo, int ttlSeconds) {
		services.getServicesMap().put(serviceInfo.getServiceName(), serviceInfo, ttlSeconds, TimeUnit.SECONDS);
	}

	/**
	 * Un-registers a service.
	 * 
	 * @param serviceInfo
	 *          service info object to be unregistered
	 */
	// TODO: check for concurency issues
	public void removeServiceInfo(ServiceInfo serviceInfo) {
		services.getServicesMap().remove(serviceInfo.getServiceName());
	}

	/**
	 * Returns a new cluster-wide unique ID from the generator with the specified
	 * name.
	 * 
	 * @param key
	 *          name of the ID generator to use
	 * @return newly created ID
	 */
	public long generateId(String key) {
		return getInstance().getIdGenerator(key).newId();
	}

	/**
	 * Checks whether the connection to the cluster is active.
	 * 
	 * @return true if the connection is active, false otherwise
	 */
	public boolean isActive() {
		if (usesHazelcastClient) {
			return ((HazelcastClient) hcInstance).isActive();
		}

		// Otherwise hcInstance is member of cluster, so
		// ClusterCTX is connected to cluster by default
		return true;
	}

	/**
	 * Get BEEN configuration properties.
	 * 
	 * @return BEEN properties
	 */
	public Properties getProperties() {
		return new Properties(properties);
	}

	/**
	 * Schedules a runnable job to be run at a periodic interval.
	 * 
	 * @param runnable
	 *          the runnable job to be scheduled
	 * @param initialDelay
	 *          initial delay before the job will be run for the first time
	 * @param period
	 *          the period after which the job will be run again
	 * @param timeUnit
	 *          the time unit in which the period and delay are represented
	 */
	public void schedule(Runnable runnable, int initialDelay, int period, TimeUnit timeUnit) {
		scheduler.scheduleAtFixedRate(runnable, initialDelay, period, timeUnit);
	}

	/**
	 * Stops the scheduler and all scheduled tasks.
	 */
	public void stop() {
		if (!scheduler.isShutdown()) {
			scheduler.shutdown();
		}
	}
}
