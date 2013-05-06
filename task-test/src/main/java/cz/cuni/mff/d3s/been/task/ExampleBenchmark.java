package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.benchmarkapi.BenchmarkException;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.benchmarkapi.Benchmark;

/**
 * @author Kuba Brecka
 */
public class ExampleBenchmark extends Benchmark {

	int i = 0;

	@Override
	public TaskContextDescriptor generateTaskContext() throws BenchmarkException {
		TaskContextDescriptor taskContextDescriptor = getTaskContextFromResource("Example.tcd.xml");

		if (i >= 10) return null;
		i++;

		return taskContextDescriptor;
	}

}
