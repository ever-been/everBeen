package cz.cuni.mff.d3s.been.hostruntime;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import cz.cuni.mff.d3s.been.bpk.*;
import org.apache.maven.artifact.Artifact;

import cz.cuni.mff.d3s.been.cluster.IClusterService;
import cz.cuni.mff.d3s.been.core.ClusterContext;
import cz.cuni.mff.d3s.been.core.JSONUtils.JSONSerializerException;
import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.messages.BaseMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.KillTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskFinishedMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskKilledMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskStartedMessage;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.sri.SWRepositoryInfo;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.core.task.TaskState;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

/**
 * 
 * This is the main implementation of Host Runtime.
 * 
 * Host runtime is responsible for launching new tasks trigged by appropriate
 * message. It is also responsible for operating with already running tasks on
 * parent machine. Operation are as follows: killing tasks, allowing and
 * supporting communication between tasks and results repository, allowing
 * logging).
 * 
 * @author Tadeáš Palusga
 * 
 */
class HostRuntime implements IClusterService {

	/**
	 * Stores basic information (name, id, host, port, OS, memory, Java) about
	 * this {@link HostRuntime} instance.
	 */
	private final RuntimeInfo hostRuntimeInfo;

	/**
	 * Translates relevant messages sent via Hazelcast into corresponding method
	 * calls.
	 */
	private HostRuntimeMessageListener messageListener;

	/**
	 * Factory for creating {@link SwRepoClient} instances from real-time obtained
	 * IP:port
	 */
	private final SwRepoClientFactory swRepoClientFactory;

	private final ClusterContext clusterContext;

	/**
	 * Creates new {@link HostRuntime} with cluster-unique id.
	 * 
	 * @param swRepoClientFactory
	 *          factory for creating {@link SwRepoClient} instances from real-time
	 *          obtained IP and port
	 * @param nodeId
	 *          cluster-unique id of {@link HostRuntime} node
	 */
	public HostRuntime(ClusterContext clusterContext, SwRepoClientFactory swRepoClientFactory, RuntimeInfo hostRuntimeInfo) {
		this.clusterContext = clusterContext;
		this.hostRuntimeInfo = hostRuntimeInfo;
		this.swRepoClientFactory = swRepoClientFactory;
	}

	/**
	 * Starts this {@link HostRuntime}. Registers all listeners and register
	 * itself in cluster.
	 */
	@Override
	public void start() {
		// All listeners must be initialized before any message will be
		// received.
		registerListeners();

		// Now, we can register the runtime without missing any messages.
		registerHostRuntime();

		// HR is now prepared to consume all important messages.
	}

	@Override
	public void stop() {
		// Runtime must be unregistered first
		unregisterHostRuntime();

		// Now, no new message should be received and we can unregister
		// listeners
		unregisterListeners();
	}

	private void unregisterListeners() {
		messageListener.stop();
	}

	private void registerListeners() {
		messageListener = new HostRuntimeMessageListener(this, clusterContext);
		messageListener.start();
	}

	/**
	 * Stores {@link RuntimeInfo} (created in constructor) in cluster.
	 */
	private void registerHostRuntime() {
		clusterContext.getRuntimesUtils().storeRuntimeInfo(hostRuntimeInfo);
	}

	/**
	 * Removes {@link RuntimeInfo} (created in constructor) from cluster.
	 */
	private void unregisterHostRuntime() {
		clusterContext.getRuntimesUtils().removeRuntimeInfo(hostRuntimeInfo.getId());
	}

	void sendTaskStartedMessage(TaskStartedMessage message) throws JSONSerializerException {
		sendMessage(message);
	}

	void sendTaskFinishedMessage(TaskFinishedMessage message) throws JSONSerializerException {
		sendMessage(message);
	}

	void sendTaskKilledMessage(TaskKilledMessage message) throws JSONSerializerException {
		sendMessage(message);
	}

	synchronized void onRunTask(RunTaskMessage message) {
		tryRunTask(message);
	}

	synchronized void onKillTask(KillTaskMessage message) {
		killTask(message);
	}

	public String getNodeId() {
		return hostRuntimeInfo.getId();
	}

	void sendMessage(final BaseMessage message) {
		clusterContext.getTopicUtils().publish(Context.GLOBAL_TOPIC.getName(), message);
	}

	void tryRunTask(RunTaskMessage message) {
		String taskId = message.taskId;
		TaskEntry taskEntry = clusterContext.getTasksUtils().getTask(taskId);
		TaskEntries.setState(taskEntry, TaskState.ACCEPTED, "Task '%s' has been accepted by HR '%s'.", taskId, getNodeId());

		try {
			TaskDescriptor descriptor = taskEntry.getTaskDescriptor();
			SwRepoClient sRClient = createSRClient();

			BpkIdentifier bpkMetaInfo = createBpkMetaInfo(descriptor);

			Bpk bpk = sRClient.getBpk(bpkMetaInfo);
			BpkConfiguration resolvedConf = null;
			try {
				resolvedConf = BpkResolver.resolve(bpk.getFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BpkConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//BpkDependencies deps = resolvedConf.getBpkDependencies();
			//for (BpkIdentifier bpkIdentifier : deps.getDependency()) {
			//	// ... zatim neuvazujeme zadne dalsi BPK zavislosti, pouze parenti BPK a ostatni
			//}

			String cmd = "";

			if (resolvedConf.getRuntime() instanceof JavaRuntime) {
				JavaRuntime runtime = (JavaRuntime) resolvedConf.getRuntime();
				// fixme delete on exit
				File tmpFolder = createTmpDir();
				ZipFileUtil.unzipToDir(bpk.getFile(), tmpFolder);

				cmd += "java -jar " + new File(tmpFolder, runtime.getJarFile()).getAbsolutePath();

				BpkArtifacts arts = runtime.getBpkArtifacts();
				// toto jsou javovske (mavenovske) zavislosti

				if (!arts.getArtifact().isEmpty()) {
					cmd += " -cp \"";
					boolean first = true;
					for (BpkArtifact art : arts.getArtifact()) {
						if (!first) {
							cmd += ";";
						}
						Artifact artifact = sRClient.getArtifact(art.getGroupId(), art.getArtifactId(), art.getVersion());
						cmd += artifact.getFile().getAbsolutePath();
						first = false;
					}
					cmd += "\"";
				}
			}
			// FIXME radek - kde vezmu zdrojaky pro sfuj BPK ? asi

			try {
				Process proc = Runtime.getRuntime().exec(cmd);
				TaskEntries.setState(taskEntry, TaskState.RUNNING, "The task '%s' has been started on HR '%s'.", taskId, getNodeId());
				proc.waitFor(); // FIXME ?? shoud=ld we handle exit codes?
				TaskEntries.setState(taskEntry, TaskState.FINISHED, "The task '%s' has been successfully finished on HR '%s'.", taskId, getNodeId());
			} catch (IOException | InterruptedException e) {
				TaskEntries.setState(taskEntry, TaskState.ABORTED, "The task '%s' has been aborted on HR '%s' due to underlaying exception '%s'.", taskId, getNodeId(), e.getMessage());
				e.printStackTrace();
				// TODO Auto-generated catch block
			}
		} catch (Throwable t) {
			TaskEntries.setState(taskEntry, TaskState.ABORTED, "The task '%s' has been aborted on HR '%s' due to unexpected exception '%s'.", taskId, getNodeId(), t.getMessage());
		}
	}

	private BpkIdentifier createBpkMetaInfo(TaskDescriptor descriptor) {
		BpkIdentifier bpkMetaInfo = new BpkIdentifier();
		bpkMetaInfo.setGroupId(descriptor.getGroupId());
		bpkMetaInfo.setBpkId(descriptor.getBpkId());
		bpkMetaInfo.setVersion(descriptor.getVersion());
		return bpkMetaInfo;
	}

	private SwRepoClient createSRClient() {
		SWRepositoryInfo swRepositoryInfo = clusterContext.getServicesUtils().getSWRepositoryInfo();

		String host = swRepositoryInfo.getHost();
		int port = swRepositoryInfo.getHttpServerPort();

		SwRepoClient swRepoClient = swRepoClientFactory.getClient(host, port);
		return swRepoClient;
	}

	void killTask(KillTaskMessage message) {
		//FIXME
	}

	private File createTmpDir() throws IOException {
		File file = File.createTempFile("unpacked", "bpk");
		file.delete();
		file.mkdir();
		return file;
	}

}
