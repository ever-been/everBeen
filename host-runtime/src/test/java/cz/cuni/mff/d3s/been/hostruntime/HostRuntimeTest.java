package cz.cuni.mff.d3s.been.hostruntime;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;

import org.apache.maven.artifact.Artifact;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cz.cuni.mff.d3s.been.bpk.Bpk;
import cz.cuni.mff.d3s.been.bpk.BpkArtifact;
import cz.cuni.mff.d3s.been.bpk.BpkArtifacts;
import cz.cuni.mff.d3s.been.bpk.BpkConfiguration;
import cz.cuni.mff.d3s.been.bpk.BpkDependencies;
import cz.cuni.mff.d3s.been.bpk.BpkIdentifier;
import cz.cuni.mff.d3s.been.bpk.BpkResolver;
import cz.cuni.mff.d3s.been.bpk.JavaRuntime;
import cz.cuni.mff.d3s.been.core.ServicesUtils;
import cz.cuni.mff.d3s.been.core.TasksUtils;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.sri.SWRepositoryInfo;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskEntries;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClient;
import cz.cuni.mff.d3s.been.swrepoclient.SwRepoClientFactory;

public class HostRuntimeTest extends Assert {

	private HostRuntime hostRuntime; // tested class

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	@Mock
	private SwRepoClientFactory swRepoClientFactory;

	@Mock
	private TasksUtils taskUtils;

	@Mock
	private TaskEntries taskEntries;

	@Mock
	private ServicesUtils servicesUtils;

	@Mock
	private BpkResolver bpkResolver;

	@Mock
	private ProcessExecutor processExecutor;

	@Mock
	private ZipFileUtil zipFileUtil;

	private RuntimeInfo runtimeInfo = new RuntimeInfo();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		hostRuntime = new HostRuntime(taskUtils, taskEntries, servicesUtils, bpkResolver, runtimeInfo, swRepoClientFactory, zipFileUtil, processExecutor);
	}

	@Test
	public void testRunTask() throws Exception {
		// why method tryRunTask starts, it knows only taskId from recieved message
		String taskId = "taskId";
		RunTaskMessage message = new RunTaskMessage("senderId", "recieverId", taskId);

		// it will find correspondent taskEntry in hazelcastMap
		TaskEntry taskEntry = new TaskEntry();
		taskEntry.setTaskDescriptor(new TaskDescriptor());
		when(taskUtils.getTask(taskId)).thenReturn(taskEntry);

		// then, we need to construct sw repository. first, we need to find host:port
		SWRepositoryInfo swRepInfo = new SWRepositoryInfo();
		swRepInfo.setHost("host");
		swRepInfo.setHttpServerPort(123456789);
		when(servicesUtils.getSWRepositoryInfo()).thenReturn(swRepInfo);

		// and then get instance for this host:port
		SwRepoClient swRepClient = mock(SwRepoClient.class);
		when(swRepoClientFactory.getClient(swRepInfo.getHost(), swRepInfo.getHttpServerPort())).thenReturn(swRepClient);

		// now we can download BPK from swrepository
		Bpk bpk = new Bpk();
		File bpkFile = tmp.newFile();
		bpk.setFile(bpkFile);
		when(swRepClient.getBpk(any(BpkIdentifier.class))).thenReturn(bpk);

		// we downloaded BPK file with java runtime, so we need to resolve BPK and Maven-like artifact dependencies
		BpkConfiguration bpkConfiguration = new BpkConfiguration();
		JavaRuntime javaRuntime = new JavaRuntime();;
		// FIXME BPK dependencies are not resolved yet
		bpkConfiguration.setBpkDependencies(new BpkDependencies());
		BpkArtifacts bpkArtifacts = new BpkArtifacts();
		BpkArtifact bpkArtifact1 = new BpkArtifact();
		bpkArtifact1.setGroupId("artifactGroupId1");
		bpkArtifact1.setArtifactId("artifactArtifactId1");
		bpkArtifact1.setVersion("artifactVersionId1");
		BpkArtifact bpkArtifact2 = new BpkArtifact();
		bpkArtifact2.setGroupId("artifactGroupId2");
		bpkArtifact2.setArtifactId("artifactArtifactId2");
		bpkArtifact2.setVersion("artifactVersionId2");
		bpkArtifacts.getArtifact().addAll(Arrays.<BpkArtifact> asList(bpkArtifact1, bpkArtifact2));
		javaRuntime.setBpkArtifacts(bpkArtifacts);
		String jarFileExpectedName = "jarFileExpectedName";
		javaRuntime.setJarFile(jarFileExpectedName);
		bpkConfiguration.setRuntime(javaRuntime);
		when(bpkResolver.resolve(bpkFile)).thenReturn(bpkConfiguration);

		// and now we want to download all these artifacts
		Artifact artifact1 = mock(Artifact.class);
		Artifact artifact2 = mock(Artifact.class);
		File artifactFile1 = tmp.newFile();
		File artifactFile2 = tmp.newFile();
		when(artifact1.getFile()).thenReturn(artifactFile1);
		when(artifact2.getFile()).thenReturn(artifactFile2);
		when(swRepClient.getArtifact(bpkArtifact1.getGroupId(), bpkArtifact1.getArtifactId(), bpkArtifact1.getVersion())).thenReturn(artifact1);
		when(swRepClient.getArtifact(bpkArtifact2.getGroupId(), bpkArtifact2.getArtifactId(), bpkArtifact2.getVersion())).thenReturn(artifact2);

		// if process is started , processExecutor should return this process as its return value
		when(processExecutor.execute(anyString())).thenReturn(mock(Process.class));

		// NOW RUN THE BEAST
		hostRuntime.tryRunTask(message);

		// If everything is correct, verify that the task process has been started with correct command line
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(processExecutor).execute(captor.capture());
		assertTrue(captor.getValue().contains("java -jar"));
		assertTrue(captor.getValue().contains("/" + jarFileExpectedName));
		assertTrue(captor.getValue().contains("-cp \"" + artifactFile1.getAbsolutePath() + ";" + artifactFile2.getAbsolutePath() + "\""));
	}
}
