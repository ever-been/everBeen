package cz.cuni.mff.d3s.been.hostruntime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.MultiMap;

import cz.cuni.mff.d3s.been.cluster.Names;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.LogMessage;
import cz.cuni.mff.d3s.been.core.TaskMessageType;
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;
import cz.cuni.mff.d3s.been.mq.IMessageReceiver;
import cz.cuni.mff.d3s.been.mq.MessagingException;

final class QueueReaderThread extends Thread {
	private static final Logger log = LoggerFactory.getLogger(QueueReaderThread.class);

	private final IMessageReceiver<String> receiver;
	private final MultiMap<String, LogMessage> logMap;
	private static final String PREFIX_SEPARATOR = "#";

	QueueReaderThread(ClusterContext clusterContext, IMessageReceiver<String> receiver) {
		this.receiver = receiver;
		this.logMap = clusterContext.getMultiMap(Names.LOGS_MULTIMAP_NAME);

	}

	private final static String[] PREFIXES;

	static {
		TaskMessageType[] types = TaskMessageType.values();

		PREFIXES = new String[types.length];

		for (int i = 0; i < types.length; ++i) {
			PREFIXES[i] = types[i].toString();
		}
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				String message = receiver.receive();

				if (TaskMessageDispatcher.STOP_MESSAGE.equals(message)) {

					break;
				}

				log.debug(message);

				TaskMessageType messageType = getType(message);

				switch (messageType) {
					case LOG_MESSAGE:
						handleLogMessage(message);
						break;
					case UNKNOWN:
						break;
				}

			} catch (RuntimeException e) {
				break;
			} catch (MessagingException e) {
				log.warn("Task message log listener received invalid message.");
			}
		}
	}

	private void handleLogMessage(String message) {
		String prefix = TaskMessageType.LOG_MESSAGE.toString() + PREFIX_SEPARATOR;

		String json = message.substring(prefix.length());
		try {
			LogMessage logMessage = JSONUtils.deserialize(json, LogMessage.class);
			logMap.put(logMessage.getSenderId(), logMessage);
		} catch (JSONUtils.JSONSerializerException e) {
			String msg = String.format("Cannot deserialize log message %s", json);
			log.warn(msg, e);
		}
	}

	private TaskMessageType getType(String message) {
		for (String prefix : PREFIXES) {
			if (message.startsWith(prefix + PREFIX_SEPARATOR)) {
				return TaskMessageType.valueOf(prefix);
			}
		}

		return TaskMessageType.UNKNOWN;

	}
}
