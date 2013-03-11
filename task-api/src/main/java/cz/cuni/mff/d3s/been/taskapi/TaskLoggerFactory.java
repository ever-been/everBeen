package cz.cuni.mff.d3s.been.taskapi;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 11.03.13
 * Time: 11:45
 * To change this template use File | Settings | File Templates.
 */
public class TaskLoggerFactory implements ILoggerFactory {

	private static TaskLogger logger = new TaskLogger();

	@Override
	public Logger getLogger(String name) {
		return logger;
	}
}
