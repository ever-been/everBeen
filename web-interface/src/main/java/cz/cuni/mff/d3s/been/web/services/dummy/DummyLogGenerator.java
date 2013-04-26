package cz.cuni.mff.d3s.been.web.services.dummy;

import cz.cuni.mff.d3s.been.web.services.websockets.LogEvent;
import cz.cuni.mff.d3s.been.web.services.websockets.WebSocketServer;

import java.util.Date;

/**
 * User: donarus Date: 4/25/13 Time: 11:05 PM
 */
public class DummyLogGenerator {

	private volatile boolean running;

	public DummyLogGenerator(final WebSocketServer server) {
		this.running = true;

        Thread t = new Thread() {
            @Override
            public void run() {
                server.newEvent(new LogEvent(LogEvent.Type.INFO, new Date().toString() + " - dummy message"));
            }
        };

        t.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                DummyLogGenerator.this.running = false;
            }
        });
	}



}
