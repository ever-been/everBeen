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

	/**
	 * Creates new ServiceInfo.
	 * 
	 * @param serviceName
	 *          name of the service
	 * @param uuid
	 *          Hazelcast ID of the service
	 */
	public ServiceInfo(String serviceName, String uuid) {
		this.serviceName = serviceName;
		this.uuid = uuid;
		this.params = new HashMap<>();
	}

	/**
	 * Sets Hazelcast UUID.
	 * 
	 * @param hazelcastUuid
	 *          ID to set
	 */
	public void setHazelcastUuid(String hazelcastUuid) {
		this.hazelcastUuid = hazelcastUuid;
	}

	/**
	 * Returns unique ID of underlying Hazelcast Instance if this instance is
	 * Hazelcat Member (data instance/lite instance). If the instance is Hazelcast
	 * Client (native instance), then this field should be obviously null.
	 * 
	 * @return the Hazecast UUID
	 */
	public String getHazelcastUuid() {
		return hazelcastUuid;
	}

	/**
	 * Returns cluster wide unique ID of the service
	 * 
	 * @return cluster wide unique ID of the service
	 */
	public final String getUuid() {
		return uuid;
	}

	/**
	 * Gets parameter of the service identified by 'paramName'
	 * 
	 * @param paramName
	 *          parameter name
	 * 
	 * @return parameter of given name
	 */
	public final Object getParam(String paramName) {
		return params.get(paramName);
	}

	/**
	 * Sets parameter of the service under the given 'paramName'
	 * 
	 * @param paramName
	 *          name of the parameter
	 * @param param
	 *          parameter to be set
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

	/**
	 * Sets service state
	 * 
	 * @param serviceState
	 *          service state to set
	 */
	public final void setServiceState(ServiceState serviceState) {
		this.serviceState = serviceState;
	}

	/**
	 * Returns service state reason.
	 * 
	 * @return service state reason
	 */
	public final String getStateReason() {
		return stateReason;
	}

	/**
	 * Sets the reason associated with the state
	 * 
	 * @param stateReason
	 *          reason to set
	 */
	public final void setStateReason(String stateReason) {
		this.stateReason = stateReason;
	}

	/**
	 * Sets associated info.
	 * 
	 * @param serviceInfo
	 *          info to set
	 */
	public void setServiceInfo(String serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	/**
	 * Returns the service info
	 * 
	 * @return service info.
	 */
	public String getServiceInfo() {
		return serviceInfo != null ? serviceInfo : "";
	}
}
