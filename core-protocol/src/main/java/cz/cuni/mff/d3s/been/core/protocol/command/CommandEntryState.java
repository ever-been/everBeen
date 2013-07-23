package cz.cuni.mff.d3s.been.core.protocol.command;

/**
 * Possible {@link CommandEntry} states
 */
public enum CommandEntryState {

    /**
     * command has been accepted
     */
    PENDING,

    /**
     * command has been successfully executed
     */
    FINISHED,

    /**
     * command execution failed
     */
    FAILED

}

