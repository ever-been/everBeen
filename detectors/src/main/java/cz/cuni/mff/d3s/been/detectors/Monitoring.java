package cz.cuni.mff.d3s.been.detectors;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import cz.cuni.mff.d3s.been.util.PropertyReader;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.ri.MonitorSample;

/**
 * @author Kuba Brecka
 */
public class Monitoring {
	private static final Logger log = LoggerFactory.getLogger(Monitoring.class);

	private int monitorInterval = MonitoringConfiguration.DEFAULT_INTERVAL;

	private volatile boolean monitoringRunning = false;
	private Thread monitoringThread;

	private Set<MonitoringListener> listeners = new HashSet<MonitoringListener>();

	public Monitoring(Properties properties) {
		PropertyReader propertyReader = PropertyReader.on(properties);
		Integer interval = propertyReader.getInteger(MonitoringConfiguration.INTERVAL, MonitoringConfiguration.DEFAULT_INTERVAL);
		setMonitoringInterval(interval);
	}

	public void addListener(MonitoringListener listener) {
		listeners.add(listener);
	}

	public void removeListener(MonitoringListener listener) {
		listeners.remove(listener);
	}

	public void setMonitoringInterval(int milliseconds) {
		if (milliseconds < 10)
			milliseconds = 10;

		monitorInterval = milliseconds;
	}

	public int getMonitorInterval() {
		return monitorInterval;
	}

	public synchronized void startMonitoring(final Path logPath) {

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
				} finally {
                    for (MonitoringListener listener: listeners) {
                        listener.close();
                    }
                }
			}
		};

		monitoringThread.start();
	}

	public synchronized void stopMonitoring() {
		monitoringRunning = false;
		monitoringThread.interrupt();

		try {
			monitoringThread.join();
		} catch (InterruptedException e) {
			// do nothing
		}
	}
}
