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
import cz.cuni.mff.d3s.been.core.utils.JSONUtils;

/**
 * Created with IntelliJ IDEA. User: Kuba Date: 24.02.13 Time: 13:49 To change
 * this template use File | Settings | File Templates.
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
		System.out.println(context.getValue("//networkInterface[1]/mtu"));

		// try the monitor
		for (int i = 0; i < 10; i++) {
			MonitorSample sample = detector.generateSample(true);

			try {
				String s = JSONUtils.serialize(sample);
				System.out.println(s);
			} catch (JSONUtils.JSONSerializerException e) {
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
