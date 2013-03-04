package cz.cuni.mff.d3s.been.detectors;

import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 25.02.13
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
public class Detector {
    public void detectAll(RuntimeInfo runtimeInfo) {
        // detect HW
        SigarDetector detector = new SigarDetector();
        runtimeInfo.setHardware(detector.detectHardware());

        // detect Java
        JavaDetector java = new JavaDetector();
        runtimeInfo.setJava(java.detectJava());

        // detect OS
        // TODO
    }
}
