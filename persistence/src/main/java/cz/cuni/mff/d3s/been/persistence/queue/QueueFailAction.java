package cz.cuni.mff.d3s.been.persistence.queue;

import com.hazelcast.core.IQueue;
import cz.cuni.mff.d3s.been.core.persistence.EntityCarrier;
import cz.cuni.mff.d3s.been.persistence.FailAction;

/**
 * Created with IntelliJ IDEA.
 * User: darklight
 * Date: 5/4/13
 * Time: 3:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueueFailAction<T> implements FailAction<T> {
    private final IQueue<T> queue;

    QueueFailAction(IQueue<T> queue) {
        this.queue = queue;
    }

    @Override
    public void perform(T on) throws InterruptedException {
        queue.put(on);
    }
}
