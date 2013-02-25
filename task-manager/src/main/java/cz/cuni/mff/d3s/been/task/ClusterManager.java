package cz.cuni.mff.d3s.been.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.HazelcastInstance;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.core.ClusterUtils;
import cz.cuni.mff.d3s.been.core.TasksUtils;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;

/**
 * 
 * TODO: race conditions 1) key ownership changes before registering the
 * membershipListener * need to rescan local keys 2) client disconnect before
 * registering the clientListener * scan connected host runtimes ?
 * 
 * @author Martin Sixta
 */
final class ClusterManager implements IClusterService {
	private final HazelcastInstance hazelcastInstance;
	private final LocalTaskListener localTaskListener;
	private final MembershipListener membershipListener;
	private final ClientListener clientListener;
	private final TasksUtils tasksUtils;
	private final TaskEntries taskEntries;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public ClusterManager(HazelcastInstance hazelcastInstance, TasksUtils tasksUtils, TaskEntries taskEntries) {
		this.hazelcastInstance = hazelcastInstance;
		this.tasksUtils = tasksUtils;
		this.taskEntries = taskEntries;
		localTaskListener = new LocalTaskListener(tasksUtils, taskEntries);
		membershipListener = new MembershipListener();
		clientListener = new ClientListener();

	}

	@Override
	public void start() {
		localTaskListener.start();
		membershipListener.start();
		clientListener.start();

		scheduler.scheduleAtFixedRate(new LocalKeyScanner(tasksUtils, taskEntries), 5, 5, TimeUnit.SECONDS);

		System.out.println("My ID is: " + ClusterUtils.getId());

	}

	@Override
	public void stop() {
		clientListener.stop();
		membershipListener.stop();
		localTaskListener.stop();
		scheduler.shutdown();
	}

}
