package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.cluster.*;

import java.util.Random;


public class HostRuntime implements MessageListener {

	public static final String HOSTRUNTIME_TOPIC = "BEEN_HOSTRUNTIME_TOPIC";

	public static void main(String[] args) {

		HostRuntime listener = new HostRuntime();

		Member member =  Factory.createMember("hazelcast", null);
		member.connect();
		Messaging messaging = member.getMessaging();



		messaging.addMessageListener(HOSTRUNTIME_TOPIC, listener);

		Random r = new Random();
		int myID = r.nextInt(1000);

		while (true) {
			int sleepTime = r.nextInt(9) + 1;

			System.out.printf("Sleeping for %d seconds\n", sleepTime );

			try {
				Thread.sleep(sleepTime * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			int messageType = r.nextInt(20);
			Message message = new Message(messageType, "ID: " + myID);
			messaging.send(HOSTRUNTIME_TOPIC, message);


		}

	}

	@Override
	public void onMessage(Message message) {
		System.out.printf("Message received: type=%d, text=%s\n", message.getType(), message.getText());
	}
}
