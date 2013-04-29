package cz.cuni.mff.d3s.been.web.services;

import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;
import org.apache.tapestry5.ioc.Invokable;
import org.apache.tapestry5.ioc.services.ParallelExecutor;
import org.lazan.t5.cometd.services.PushManager;

import java.util.Collection;

/**
 * @author Kuba Brecka
 */
public class LiveFeedServiceImpl implements LiveFeedService {

	private final PushManager pushManager;
	private final ParallelExecutor executor;
	private final BeenApiService api;

	public LiveFeedServiceImpl(PushManager pushManager, ParallelExecutor executor, BeenApiService api) {
		this.pushManager = pushManager;
		this.executor = executor;
		this.api = api;

		executor.invoke(new LogFeedWorker());
	}


	private class LogFeedWorker implements Invokable<Object> {

		@Override
		public Object invoke() {
			while (true) {

				if (api.isConnected()) {
					try {
						Collection<RuntimeInfo> runtimeInfoCollection = api.getApi().getRuntimes();
						pushManager.broadcast("/runtimes", runtimeInfoCollection);

						Collection<TaskEntry> taskEntries = api.getApi().getTasks();
						pushManager.broadcast("/tasks", taskEntries);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
