package cz.cuni.mff.d3s.been.task;

import com.hazelcast.core.HazelcastInstance;

/**
 * @author Martin Sixta
 */
final class ClusterManager implements IManager {
	private final HazelcastInstance hazelcastInstance;
	private final LocalTaskListener localTaskListener;
	private final MembershipListener membershipListener;

	public ClusterManager(final HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
		localTaskListener = new LocalTaskListener();
		membershipListener = new MembershipListener(hazelcastInstance);
	}


	@Override
	public void start() {
		localTaskListener.start();
		membershipListener.start();

		// We can miss some events before setting up the listener
		rescanLocalKeys();
	}

	@Override
	public void stop() {
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
