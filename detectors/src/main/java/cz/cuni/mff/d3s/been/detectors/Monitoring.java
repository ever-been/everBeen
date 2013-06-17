package cz.cuni.mff.d3s.been.detectors;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.ri.MonitorSample;

/**
 * @author Kuba Brecka
 */
public class Monitoring {
	private static final Logger log = LoggerFactory.getLogger(Monitoring.class);

	private static int monitorInterval = 5000;

	private static volatile boolean monitoringRunning = false;
	private static Thread monitoringThread;

	private static Set<MonitoringListener> listeners = new HashSet<MonitoringListener>();

	public static void addListener(MonitoringListener listener) {
		listeners.add(listener);
	}

	public static void removeListener(MonitoringListener listener) {
		listeners.remove(listener);
	}

	public static void setMonitoringInterval(int milliseconds) {
		if (milliseconds < 10)
			milliseconds = 10;

		monitorInterval = milliseconds;
	}

	public static int getMonitorInterval() {
		return monitorInterval;
	}

	public static synchronized void startMonitoring(final Path logPath) {

		if (monitoringRunning)
			return;

		monitoringRunning = true;

		monitoringThread = new Thread() {
			@Override
			public void run() {
				Detector detector = new Detector();

				try {
					OutputStream out = Files.newOutputStream(logPath, CREATE, TRUNCATE_EXISTING);
					ObjectMapper mapper = new ObjectMapper();

					while (monitoringRunning) {
						MonitorSample sample = detector.generateSample(true);
						out.write(mapper.writeValueAsBytes(sample));
						out.write('\n');

						for (MonitoringListener listener : listeners) {
							listener.sampleGenerated(sample);
						}

						try {
							Thread.sleep(monitorInterval);
						} catch (InterruptedException e) {
							// do nothing
						}
					}

					out.close();
				} catch (IOException e) {
					log.error("Cannot start monitoring", e);
				}
			}
		};

		monitoringThread.start();
	}

	public static synchronized void stopMonitoring() {
		monitoringRunning = false;
		monitoringThread.interrupt();

		try {
			monitoringThread.join();
		} catch (InterruptedException e) {
			// do nothing
		}
	}
}
