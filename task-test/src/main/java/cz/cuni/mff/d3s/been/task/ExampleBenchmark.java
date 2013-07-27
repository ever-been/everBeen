package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.benchmarkapi.BenchmarkException;
import cz.cuni.mff.d3s.been.benchmarkapi.ContextBuilder;
import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import cz.cuni.mff.d3s.been.benchmarkapi.Benchmark;
import cz.cuni.mff.d3s.been.core.task.TaskContextState;
import cz.cuni.mff.d3s.been.taskapi.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kuba Brecka
 */
public class ExampleBenchmark extends Benchmark {

	private static final Logger log = LoggerFactory.getLogger(ExampleBenchmark.class);

	@Override
	public void onResubmit() {
		log.info("Resubmit.");
	}

	@Override
	public void onTaskContextFinished(String taskContextId, TaskContextState state) {
		log.info("Task context {} finished with state {}", taskContextId, state);
	}

	@Override
	public TaskContextDescriptor generateTaskContext() throws BenchmarkException {
		log.debug("Generating new context...");

		int currentRun = Integer.parseInt(this.storageGet("i", "0"));
		TaskContextDescriptor taskContextDescriptor;
		if (currentRun < 5) {
			// generate a regular context
			taskContextDescriptor = getTaskContextFromResource("Example.tcd.xml");
			setTaskContextProperty(taskContextDescriptor, "iteration", Integer.toString(currentRun));
		} else if (currentRun == 5) {
			// run evaluator
			ContextBuilder contextBuilder = ContextBuilder.createFromResource(this.getClass(), "ExampleEvaluator.tcd.xml");
			taskContextDescriptor = contextBuilder.build();
		} else {
			// we're done
			taskContextDescriptor = null;
		}

		currentRun++;
		this.storageSet("i", Integer.toString(currentRun));

		return taskContextDescriptor;
	}

}
