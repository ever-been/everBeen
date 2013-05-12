package cz.cuni.mff.d3s.been.taskapi;

import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Kuba Brecka
 */
public abstract class Evaluator extends Task {

	private static final Logger log = LoggerFactory.getLogger(Evaluator.class);

	public abstract File evaluate();

	@Override
	public void run(String[] args) {
		File outputFile = evaluate();

		System.out.println(outputFile);
	}
}
