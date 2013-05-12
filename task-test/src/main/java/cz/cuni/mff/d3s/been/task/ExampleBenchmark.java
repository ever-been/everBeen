package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.benchmarkapi.BenchmarkException;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.benchmarkapi.Benchmark;

/**
 * @author Kuba Brecka
 */
public class ExampleBenchmark extends Benchmark {

	@Override
	public TaskContextDescriptor generateTaskContext() throws BenchmarkException {
		int currentRun = Integer.parseInt(this.storageGet("i", "0"));
		if (currentRun >= 30) return null;
		currentRun++;
		this.storageSet("i", Integer.toString(currentRun));

		TaskContextDescriptor taskContextDescriptor = getTaskContextFromResource("Example.tcd.xml");
		setTaskContextProperty(taskContextDescriptor, "iteration", Integer.toString(currentRun));
		return taskContextDescriptor;
	}

}
