package cz.cuni.mff.d3s.been.detectors;

import cz.cuni.mff.d3s.been.core.ri.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 25.02.13
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
public class Detector {

	SigarDetector detector;

	public Detector() {
		// detect HW
		detector = new SigarDetector();
	}

    public void detectAll(RuntimeInfo runtimeInfo) {
		// detect hardware
        runtimeInfo.setHardware(detector.detectHardware());

        // detect Java
        JavaDetector java = new JavaDetector();
        runtimeInfo.setJava(java.detectJava());

        // detect OS
        runtimeInfo.setOperatingSystem(detector.detectOperatingSystem());

		// detect filesystems
		for (Filesystem fs : detector.detectFilesystems()) {
			runtimeInfo.getFilesystem().add(fs);
		}
    }

	private MonitorSample lastSample;

	public MonitorSample generateSample(boolean differential) {
		MonitorSample newSample = detector.generateSample();
		MonitorSample sample = newSample;

		if (differential)
			sample = calculateDifferentialSample(sample, lastSample);

		lastSample = newSample;

		sample.setTimestamp(System.nanoTime());
		return sample;
	}

	private MonitorSample calculateDifferentialSample(MonitorSample newSample, MonitorSample oldSample) {

		MonitorSample diff = new MonitorSample();
		diff.setLoadAverage(newSample.getLoadAverage());
		diff.setTimestamp(newSample.getTimestamp());
		diff.setFreeMemory(newSample.getFreeMemory());
		diff.setProcessCount(newSample.getProcessCount());

		// network
		for (int i = 0; i < newSample.getInterfaces().size(); i++) {
			NetworkSample n1 = newSample.getInterfaces().get(i);
			NetworkSample diffSample = new NetworkSample();
			diffSample.setName(n1.getName());

			if (oldSample != null) {
				NetworkSample n2 = oldSample.getInterfaces().get(i);
				diffSample.setBytesIn(n1.getBytesIn() - n2.getBytesIn());
				diffSample.setBytesOut(n1.getBytesOut() - n2.getBytesOut());
			}

			diff.getInterfaces().add(diffSample);
		}

		// filesystems
		for (int i = 0; i < newSample.getFilesystems().size(); i++) {
			FilesystemSample f1 = newSample.getFilesystems().get(i);
			FilesystemSample diffSample = new FilesystemSample();

			if (oldSample != null) {
				FilesystemSample f2 = oldSample.getFilesystems().get(i);
				diffSample.setReadBytes(f1.getReadBytes() - f2.getReadBytes());
				diffSample.setReads(f1.getReads() - f2.getReads());
				diffSample.setWriteBytes(f1.getWriteBytes() - f2.getWriteBytes());
				diffSample.setWrites(f1.getWrites() - f2.getWrites());
			}

			diff.getFilesystems().add(diffSample);
		}

		return diff;
	}
}
