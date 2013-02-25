package cz.cuni.mff.d3s.been.detectors;

import cz.cuni.mff.d3s.been.core.hwi.Hardware;
import org.apache.commons.jxpath.JXPathContext;
import org.hyperic.sigar.SigarException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 24.02.13
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
public class DetectorRunner {
    public static void main(String[] args) {
        SigarDetector detector = new SigarDetector();

        // get HW info
        Hardware hw = null;
        try {
            hw = detector.detectHardware();
        } catch (SigarException e) {
            e.printStackTrace();
        }

        // show example XML output
        try {
            JAXBContext context = JAXBContext.newInstance(Hardware.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(hw, System.out);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        // try some XPaths
        JXPathContext context = JXPathContext.newContext(hw);
        System.out.println(context.getValue("cpu/vendor"));
        System.out.println(context.getValue("networkInterface[name='en0']/mtu"));
    }
}
