package cz.cuni.mff.d3s.been.taskapi;

import org.slf4j.Logger;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/**
 * Created with IntelliJ IDEA.
 * User: Kuba
 * Date: 11.03.13
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public class TaskLogger extends TaskLoggerBase {
	void log(int level, String message, Throwable t) {
		System.err.println(message);
	}
}
