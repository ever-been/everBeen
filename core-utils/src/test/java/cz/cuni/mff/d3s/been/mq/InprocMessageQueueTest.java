package cz.cuni.mff.d3s.been.mq;

import org.junit.Before;

import java.util.UUID;

/**
 * @author Martin Sixta
 */
public class InprocMessageQueueTest extends BasicQueueTests {

    private String queueName;

    @Before
    public void setupQueueName() {
        this.queueName = UUID.randomUUID().toString();
    }

	@Override
	protected IMessageQueue<String> getQueue() {
		return new InprocMessageQueue<String>(queueName);
	}
}
