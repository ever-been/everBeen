package cz.cuni.mff.d3s.been.detectors;

import cz.cuni.mff.d3s.been.core.ri.MonitorSample;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;

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
    }

	public MonitorSample generateSample() {
		MonitorSample sample = detector.generateSample();
		sample.setTimestamp(System.nanoTime());
		return sample;
	}
}
