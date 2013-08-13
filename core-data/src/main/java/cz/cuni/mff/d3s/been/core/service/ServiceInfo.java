package cz.cuni.mff.d3s.been.core.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This object stores information about service in cluster.
 *
 * @author donarus
 */
public final class ServiceInfo implements Serializable {

    private final String uuid;

    private final Map<String, Object> params;

    private final String serviceName;

    private ServiceState serviceState;

    private String stateReason;

    private String serviceInfo;

    private String hazelcastUuid;

    public ServiceInfo(String serviceName, String uuid) {
        this.serviceName = serviceName;
        this.uuid = uuid;
        this.params = new HashMap<>();
    }

    public void setHazelcastUuid(String hazelcastUuid) {
        this.hazelcastUuid = hazelcastUuid;
    }

    /**
     * Returns unique ID of underlying Hazelcast Instance if this instance is Hazelcat Member (data instance/lite instance).
     * If the instance is Hazelcast Client (native instance), then this field should be obviously null.
     *
     * @return
     */
    public String getHazelcastUuid() {
        return hazelcastUuid;
    }

    /**
     * returns cluster wide unique ID of the service
     *
     * @return
     */
    public final String getUuid() {
        return uuid;
    }

    /**
     * Gets parameter of the service identified by 'paramName'
     *
     * @param paramName
     * @return parameter of given name
     */
    public final Object getParam(String paramName) {
        return params.get(paramName);
    }

    /**
     * Sets parameter of the service under the given 'paramName'
     *
     * @param paramName name of the parameter
     * @param param     parameter to be set
     */
    public final void setParam(String paramName, Object param) {
        params.put(paramName, param);
    }

    /**
     * Returns service parameters
     *
     * @return unmodifiable map of parameters
     */
    public final Map<String, Object> getParams() {
        return Collections.unmodifiableMap(params);
    }

    /**
     * Name of the service
     *
     * @return service name
     */
    public final String getServiceName() {
        return serviceName;
    }

    /**
     * State of the service
     *
     * @return service state
     */
    public final ServiceState getServiceState() {
        return serviceState;
    }

    public final void setServiceState(ServiceState serviceState) {
        this.serviceState = serviceState;
    }

    public final String getStateReason() {
        return stateReason;
    }

    public final void setStateReason(String stateReason) {
        this.stateReason = stateReason;
    }

    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public String getServiceInfo() {
        return serviceInfo != null ? serviceInfo : "";
    }
}