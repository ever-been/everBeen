package cz.cuni.mff.d3s.been.hostruntime;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cz.cuni.mff.d3s.been.core.protocol.Context;
import cz.cuni.mff.d3s.been.core.protocol.cluster.DataPersistence;
import cz.cuni.mff.d3s.been.core.protocol.cluster.Messaging;
import cz.cuni.mff.d3s.been.core.protocol.messages.NodeRegisteredMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.NodeTerminatedMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskFinishedMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskKilledMessage;
import cz.cuni.mff.d3s.been.core.protocol.messages.TaskStartedMessage;
import cz.cuni.mff.d3s.been.core.protocol.pojo.BaseNodeInfo.HostRuntimeNodeInfo;

public class HostRuntimeTest extends Assert {

	@Mock
	private TaskRunner taskRunner;

	@Mock
	private Messaging messaging;

	@Mock
	private DataPersistence dataPersistence;

	private String nodeId;

	private HostRuntime hostRuntime;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		nodeId = "node identifier";

		hostRuntime = new HostRuntime(messaging, dataPersistence, taskRunner, nodeId);
	}
	
	@Ignore("?")
	@Test
	public void testNodeInfoIsPassedToTaskRunnerInConstructor() throws Exception {
		ArgumentCaptor<HostRuntimeNodeInfo> captor = ArgumentCaptor.forClass(HostRuntimeNodeInfo.class);
		verify(taskRunner).setNodeInfo(captor.capture());
		assertEquals(nodeId,  captor.getValue().nodeId);
	}

	@Ignore("?") @Test
	public void testListenersAreRegisteredOnNodeStart() throws Exception {
		hostRuntime.start();
		verify(messaging).addMessageListener(same(Context.GLOBAL_TOPIC), any(HostRuntimeMessageListener.class));
	}

	@Ignore("?")
	@Test
	public void testNodeInfoIsRegisteredOnNodeStart() throws Exception {
		List<Object> list = new ArrayList<>();
		when(dataPersistence.getList(Context.NODE_INFO_LIST)).thenReturn(list);

		hostRuntime.start();

		assertEquals(((HostRuntimeNodeInfo) list.get(0)).nodeId, nodeId);
	}

	@Test
	public void testListenerRegisteredBeforeNodeInfoStoredOnNodeStart() throws Exception {
		// Ordering is very important, because if we register new host runtime
		// and someone send us some message immediately, we have to have
		// prepared all message dispatchers/listeners already
		@SuppressWarnings("unchecked")
		List<Object> list = mock(List.class);
		when(dataPersistence.getList(Context.NODE_INFO_LIST)).thenReturn(list);

		hostRuntime.start();

		InOrder inOrder = inOrder(messaging, list);
		inOrder.verify(messaging).addMessageListener(any(Context.class), any(HostRuntimeMessageListener.class));
		inOrder.verify(list).add(any(HostRuntimeNodeInfo.class));
	}

	@Test
	public void testSendingNodeRegisteredMessage() throws Exception {
		NodeRegisteredMessage msg = new NodeRegisteredMessage();
		hostRuntime.sendNodeRegisteredMessage(msg);
		verify(messaging).send(Context.GLOBAL_TOPIC, msg);
	}

	@Test
	public void testSendingNodeTerminatedMessage() throws Exception {
		NodeTerminatedMessage msg = new NodeTerminatedMessage();
		hostRuntime.sendNodeTerminatedMessage(msg);
		verify(messaging).send(Context.GLOBAL_TOPIC, msg);
	}

	@Test
	public void testSendingTaskStartedMessage() throws Exception {
		TaskStartedMessage msg = new TaskStartedMessage();
		hostRuntime.sendTaskStartedMessage(msg);
		verify(messaging).send(Context.GLOBAL_TOPIC, msg);
	}

	@Test
	public void testSendingTaskFinisheddMessage() throws Exception {
		TaskFinishedMessage msg = new TaskFinishedMessage();
		hostRuntime.sendTaskFinishedMessage(msg);
		verify(messaging).send(Context.GLOBAL_TOPIC, msg);
	}

	@Test
	public void testSendingTaskKilledMessage() throws Exception {
		TaskKilledMessage msg = new TaskKilledMessage();
		hostRuntime.sendTaskKilledMessage(msg);
		verify(messaging).send(Context.GLOBAL_TOPIC, msg);
	}

}
