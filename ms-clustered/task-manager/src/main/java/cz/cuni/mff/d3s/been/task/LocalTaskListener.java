package cz.cuni.mff.d3s.been.task;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;
import cz.cuni.mff.d3s.been.core.TasksUtils;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;

/**
 * Listens for local key events of the Task Map.
 *
 *
 * @author Martin Sixta
 */
final class LocalTaskListener implements EntryListener<String, TaskEntry> {
	private IMap<String, TaskEntry> taskMap;

	public LocalTaskListener() {
		taskMap = TasksUtils.getTasksMap();
	}

	public void start() {
		taskMap.addLocalEntryListener(this);
	}

	public void stop() {
		taskMap.removeEntryListener(this);
	}


	@Override
	public void entryAdded(EntryEvent<String, TaskEntry> event) {

		System.out.printf("Received task [%s, %s]\n", event.getKey(), event.getValue());

	}

	@Override
	public void entryRemoved(EntryEvent<String, TaskEntry> event) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void entryUpdated(EntryEvent<String, TaskEntry> event) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void entryEvicted(EntryEvent<String, TaskEntry> event) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
