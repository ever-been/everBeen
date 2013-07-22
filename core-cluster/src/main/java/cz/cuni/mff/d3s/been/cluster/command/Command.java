package cz.cuni.mff.d3s.been.cluster.command;

import java.util.Date;

/**
 * Author: donarus
 */
public abstract class Command {

    private Date time = new Date();

    private CommandState state = CommandState.NEW;

    private String reason;

    public final Date getTime() {
        return time;
    }

    public final CommandState getState() {
        return state;
    }

    public final String getReason() {
        return reason;
    }

}
