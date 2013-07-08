package cz.cuni.mff.d3s.been.persistence.queue;

import com.hazelcast.core.IQueue;
import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.persistence.Poll;

/**
 * Created with IntelliJ IDEA.
 * User: darklight
 * Date: 5/4/13
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueuePoll<T> implements Poll<T> {
    private final IQueue<T> queue;

    QueuePoll(IQueue<T> queue) {
        this.queue = queue;
    }

    @Override
    public T perform() {
        return queue.poll();
    }
}
