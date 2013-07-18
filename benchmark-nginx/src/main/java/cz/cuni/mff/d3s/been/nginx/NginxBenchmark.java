package cz.cuni.mff.d3s.been.nginx;

import cz.cuni.mff.d3s.been.benchmarkapi.Benchmark;
import cz.cuni.mff.d3s.been.benchmarkapi.BenchmarkException;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kuba Brecka
 */
public class NginxBenchmark extends Benchmark {

	private static final Logger log = LoggerFactory.getLogger(NginxBenchmark.class);

	@Override
	public TaskContextDescriptor generateTaskContext() throws BenchmarkException {
		int fromRevision = Integer.parseInt(this.getProperty("fromRevision"));
		int toRevision = Integer.parseInt(this.getProperty("toRevision"));
		boolean fakeRun = Boolean.parseBoolean(this.getProperty("fakeRun"));

		int currentRevision = Integer.parseInt(this.storageGet("currentRevision", Integer.toString(fromRevision)));
		if (currentRevision > toRevision) return null;

		log.info("Generating context for revision {}", currentRevision);

		TaskContextDescriptor taskContextDescriptor = getTaskContextFromResource("Nginx.tcd.xml");
		setTaskContextProperty(taskContextDescriptor, "revision", Integer.toString(currentRevision));
		setTaskContextProperty(taskContextDescriptor, "fakeRun", Boolean.toString(fakeRun));

		currentRevision++;
		this.storageSet("currentRevision", Integer.toString(currentRevision));

		return taskContextDescriptor;
	}
}
