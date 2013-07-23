package cz.cuni.mff.d3s.been.core.protocol.messages;

import java.io.Serializable;

/**
 * Represents message used in communication between services
 */
@SuppressWarnings("serial")
public abstract class BaseMessage implements Serializable {

    /**
     * id of node for which is this message intended
     */
    public String recieverId;

    /**
     * @param recieverId id of node for which is this message intended
     */
    public BaseMessage(String recieverId) {
        this.recieverId = recieverId;
    }

}
