package cz.cuni.mff.d3s.been.hostruntime;

import static cz.cuni.mff.d3s.been.core.task.TaskExclusivity.*;
import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hazelcast.core.IMap;

import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.cluster.context.Runtimes;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskDescriptor;
import cz.cuni.mff.d3s.been.core.task.TaskExclusivity;
import cz.cuni.mff.d3s.been.hostruntime.task.TaskHandle;
import cz.cuni.mff.d3s.been.hostruntime.task.TaskProcess;

/**
 * 
 * 
 * @author Martin Sixta
 */
public class ProcessManagerContextTest extends Assert {

	private static final String runtimeId = "851e0f34-9271-4c2f-a970-4cbcac5f033e";
	private static final String taskId1 = "bd6dbca3-9825-4b16-9bbe-447e6333a3d9";
	private static final String taskId2 = "dc7750c7-f661-4c26-bf82-ccadeba3f524";

	private static final String contextId1 = "1234567890";
	private static final String contextId2 = "0987654321";

	@Mock
	private TaskProcess process1;

	@Mock
	private Runtimes runtimes;

	@Mock
	private ClusterContext clusterContext;

	@Mock
	private TaskDescriptor taskDescriptor1;

	@Mock
	private TaskDescriptor taskDescriptor2;

	@Mock
	private TaskHandle taskHandle1;

	@Mock
	private TaskHandle taskHandle2;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(clusterContext.getRuntimes()).thenReturn(runtimes);

	}

	// ------------------------------------------------------------------------
	// IN NON_EXCLUSIVE
	// ------------------------------------------------------------------------

	@Test
	public void testAcceptNonExclusiveInNonExclusive() {
		RuntimeInfo runtimeInfo = createRuntimeInfo();
		testAcceptTaskTemplate(NON_EXCLUSIVE, null, runtimeInfo);
	}

	@Test
	public void testAcceptSecondNonExclusiveInNonExclusive() {
		testAcceptSecondTemplate(NON_EXCLUSIVE, NON_EXCLUSIVE, null);
	}

	@Test(expected = IllegalStateException.class)
	public void testRejectSecondExclusiveInNonExclusive() {
		testRejectSecondTemplate(NON_EXCLUSIVE, EXCLUSIVE, null);
	}

	@Test(expected = IllegalStateException.class)
	public void testRejectSecondContextExclusiveInNonExclusive() {
		testRejectSecondTemplate(NON_EXCLUSIVE, CONTEXT_EXCLUSIVE, null);
	}

	// ------------------------------------------------------------------------
	// IN EXCLUSIVE
	// ------------------------------------------------------------------------
	@Test
	public void testAcceptExclusiveInNonExclusive() {
		RuntimeInfo runtimeInfo = createRuntimeInfo();
		testAcceptTaskTemplate(EXCLUSIVE, taskId1, runtimeInfo);

	}

	@Test(expected = IllegalStateException.class)
	public void testRejectSecondNonExclusiveInExclusive() {
		testRejectSecondTemplate(EXCLUSIVE, NON_EXCLUSIVE, taskId1);
	}

	@Test(expected = IllegalStateException.class)
	public void testRejectSecondExclusiveInExclusive() {
		testRejectSecondTemplate(EXCLUSIVE, EXCLUSIVE, taskId1);
	}

	@Test(expected = IllegalStateException.class)
	public void testRejectSecondContextExclusiveInExclusive() {
		testRejectSecondTemplate(EXCLUSIVE, CONTEXT_EXCLUSIVE, taskId1);
	}

	// ------------------------------------------------------------------------
	// IN CONTEXT_EXCLUSIVE
	// ------------------------------------------------------------------------

	@Test
	public void testAcceptContextExclusiveInNonExclusive() {
		RuntimeInfo runtimeInfo = createRuntimeInfo();
		testAcceptTaskTemplate(CONTEXT_EXCLUSIVE, contextId1, runtimeInfo);
	}

	@Test
	public void testAcceptSecondContextExclusiveInContextExclusiveSameContext() {
		testAcceptSecondTemplate(CONTEXT_EXCLUSIVE, CONTEXT_EXCLUSIVE, contextId1);

	}

	@Test(expected = IllegalStateException.class)
	public void testRejectSecondContextExclusiveInContextExclusiveDifferentContext() {

		RuntimeInfo runtimeInfo = createRuntimeInfo();

		ProcessManagerContext managerContext = new ProcessManagerContext(clusterContext, runtimeInfo);

		setUpTaskHandle(taskHandle1, CONTEXT_EXCLUSIVE, taskId1, contextId1);
		setUpTaskHandle(taskHandle2, CONTEXT_EXCLUSIVE, taskId2, contextId2);

		try {
			managerContext.tryAcceptTask(taskHandle1);
		} catch (Exception e) {
			assertFalse(true); // no exception allowed
		}

		try {
			managerContext.tryAcceptTask(taskHandle2);
		} finally {
			verifySetAcceptedCalled(taskHandle1);
			verifySetAcceptedNotCalled(taskHandle2);

			verifyMangerContext(managerContext, 1, CONTEXT_EXCLUSIVE, contextId1, runtimeInfo);

		}

	}

	@Test(expected = IllegalStateException.class)
	public void testRejectSecondNonExclusiveInContextExclusive() {
		testRejectSecondTemplate(CONTEXT_EXCLUSIVE, NON_EXCLUSIVE, contextId1);
	}

	@Test(expected = IllegalStateException.class)
	public void testRejectSecondExclusiveInContextExclusive() {
		testRejectSecondTemplate(CONTEXT_EXCLUSIVE, EXCLUSIVE, contextId1);
	}

	// ------------------------------------------------------------------------
	// REMOVE
	// ------------------------------------------------------------------------

	@Mock
	TaskProcess taskProcess;

	@Mock
	IMap map;

	@Test
	public void testRemoveNonExclusiveLast() {
		testRemoveLastTemplate(NON_EXCLUSIVE, null);
	}

	@Test
	public void testRemoveExclusiveLast() {
		testRemoveLastTemplate(EXCLUSIVE, taskId1);
	}

	@Test
	public void testRemoveContextExclusiveLast() {
		testRemoveLastTemplate(CONTEXT_EXCLUSIVE, contextId1);
	}

	// ------------------------------------------------------------------------
	// AUXILIARIES
	// ------------------------------------------------------------------------

	private void testRejectSecondTemplate(TaskExclusivity firstExclusivity, TaskExclusivity secondExclusivity,
			String exclusivityId) {
		RuntimeInfo runtimeInfo = createRuntimeInfo();

		ProcessManagerContext managerContext = new ProcessManagerContext(clusterContext, runtimeInfo);

		setUpTaskHandle(taskHandle1, firstExclusivity, taskId1, contextId1);
		setUpTaskHandle(taskHandle2, secondExclusivity, taskId2, contextId2);

		try {
			managerContext.tryAcceptTask(taskHandle1);
		} catch (Exception e) {
			assertFalse(true);
		}

		try {
			managerContext.tryAcceptTask(taskHandle2);
		} finally {
			verifySetAcceptedCalled(taskHandle1);
			verifySetAcceptedNotCalled(taskHandle2);
			verifyMangerContext(managerContext, 1, firstExclusivity, exclusivityId, runtimeInfo);
		}
	}

	private ProcessManagerContext testAcceptTaskTemplate(TaskExclusivity exclusivity, String exclusiveId,
			RuntimeInfo runtimeInfo) {

		ProcessManagerContext managerContext = new ProcessManagerContext(clusterContext, runtimeInfo);

		setUpTaskHandle(taskHandle1, exclusivity, taskId1, contextId1);

		managerContext.tryAcceptTask(taskHandle1);

		verifySetAcceptedCalled(taskHandle1);
		verifyMangerContext(managerContext, 1, exclusivity, exclusiveId, runtimeInfo);

		return managerContext;

	}

	private void testAcceptSecondTemplate(TaskExclusivity firstExclusivity, TaskExclusivity secondExclusivity,
			String exclusiveId) {

		RuntimeInfo runtimeInfo = createRuntimeInfo();
		ProcessManagerContext managerContext = testAcceptTaskTemplate(firstExclusivity, exclusiveId, runtimeInfo);

		setUpTaskHandle(taskHandle2, secondExclusivity, taskId2, contextId1);

		managerContext.tryAcceptTask(taskHandle2);

		verifySetAcceptedCalled(taskHandle1, taskHandle2);
		verifyMangerContext(managerContext, 2, firstExclusivity, exclusiveId, runtimeInfo);
	}

	private void testRemoveLastTemplate(TaskExclusivity exclusivity, String exclusivityId) {
		RuntimeInfo runtimeInfo = createRuntimeInfo();
		ProcessManagerContext managerContext = testAcceptTaskTemplate(exclusivity, exclusivityId, runtimeInfo);

		managerContext.addTask(taskId1, taskProcess);

		assertEquals(1, managerContext.getTasksCount());

		when(clusterContext.getMap("BEEN_MAP_DEBUG_ASSISTANT")).thenReturn(map);

		managerContext.removeTask(taskHandle1);

		verifyMangerContext(managerContext, 0, NON_EXCLUSIVE, null, runtimeInfo);
	}

	private void setUpTaskHandle(TaskHandle taskHandle, TaskExclusivity exclusivity, String taskId, String contextId) {
		when(taskHandle.getTaskId()).thenReturn(taskId);
		when(taskHandle.getContextId()).thenReturn(contextId);
		when(taskHandle.getExclusivity()).thenReturn(exclusivity);
	}

	private void verifyMangerContext(ProcessManagerContext managerContext, int tasks, TaskExclusivity exclusivity,
			String exclusiveId, RuntimeInfo runtimeInfo) {
		assertEquals(tasks, managerContext.getTasksCount());
		assertEquals(tasks, runtimeInfo.getTaskCount());
		assertEquals(exclusivity.toString(), runtimeInfo.getExclusivity());
		assertEquals(exclusiveId, runtimeInfo.getExclusiveId());

	}

	private void verifySetAcceptedCalled(TaskHandle... handles) {
		for (TaskHandle handle : handles) {
			verify(handle, times(1)).setAccepted();
		}
	}

	private void verifySetAcceptedNotCalled(TaskHandle... handles) {
		for (TaskHandle handle : handles) {
			verify(handle, times(0)).setAccepted();
		}
	}

	private RuntimeInfo createRuntimeInfo() {
		return new RuntimeInfo().withId(runtimeId);
	}

}
