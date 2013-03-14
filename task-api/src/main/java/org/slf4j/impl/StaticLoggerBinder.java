package org.slf4j.impl;

import cz.cuni.mff.d3s.been.taskapi.TaskLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 11.03.13
 * Time: 11:43
 * To change this template use File | Settings | File Templates.
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {
	private static TaskLoggerFactory factory = new TaskLoggerFactory();

	private static StaticLoggerBinder singleton = new StaticLoggerBinder();

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
