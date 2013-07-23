package cz.cuni.mff.d3s.been.api;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import cz.cuni.mff.d3s.been.core.protocol.command.CommandEntry;

import java.util.Queue;


/**
 * Hazelcast map entry listener for {@link CommandEntry} entities, which listens for
 * UPDATE / REMOVE / EVICT operations. ADD operation is quietly ignored.
 * <br/><br/>
 * Each updated/removed/evicted entry is added to queue given in constructor.
 *
 * @author donarus
 */
final class CommandEntryMapWaiter implements EntryListener<Long, CommandEntry> {

    private Queue queue;


    public CommandEntryMapWaiter(Queue<CommandEntry> queue) {
        this.queue = queue;
    }

    /**
     * IGNORED - just must be here because of parent interface
     *
     * @param event
     */
    @Override
    public final void entryAdded(EntryEvent<Long, CommandEntry> event) {
        // ignore
    }

    /**
     * Adds value from given event to queue
     *
     * @param event
     */
    @Override
    public final void entryRemoved(EntryEvent<Long, CommandEntry> event) {
        queue.add(event.getValue());
    }

    /**
     * Adds value from given event to queue
     *
     * @param event
     */
    @Override
    public final void entryUpdated(EntryEvent<Long, CommandEntry> event) {
        queue.add(event.getValue());
    }

    /**
     * Adds value from given event to queue
     *
     * @param event
     */
    @Override
    public void entryEvicted(EntryEvent<Long, CommandEntry> event) {
        queue.add(event.getValue());
    }

}