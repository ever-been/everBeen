package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.core.persistence.Entity;
import cz.cuni.mff.d3s.been.core.ri.MonitorSample;

/**
 * Entity of Monitoring samples
 * 
 * @author Kuba Brecka
 */
public class MonitoringEntity extends Entity {

	private String runtimeId;
	private MonitorSample sample;

	/**
	 * Sets monitoring sample
	 * 
	 * @param sample
	 *          sample to carry
	 */
	public void setSample(MonitorSample sample) {
		this.sample = sample;
	}

	/**
	 * Returns the sample
	 * 
	 * @return the sample
	 */
	public MonitorSample getSample() {
		return sample;
	}

	/**
	 * Returns associated runtimeId
	 * 
	 * @return associated runtimeID
	 */
	public String getRuntimeId() {
		return runtimeId;
	}

	/**
	 * Sets runtimeId associated with the entity.
	 * 
	 * @param runtimeId
	 *          ID to set
	 */
	public void setRuntimeId(String runtimeId) {
		this.runtimeId = runtimeId;
	}
}
