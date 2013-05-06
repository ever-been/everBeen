package cz.cuni.mff.d3s.been.nginx;

import cz.cuni.mff.d3s.been.core.task.TaskContextDescriptor;

/**
 * @author Kuba Brecka
 */
public class NginxBenchmark /*extends Benchmark*/ {

	private int revision = 1000;

	//@Override
	TaskContextDescriptor generateContextDescriptor() {
		/*
		TaskContextDescriptor taskContextDescriptor = getTaskContextFromResource("Nginx.tcd.xml");
		setTaskContextProperty(taskContextDescriptor, "revision", revision);
		revision++;

		return taskContextDescriptor;
		*/
		return null;
	}

}
