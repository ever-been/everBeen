package cz.cuni.mff.d3s.been.detectors;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.jxpath.JXPathContext;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import cz.cuni.mff.d3s.been.core.ri.MonitorSample;
import cz.cuni.mff.d3s.been.core.ri.RuntimeInfo;
import cz.cuni.mff.d3s.been.util.JSONUtils;
import cz.cuni.mff.d3s.been.util.JsonException;

/**
 * Test run of the Detector.
 * 
 * For testing purposes.
 * 
 * @author Kuba Brecka
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
		try {
			System.out.println(context.getValue("//cpu/vendor"));
			System.out.println(context.getValue("//networkInterface[1]/mtu"));
		} catch (Exception e) {
			System.out.println(e);
		}

		// try the monitor
		for (int i = 0; i < 10; i++) {
			MonitorSample sample = detector.generateSample(true);

			try {
				String s = JSONUtils.newInstance().serialize(sample);
				System.out.println(s);
			} catch (JsonException e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// monitor sampling benchmark
		long startTime = System.nanoTime();
		long counter = 0;
		ObjectMapper mapper = new ObjectMapper();
		while (true) {
			if (System.nanoTime() - startTime > 1000 * 1000 * 1000)
				break;

			MonitorSample sample = detector.generateSample(true);

			try {
				mapper.writeValueAsString(sample);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			counter++;
		}

		System.out.println("Monitoring performs " + counter + " samples/second");
	}
}
