package cz.cuni.mff.d3s.been.task;

import com.hazelcast.core.HazelcastInstance;
import cz.cuni.mff.d3s.been.cluster.IClusterService;

/**
 *
 * TODO: race conditions
 * 1) key ownership changes before registering the membershipListener
 * 	* need to rescan local keys
 * 2) client disconnect before registering the clientListener
 *  * scan connected host runtimes ?
 *
 * @author Martin Sixta
 */
final class ClusterManager implements IClusterService {
	private final HazelcastInstance hazelcastInstance;
	private final LocalTaskListener localTaskListener;
	private final MembershipListener membershipListener;
	private final ClientListener clientListener;


	public ClusterManager(HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
		localTaskListener = new LocalTaskListener();
		membershipListener = new MembershipListener();
		clientListener = new ClientListener();
	}

	@Override
	public void start() {
		localTaskListener.start();
		membershipListener.start();
		clientListener.stop();


		rescanLocalKeys();
	}

	@Override
	public void stop() {
		clientListener.stop();
		membershipListener.stop();
		localTaskListener.stop();
	}

	/**
	 * Goes through the local keys looking for orphaned objects.
	 *
	 * These can be:
	 * 	- unowned
	 * 	- migrated
	 */
	private void rescanLocalKeys() {

	}
}
