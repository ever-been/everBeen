package cz.cuni.mff.d3s.been.detectors;

import cz.cuni.mff.d3s.been.core.ri.Java;

/**
 * @author Kuba Brecka
 */
public class JavaDetector {
    public Java detectJava() {
        Java java = new Java();

        java.setVersion(System.getProperty("java.version"));
        java.setVendor(System.getProperty("java.vendor"));
        java.setVersion(System.getProperty("java.version"));
        java.setVendor(System.getProperty("java.vendor"));
        java.setRuntimeName(System.getProperty("java.runtime.name"));
        java.setVMVersion(System.getProperty("java.vm.version"));
        java.setVMVendor(System.getProperty("java.vm.vendor"));
        java.setRuntimeVersion(System.getProperty("java.runtime.version"));
        java.setSpecificationVersion(System.getProperty("java.specification.version"));

        return java;
    }
}
