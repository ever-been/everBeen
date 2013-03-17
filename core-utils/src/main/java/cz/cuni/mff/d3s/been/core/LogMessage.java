package cz.cuni.mff.d3s.been.core;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Message used to log events on tasks. Java Task API.
 *
 * @author Martin Sixta
 */
public class LogMessage {

	public int level;
	public String message;
	public String errorTrace;

	public LogMessage(int level, String message, Throwable t) {
		this.level = level;
		this.message = message;

		if (t != null) {
			try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
				t.printStackTrace(pw);
				errorTrace = sw.toString();
			} catch (IOException e) {
				// TODO handle exception
			}

		}
	}
}
