package cz.cuni.mff.d3s.been.hostruntime;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.cluster.Reapable;
import cz.cuni.mff.d3s.been.cluster.Reaper;
import cz.cuni.mff.d3s.been.cluster.Service;
import cz.cuni.mff.d3s.been.cluster.ServiceException;
import cz.cuni.mff.d3s.been.cluster.context.ClusterContext;
import cz.cuni.mff.d3s.been.core.TaskMessageType;
import cz.cuni.mff.d3s.been.mq.*;

/**
 * Purpose of this service is to allow communication between HostRuntime and
 * running tasks.
 * 
 * Usage of this class
 * 
 * @author Tadeáš Palusga
 * 
 */

public class TaskMessageDispatcher implements Service, Reapable {

	static final Logger log = LoggerFactory.getLogger(TaskMessageDispatcher.class);

	/**
	 * THIS MESSAGE STRING IS FOR PRIVATE USE ONLY. Purpose of this message is to
	 * kill the receiver immediately.
	 */
	static final String STOP_MESSAGE = "XX1456123_STOP_RECEIVER_MESSAGE";

	/**
	 * identification of not bounded port
	 */
	static final int NOT_BOUND_PORT = -1;

	/** Cluster context */
	private final ClusterContext clusterContext;

	/**
	 * Message queue listening for message from tasks.
	 */
	int receiverPort = NOT_BOUND_PORT;

	Thread msgQueueReader;
	IMessageQueue<String> taskMQ;

	/**
	 * Receiver of task messages.
	 */
	IMessageReceiver<String> receiver;

	public TaskMessageDispatcher(ClusterContext clusterContext) {

		this.clusterContext = clusterContext;
	}
	/**
	 * Starts new receiver thread if not already running.
	 */
	@Override
	public void start() throws ServiceException {
		taskMQ = Messaging.createTcpQueue("localhost");
		try {
			receiver = taskMQ.getReceiver();
		} catch (MessagingException e) {
			throw new ServiceException("Cannot initialize ZMQ message receiver", e);
		}

		receiverPort = receiver.getPort();

		msgQueueReader = new QueueReaderThread(clusterContext, receiver);
		msgQueueReader.start();
	}

	/**
	 * Stop the task message listener.
	 */
	@Override
	public void stop() {
		try {
			try {
				poisonReader();
				msgQueueReader.join();
			} catch (MessagingException e) {
				log.warn("Failed to poison task log reader, socket leak is likely.", e);
			}
		} catch (InterruptedException e) {
			log.warn("Interrupted while closing task message listener connections. Socket leaks are likely.");
		}
		taskMQ.terminate();
		taskMQ = null;
		receiverPort = TaskMessageDispatcher.NOT_BOUND_PORT;
	}

	@Override
	public Reaper createReaper() {
		return new Reaper() {
			@Override
			protected void reap() throws InterruptedException {
				TaskMessageDispatcher.this.stop();
			}
		};
	}

	private void poisonReader() throws MessagingException {
		IMessageSender<String> sender = taskMQ.createSender();
		sender.send(STOP_MESSAGE);
		sender.close();
	}

	/**
	 * Returns port on which the running receiver has been started
	 * 
	 * @return port on which the receiver is running
	 */
	public int getReceiverPort() {
		return receiverPort;
	}
}
