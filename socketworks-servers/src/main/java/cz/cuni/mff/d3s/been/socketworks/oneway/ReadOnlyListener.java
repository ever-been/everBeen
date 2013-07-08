package cz.cuni.mff.d3s.been.socketworks.oneway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.mq.IMessageReceiver;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.socketworks.SocketHandlerException;

/**
 * Message queue listener for one-way (read-only) message queues.
 * 
 * @author darklight
 */
final class ReadOnlyListener extends Thread {
	private static final Logger log = LoggerFactory.getLogger(ReadOnlyListener.class);

	private final ReadOnlyHandler handler;
	private final IMessageReceiver<String> receiver;

	private ReadOnlyListener(IMessageReceiver<String> receiver, ReadOnlyHandler handler) {
		this.receiver = receiver;
		this.handler = handler;
	}

	public static final ReadOnlyListener create(IMessageReceiver<String> receiver, ReadOnlyHandler handler) {
		return new ReadOnlyListener(receiver, handler);
	}

	@Override
	public void run() {
        setName(String.format("%s(%s)", getClass().getSimpleName(), handler.getClass().getSimpleName()));
		while (true) {
			try {
				final String message = receiver.receive();
				if (ReadOnlyGuard.STOP_MESSAGE.equals(message)) {
					log.info("Received stop message");
					break;
				}
				log.debug(message);
				handler.handle(message);
			} catch (SocketHandlerException e) {
				log.warn("Could not handle incoming message.:", e);
			} catch (MessagingException e) {
				log.warn("Receiver thread terminating with transport error", e);
				break;
			} catch (Throwable t) {
				log.error("Unknown exception on listener, terminating with error.", t);
				break;
			}
		}
		log.info("Listener terminating");
	}
}
