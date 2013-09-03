package cz.cuni.mff.d3s.been.mq;

import org.jeromq.ZMQ;
import org.jeromq.ZMQ.Poller;
import org.jeromq.ZMQ.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A singleton provider for {@link ZMQContext} instances.
 * 
 * @author Martin Sixta
 * @author darklight
 */
public final class Context implements ZMQContext {
	private static final Logger log = LoggerFactory.getLogger(Context.class);

	// STATIC FIELDS

	/** Actual static {@link org.jeromq.ZMQ.Context} */
	private static ZMQ.Context zctx = null;
	/** Number of references provided */
	private static int refCount = 0;
	/** Number of I/O threads 0MQ uses */
	private static final int ZMQ_IO_THREADS = 1;

	// INSTANCE FIELDS

	/**
	 * Whether this {@link ZMQContext} is still valid (i.e. hasn't been released
	 * yet)
	 */
	private boolean valid = true;

	/**
	 * Deny instantiation, we only want to have this object created via
	 * {@link #getReference()}.
	 */
	private Context() {}

	@Override
	public synchronized void term() throws MessagingException {
		checkValid();
		valid = false;
		releaseContext();
	}

	@Override
	public Socket socket(int type) throws MessagingException {
		checkValid();
		return zctx.socket(type);
	}

	@Override
	public Poller poller() throws MessagingException {
		checkValid();
		return zctx.poller();
	}

	@Override
	public Poller poller(int size) throws MessagingException {
		checkValid();
		return zctx.poller(size);
	}

	private void checkValid() throws MessagingException {
		if (!valid) {
			throw new MessagingException("Invoked operation on released context.");
		}
	}

	/**
	 * Get a counted reference to the 0MQ context
	 *
	 * @return The reference
	 */
	public synchronized static ZMQContext getReference() {
		if (zctx == null) {
			zctx = ZMQ.context(ZMQ_IO_THREADS);
		}

		++refCount;
		return new Context();
	}

	private static synchronized void releaseContext() {
		if (refCount == 0) {
			throw new IllegalStateException("No context to be released");
		}

		--refCount;

		if (refCount == 0) {
			try {
				zctx.term();
			} catch (RuntimeException e) {
				log.warn("0MQ context termination failed due to underlying runtime exception", e);
			}
			zctx = null;
		}
	}

}
