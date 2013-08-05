package cz.cuni.mff.d3s.been.hostruntime;

import cz.cuni.mff.d3s.been.core.persistence.Entity;
import cz.cuni.mff.d3s.been.core.ri.MonitorSample;

/**
 * @author Kuba Brecka
 */
public class MonitoringEntity extends Entity {

	private String runtimeId;
	private MonitorSample sample;

	public void setSample(MonitorSample sample) {
		this.sample = sample;
	}

	public MonitorSample getSample() {
		return sample;
	}

	public String getRuntimeId() {
		return runtimeId;
	}

	public void setRuntimeId(String runtimeId) {
		this.runtimeId = runtimeId;
	}
}
