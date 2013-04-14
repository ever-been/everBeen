package cz.cuni.mff.d3s.been.hostruntime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.mq.IMessageReceiver;
import cz.cuni.mff.d3s.been.mq.MessagingException;

final class QueueReaderThread extends Thread {
	private static final Logger log = LoggerFactory.getLogger(QueueReaderThread.class);

	private final IMessageReceiver<String> receiver;

	QueueReaderThread(TaskMessageDispatcher taskMessageDispatcher, IMessageReceiver<String> receiver) {
		this.receiver = receiver;
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				String message = receiver.receive();
				log.debug(message);
				if (TaskMessageDispatcher.STOP_MESSAGE.equals(message)) {
					break;
				}
			} catch (RuntimeException e) {
				break;
			} catch (MessagingException e) {
				log.warn("Task message log listener received invalid message.");
			}
		}
	}
}
