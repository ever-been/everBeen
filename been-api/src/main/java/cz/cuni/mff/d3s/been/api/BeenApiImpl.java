package cz.cuni.mff.d3s.been.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import com.hazelcast.core.Member;
import cz.cuni.mff.d3s.been.core.persistence.Entities;
import cz.cuni.mff.d3s.been.core.task.*;
import cz.cuni.mff.d3s.been.logging.ServiceLogMessage;
import cz.cuni.mff.d3s.been.logging.TaskLogMessage;
import cz.cuni.mff.d3s.been.persistence.*;
import cz.cuni.mff.d3s.been.persistence.task.PersistentDescriptors;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.*;
import com.hazelcast.query.SqlPredicate;
import cz.cuni.mff.d3s.been.bpk.*;
import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.protocol.command.CommandEntry;
import cz.cuni.mff.d3s.been.core.protocol.command.CommandEntryState;
import cz.cuni.mff.d3s.been.core.protocol.messages.DeleteTaskWrkDirMessage;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.sri.SWRepositoryInfo;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreBuilderFactory;
import cz.cuni.mff.d3s.been.debugassistant.DebugAssistant;
import cz.cuni.mff.d3s.been.debugassistant.DebugListItem;
import cz.cuni.mff.d3s.been.evaluators.EvaluatorResult;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static cz.cuni.mff.d3s.been.persistence.task.PersistentDescriptors.*;


/**
 * User: donarus Date: 4/27/13 Time: 11:50 AM
 */
public class BeenApiImpl implements BeenApi {

    private static final Logger log = LoggerFactory.getLogger(BeenApiImpl.class);

    private final ClusterContext clusterContext;

    private final JSONUtils jsonUtils = JSONUtils.newInstance();

    public BeenApiImpl(String host, int port, String groupName, String groupPassword) {
        HazelcastInstance instance = Instance.newNativeInstance(host, port, groupName, groupPassword);
        clusterContext = new ClusterContext(instance);
    }

    public BeenApiImpl(ClusterContext clusterContext) {
        this.clusterContext = clusterContext;
    }

    @Override
    public void shutdown() {
        Instance.shutdown();
    }

    @Override
    public Collection<Member> getClusterMembers() {

        return clusterContext.getMembers();
    }

    @Override
    public Map<String, String> getClusterServices() {
        return clusterContext.getServices().getServicesInfo();
    }

    @Override
    public Collection<TaskEntry> getTasks() {
        return clusterContext.getTasks().getTasks();
    }

    @Override
    public TaskEntry getTask(String id) {
        return clusterContext.getTasks().getTask(id);
    }

    @Override
    public Collection<TaskContextEntry> getTaskContexts() {
        return clusterContext.getTaskContexts().getTaskContexts();
    }

    @Override
    public TaskContextEntry getTaskContext(String id) {
        return clusterContext.getTaskContexts().getTaskContext(id);
    }

    @Override
    public Collection<BenchmarkEntry> getBenchmarks() {
        return clusterContext.getBenchmarks().getBenchmarksMap().values();
    }

    @Override
    public BenchmarkEntry getBenchmark(String id) {
        return clusterContext.getBenchmarks().get(id);
    }

    @Override
    public Collection<TaskContextEntry> getTaskContextsInBenchmark(String benchmarkId) {
        return clusterContext.getBenchmarks().getTaskContextsInBenchmark(benchmarkId);
    }

    @Override
    public Collection<TaskEntry> getTasksInTaskContext(String taskContextId) {
        return clusterContext.getTaskContexts().getTasksInTaskContext(taskContextId);
    }

    @Override
    public Collection<RuntimeInfo> getRuntimes() {
        return clusterContext.getRuntimes().getRuntimes();
    }

    @Override
    public RuntimeInfo getRuntime(String id) {
        return clusterContext.getRuntimes().getRuntimeInfo(id);
    }


    // --------------------
    // CONFIG PERSISTENCE
    // ------------------


    @Override
    public void saveTaskDescriptor(TaskDescriptor descriptor, String taskId, String contextId, String benchmarkId) throws DAOException {
        clusterContext.getPersistence().asyncPersist(PersistentDescriptors.TASK_DESCRIPTOR, PersistentDescriptors.wrapTaskDescriptor(descriptor, taskId, contextId, benchmarkId));
    }

    @Override
    public void saveNamedTaskDescriptor(TaskDescriptor descriptor, String name, BpkIdentifier bpkId) throws DAOException {
        clusterContext.getPersistence().asyncPersist(NAMED_TASK_DESCRIPTOR, PersistentDescriptors.wrapNamedTaskDescriptor(descriptor, name, bpkId));
    }

    @Override
    public void saveContextDescriptor(TaskContextDescriptor descriptor, String taskId, String contextId, String benchmarkId) throws DAOException {
        clusterContext.getPersistence().asyncPersist(CONTEXT_DESCRIPTOR, PersistentDescriptors.wrapContextDescriptor(descriptor, taskId, contextId, benchmarkId));
    }

    @Override
    public void saveNamedContextDescriptor(TaskContextDescriptor descriptor, String name, BpkIdentifier bpkId) throws DAOException {
        clusterContext.getPersistence().asyncPersist(NAMED_CONTEXT_DESCRIPTOR, PersistentDescriptors.wrapNamedContextDescriptor(descriptor, name, bpkId));
    }

    @Override
    public TaskDescriptor getDescriptorForTask(String taskId) throws DAOException {
        final QueryAnswer answer = clusterContext.getPersistence().query(new QueryBuilder().on(TASK_DESCRIPTOR).with("taskId", taskId).fetch());
        if (!answer.isCarryingData()) {
            throw new DAOException(String.format("Query for task descriptor with contextId='%s' yielded no result: %s", taskId, answer.getStatus().getDescription()));
        }
        return PersistentDescriptors.unpackTaskDescriptor(answer);
    }

    @Override
    public TaskContextDescriptor getDescriptorForContext(String contextId) throws DAOException {
        final QueryAnswer answer = clusterContext.getPersistence().query(new QueryBuilder().on(CONTEXT_DESCRIPTOR).with("contextId", contextId).fetch());
        if (!answer.isCarryingData()) {
            throw new DAOException(String.format("Query for context descriptor with contextId='%s' yielded no result: %s", contextId, answer.getStatus().getDescription()));
        }
        return PersistentDescriptors.unpackContextDescriptor(answer);
    }

    @Override
    public Map<String, TaskDescriptor> getNamedTaskDescriptorsForBpk(BpkIdentifier bpkIdentifier) throws DAOException {
        final QueryAnswer answer = clusterContext.getPersistence().query(new QueryBuilder().on(NAMED_TASK_DESCRIPTOR).with("bpkId", PersistentDescriptors.serializeBpkId(bpkIdentifier)).fetch());
        if (!answer.isCarryingData()) {
            throw new DAOException(String.format("Query for task descriptors for BPK '%s' yielded no result: %s", bpkIdentifier.toString(), answer.getStatus().getDescription()));
        }
        return PersistentDescriptors.unpackNamedTaskDescriptors(answer);
    }

    @Override
    public Map<String, TaskContextDescriptor> getNamedContextDescriptorsForBpk(BpkIdentifier bpkIdentifier) throws DAOException {
        final QueryAnswer answer = clusterContext.getPersistence().query(new QueryBuilder().on(NAMED_CONTEXT_DESCRIPTOR).with("bpkId", PersistentDescriptors.serializeBpkId(bpkIdentifier)).fetch());
        if (!answer.isCarryingData()) {
            throw new DAOException(String.format("Query for context descriptors for BPK '%s' yielded no result: %s", bpkIdentifier.toString(), answer.getStatus().getDescription()));
        }
        return PersistentDescriptors.unpackNamedContextDescriptors(answer);
    }

    @Override
    public void addLogListener(final LogListener listener) {
        EntryListener<String, String> logsListener = new EntryListener<String, String>() {
            @Override
            public void entryAdded(EntryEvent<String, String> event) {
                listener.logAdded(event.getValue());
            }

            @Override
            public void entryRemoved(EntryEvent<String, String> event) {
            }

            @Override
            public void entryUpdated(EntryEvent<String, String> event) {
                listener.logAdded(event.getValue());
            }

            @Override
            public void entryEvicted(EntryEvent<String, String> event) {
            }
        };

        clusterContext.<String, String>getMap(Names.LOGS_TASK_MAP_NAME).addEntryListener(logsListener, true);
    }

    @Override
    public void removeLogListener(LogListener listener) {
        // TODO
    }

    @Override
    public Collection<TaskLogMessage> getLogsForTask(String taskId) throws DAOException {
        Query query = new QueryBuilder().on(Entities.LOG_TASK.getId()).with("taskId", taskId).fetch();

        Collection<String> stringCollection = this.queryPersistence(query).getData();
        try {
            return jsonUtils.deserialize(stringCollection, TaskLogMessage.class);
        } catch (JsonException e) {
            throw new DAOException(String.format("Failed to deserialize task logs for task '%s'", taskId), e);
        }
    }

	@Override
	public Collection<EvaluatorResult> getEvaluatorResults() {
		Query query = new QueryBuilder().on(Entities.RESULT_EVALUATOR.getId()).fetch();

		Collection<String> stringCollection = this.queryPersistence(query).getData();
		try {
			return jsonUtils.deserialize(stringCollection, EvaluatorResult.class);
		} catch (JsonException e) {
			e.printStackTrace();
			// TODO error handling
			return null;
		}
	}

	@Override
	public void deleteResult(String resultId) {
		Query query = new QueryBuilder().on(Entities.RESULT_EVALUATOR.getId()).with("id", resultId).delete();
		QueryStatus status = this.queryPersistence(query).getStatus();
		if (status != QueryStatus.OK) {
			log.error("Delete query failed with status {}", status.getDescription());
		}
	}

	@Override
	public EvaluatorResult getEvaluatorResult(String resultId) {
		Query query = new QueryBuilder().on(Entities.RESULT_EVALUATOR.getId()).with("id", resultId).fetch();

		Collection<String> stringCollection = this.queryPersistence(query).getData();
		try {
			Collection<EvaluatorResult> evaluatorResults = jsonUtils.deserialize(stringCollection, EvaluatorResult.class);
			for (EvaluatorResult evaluatorResult : evaluatorResults) {
				return evaluatorResult;
			}
			return null;
		} catch (JsonException e) {
			e.printStackTrace();
			// TODO error handling
			return null;
		}
	}

	@Override
    public Collection<BpkIdentifier> getBpks() {
        SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
        SwRepoClient client = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache()).getClient(
                swInfo.getHost(),
                swInfo.getHttpServerPort());
        return client.listBpks();
    }

    @Override
    public void uploadBpk(InputStream bpkInputStream) throws BpkConfigurationException {
        SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
        SwRepoClient client = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache()).getClient(
                swInfo.getHost(),
                swInfo.getHttpServerPort());

        BpkIdentifier bpkIdentifier = new BpkIdentifier();

        ByteArrayOutputStream tempStream = new ByteArrayOutputStream();

        try {
            IOUtils.copy(bpkInputStream, tempStream);
        } catch (IOException e) {
            log.error("Cannot upload BPK.", e);
            return;
        }

        ByteArrayInputStream tempInputStream = new ByteArrayInputStream(tempStream.toByteArray());

        BpkConfiguration bpkConfiguration = BpkResolver.resolve(tempInputStream);
        MetaInf metaInf = bpkConfiguration.getMetaInf();
        bpkIdentifier.setGroupId(metaInf.getGroupId());
        bpkIdentifier.setBpkId(metaInf.getBpkId());
        bpkIdentifier.setVersion(metaInf.getVersion());

        tempInputStream.reset();

        client.putBpk(bpkIdentifier, tempInputStream);
    }

    @Override
    public InputStream downloadBpk(BpkIdentifier bpkIdentifier) {
        SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
        SwRepoClient client = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache()).getClient(
                swInfo.getHost(),
                swInfo.getHttpServerPort());

        Bpk bpk = client.getBpk(bpkIdentifier);
        try {
            return bpk.getInputStream();
        } catch (IOException e) {
            log.error("Cannot get input stream from BPK.", e);
            return null;
        }
    }

    @Override
    public String submitTask(TaskDescriptor taskDescriptor) {
        return clusterContext.getTaskContexts().submitTaskInNewContext(taskDescriptor);
    }

    @Override
    public String submitTaskContext(TaskContextDescriptor taskContextDescriptor) {
        return clusterContext.getTaskContexts().submit(taskContextDescriptor, null);
    }

    @Override
    public String submitTaskContext(TaskContextDescriptor taskContextDescriptor, String benchmarkId) {
        return clusterContext.getTaskContexts().submit(taskContextDescriptor, benchmarkId);
    }

    @Override
    public void killTask(String taskId) {
        clusterContext.getTasks().kill(taskId);
    }

    @Override
    public void killTaskContext(String taskContextId) {
        clusterContext.getTaskContexts().kill(taskContextId);
    }

    @Override
    public void killBenchmark(String benchmarkId) {
        clusterContext.getBenchmarks().kill(benchmarkId);
    }

    @Override
    public void removeTaskEntry(String taskId) {
        clusterContext.getTasks().remove(taskId);
    }

    @Override
    public void removeTaskContextEntry(String taskContextId) {
        clusterContext.getTaskContexts().remove(taskContextId);
    }

    @Override
    public void removeBenchmarkEntry(String benchmarkId) {
        clusterContext.getBenchmarks().remove(benchmarkId);
    }


    @Override
    public CommandEntry deleteTaskWrkDirectory(String runtimeId, String taskWrkDir) throws CommandTimeoutException {

        long operationId = clusterContext.generateId(DeleteTaskWrkDirMessage.OPERATION_ID_KEY);
        DeleteTaskWrkDirMessage deleteMessage = new DeleteTaskWrkDirMessage(runtimeId, taskWrkDir, operationId);
        clusterContext.getTopics().publishInGlobalTopic(deleteMessage);

        IMap<Long, CommandEntry> map = clusterContext.getMap(Names.BEEN_MAP_COMMAND_ENTRIES);


        BlockingQueue<CommandEntry> queue = new LinkedBlockingQueue<>();
        final EntryListener<Long, CommandEntry> waiter = new CommandEntryMapWaiter(queue);

        map.addEntryListener(waiter, operationId, true);

        CommandEntry commandEntry = map.get(operationId);

        if (commandEntry == null || commandEntry.getState() == CommandEntryState.PENDING) {
            try {
                commandEntry = queue.poll(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.warn("CommandEntry poll interrupted", e);
            }
        }

        map.removeEntryListener(waiter);
        queue.clear();

        if (commandEntry == null) {
            throw new CommandTimeoutException(String.format("delete task working directory command " +
                    "with parameters [runtimeId : '%s', taskWrkDirName : '%s'] timeouted", runtimeId, taskWrkDir));
        }

        return commandEntry;
    }

    @Override
    public String submitBenchmark(TaskDescriptor benchmarkTaskDescriptor) {
        return clusterContext.getBenchmarks().submit(benchmarkTaskDescriptor);
    }

    @Override
    public Collection<DebugListItem> getDebugWaitingTasks() {
        DebugAssistant debugAssistant = new DebugAssistant(clusterContext);
        return debugAssistant.listWaitingProcesses();
    }

    @Override
    public Map<String, TaskDescriptor> getTaskDescriptors(BpkIdentifier bpkIdentifier) {
        SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
        SwRepoClient client = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache()).getClient(
                swInfo.getHost(),
                swInfo.getHttpServerPort());

        return client.listTaskDescriptors(bpkIdentifier);
    }

    @Override
    public TaskDescriptor getTaskDescriptor(BpkIdentifier bpkIdentifier, String descriptorName) {
        return getTaskDescriptors(bpkIdentifier).get(descriptorName);
    }

    @Override
    public Map<String, TaskContextDescriptor> getTaskContextDescriptors(BpkIdentifier bpkIdentifier) {
        SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
        SwRepoClient client = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache()).getClient(
                swInfo.getHost(),
                swInfo.getHttpServerPort());

        return client.listTaskContextDescriptors(bpkIdentifier);
    }

    @Override
    public TaskContextDescriptor getTaskContextDescriptor(BpkIdentifier bpkIdentifier, String descriptorName) {
        return getTaskContextDescriptors(bpkIdentifier).get(descriptorName);
    }

    @Override
    public QueryAnswer queryPersistence(Query query) {
        try {
            return clusterContext.getPersistence().query(query);
        } catch (DAOException e) {
            log.error("Interrupted when trying to execute persistence query '{}'", query.toString(), e);
            // TODO error handling
            return null;
        }
    }

    @Override
    public Collection<CommandEntry> listCommandEntries(String runtimeId) {
        String sql = String.format("runtimeId = '%s'", runtimeId);
        SqlPredicate predicate = new SqlPredicate(sql);
        return queryHazelcastMap(Names.BEEN_MAP_COMMAND_ENTRIES, predicate);
    }


    @Override
    public Collection<TaskEntry> listActiveTasks(String runtimeId) {
        String sql = String.format("runtimeId = '%s' AND state != %s", runtimeId, TaskState.ABORTED);
        SqlPredicate predicate = new SqlPredicate(sql);
        return queryHazelcastMap(Names.TASKS_MAP_NAME, predicate);
    }


    @Override
    public Collection<TaskEntry> listTasks(String runtimeId) {
        String sql = String.format("runtimeId = '%s'", runtimeId, TaskState.ABORTED);
        SqlPredicate predicate = new SqlPredicate(sql);
        return queryHazelcastMap(Names.TASKS_MAP_NAME, predicate);
    }

    private <T> Collection<T> queryHazelcastMap(String mapName, SqlPredicate queryPredicate) {
        IMap<?, T> map = clusterContext.getMap(mapName);
        return map.values(queryPredicate);
    }
    @Override
    public Collection<ServiceLogMessage> getServiceLogsByBeenId(String beenId) throws DAOException {
        final QueryAnswer qa = clusterContext.getPersistence().query(new QueryBuilder().on(Entities.LOG_SERVICE.getId()).with("beenId", beenId).fetch());
        if (!qa.isCarryingData()) {
            throw new DAOException(String.format("Persistence layer response for service logs from node '%s' yielded no data: %s", beenId, qa.getStatus().getDescription()));
        }
        try {
            return jsonUtils.deserialize(qa.getData(), ServiceLogMessage.class);
        } catch (JsonException e) {
            throw new DAOException(String.format("Cannot deserialize service logs from node '%s'", beenId), e);
        }
    }

    @Override
    public Collection<ServiceLogMessage> getServiceLogsByHostRuntimeId(String hostRuntimeId) throws DAOException {
        final QueryAnswer qa = clusterContext.getPersistence().query(new QueryBuilder().on(Entities.LOG_SERVICE.getId()).with("hostRuntimeId", hostRuntimeId).fetch());
        if (!qa.isCarryingData()) {
            throw new DAOException(String.format("Persistence layer response for service logs from host runtime '%s' yielded no data: %s", hostRuntimeId, qa.getStatus().getDescription()));
        }
        try {
            return jsonUtils.deserialize(qa.getData(), ServiceLogMessage.class);
        } catch (JsonException e) {
            throw new DAOException(String.format("Cannot deserialize service logs from host runtime '%s'", hostRuntimeId), e);
        }
    }

    @Override
    public Collection<ServiceLogMessage> getServiceLogsByServiceName(String serviceName) throws DAOException {
        final QueryAnswer qa = clusterContext.getPersistence().query(new QueryBuilder().on(Entities.LOG_SERVICE.getId()).with("serviceName", serviceName).fetch());
        if (!qa.isCarryingData()) {
            throw new DAOException(String.format("Persistence layer response for service logs from service '%s' yielded no data: %s", serviceName, qa.getStatus().getDescription()));
        }
        try {
            return jsonUtils.deserialize(qa.getData(), ServiceLogMessage.class);
        } catch (JsonException e) {
            throw new DAOException(String.format("Cannot deserialize service logs from service '%s'", serviceName), e);
        }
    }

}
