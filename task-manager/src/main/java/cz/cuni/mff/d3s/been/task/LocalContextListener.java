package cz.cuni.mff.d3s.been.task;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.context.TaskContexts;
import cz.cuni.mff.d3s.been.core.task.TaskContextEntry;
import cz.cuni.mff.d3s.been.mq.IMessageSender;
import cz.cuni.mff.d3s.been.task.msg.TaskMessage;

/**
 * @author Martin Sixta
 */
final class LocalContextListener extends TaskManagerService implements EntryListener<String, TaskContextEntry> {

	private final TaskContexts contexts;
	private final IMap<String, TaskContextEntry> contextsMap;
	private final ClusterContext clusterCtx;
	private final PersistentContextStateRegistrar persistentStateRegistrar;
	private IMessageSender<TaskMessage> sender;

	LocalContextListener(ClusterContext clusterCtx) {
		this.clusterCtx = clusterCtx;
		this.contexts = clusterCtx.getTaskContexts();
		this.contextsMap = contexts.getTaskContextsMap();
		this.persistentStateRegistrar = new PersistentContextStateRegistrar(clusterCtx);
	}

	@Override
	public void start() throws ServiceException {
		sender = createSender();
		contextsMap.addLocalEntryListener(this);
	}

	@Override
	public void stop() {
		contextsMap.removeEntryListener(this);
		sender.close();
	}

	@Override
	public synchronized void entryAdded(EntryEvent<String, TaskContextEntry> event) {
		final TaskContextEntry entry = event.getValue();
		contexts.runContext(entry.getId());
	}

	@Override
	public void entryRemoved(EntryEvent<String, TaskContextEntry> event) {}

	@Override
	public void entryUpdated(EntryEvent<String, TaskContextEntry> event) {
		final TaskContextEntry entry = event.getValue();
		persistentStateRegistrar.processContextStateChange(entry.getId(), entry.getBenchmarkId(), entry.getContextState());
	}

	@Override
	public void entryEvicted(EntryEvent<String, TaskContextEntry> event) {}
}
