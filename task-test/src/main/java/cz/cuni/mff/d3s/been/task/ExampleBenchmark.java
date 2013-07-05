package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.benchmarkapi.BenchmarkException;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.benchmarkapi.Benchmark;
import cz.cuni.mff.d3s.been.taskapi.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kuba Brecka
 */
public class ExampleBenchmark extends Benchmark {

	private static final Logger log = LoggerFactory.getLogger(ExampleBenchmark.class);

	@Override
	public TaskContextDescriptor generateTaskContext() throws BenchmarkException {
		log.debug("Debug log");

		int currentRun = Integer.parseInt(this.storageGet("i", "0"));
		if (currentRun >= 30) return null;
		currentRun++;
		this.storageSet("i", Integer.toString(currentRun));

		log.warn("Example warning - just generated {}", currentRun);
		log.error("Example error - just generated {}", currentRun);

		TaskContextDescriptor taskContextDescriptor = getTaskContextFromResource("Example.tcd.xml");
		setTaskContextProperty(taskContextDescriptor, "iteration", Integer.toString(currentRun));
		return taskContextDescriptor;
	}

}
