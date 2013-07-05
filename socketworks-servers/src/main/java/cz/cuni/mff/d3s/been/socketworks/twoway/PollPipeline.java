package cz.cuni.mff.d3s.been.socketworks.twoway;

import java.util.List;

import org.jeromq.ZMQ;
import org.jeromq.ZMQ.Poller;
import org.jeromq.ZMQ.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.mq.Context;
import cz.cuni.mff.d3s.been.mq.MessagingException;
import cz.cuni.mff.d3s.been.mq.ZMQContext;

/**
 * A thread that helps pipeline requests (events) on multiple connected 0MQ
 * sockets using a single poll-driven loop.
 * 
 * This class serves to unify the logic of the particular socket combination
 * into one visible whole.
 * 
 * @author darklight
 * 
 */
final class PollPipeline extends Thread {

	private static final Logger log = LoggerFactory.getLogger(PollPipeline.class);
	private static final long POLL_TIMEOUT = 300;

	private static final int NOFLAGS = 0;

	private final ZMQ.Poller poller;
	private final List<PollPartaker> partakers;
	private final ZMQContext ctx;
	private final FrameForwardMapper forwards;
	private volatile boolean keepRunning = true;

	private PollPipeline(ZMQContext context, Poller poller, List<PollPartaker> partakers, FrameForwardMapper forwards) {
		this.ctx = context;
		this.poller = poller;
		this.partakers = partakers;
		this.forwards = forwards;
	}

	/**
	 * Create a 0MQ poll pipeline.
	 * 
	 * @param partakers
	 *          Participants of the polling
     *
	 * @return The poll pipeline
	 * 
	 * @throws MessagingException
	 *           If an invalid context is in play while the pipeline gets created
	 */
	public static
			PollPipeline
			create(List<PollPartaker> partakers, FrameForwardMapper forwards) throws MessagingException {
		ZMQContext ctx = Context.getReference();
		Poller poller = null;
		try {
			poller = ctx.poller(partakers.size());
		} catch (MessagingException e) {
			ctx.term();
		}
		return new PollPipeline(ctx, poller, partakers, forwards);
	}

	public void doStop() {
		keepRunning = false;
	}

	@Override
	public void run() {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (PollPartaker partaker: partakers) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(partaker.getClass().getSimpleName());
        }
        setName(String.format("%s(%s)", getClass().getSimpleName(),sb.toString()));
		super.run();

		for (PollPartaker partaker : partakers) {
			poller.register(partaker.getSocket(), partaker.getPollType());
		}

		while (keepRunning) {
			// Set up a timeout here so if this thread gets a stop request,
			// we have a chance to find out.
			if (poller.poll(POLL_TIMEOUT) == 0) {
				continue;
			}

			// Poll found something, select the partaker and have him handle his thing.
			for (int i = 0; i < partakers.size(); ++i) {
                log.debug("Got something");
				if (poller.pollin(i)) {
                    log.debug("It belongs to {}", partakers.get(i));
					// Make sure to crunch the entire multi-frame message.
					final Frames frames = Frames.create();
					do {
						frames.add(poller.getSocket(i).recv(NOFLAGS));
						// TODO check whether the recv can't eat two consecutive messages within one cycle 
					} while (poller.getSocket(i).hasReceiveMore());
					try {
						partakers.get(i).receiveFromWire(frames);
					} catch (MessagingException e) {
						log.error("Failed to delegate frames {}", frames.toString(), e);
					}
				}
			}
		}

		log.debug("Poll pipeline about to terminate");

		for (PollPartaker partaker : partakers) {
			poller.unregister(partaker.getSocket());
		}

		log.debug("Poll pipeline about to release ZMQ context");

		try {
			ctx.term();
		} catch (MessagingException e) {
			log.error("Failed to terminate ZMQContext", e);
		}
		log.debug("Poll pipeline terminated");
	}
}
