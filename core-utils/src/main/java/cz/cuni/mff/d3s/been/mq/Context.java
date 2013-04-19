package cz.cuni.mff.d3s.been.mq;

import org.jeromq.ZMQ;

/**
 * @author Martin Sixta
 */
public class Context {
	private static ZMQ.Context context = null;
	private static int refCount = 0;
	private static final int ZMQ_IO_THREADS = 1;

	public synchronized static ZMQ.Context getReference() {
		if (context == null) {
			context = ZMQ.context(ZMQ_IO_THREADS);
		}

		++refCount;
		return context;
	}

	public static synchronized void releaseContext() {
		if (refCount == 0) {
			throw new IllegalStateException("No context to be released");
		}

		--refCount;

		if (refCount == 0) {
			context.term();
		}
	}
}
