package org.slf4j.impl;

import cz.cuni.mff.d3s.been.taskapi.TaskLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * This logger binder ensures <em>slf4j</em> loads the EverBEEN implementation of the task logger
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {
	private static TaskLoggerFactory factory = new TaskLoggerFactory();

	private static StaticLoggerBinder singleton = new StaticLoggerBinder();

	/**
	 * Get the logger binder singleton
	 *
	 * @return the singleton
	 */
	public static StaticLoggerBinder getSingleton() {
		return singleton;
	}

	@Override
	public ILoggerFactory getLoggerFactory() {
		return factory;
	}

	@Override
	public String getLoggerFactoryClassStr() {
		return TaskLoggerFactory.class.getName();
	}
}
