package cz.cuni.mff.d3s.been.taskapi;

import cz.cuni.mff.d3s.been.mq.IMessageQueue;
import cz.cuni.mff.d3s.been.mq.Messaging;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.socketworks.NamedSockets;

/** Factory for result serialization facade */
final class ResultFacadeFactory {

	private static IMessageQueue<String> resultQueue = null;
	private static JSONResultFacade resultFacade = null;

	private ResultFacadeFactory() {}

	/**
	 * Get a shared instance of {@link ResultFacade} for this task.
	 *
	 * @return The facade shared instance
	 */
	static synchronized ResultFacade getResultFacade() {
		if (resultFacade == null) {
			resultQueue = Messaging.createTaskQueue(NamedSockets.TASK_RESULT_PERSIST_0MQ.getConnection());
			resultFacade = JSONResultFacade.create(resultQueue);
		}
		return  resultFacade;
	}

	static synchronized void quit() throws MessagingException {
		if (resultFacade != null) {
			resultFacade.purge();
			resultFacade = null;
			resultQueue.terminate();
		}
	}

	/**
	 * Set task id to the facade's shared instance
	 *
	 * @param id Task id to set
	 */
	static void setTaskId(String id) {
		resultFacade.taskId = id;
	}

	/**
	 * Set the context id to the facade's shared instance
	 *
	 * @param id Context id to set
	 */
	static void setContextId(String id) {
		resultFacade.contextId = id;
	}

	/**
	 * Set the benchmark id to the facade's shared instance
	 *
	 * @param id Benchmark id to set
	 */
	static void setBenchmarkId(String id) {
		resultFacade.benchmarkId = id;
	}
}
