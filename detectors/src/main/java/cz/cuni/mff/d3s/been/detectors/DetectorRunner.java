package cz.cuni.mff.d3s.been.detectors;

import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import org.apache.commons.jxpath.JXPathContext;

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
        RuntimeInfo ri = new RuntimeInfo();
        ri.setId("FAKE ID");
        ri.setHost("localhost");
        ri.setPort(0);

        Detector detector = new Detector();
        detector.detectAll(ri);

        // show example XML output
        try {
            JAXBContext context = JAXBContext.newInstance(RuntimeInfo.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(ri, System.out);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        // try some XPaths
        JXPathContext context = JXPathContext.newContext(ri);
        System.out.println(context.getValue("//cpu/vendor"));
        System.out.println(context.getValue("//networkInterface[name='en0']/mtu"));
    }
}
