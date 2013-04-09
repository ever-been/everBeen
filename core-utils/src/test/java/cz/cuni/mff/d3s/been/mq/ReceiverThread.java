package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;

/**
 * @author Martin Sixta
 */
final class ReceiverThread<T extends Serializable> extends Thread {
	private final IMessageReceiver<T> receiver;
	private T receivedMessage;

	ReceiverThread(IMessageReceiver<T> receiver) {
		this.receiver = receiver;
	}

	@Override
	public void run() {
		try {
			receivedMessage = receiver.receive();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	T getReceivedMessage() {
		return receivedMessage;
	}
}
