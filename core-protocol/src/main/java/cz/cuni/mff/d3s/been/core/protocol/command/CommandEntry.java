package cz.cuni.mff.d3s.been.core.protocol.command;

import java.io.Serializable;

/**
 * Objects of this type are used to represent state of the invoked command in map in Hazelcast cluster.
 */
public final class CommandEntry implements Serializable {

    /**
     * id of runtime for which is this command aimed
     */
    private final String runtimeId;

    /**
     * simple human readable command description
     */
    private final String description;

    /**
     * current state of the command
     */
    private final CommandEntryState state;

    /**
     * cluster wide unique operation id
     */
    private final long operationId;


    /**
     * creates new command entry
     *
     * @param runtimeId   id of runtime for which the entry is aimed
     * @param description simple human readable description
     * @param state       state of the command
     * @param operationId cluster wide unique id of operation
     */
    public CommandEntry(String runtimeId, String description, CommandEntryState state, long operationId) {
        this.runtimeId = runtimeId;
        this.description = description;
        this.state = state;
        this.operationId = operationId;
    }

    /**
     * @return current state of the command
     */
    public CommandEntryState getState() {
        return state;
    }

    /**
     * @return id of runtime for which the command is aimed
     */
    public String getRuntimeId() {
        return runtimeId;
    }

    /**
     * @return simple human readable command description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return cluster wide unique id of operation
     */
    public long getOperationId() {
        return operationId;
    }
}