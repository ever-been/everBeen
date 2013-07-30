package cz.cuni.mff.d3s.been.web.services;

import java.util.Collection;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import cz.cuni.mff.d3s.been.api.BeenApiException;
import org.apache.tapestry5.ioc.Invokable;
import org.apache.tapestry5.ioc.services.ParallelExecutor;
import org.lazan.t5.cometd.services.PushManager;

import cz.cuni.mff.d3s.been.api.BeenApi;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.core.task.TaskEntry;

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

		private synchronized void broadcast(String topic, Object object) {
			pushManager.broadcast(topic, object);
		}

		@Override
		public Object invoke() {

			while (true) {

				while (!api.isConnected()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
				}

                try {

                    EntryListener<String, String> logsListener = new EntryListener<String, String>() {
                        @Override
                        public void entryAdded(EntryEvent<String, String> event) {
                            broadcast("/logs", event.getValue());
                        }

                        @Override
                        public void entryRemoved(EntryEvent<String, String> event) {
                        }

                        @Override
                        public void entryUpdated(EntryEvent<String, String> event) {
                            broadcast("/logs", event.getValue());
                        }

                        @Override
                        public void entryEvicted(EntryEvent<String, String> event) {
                        }
                    };





                    api.getApi().addLogListener(logsListener);
                } catch (BeenApiException e) {
                    e.printStackTrace();
                }

               while (true) {

					try {
						if (!api.isConnected()) {
							break;
						}

						Collection<RuntimeInfo> runtimeInfoCollection = api.getApi().getRuntimes();
						broadcast("/runtimes", runtimeInfoCollection);

						Collection<TaskEntry> taskEntries = api.getApi().getTasks();
						broadcast("/tasks", taskEntries);
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
				}

			}
		}
	}
}
