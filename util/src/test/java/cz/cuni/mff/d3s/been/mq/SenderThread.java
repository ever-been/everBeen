package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;

/**
 * @author Martin Sixta
 */
class SenderThread<T extends Serializable> extends Thread {
	private final IMessageSender<T> sender;
	private final T message;
	private final int count;

	SenderThread(IMessageSender<T> sender, T message, int count) {
		this.sender = sender;
		this.message = message;
		this.count = count;
	}

	@Override
	public void run() {
		try {
			for (int i = 0; i < count; ++i) {
				sender.send(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
