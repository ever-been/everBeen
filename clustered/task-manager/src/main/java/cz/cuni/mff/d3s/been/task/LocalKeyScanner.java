package cz.cuni.mff.d3s.been.task;

import com.hazelcast.core.IMap;
import com.hazelcast.core.Transaction;
import cz.cuni.mff.d3s.been.core.ClusterUtils;
import cz.cuni.mff.d3s.been.core.TasksUtils;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Scans local keys of tasks map and looks for irregularities.
 *
 * Hazelcast (as of version 2.5) is not capable to inform client application
 * when a key migrates. So the idea is to periodically scan local
 * keys and see if a key needs our attention. This covers leaving (or crashing)
 * as well as joining of data nodes.
 *
 * This method is also used by Mozilla organization in their crash analyzing
 * system.
 *
 * TODO: maybe add reference to discussion
 *
 * @author Martin Sixta
 */
public class LocalKeyScanner implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(LocalKeyScanner.class);
	private static final String ZERO_ID = "0";
	@Override
	public void run() {

		IMap<String, TaskEntry> map = TasksUtils.getTasksMap();
		String nodeId = ClusterUtils.getId();

		for(String taskId: map.localKeySet()) {


			TaskEntry entry = map.get(taskId);
			if (entry == null) {
				continue;
			}

			String ownerId = entry.isSetOwnerId() ? entry.getOwnerId() : ZERO_ID;

			if (ownerId.equals(ZERO_ID)) {
				// TODO: this can happen when:
				// 1) the Task Manager did no get the chance to schedule the task yet
				// 2) the key migrated before - crash / normal migration
				// -- we must handle the case when the original owner does not disconnect cleanly

				log.info("ZERO ID found during local key scan: " + entry.getId());
			} else if (!ownerId.equals(nodeId)) {
				// Welcome to hell ...
				Transaction txn = ClusterUtils.getTransaction();

				try {
					log.info("Task entry migrated: " + entry.getId());


					{
						txn.begin();

						TasksUtils.assertEquals(entry);

						entry.setOwnerId(nodeId);
						map.put(entry.getId(), entry);
						txn.commit();
					}
				} catch (Throwable e) {
					log.warn("Unable to change owernship of ", e);
					//quell, here it does not matter
					txn.rollback();
				}
			} else {
				if (entry.getState() == TaskState.SCHEDULED) {
					long count = ClusterUtils.getInstance().getAtomicNumber(entry.getId()).decrementAndGet();
					if (count < 1) {
						log.info("Stale task " + entry.getId() + "detected! " + count);
						// TODO: handle the situation
					}
				}

			}
		}

	}

}
