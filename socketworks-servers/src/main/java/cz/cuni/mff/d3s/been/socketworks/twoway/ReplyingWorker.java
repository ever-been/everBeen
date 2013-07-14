package cz.cuni.mff.d3s.been.socketworks.twoway;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.socketworks.SocketHandlerException;

final class ReplyingWorker implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(ReplyingWorker.class);

	private final FrameSink replySink;
	private ReadReplyHandler handler;
	private Frames work;
	private Object runLock;

	private ReplyingWorker(FrameSink replySink) {
		this.replySink = replySink;
		this.runLock = new Object();
	}

	/**
	 * Create a worker that dumps the responses to a specific {@link FrameSink}.
	 * 
	 * @param replySink
	 *          Sink to dump responses to
	 * 
	 * @return A ready worker
	 */
	public static ReplyingWorker create(FrameSink replySink) {
		return new ReplyingWorker(replySink);
	}

	/**
	 * Set up input and processor for this thread.
	 * 
	 * @param work
	 *          Set the work that this handler should process
	 * @param handler
	 *          Set the handler which will perform this worker's logic
	 */
	void setUp(ReadReplyHandler handler, Frames work) {
		// we don't want people to tamper with our data while processing
		synchronized (runLock) {
			this.work = work;
			this.handler = handler;
		}
	}

	@Override
	public void run() {
		// lock here to prevent input data from being changed under our hands 
		synchronized (runLock) {
			if (work == null) {
				log.error("No data to process");
				return;
			}

			if (handler == null) {
				log.error("No handler set");
				return;
			}

			final Frames response = Frames.create();
			final Iterator<byte[]> wi = work.iterator();

			// copy header with client ID
			if (!wi.hasNext()) {
				log.error("No header in supplied message ({})", work.toString());
				return;
			}
			response.add(wi.next());

			// copy separator
			if (!wi.hasNext()) {
				log.error("No separator in supplied message ({})", work.toString());
				return;
			}
			response.add(wi.next());

			// handle content
			if (!wi.hasNext()) {
				log.error("No content in supplied message ({})", work.toString());
			}
			String handlerResponse = "";
			try {
				handlerResponse = handleFrame(wi.next());
			} catch (MessagingException e) {
				log.error("Transport logic error on message {}", work.toString(), e);
			} catch (SocketHandlerException e) {
				log.error("Handler error on message {}", work.toString(), e);
			} catch (InterruptedException e) {
				log.error("Handler {} interrupted", handler.toString(), e);
			} catch (Throwable t) {
				log.error("Unknown error on message {}", work.toString(), t);
			} finally {
				response.add(handlerResponse.getBytes());
				try {
					replySink.receiveFromBuddy(response);
				} catch (MessagingException e) {
					log.error("Failed to send reply, thread pair will block", e);
				}
				if (wi.hasNext()) {
					log.warn("Trailing content (from frame 3 onward) truncated for message {}", work.toString());
				}
			}
		}

		// give the user a clue that he can recycle his handler
		handler.markAsRecyclable();
	}

	/**
	 * Crunch the (presumably) last frame of work with {@link #handler} and return
	 * the response.
	 * 
	 * @param frame
	 *          Frame to crunch
	 * 
	 * @return Response to this frame
	 * 
	 * @throws MessagingException
	 *           In case the underlying message channels go bad
	 * @throws SocketHandlerException
	 *           If the user signals an error or supplies no response
	 * @throws InterruptedException
	 *           If the user handler gets interrupted
	 */
	private final
			String
			handleFrame(byte[] frame) throws MessagingException, SocketHandlerException, InterruptedException {
		final String frameContent = new String(frame);
		final String responseContent = handler.handle(frameContent);
		if (responseContent == null) {
			throw new SocketHandlerException(String.format(
					"Handler %s provided no response for message (%s)",
					handler.toString(),
					work.toString()));
		}
		return responseContent;
	}
}
