package cz.cuni.mff.d3s.been.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;
import com.hazelcast.core.Transaction;

import cz.cuni.mff.d3s.been.core.ClusterUtils;
import cz.cuni.mff.d3s.been.core.TasksUtils;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;

/**
 * 
 * Scans local keys of tasks map and looks for irregularities.
 * 
 * Hazelcast (as of version 2.5) is not capable to inform client application
 * when a key migrates. So the idea is to periodically scan local keys and see
 * if a key needs our attention. This covers leaving (or crashing) as well as
 * joining of data nodes.
 * 
 * This method is also used by Mozilla organization in their crash analyzing
 * system.
 * 
 * TODO: maybe add reference to discussion TODO: create a document describing
 * all cases taken into account instead of inline comments TODO: reformat the
 * code (after it stabilizes a bit)
 * 
 * @author Martin Sixta
 */
public class LocalKeyScanner implements Runnable {

	private final TasksUtils taskUtils;
	private final TaskEntries taskEntries;

	public LocalKeyScanner(TasksUtils taskUtils, TaskEntries taskEntries) {
		this.taskUtils = taskUtils;
		this.taskEntries = taskEntries;
	}

	private static final Logger log = LoggerFactory.getLogger(LocalKeyScanner.class);
	private static final String ZERO_ID = "0";
	@Override
	public void run() {

		IMap<String, TaskEntry> map = taskUtils.getTasksMap();
		String nodeId = ClusterUtils.getId();

		for (String taskId : map.localKeySet()) {

			TaskEntry entry = map.get(taskId);

			if (entry == null) {
				continue;
			}

			entry.getId();
			String ownerId = entry.isSetOwnerId() ? entry.getOwnerId() : ZERO_ID;

			if (ownerId.equals(ZERO_ID) || !ownerId.equals(nodeId)) {
				// The owner of the key is different, reclaim it.
				// This could be just a normal migration or something more
				// serious, like a node crashing.
				// In case of ZERE_ID it could ALSO just mean that this thread got
				// lucky -> before entryAdded

				Transaction txn = ClusterUtils.getTransaction();
				try {
					txn.begin();
					taskUtils.assertClusterEqual(entry);
					entry.setOwnerId(nodeId);
					map.put(entry.getId(), entry);

				} catch (Throwable e) {
					log.warn("Unable to change ownership of the entry: ", e);
					txn.rollback(); // OK, will get it next time if needed
				}

			} else if (entry.getState() == TaskState.SCHEDULED) {
				// Checks whether the task got response from a runtime in a timely fashion
				long count = ClusterUtils.getInstance().getAtomicNumber(entry.getId()).decrementAndGet();
				if (count < 1) {
					log.info("Stale task " + entry.getId() + "detected! " + count);
					Transaction txn = ClusterUtils.getTransaction();

					try {
						txn.begin();
						taskUtils.assertClusterEqual(entry);
						taskEntries.setState(entry, TaskState.SUBMITTED, entry.getRuntimeId() + "did not respond!");
						taskUtils.putTask(entry);
						txn.commit();

					} catch (Throwable e) {
						log.warn("Unable to update stale task: " + taskId, e);
						txn.rollback(); // OK, will get it next time if needed

					}
				}

			} else if (entry.getState() == TaskState.ABORTED) {
				// TODO: reclamation of the task entry
			}
		}

	}

}
