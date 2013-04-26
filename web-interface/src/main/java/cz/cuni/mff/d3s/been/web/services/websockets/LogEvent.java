package cz.cuni.mff.d3s.been.web.services.websockets;

/**
 * User: donarus
 * Date: 4/25/13
 * Time: 10:12 PM
 */
public class LogEvent implements Event {

    public final Type type;

    public final String message;

    public enum Type {
        ERROR, DEBUG, INFO

    }

    public LogEvent(final Type type, final String message) {
        this.type = type;
        this.message = message;
    }
}
