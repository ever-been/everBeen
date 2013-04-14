package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;

/**
 * @author Martin Sixta
 */
class SenderThread<T extends Serializable> extends Thread {
	private final IMessageSender<T> sender;
	private final T message;

	SenderThread(IMessageSender<T> sender, T message) {
		this.sender = sender;
		this.message = message;
	}

	@Override
	public void run() {
		try {
			sender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
