package cz.cuni.mff.d3s.been.logging;

import cz.cuni.mff.d3s.been.core.persistence.Entity;

/**
 * Log message logged by a BEEN service. Wraps {@link LogMessage} with additional info
 *
 * @author darklight
 */
public class ServiceLogMessage extends Entity {

    private LogMessage message;
    private String hostRuntimeId;
    private String beenId;
    private String serviceName; // DON'T DELETE THIS FIELD; IT'S NOT BEING SET YET, BUT WILL BE IN THE FUTURE

    public ServiceLogMessage() {
        created = System.currentTimeMillis();
    }

    /**
     * Get the actual log message
     *
     * @return The message
     */
    public LogMessage getMessage() {
        return message;
    }

    /**
     * Set the actual log message
     *
     * @param message Message to set
     */
    public void setMessage(LogMessage message) {
        this.message = message;
    }

    /**
     * Get the ID of the host runtime from which this message is coming. May be <code>null</code> if the logging node is not running a Host Runtime
     *
     * @return The Host ID
     */
    public String getHostRuntimeId() {
        return hostRuntimeId;
    }

    /**
     * Set the ID of the host runtime which logs this message
     *
     * @param hostRuntimeId ID of the logging host
     */
    public void setHostRuntimeId(String hostRuntimeId) {
        this.hostRuntimeId = hostRuntimeId;
    }

    /**
     * Get the Been ID of the node that logged this message
     *
     * @return The Been ID
     */
    public String getBeenId() {
        return beenId;
    }

    /**
     * Set the Been ID of the node that logged this message
     *
     * @param beenId Been ID to set
     */
    public void setBeenId(String beenId) {
        this.beenId = beenId;
    }

    /**
     * Get the name of the service that produced this message
     *
     * @return The name of the logging service
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Set the name of the service that produced this message
     *
     * @param serviceName Service name to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Fluently set the actual message
     *
     * @param message Actual message to set
     *
     * @return This {@link ServiceLogMessage}, with changed message
     */
    public ServiceLogMessage withMessage(LogMessage message) {
        setMessage(message);
        return this;
    }

    /**
     * Fluently set the host ID
     *
     * @param hostRuntimeId Host ID to set
     *
     * @return This {@link ServiceLogMessage}, with changed host ID
     */
    public ServiceLogMessage withHostRuntimeId(String hostRuntimeId) {
        setHostRuntimeId(hostRuntimeId);
        return this;
    }

    /**
     * Fluently set the Been ID
     *
     * @param beenId Been ID to set
     *
     * @return This {@link ServiceLogMessage}, with changed Been ID
     */
    public ServiceLogMessage withBeenId(String beenId) {
        setBeenId(beenId);
        return this;
    }

    /**
     * Fluently set the service name
     *
     * @param serviceName Name of the service producing this message
     *
     * @return This {@link ServiceLogMessage}, with changed service name
     */
    public ServiceLogMessage withServiceName(String serviceName) {
        setServiceName(serviceName);
        return this;
    }
}
