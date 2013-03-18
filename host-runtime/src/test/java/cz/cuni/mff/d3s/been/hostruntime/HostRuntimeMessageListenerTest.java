package cz.cuni.mff.d3s.been.hostruntime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.protocol.messages.KillTaskMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.RunTaskMessage;

public class HostRuntimeMessageListenerTest extends Assert {

	@Mock
	private HostRuntime hostRuntime;

	@Mock
	private ClusterContext clusterContext;

	private HostRuntimeMessageListener listener;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		listener = new HostRuntimeMessageListener(hostRuntime, clusterContext);
	}

	@Ignore
	@Test
	public void testKillTaskMessageWillCallCorrectMethod() throws Exception {
		KillTaskMessage msg = new KillTaskMessage(null, null, null, null);

		// TODO: FIXME onMessage signature changed after moving to pure Hazelcast interface
		//Message<BaseMessage> message = new Message<BaseMessage>(...)
		//listener.onMessage(msg);

		verify(hostRuntime).onKillTask(msg);
		verifyNoMoreInteractions(hostRuntime);
	}

	@Ignore
	@Test
	public void testRunTaskMessageWillCallCorrectMethod() throws Exception {
		RunTaskMessage msg = new RunTaskMessage(null, null, null);

		// TODO: FIXME onMessage signature changed after moving to pure Hazelcast interface
		//Message<BaseMessage> message = new Message<BaseMessage>(...)
		//listener.onMessage(msg);

		verify(hostRuntime).onRunTask(msg);
		verifyNoMoreInteractions(hostRuntime);
	}

}
