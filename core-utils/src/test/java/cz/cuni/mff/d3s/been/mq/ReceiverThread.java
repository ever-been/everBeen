package cz.cuni.mff.d3s.been.mq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Martin Sixta
 */
final class ReceiverThread<T extends Serializable> extends Thread {
	private final IMessageReceiver<T> receiver;
	private final int count;
	List<T> list = new ArrayList<>();

	ReceiverThread(IMessageReceiver<T> receiver, int count) {
		this.receiver = receiver;
		this.count = count;
	}

	@Override
	public void run() {
		try {
			for (int i = 0; i < count; ++i) {
				list.add(receiver.receive());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Collection<T> getReceivedMessages() {
		return list;
	}
}
