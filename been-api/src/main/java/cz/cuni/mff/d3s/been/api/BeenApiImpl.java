package cz.cuni.mff.d3s.been.api;

import com.hazelcast.core.*;
import com.hazelcast.query.SqlPredicate;
import cz.cuni.mff.d3s.been.bpk.*;
import cz.cuni.mff.d3s.been.cluster.Instance;
import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.benchmark.BenchmarkEntry;
import cz.cuni.mff.d3s.been.core.persistence.Entities;
import cz.cuni.mff.d3s.been.core.persistence.Entity;
import cz.cuni.mff.d3s.been.core.persistence.EntityID;
import cz.cuni.mff.d3s.been.core.protocol.command.CommandEntry;
import cz.cuni.mff.d3s.been.core.protocol.command.CommandEntryState;
import cz.cuni.mff.d3s.been.core.protocol.messages.DeleteTaskWrkDirMessage;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.sri.SWRepositoryInfo;
import cz.cuni.mff.d3s.been.core.task.*;
import cz.cuni.mff.d3s.been.datastore.SoftwareStoreBuilderFactory;
import cz.cuni.mff.d3s.been.debugassistant.DebugAssistant;
import cz.cuni.mff.d3s.been.debugassistant.DebugListItem;
import cz.cuni.mff.d3s.been.evaluators.EvaluatorResult;
import cz.cuni.mff.d3s.been.logging.ServiceLogMessage;
import cz.cuni.mff.d3s.been.logging.TaskLogMessage;
import cz.cuni.mff.d3s.been.persistence.*;
import cz.cuni.mff.d3s.been.persistence.task.PersistentDescriptors;
import cz.cuni.mff.d3s.been.persistence.task.PersistentTaskState;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
        Instance.newNativeInstance(host, port, groupName, groupPassword);
        clusterContext = Instance.createContext();
    }

    public BeenApiImpl(ClusterContext clusterContext) {
        this.clusterContext = clusterContext;
    }

    @Override
    public void shutdown() {
        Instance.shutdown();
    }

    @Override
    public Collection<Member> getClusterMembers() throws BeenApiException {
        checkIsActive("Been API can't list connected members. Been API is not connected to Cluster.");
        try {
            return clusterContext.getMembers();
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list connected members due to unknown exception. Message: %s", e.getMessage());
        }
    }

    @Override
    public Map<String, String> getClusterServices() throws BeenApiException {
        checkIsActive("Been API can't list cluster services. Been API is not connected to Cluster.");
        try {
            return clusterContext.getServices().getServicesInfo();
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list cluster services due to unknown exception. Message: %s", e.getMessage());
        }
    }

    @Override
    public Collection<TaskEntry> getTasks() throws BeenApiException {
        checkIsActive("Been API can't list task entries. Been API is not connected to Cluster.");
        try {
            return clusterContext.getTasks().getTasks();
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list task entries due to unknown exception. Message: %s", e.getMessage());
        }
    }

    @Override
    public TaskEntry getTask(String id) throws BeenApiException {
        checkIsActive("Been API can't get task with id '%s'. Been API is not connected to Cluster.", id);
        try {
            return clusterContext.getTasks().getTask(id);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't get task with id '%s' due to unknown exception. Message: %s", id, e.getMessage());
        }
    }

    @Override
    public Collection<TaskContextEntry> getTaskContexts() throws BeenApiException {
        checkIsActive("Been API can't list task contexts. Been API is not connected to Cluster.");
        try {
            return clusterContext.getTaskContexts().getTaskContexts();
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list task contexts due to unknown exception. Message: %s", e.getMessage());
        }
    }

    @Override
    public TaskContextEntry getTaskContext(String id) throws BeenApiException {
        checkIsActive("Been API can't get task context with id '%s'. Been API is not connected to Cluster.", id);
        try {
            return clusterContext.getTaskContexts().getTaskContext(id);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't get task context with id '%s' due to unknown exception. Message: %s", id, e.getMessage());
        }
    }

    @Override
    public Collection<BenchmarkEntry> getBenchmarks() throws BeenApiException {
        checkIsActive("Been API can't list benchmarks. Been API is not connected to Cluster.");
        try {
            return clusterContext.getBenchmarks().getBenchmarksMap().values();
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list benchmarks due to unknown exception. Message: %s", e.getMessage());
        }
    }

    @Override
    public BenchmarkEntry getBenchmark(String id) throws BeenApiException {
        checkIsActive("Been API can't get benchmark with id '%s'. Been API is not connected to Cluster.", id);
        try {
            return clusterContext.getBenchmarks().get(id);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't get benchmark with id '%s' due to unknown exception. Message: %s", id, e.getMessage());
        }
    }

    @Override
    public Collection<TaskContextEntry> getTaskContextsInBenchmark(String benchmarkId) throws BeenApiException {
        checkIsActive("Been API can't list task contexts for benchmark with id '%s'. Been API is not connected to Cluster.", benchmarkId);
        try {
            return clusterContext.getBenchmarks().getTaskContextsInBenchmark(benchmarkId);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list task contexts for benchmark with id '%s' due to unknown exception. Message: %s", benchmarkId, e.getMessage());
        }
    }

    @Override
    public Collection<TaskEntry> getTasksInTaskContext(String taskContextId) throws BeenApiException {
        checkIsActive("Been API can't list tasks for task context with id '%s'. Been API is not connected to Cluster.", taskContextId);
        try {
            return clusterContext.getTaskContexts().getTasksInTaskContext(taskContextId);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list tasks for task context with id '%s' due to unknown exception. Message: %s", taskContextId, e.getMessage());
        }
    }

    @Override
    public Collection<RuntimeInfo> getRuntimes() throws BeenApiException {
        checkIsActive("Been API can't list host runtimes. Been API is not connected to Cluster.");
        try {
            return clusterContext.getRuntimes().getRuntimes();
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list host runtimes due to unknown exception. Message: %s", e.getMessage());
        }
    }

    @Override
    public RuntimeInfo getRuntime(String id) throws BeenApiException {
        checkIsActive("Been API can't get host runtime with id '%s'. Been API is not connected to Cluster.", id);
        try {
            return clusterContext.getRuntimes().getRuntimeInfo(id);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't get host runtime with id '%s' due to unknown exception. Message: %s", id, e.getMessage());
        }
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
    public Collection<TaskLogMessage> getLogsForTask(String taskId) throws BeenApiException {
        Collection<String> stringCollection;
        try {
	        Query query = new QueryBuilder().on(Entities.LOG_TASK.getId()).with("taskId", taskId).fetch();

            stringCollection = this.queryPersistence(query).getData();

        } catch (Exception e) {
            throw createBeenApiException(e, "Interrupted when collecting logs for task with id '%s'. Reason: %s", taskId, e.getMessage());
        }

        try {
            return jsonUtils.deserialize(stringCollection, TaskLogMessage.class);
        } catch (JsonException e) {
            throw new BeenApiException(String.format("Failed to collect logs for task '%s'", taskId), e);
        }
    }

    @Override
    public Collection<EvaluatorResult> getEvaluatorResults() throws BeenApiException {
        Collection<String> stringCollection;
        try {
	        Query query = new QueryBuilder().on(Entities.RESULT_EVALUATOR.getId()).fetch();

            stringCollection = this.queryPersistence(query).getData();
        } catch (Exception e) {
            throw createBeenApiException(e, "Interrupted when collecting evaluator results. Reason: %s", e.getMessage());
        }

        try {
            return jsonUtils.deserialize(stringCollection, EvaluatorResult.class);
        } catch (JsonException e) {
            throw new BeenApiException("Failed to collect evaluator results.", e);
        }
    }

    @Override
    public void deleteResult(String resultId) throws BeenApiException {
	    Query query = new QueryBuilder().on(Entities.RESULT_EVALUATOR.getId()).with("id", resultId).delete();
        try {
            QueryStatus status = this.queryPersistence(query).getStatus();
            if (status != QueryStatus.OK) {
                log.error("Delete query failed with status {}", status.getDescription());
            }
        } catch (Exception e) {
            throw new BeenApiException(String.format("Failed to delete result with id '%s'. Reason: ", resultId, e.getMessage()), e);
        }
    }

    @Override
    public EvaluatorResult getEvaluatorResult(String resultId) throws BeenApiException {
	    Query query = new QueryBuilder().on(Entities.RESULT_EVALUATOR.getId()).with("id", resultId).fetch();
        Collection<String> stringCollection;
        try {
            stringCollection = this.queryPersistence(query).getData();
        } catch (Exception e) {
            throw createBeenApiException(e, "Interrupted when retrieving evaluator result with id '%s'. Reason: %s", resultId, e.getMessage());
        }

        try {
            Collection<EvaluatorResult> evaluatorResults = jsonUtils.deserialize(stringCollection, EvaluatorResult.class);
            for (EvaluatorResult evaluatorResult : evaluatorResults) {
                return evaluatorResult;
            }
            return null;
        } catch (JsonException e) {
            throw new BeenApiException("Failed to retrieve evaluator result.", e);
        }
    }

    @Override
    public Collection<BpkIdentifier> getBpks() throws BeenApiException {
        checkIsActive("Been API can't list bpks. Been API is not connected to Cluster.");

        SWRepositoryInfo swInfo;
        try {
            swInfo = clusterContext.getServices().getSWRepositoryInfo();
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list bpks due to unknown exception. Message: %s", e.getMessage());
        }

        if (swInfo == null) {
            throw new SoftwareRepositoryUnavailableException("Software repository is not available.");
        }

        try {
            SwRepoClient client = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache()).getClient(
                    swInfo.getHost(),
                    swInfo.getHttpServerPort());
            return client.listBpks();
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list bpks due to unknown exception. Message: %s", e.getMessage());
        }
    }

    @Override
    public void uploadBpk(InputStream bpkInputStream) throws BeenApiException {
        checkIsActive("Been API can't upload bpk. Been API is not connected to Cluster.");

        try {
            SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
            SwRepoClient client = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache()).getClient(
                    swInfo.getHost(),
                    swInfo.getHttpServerPort());

            BpkIdentifier bpkIdentifier = new BpkIdentifier();

            ByteArrayOutputStream tempStream = new ByteArrayOutputStream();

            try {
                IOUtils.copy(bpkInputStream, tempStream);
            } catch (IOException e) {
                throw new BeenApiException("Cannot upload BPK.", e);
            }

            ByteArrayInputStream tempInputStream = new ByteArrayInputStream(tempStream.toByteArray());

            BpkConfiguration bpkConfiguration = BpkResolver.resolve(tempInputStream);
            MetaInf metaInf = bpkConfiguration.getMetaInf();
            bpkIdentifier.setGroupId(metaInf.getGroupId());
            bpkIdentifier.setBpkId(metaInf.getBpkId());
            bpkIdentifier.setVersion(metaInf.getVersion());

            tempInputStream.reset();

            client.putBpk(bpkIdentifier, tempInputStream);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't upload bpk due to unknown exception. Message: %s", e.getMessage());
        }
    }

    @Override
    public InputStream downloadBpk(BpkIdentifier bpkIdentifier) throws BeenApiException {
        checkIsActive("Been API can't download bpk. Been API is not connected to Cluster.");

        try {
            SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
            SwRepoClient client = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache()).getClient(
                    swInfo.getHost(),
                    swInfo.getHttpServerPort());

            Bpk bpk = client.getBpk(bpkIdentifier);
            try {
                return bpk.getInputStream();
            } catch (IOException e) {
                String msg = "Cannot get input stream from BPK.";
                log.error(msg, e);
                throw new BeenApiException(msg, e);
            }
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't download bpk due to unknown exception. Message: %s", e.getMessage());
        }
    }

    @Override
    public String submitTask(TaskDescriptor taskDescriptor) throws BeenApiException {
        checkIsActive("Been API can't submit task descriptor. Been API is not connected to Cluster.");

        try {
            return clusterContext.getTaskContexts().submitTaskInNewContext(taskDescriptor);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't submit task descriptor due to unknown exception. Message: %s", e.getMessage());
        }
    }

    @Override
    public String submitTaskContext(TaskContextDescriptor taskContextDescriptor) throws BeenApiException {
        checkIsActive("Been API can't submit task context descriptor. Been API is not connected to Cluster.");

        try {
            return clusterContext.getTaskContexts().submit(taskContextDescriptor, null);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't submit task context descriptor due to unknown exception. Message: %s", e.getMessage());
        }
    }

    @Override
    public String submitTaskContext(TaskContextDescriptor taskContextDescriptor, String benchmarkId) throws BeenApiException {
        checkIsActive("Been API can't submit task context descriptor for benchmark with id '%s'. Been API is not connected to Cluster.", benchmarkId);

        try {
            return clusterContext.getTaskContexts().submit(taskContextDescriptor, benchmarkId);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't submit task context descriptor for benchmark with id '%s' due to unknown exception. Message: %s", benchmarkId, e.getMessage());
        }
    }

    @Override
    public void killTask(String taskId) throws BeenApiException {
        checkIsActive("Been API can't kill task with id '%s'. Been API is not connected to Cluster.", taskId);

        try {
            clusterContext.getTasks().kill(taskId);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't kill task with id '%s' due to unknown exception. Message: %s", taskId, e.getMessage());
        }
    }

    @Override
    public void killTaskContext(String taskContextId) throws BeenApiException {
        checkIsActive("Been API can't kill task context with id '%s'. Been API is not connected to Cluster.", taskContextId);

        try {
            clusterContext.getTaskContexts().kill(taskContextId);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't kill task context with id '%s' due to unknown exception. Message: %s", taskContextId, e.getMessage());
        }
    }

    @Override
    public void killBenchmark(String benchmarkId) throws BeenApiException {
        checkIsActive("Been API can't kill benchmark with id '%s'. Been API is not connected to Cluster.", benchmarkId);

        try {
            clusterContext.getBenchmarks().kill(benchmarkId);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't kill benchmark with id '%s' due to unknown exception. Message: %s", benchmarkId, e.getMessage());
        }
    }

    @Override
    public void removeTaskEntry(String taskId) throws BeenApiException {
        checkIsActive("Been API can't remove task with id '%s'. Been API is not connected to Cluster.", taskId);

        try {
            clusterContext.getTasks().remove(taskId);
            ;
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't remove task with id '%s' due to unknown exception. Message: %s", taskId, e.getMessage());
        }
    }

    @Override
    public void removeTaskContextEntry(String taskContextId) throws BeenApiException {
        checkIsActive("Been API can't remove task context with id '%s'. Been API is not connected to Cluster.", taskContextId);

        try {
            clusterContext.getTaskContexts().remove(taskContextId);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't remove task context with id '%s' due to unknown exception. Message: %s", taskContextId, e.getMessage());
        }
    }

    @Override
    public void removeBenchmarkEntry(String benchmarkId) throws BeenApiException {
        checkIsActive("Been API can't remove benchmark context with id '%s'. Been API is not connected to Cluster.", benchmarkId);

        try {
            clusterContext.getBenchmarks().remove(benchmarkId);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't remove benchmark context with id '%s' due to unknown exception. Message: %s", benchmarkId, e.getMessage());
        }
    }


    @Override
    public CommandEntry deleteTaskWrkDirectory(String runtimeId, String taskWrkDir) throws BeenApiException {
        checkIsActive("Been API can't delete task working directory '%s' on runtime with id '%s'. Been API is not connected to Cluster.", taskWrkDir, runtimeId);

        try {
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
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't delete task working directory '%s' on runtime with id '%s' due to unknown exception. Message: %s", taskWrkDir, runtimeId, e.getMessage());
        }

    }

    @Override
    public String submitBenchmark(TaskDescriptor benchmarkTaskDescriptor) throws BeenApiException {
        checkIsActive("Been API can't submit benchmark descriptor. Been API is not connected to Cluster.");

        try {
            return clusterContext.getBenchmarks().submit(benchmarkTaskDescriptor);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't submit benchmark descriptor due to unknown exception. Message: %s", e.getMessage());
        }
    }

    @Override
    public Collection<DebugListItem> getDebugWaitingTasks() throws BeenApiException {
        checkIsActive("Been API can't list tasks waiting for debug. Been API is not connected to Cluster.");

        try {
            DebugAssistant debugAssistant = new DebugAssistant(clusterContext);
            return debugAssistant.listWaitingProcesses();
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list tasks waiting for debug due to unknown exception. Message: %s", e.getMessage());
        }
    }

    @Override
    public Map<String, TaskDescriptor> getTaskDescriptors(BpkIdentifier bpkIdentifier) throws BeenApiException {
        checkIsActive("Been API can't list task descriptors for bpk with id '%s'. Been API is not connected to Cluster.", bpkIdentifier.getBpkId());

        try {
            SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
            SwRepoClient client = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache()).getClient(
                    swInfo.getHost(),
                    swInfo.getHttpServerPort());

            return client.listTaskDescriptors(bpkIdentifier);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list task descriptors for bpk with id '%s' due to unknown exception. Message: %s", bpkIdentifier.getBpkId(), e.getMessage());
        }
    }

    @Override
    public TaskDescriptor getTaskDescriptor(BpkIdentifier bpkIdentifier, String descriptorName) throws BeenApiException {
        checkIsActive("Been API can't get task descriptor with name '%s' for bpk with id '%s'. Been API is not connected to Cluster.", descriptorName, bpkIdentifier.getBpkId());

        try {
            return getTaskDescriptors(bpkIdentifier).get(descriptorName);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't get task descriptor with name '%s' for bpk with id '%s' due to unknown exception. Message: %s", descriptorName, bpkIdentifier.getBpkId(), e.getMessage());
        }
    }

    @Override
    public Map<String, TaskContextDescriptor> getTaskContextDescriptors(BpkIdentifier bpkIdentifier) throws BeenApiException {
        checkIsActive("Been API can't list task context descriptors for bpk with id '%s'. Been API is not connected to Cluster.", bpkIdentifier.getBpkId());

        try {
            SWRepositoryInfo swInfo = clusterContext.getServices().getSWRepositoryInfo();
            SwRepoClient client = new SwRepoClientFactory(SoftwareStoreBuilderFactory.getSoftwareStoreBuilder().buildCache()).getClient(
                    swInfo.getHost(),
                    swInfo.getHttpServerPort());

            return client.listTaskContextDescriptors(bpkIdentifier);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list task context descriptors for bpk with id '%s' due to unknown exception. Message: %s", bpkIdentifier.getBpkId(), e.getMessage());
        }
    }

    @Override
    public TaskContextDescriptor getTaskContextDescriptor(BpkIdentifier bpkIdentifier, String descriptorName) throws BeenApiException {
        checkIsActive("Been API can't get task context descriptor with name '%s' for bpk with id '%s'. Been API is not connected to Cluster.", descriptorName, bpkIdentifier.getBpkId());

        try {
            return getTaskContextDescriptors(bpkIdentifier).get(descriptorName);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't get task context descriptor with name '%s' for bpk with id '%s' due to unknown exception. Message: %s", descriptorName, bpkIdentifier.getBpkId(), e.getMessage());
        }
    }

    @Override
    public QueryAnswer queryPersistence(Query query) throws BeenApiException {
        try {
            return clusterContext.getPersistence().query(query);
        } catch (DAOException e) {
            throw createBeenApiException(e, "Interrupted when trying to execute persistence query '%s'", query.toString());
        }
    }

    @Override
    public Collection<CommandEntry> listCommandEntries(String runtimeId) throws BeenApiException {
        checkIsActive("Been API can't list command entries for host runtime '%s'. Been API is not connected to Cluster.", runtimeId);

        SqlPredicate predicate = new SqlPredicate(String.format("runtimeId = '%s'", runtimeId));

        try {
            return queryHazelcastMap(Names.BEEN_MAP_COMMAND_ENTRIES, predicate);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list command entries for host runtime '%s' due to unknown exception. Message: %s", runtimeId, e.getMessage());
        }
    }


    @Override
    public Collection<TaskEntry> listActiveTasks(String runtimeId) throws BeenApiException {
        checkIsActive("Been API can't list active tasks for host runtime '%s'. Been API is not connected to Cluster.", runtimeId);

        SqlPredicate predicate = new SqlPredicate(String.format("runtimeId = '%s' AND state != %s", runtimeId, TaskState.ABORTED));

        try {
            return queryHazelcastMap(Names.TASKS_MAP_NAME, predicate);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list active tasks for host runtime '%s' due to unknown exception. Message: %s", runtimeId, e.getMessage());
        }
    }


    @Override
    public Collection<TaskEntry> listTasks(String runtimeId) throws BeenApiException {
        checkIsActive("Been API can't list tasks for host runtime '%s'. Been API is not connected to Cluster.", runtimeId);

        SqlPredicate predicate = new SqlPredicate(String.format("runtimeId = '%s'", runtimeId, TaskState.ABORTED));

        try {
            return queryHazelcastMap(Names.TASKS_MAP_NAME, predicate);
        } catch (Exception e) {
            throw createBeenApiException(e, "Been API can't list tasks for host runtime '%s' due to unknown exception. Message: %s", runtimeId, e.getMessage());
        }
    }

    private <T> Collection<T> queryHazelcastMap(String mapName, SqlPredicate queryPredicate) {
        IMap<?, T> map = clusterContext.getMap(mapName);
        return map.values(queryPredicate);
    }

    @Override
    public Collection<ServiceLogMessage> getServiceLogsByBeenId(String beenId) throws BeenApiException {
        Query query = new QueryBuilder().on(Entities.LOG_SERVICE.getId()).with("beenId", beenId).fetch();
        try {
            final QueryAnswer qa = clusterContext.getPersistence().query(query);
            if (!qa.isCarryingData()) {
                throw new DAOException(String.format("Persistence layer response for service logs from node '%s' yielded no data: %s", beenId, qa.getStatus().getDescription()));
            }
            try {
                return jsonUtils.deserialize(qa.getData(), ServiceLogMessage.class);
            } catch (JsonException e) {
                throw new DAOException(String.format("Cannot deserialize service logs from node '%s'", beenId), e);
            }
        } catch (Exception e) {
            throw createBeenApiException(e, "Interrupted when trying to execute persistence query '%s'", query.toString());
        }

    }

    @Override
    public Collection<ServiceLogMessage> getServiceLogsByHostRuntimeId(String hostRuntimeId) throws BeenApiException {
        Query query = new QueryBuilder().on(Entities.LOG_SERVICE.getId()).with("hostRuntimeId", hostRuntimeId).fetch();
        try {
            final QueryAnswer qa = clusterContext.getPersistence().query(query);
            if (!qa.isCarryingData()) {
                throw new DAOException(String.format("Persistence layer response for service logs from host runtime '%s' yielded no data: %s", hostRuntimeId, qa.getStatus().getDescription()));
            }
            try {
                return jsonUtils.deserialize(qa.getData(), ServiceLogMessage.class);
            } catch (JsonException e) {
                throw new DAOException(String.format("Cannot deserialize service logs from host runtime '%s'", hostRuntimeId), e);
            }
        } catch (Exception e) {
            throw createBeenApiException(e, "Interrupted when trying to execute persistence query '%s'", query.toString());
        }
    }

    @Override
    public Collection<ServiceLogMessage> getServiceLogsByServiceName(String serviceName) throws BeenApiException {
        Query query = new QueryBuilder().on(Entities.LOG_SERVICE.getId()).with("serviceName", serviceName).fetch();
        try {
            final QueryAnswer qa = clusterContext.getPersistence().query(query);
            if (!qa.isCarryingData()) {
                throw new DAOException(String.format("Persistence layer response for service logs from service '%s' yielded no data: %s", serviceName, qa.getStatus().getDescription()));
            }
            try {
                return jsonUtils.deserialize(qa.getData(), ServiceLogMessage.class);
            } catch (JsonException e) {
                throw new DAOException(String.format("Cannot deserialize service logs from service '%s'", serviceName), e);
            }
        } catch (Exception e) {
            throw createBeenApiException(e, "Interrupted when trying to execute persistence query '%s'", query.toString());

        }
    }

    private void checkIsActive(String format, String... args) throws ClusterConnectionUnavailableException {
        String message = String.format(format, args);

        if (!isConnected()) {
            throw new ClusterConnectionUnavailableException(message);
        }
    }

    @Override
    public boolean isConnected() {
        return clusterContext.isActive();
    }


    private BeenApiException createBeenApiException(Exception e, String format, String... args) {
        return new BeenApiException(String.format(format, args), e);
    }


    // TASK STATE RETRIEVAL

    @Override
    public Collection<String> getTasksWithFinalState(TaskState state) throws DAOException {
        final Query fetchQuery = new QueryBuilder().on(Entities.OUTCOME_TASK.getId()).with("taskState", state.name()).fetch();
        final Collection<PersistentTaskState> pStates = unpackDataAnswer(fetchQuery, clusterContext.getPersistence().query(fetchQuery), PersistentTaskState.class);
        final Collection<String> res = new ArrayList<String>(pStates.size());
        for (PersistentTaskState pState : pStates) {
            res.add(pState.getTaskId());
        }
        return res;
    }

    @Override
    public Collection<String> getTasksWithFinalStateFromContext(TaskState state, String contextId) throws DAOException {
        final Query fetchQuery = new QueryBuilder().on(Entities.OUTCOME_TASK.getId()).with("taskState", state.name()).with("contextId", contextId).fetch();
        final Collection<PersistentTaskState> pStates = unpackDataAnswer(fetchQuery, clusterContext.getPersistence().query(fetchQuery), PersistentTaskState.class);
        final Collection<String> res = new ArrayList<String>(pStates.size());
        for (PersistentTaskState pState : pStates) {
            res.add(pState.getTaskId());
        }
        return res;
    }

    @Override
    public Collection<String> getTasksWithFinalStateFromBenchmark(TaskState state, String benchmarkId) throws DAOException {
        final Query fetchQuery = new QueryBuilder().on(Entities.OUTCOME_TASK.getId()).with("taskState", state.name()).with("benchmarkId", benchmarkId).fetch();
        final Collection<PersistentTaskState> pStates = unpackDataAnswer(fetchQuery, clusterContext.getPersistence().query(fetchQuery), PersistentTaskState.class);
        final Collection<String> res = new ArrayList<String>(pStates.size());
        for (PersistentTaskState pState : pStates) {
            res.add(pState.getTaskId());
        }
        return res;
    }

    @Override
    public TaskState getFinalTaskState(String taskId) throws DAOException {
        final Query fetchQuery = new QueryBuilder().on(Entities.OUTCOME_TASK.getId()).with("taskId", taskId).fetch();
        final Collection<PersistentTaskState> pStates = unpackDataAnswer(fetchQuery, clusterContext.getPersistence().query(fetchQuery), PersistentTaskState.class);
        for (PersistentTaskState pState : pStates) {
            return pState.getTaskState();
        }
        throw new DAOException(String.format("No final task state found for task '%s'", taskId));
    }

    @Override
    public Map<String, TaskState> getFinalTaskStatesForContext(String contextId) throws DAOException {
        final Query fetchQuery = new QueryBuilder().on(Entities.OUTCOME_TASK.getId()).with("contextId", contextId).fetch();
        final Collection<PersistentTaskState> pStates = unpackDataAnswer(fetchQuery, clusterContext.getPersistence().query(fetchQuery), PersistentTaskState.class);
        final Map<String, TaskState> res = new HashMap<String, TaskState>(pStates.size());
        for (PersistentTaskState pState : pStates) {
            res.put(pState.getTaskId(), pState.getTaskState());
        }
        return res;
    }

    @Override
    public Map<String, TaskState> getFinalTaskStatesForBenchmark(String benchmarkId) throws DAOException {
        final Query fetchQuery = new QueryBuilder().on(Entities.OUTCOME_TASK.getId()).with("benchmarkId", benchmarkId).fetch();
        final Collection<PersistentTaskState> pStates = unpackDataAnswer(fetchQuery, clusterContext.getPersistence().query(fetchQuery), PersistentTaskState.class);
        final Map<String, TaskState> res = new HashMap<String, TaskState>(pStates.size());
        for (PersistentTaskState pState : pStates) {
            res.put(pState.getTaskId(), pState.getTaskState());
        }
        return res;
    }

    /**
     * Unmarshall a collection of accordingly typed entities from a {@link QueryAnswer}, or throw a {@link DAOException} if something goes wrong
     * Call this on a {@link QueryAnswer} that should be bringing you data. Provide the {@link Query} this answer originated from. If the answer is not carrying the data it should, an informative exception is thrown.
     *
     * @param query       {@link Query} you used to retrieve data. This method should only be using for fetch-type queries
     * @param answer      The {@link QueryAnswer} you got as a response to this query
     * @param entityClass Class of the entity to unmarshall
     * @throws DAOException When there is no data in the answer
     */
    private <T extends Entity> Collection<T> unpackDataAnswer(Query query, QueryAnswer answer, Class<T> entityClass) throws DAOException {
        if (!answer.isCarryingData()) {
            throw new DAOException(String.format("Answer for query '%s' returned with no data: %s", query.toString(), answer.getStatus().getDescription()));
        }
        try {
            return jsonUtils.deserialize(answer.getData(), entityClass);
        } catch (JsonException e) {
            throw new DAOException(String.format("Failed to unmarshall data responding to query '%s'", query.toString()), e);
        }
    }

    @Override
    public void clearPersistenceForTask(String taskId) throws DAOException {
        final Query deleteQuery = new QueryBuilder().with("taskId", taskId).delete();
        final QueryAnswer answer = clusterContext.getPersistence().query(deleteQuery);
        if (!answer.getStatus().isOk()) {
            throw new DAOException(String.format("Failed to delete leftover entities with taskId '%s': %s", taskId, answer.getStatus().getDescription()));
        }
    }

    @Override
    public void clearPersistenceForContext(String contextId) throws DAOException {
        final Query deleteQuery = new QueryBuilder().with("contextId", contextId).delete();
        final QueryAnswer answer = clusterContext.getPersistence().query(deleteQuery);
        if (!answer.getStatus().isOk()) {
            throw new DAOException(String.format("Failed to delete leftover entities with contextId '%s': %s", contextId, answer.getStatus().getDescription()));
        }
    }

    @Override
    public void clearPersistenceForBenchmark(String benchmarkId) throws DAOException {
        final Query deleteQuery = new QueryBuilder().with("benchmarkId", benchmarkId).delete();
        final QueryAnswer answer = clusterContext.getPersistence().query(deleteQuery);
        if (!answer.getStatus().isOk()) {
            throw new DAOException(String.format("Failed to delete leftover entities with benchmarkId '%s': %s", benchmarkId, answer.getStatus().getDescription()));
        }
    }

}
