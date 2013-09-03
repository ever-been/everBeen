package cz.cuni.mff.d3s.been.detectors;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.d3s.been.core.ri.MonitorSample;
import cz.cuni.mff.d3s.been.util.PropertyReader;

/**
 * @author Kuba Brecka
 */
public class Monitoring {
	private static final Logger log = LoggerFactory.getLogger(Monitoring.class);

	private int monitorInterval = MonitoringConfiguration.DEFAULT_INTERVAL;

	private volatile boolean monitoringRunning = false;
	private Thread monitoringThread;

	private Set<MonitoringListener> listeners = new HashSet();

	public Monitoring(Properties properties) {
		PropertyReader propertyReader = PropertyReader.on(properties);
		Integer interval = propertyReader.getInteger(
				MonitoringConfiguration.INTERVAL,
				MonitoringConfiguration.DEFAULT_INTERVAL);
		setMonitoringInterval(interval);
	}

	public void addListener(MonitoringListener listener) {
		listeners.add(listener);
	}

	public void setMonitoringInterval(int milliseconds) {
		if (milliseconds < 10) {
			milliseconds = 10;
		}

		monitorInterval = milliseconds;
	}

	public synchronized void startMonitoring() {

		if (monitoringRunning)
			return;

		monitoringRunning = true;

		monitoringThread = new Thread() {
			@Override
			public void run() {
				Detector detector = new Detector();

				try {
					while (monitoringRunning) {
						MonitorSample sample = detector.generateSample(true);

						for (MonitoringListener listener : listeners) {
							listener.sampleGenerated(sample);
						}

						try {
							Thread.sleep(monitorInterval);
						} catch (InterruptedException e) {
							// do nothing
						}
					}
				} catch (Exception e) {
					log.error("Monitoring failed", e);
				} finally {
					for (MonitoringListener listener : listeners) {
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
