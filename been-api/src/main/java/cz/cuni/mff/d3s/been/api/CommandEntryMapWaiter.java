package cz.cuni.mff.d3s.been.api;

import java.util.Queue;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import cz.cuni.mff.d3s.been.core.protocol.command.CommandEntry;

/**
 * Hazelcast map entry listener for {@link CommandEntry} entities, which listens
 * for UPDATE / REMOVE / EVICT operations. ADD operation is quietly ignored. <br/>
 * <br/>
 * Each updated/removed/evicted entry is added to queue given in constructor.
 * 
 * @author donarus
 */
final class CommandEntryMapWaiter implements EntryListener<Long, CommandEntry> {

	/** the queue into which the event should be added */
	private Queue queue;

	/**
	 * Default constructor, creates an instance with the specified queue.
	 * 
	 * @param queue
	 *          the queue into which the event should be added
	 */
	public CommandEntryMapWaiter(Queue<CommandEntry> queue) {
		this.queue = queue;
	}

	/**
	 * IGNORED - just must be here because of parent interface
	 * 
	 * @param event
	 *          the event that occurred
	 */
	@Override
	public final void entryAdded(EntryEvent<Long, CommandEntry> event) {
		// ignore
	}

	/**
	 * Adds value from given event to queue
	 * 
	 * @param event
	 *          the event that occurred
	 */
	@Override
	public final void entryRemoved(EntryEvent<Long, CommandEntry> event) {
		queue.add(event.getValue());
	}

	/**
	 * Adds value from given event to queue
	 * 
	 * @param event
	 *          the event that occurred
	 */
	@Override
	public final void entryUpdated(EntryEvent<Long, CommandEntry> event) {
		queue.add(event.getValue());
	}

	/**
	 * Adds value from given event to queue
	 * 
	 * @param event
	 *          the event that occurred
	 */
	@Override
	public void entryEvicted(EntryEvent<Long, CommandEntry> event) {
		queue.add(event.getValue());
	}

}
