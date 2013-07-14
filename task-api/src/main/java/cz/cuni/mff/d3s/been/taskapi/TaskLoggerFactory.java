package cz.cuni.mff.d3s.been.taskapi;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * @author Kuba Brecka
 */
public class TaskLoggerFactory implements ILoggerFactory {

	@Override
	public Logger getLogger(String name) {
		return new TaskLogger(name);
	}
}
