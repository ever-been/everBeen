package cz.cuni.mff.d3s.been.detectors;

import cz.cuni.mff.d3s.been.core.ri.*;

/**
 * @author Kuba Brecka
 */
public class Detector {

	SigarDetector nativeDetector;
	JavaDetector javaDetector;

	public Detector() {
		// detect HW
		nativeDetector = new SigarDetector();
		javaDetector = new JavaDetector();
	}

    public void detectAll(RuntimeInfo runtimeInfo) {
	    // detect Java
	    runtimeInfo.setJava(javaDetector.detectJava());

	    if (! nativeDetector.isSigarAvailable()) {
		    javaDetector.detectOperatingSystem(runtimeInfo);
		    javaDetector.detectHardware(runtimeInfo);
		    javaDetector.detectFilesystems(runtimeInfo);
		    return;
	    }

	    // detect hardware
	    runtimeInfo.setHardware(nativeDetector.detectHardware());

        // detect OS
        runtimeInfo.setOperatingSystem(nativeDetector.detectOperatingSystem());

		// detect filesystems
		for (Filesystem fs : nativeDetector.detectFilesystems()) {
			runtimeInfo.getFilesystem().add(fs);
		}
    }

	private MonitorSample lastSample;

	public MonitorSample generateSample(boolean differential) {
		MonitorSample newSample;
		if (nativeDetector.isSigarAvailable()) {
			newSample = nativeDetector.generateSample();
		} else {
			newSample = javaDetector.generateSample();
		}

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
		diff.setCpuUsage(newSample.getCpuUsage());

		// network
		int networkCount = newSample.getInterfaces().size();
		if (oldSample != null) networkCount = Math.min(networkCount, oldSample.getInterfaces().size());
		for (int i = 0; i < networkCount; i++) {
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
		int fileSystemCount = newSample.getFilesystems().size();
		if (oldSample != null) fileSystemCount = Math.min(fileSystemCount, oldSample.getFilesystems().size());
		for (int i = 0; i < fileSystemCount; i++) {
			FilesystemSample f1 = newSample.getFilesystems().get(i);
			FilesystemSample diffSample = new FilesystemSample();
			diffSample.setDeviceName(f1.getDeviceName());
			diffSample.setDirectory(f1.getDirectory());

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
