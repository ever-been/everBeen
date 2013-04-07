package cz.cuni.mff.d3s.been.hostruntime;

import org.jeromq.ZMQ.Socket;

final class QueueReaderThread extends Thread {
	private TaskMessageDispatcher taskMessageDispatcher;
	private final Socket receiver;
	QueueReaderThread(TaskMessageDispatcher taskMessageDispatcher, Socket receiver) {
		this.taskMessageDispatcher = taskMessageDispatcher;
		this.receiver = receiver;
	}
	@Override
	public void run() {
		while (!isInterrupted()) {
			String message = new String(receiver.recv(0)).trim();
			TaskMessageDispatcher.log.debug(message);
			if (TaskMessageDispatcher.STOP_MESSAGE.equals(message)) {
				this.interrupt();
			} else {
				this.taskMessageDispatcher.processMessage(message);
			}
		}

		receiver.close();
	}
}